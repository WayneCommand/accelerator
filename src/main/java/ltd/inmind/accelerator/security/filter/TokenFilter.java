package ltd.inmind.accelerator.security.filter;

import ltd.inmind.accelerator.security.Jwt;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static ltd.inmind.accelerator.constants.SecurityConst.AUTHENTICATION_HEADER;
import static ltd.inmind.accelerator.constants.SecurityConst.TOKEN_ATTR_NAME;

/**
 * 负责 从合适的 header 里面取出 Jwt
 */
public class TokenFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        transformToken(serverWebExchange);

        return webFilterChain.filter(serverWebExchange);
    }

    void transformToken(ServerWebExchange exchange) {
        //从header里取出token
        String token = exchange.getRequest()
                .getHeaders()
                .getFirst(AUTHENTICATION_HEADER);

        exchange.getAttributes()
                .put(TOKEN_ATTR_NAME, Jwt.from(token));
    }
}
