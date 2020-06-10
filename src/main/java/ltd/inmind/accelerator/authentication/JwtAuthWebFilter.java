package ltd.inmind.accelerator.authentication;

import com.google.gson.Gson;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.utils.KVPlusMap;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class JwtAuthWebFilter implements WebFilter {

    private ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers
            .pathMatchers(HttpMethod.POST, "/login");

    private final ReactiveAuthenticationManagerResolver<ServerHttpRequest> authenticationManagerResolver;

    //想办法怎么从spring security里拿出来
    private ServerSecurityContextRepository securityContextRepository;


    private String usernameParameter = "username";

    private String passwordParameter = "password";

    private Gson gson;

    private KVPlusMap<String, SecurityContext> cache = new KVPlusMap<>();

    public JwtAuthWebFilter(ReactiveAuthenticationManager authenticationManager, ServerSecurityContextRepository serverSecurityContextRepository, Gson gson) {
        this.gson = gson;
        this.authenticationManagerResolver = request -> Mono.just(authenticationManager);
        this.securityContextRepository = serverSecurityContextRepository;
    }

    /**
     * 1.判断路径是否为login(POST)
     * 2.取出用户输入的账户和密码 生成token
     * 3.开始认证过程
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return this.matcher.matches(exchange)
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .flatMap( matchResult -> this.convert(exchange))
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .flatMap( token -> authenticate(exchange, chain, token));
    }

    /**
     * 1. authentication manager开始认证
     * 2. 认证成功调用  on success
     * 3. 认证失败调用  on failure
     * @param token 登陆用的token
     */
    private Mono<Void> authenticate(ServerWebExchange exchange,
                                    WebFilterChain chain, Authentication token) {
        WebFilterExchange webFilterExchange = new WebFilterExchange(exchange, chain);

        return this.authenticationManagerResolver.resolve(exchange.getRequest())
                .flatMap(authenticationManager -> authenticationManager.authenticate(token))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalStateException("No provider found for " + token.getClass()))))
                .flatMap(authentication -> onAuthenticationSuccess(authentication, webFilterExchange))
                .onErrorResume(AuthenticationException.class, e -> onAuthenticationFailure(webFilterExchange, e));
    }

    private Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange webFilterExchange) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        this.securityContextRepository.save(exchange, securityContext)
                .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));


        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("X-AUTH-TOKEN", exchange.getAttribute("token"));

        DataResponse dataResponse = new DataResponse()
                .success();

        String resp = gson.toJson(dataResponse);

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange,
                                              AuthenticationException exception) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders()
                .add("Content-Type", "application/json");

        DataResponse dataResponse;
        if (exception instanceof BadCredentialsException){
            dataResponse = new DataResponse()
                    .failed()
                    .msg("用户名或密码错误.");
        }else {
            dataResponse = new DataResponse()
                    .failed()
                    .msg("登陆失败 请稍后再试.");
        }


        String resp = gson.toJson(dataResponse);

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));

    }


    private Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(this::createAuthentication);
    }

    private Mono<UsernamePasswordAuthenticationToken> createAuthentication(
            MultiValueMap<String, String> data) {
        String username = data.getFirst(this.usernameParameter);
        String password = data.getFirst(this.passwordParameter);

        return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
    }

}
