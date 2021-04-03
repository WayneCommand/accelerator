package ltd.inmind.accelerator.security.context;

import ltd.inmind.accelerator.constants.SecurityConst;
import ltd.inmind.accelerator.security.Jwt;
import ltd.inmind.accelerator.utils.UUIDUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtTokenSecurityContextAdapter implements TokenSecurityContextAdapter<Jwt> {

    private final Map<Jwt, SecurityContext> cache = new ConcurrentHashMap<>();
    private final Map<Jwt, String> tokenSecrets = new HashMap<>();

    @Override
    public Mono<Jwt> save(SecurityContext securityContext) {
        //从context里取出authentication信息
        Authentication authentication = securityContext.getAuthentication();
        String account = authentication.getName();

        //最简版本
        //生成jwt token
        String secret = UUIDUtil.generateShortUuid();
        return Mono.just(Jwt.create(Collections.singletonMap(SecurityConst.TOKEN_USER_KEY, account), secret))
                .doOnNext(jwt -> {
                    //缓存这个context
                    cache.put(jwt, securityContext);
                    tokenSecrets.put(jwt, secret);
                });
    }

    @Override
    public Mono<SecurityContext> load(Jwt _token) {
        //先进行校验
        return Mono.just(_token.changeSecret(tokenSecrets.get(_token)))
                .flatMap(token -> {
                    if (token.isValid())
                        return Mono.just(cache.get(token));
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> remove(Jwt _token) {
        cache.remove(_token);
        tokenSecrets.remove(_token);
        return Mono.empty();
    }

    public Mono<Jwt> create(UserDetails userDetails) {
        return createAuthentication(userDetails)
                .map(SecurityContextImpl::new)
                .flatMap(this::save);
    }

    private Mono<Authentication> createAuthentication(UserDetails userDetails){
        return Mono.just(new UsernamePasswordAuthenticationToken(userDetails,
                userDetails.getPassword(), userDetails.getAuthorities()));
    }

}
