package ltd.inmind.accelerator.service.impl;

import ltd.inmind.accelerator.service.IJwtTokenSecurityContext;
import ltd.inmind.accelerator.utils.JwtUtil;
import ltd.inmind.accelerator.utils.UUIDUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtTokenSecurityContext implements IJwtTokenSecurityContext {

    private static Map<String, SecurityContext> cache = new ConcurrentHashMap<>();
    private static Map<String, String> tokenSecrets = new HashMap<>();

    @Override
    public String save(SecurityContext securityContext) {
        //从context里取出authentication信息
        Authentication authentication = securityContext.getAuthentication();

        String account = authentication.getName();

        //最简版本
        //生成jwt token
        String secret = UUIDUtil.generateShortUuid();
        String token = JwtUtil.sign(account, secret);

        //缓存这个context
        cache.put(token, securityContext);
        tokenSecrets.put(token, secret);

        return token;
    }

    @Override
    public SecurityContext load(String token) {

        if (!tokenSecrets.containsKey(token))
            return null;

        //先进行校验
        boolean verify = JwtUtil.verify(token, getUsername(token), tokenSecrets.get(token));

        //如果通过 把context从缓存里拿出来
        if (verify){
            return cache.get(token);
        }

        return null;
    }

    @Override
    public void remove(String token) {
        cache.remove(token);
        tokenSecrets.remove(token);
    }

    @Override
    public Mono<String> create(Mono<UserDetails> userDetailsMono) {
        return createAuthentication(userDetailsMono)
                .map(SecurityContextImpl::new)
                .map(this::save);
    }

    private String getUsername(String token) {
        return JwtUtil.getClaim(token, JwtUtil.JWT_USERNAME);
    }

    private Mono<Authentication> createAuthentication(Mono<UserDetails> userDetailsMono){

        return userDetailsMono
                .map(ud -> new UsernamePasswordAuthenticationToken(ud, ud.getPassword(), ud.getAuthorities()));
    }
}
