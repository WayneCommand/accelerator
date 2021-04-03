package ltd.inmind.accelerator.security.repository;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.security.Jwt;
import ltd.inmind.accelerator.security.context.JwtTokenSecurityContextAdapter;
import ltd.inmind.accelerator.security.context.TokenSecurityContextAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static ltd.inmind.accelerator.constants.SecurityConst.AUTHENTICATION_HEADER;
import static ltd.inmind.accelerator.constants.SecurityConst.TOKEN_ATTR_NAME;



/**
 * Repository need delegating. and directly provide to spring security.
 * @author shenlanAZ
 */
@Slf4j
public class DelegatingTokenServerSecurityContextRepository implements ServerSecurityContextRepository {

    private final TokenSecurityContextAdapter<Jwt> securityContextAdapter = new JwtTokenSecurityContextAdapter();

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        // 如果context为null 说明用户要做登出操作
        if (context == null){
            securityContextAdapter.remove(exchange.getAttribute(TOKEN_ATTR_NAME));
            return Mono.empty();
        }

        // FIXME Reactive?
        Jwt block = securityContextAdapter.save(context)
                .block();

        exchange.getAttributes()
                .put(TOKEN_ATTR_NAME, block);

        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        //从header里取出token
        String token = exchange.getRequest()
                .getHeaders()
                .getFirst(AUTHENTICATION_HEADER);

        if (StringUtils.isEmpty(token))
            return Mono.empty();

        // 在每个请求里面都把token放到 attr里 以便日后使用
        exchange.getAttributes()
                .put(TOKEN_ATTR_NAME, token);

        return securityContextAdapter.load(Jwt.from(token));
    }
}
