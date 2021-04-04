package ltd.inmind.accelerator.security.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.security.handler.AuthenticationFailureHandler;
import ltd.inmind.accelerator.security.handler.AuthenticationSuccessHandler;
import ltd.inmind.accelerator.security.repository.DelegatingTokenServerSecurityContextRepository;
import ltd.inmind.accelerator.serializer.DataResponseSerializer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 负责 登录操作
 * 签发 JWT Token
 */
public class LoginWebFilter implements WebFilter {

    private final ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers
            .pathMatchers(HttpMethod.POST, "/login");

    private final ReactiveAuthenticationManagerResolver<ServerHttpRequest> authenticationManagerResolver;

    private final ServerSecurityContextRepository securityContextRepository;

    // TODO Spring DI
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(DataResponse.class, new DataResponseSerializer())
            .create();

    private final ServerAuthenticationSuccessHandler authenticationSuccessHandler = new AuthenticationSuccessHandler(gson);

    private final ServerAuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler(gson);


    private final String usernameParameter = "username";

    private final String passwordParameter = "password";

    public LoginWebFilter(ReactiveAuthenticationManager authenticationManager, ServerSecurityContextRepository contextRepository) {
        this.authenticationManagerResolver = request -> Mono.just(authenticationManager);
        this.securityContextRepository = contextRepository;
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
                .onErrorResume(AuthenticationException.class, e -> this.authenticationFailureHandler
                        .onAuthenticationFailure(webFilterExchange, e));
    }

    private Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange webFilterExchange) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return this.securityContextRepository.save(exchange, securityContext)
                .then(this.authenticationSuccessHandler
                        .onAuthenticationSuccess(webFilterExchange, authentication))
                .contextWrite(context -> ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
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
