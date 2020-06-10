package ltd.inmind.accelerator.authentication;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.utils.JwtUtil;
import ltd.inmind.accelerator.utils.KVPlusMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtTokenServerSecurityContextRepository implements ServerSecurityContextRepository {

    private KVPlusMap<String, SecurityContext> cache = new KVPlusMap<>();

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        //从context里取出authentication信息
        Authentication authentication = context.getAuthentication();

        String name = authentication.getName();

        //最简版本
        //生成jwt token
        String token = JwtUtil.sign(name, "123456");

        //缓存这个context
        cache.put(token, context);

        //把token设置到attr
        exchange.getAttributes().put("token", token);

        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        //从header里取出token
        String token = exchange.getRequest()
                .getHeaders()
                .getFirst("X-AUTH-TOKEN");

        if (StringUtils.isEmpty(token))
            return Mono.empty();

        //先进行校验
        boolean verify = JwtUtil.verify(token, getUsername(token), "123456");

        //如果通过 把context从缓存里拿出来
        if (verify){
            return Mono.justOrEmpty(cache.get(token));
        }

        return Mono.empty();
    }

    private String getUsername(String token) {
        return JwtUtil.getClaim(token, JwtUtil.JWT_USERNAME);
    }
}
