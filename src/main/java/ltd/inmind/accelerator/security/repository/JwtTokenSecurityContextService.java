package ltd.inmind.accelerator.security.repository;

import ltd.inmind.accelerator.constants.SecurityConst;
import ltd.inmind.accelerator.security.Jwt;
import ltd.inmind.accelerator.utils.UUIDUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtTokenSecurityContextService {

    private final Map<Jwt, SecurityContext> cache = new ConcurrentHashMap<>();
    private final Map<Jwt, String> tokenSecrets = new HashMap<>();

    public Jwt save(SecurityContext securityContext) {
        //从context里取出authentication信息
        Authentication authentication = securityContext.getAuthentication();
        String account = authentication.getName();

        //最简版本
        //生成jwt token
        String secret = UUIDUtil.generateShortUuid();

        Jwt jwt = Jwt.create(Collections.singletonMap(SecurityConst.TOKEN_USER_KEY, account), secret);

        //缓存这个context
        cache.put(jwt, securityContext);
        tokenSecrets.put(jwt, secret);

        return jwt;
    }

    public SecurityContext load(Jwt _token) {

        //先进行校验
        Jwt jwt = _token.changeSecret(tokenSecrets.get(_token));

        if (jwt.isValid())
            return cache.get(jwt);

        return null;
    }

    public void remove(Jwt _token) {

        cache.remove(_token);
        tokenSecrets.remove(_token);
    }

}
