package ltd.inmind.accelerator.security.oauth2.server.authorization.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 一个简单的 reactive 认证转换器
 * @author shenlanluck@gmail.com
 * @since 0.1
 */
public interface AuthenticationConverter {

    Mono<Authentication> convert(ServerWebExchange exchange);
}
