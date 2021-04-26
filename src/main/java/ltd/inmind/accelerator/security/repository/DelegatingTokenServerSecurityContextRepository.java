package ltd.inmind.accelerator.security.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.security.Jwt;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static ltd.inmind.accelerator.constants.SecurityConst.TOKEN_ATTR_NAME;

/**
 * Repository need delegating. and directly provide to spring security.
 * @author shenlanAZ
 */
@RequiredArgsConstructor
@Slf4j
public class DelegatingTokenServerSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtTokenSecurityContextService contextService;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        // 如果context为null 说明用户要做登出操作
        if (context == null){
            contextService.remove(exchange.getAttribute(TOKEN_ATTR_NAME));
            return Mono.empty();
        }

        Jwt jwt = contextService.save(context);

        exchange.getAttributes()
                .put(TOKEN_ATTR_NAME, jwt);

        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        Jwt token = exchange.getAttribute(TOKEN_ATTR_NAME);

        if (token == null)
            return Mono.empty();

        SecurityContext context = contextService.load(token);

        return Mono.justOrEmpty(context);
    }
}
