package ltd.inmind.accelerator.security.filter;

import com.google.gson.Gson;
import lombok.Data;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.service.Oauth2ClientService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 接口参考：https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps
 */
public class Oauth2Filter implements WebFilter {

    private final ServerWebExchangeMatcher authorizeMatcher = ServerWebExchangeMatchers
            .pathMatchers(HttpMethod.GET, "/login/oauth/authorize");

    private final ServerWebExchangeMatcher accessTokenMatcher = ServerWebExchangeMatchers
            .pathMatchers(HttpMethod.POST, "/login/oauth/access_token");

    private final Oauth2ClientService oauth2ClientService;

    private final ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();


    public Oauth2Filter(Oauth2ClientService clientService) {
        this.oauth2ClientService = clientService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        return authorizeMatcher.matches(serverWebExchange)
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .flatMap(mr -> convertForm(serverWebExchange))
                .switchIfEmpty(webFilterChain.filter(serverWebExchange).then(Mono.empty()))
                .flatMap(data -> authorize(data.getFirst("client_id"),
                        data.getFirst("redirect_uri"),
                        data.getFirst("scope"), serverWebExchange))
                .and(accessTokenMatcher.matches(serverWebExchange)
                        .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                        .flatMap(mr -> convertForm(serverWebExchange))
                        .switchIfEmpty(webFilterChain.filter(serverWebExchange).then(Mono.empty()))
                        .flatMap(data -> accessToken(data.getFirst("client_id"),
                                data.getFirst("client_secret"),
                                data.getFirst("code"),
                                serverWebExchange))
                        .onErrorResume(AcceleratorException.class, e -> onErr(serverWebExchange, e)));
    }

    Mono<MultiValueMap<String, String>> convertForm(ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(Mono::just);
    }

    // 1. 用户确认认证请求 (/login/oauth/authorize)
    Mono<Void> authorize(String clientId, String redirectUri, String scope, ServerWebExchange exchange) {

        if (!oauth2ClientService.verifyClientId(clientId))
            throw new AcceleratorException();

        // 将带着授权码(code) 重定向到redirect url
        String code = oauth2ClientService.grantCode(clientId, null);

        return redirectStrategy.sendRedirect(exchange, URI.create(String.format("%s?code=%s", redirectUri, code)));
    }

    // 3. 请求 token (/login/oauth/access_token)
    Mono<Void> accessToken(String clientId, String clientSecret, String code, ServerWebExchange exchange) {

        //验证id 和 secret 和 code
        try {
            String accessToken = oauth2ClientService.accessToken(clientId, clientSecret, code);

            AccessToken token = new AccessToken();
            token.setAccess_token(accessToken);
            token.setToken_type("bearer");
            return onAccessToken(token, exchange);
        } catch (AcceleratorException e) {
            System.err.println(e.getMsg());
            throw e;
        }

        // {"access_token":"gho_16C7e42F292c6912E7710c838347Ae178B4a", "scope":"repo,gist", "token_type":"bearer"}
    }

    Mono<Void> onAccessToken(AccessToken accessToken, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders()
                .add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        String resp = new Gson().toJson(accessToken);

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    Mono<Void> onErr(ServerWebExchange exchange, AcceleratorException exception){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders()
                .add("Content-Type", MediaType.APPLICATION_JSON_VALUE);


        String resp = "access token error";

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Data
    public class AccessToken {
        private String access_token;
        private String scope;
        private String token_type;
    }


}
