package ltd.inmind.accelerator.service;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface IJwtTokenSecurityContext {

    /**
     * 保存context并签发token
     * @param securityContext 已认证的context 不能为null
     * @return token
     */
    String save(SecurityContext securityContext);

    /**
     * 根据token从缓存中取出context
     * @param token
     * @return
     */
    SecurityContext load(String token);

    /**
     * 移除该token的 context
     * @param token
     */
    void remove(String token);

    /**
     * 用userDetail 创建token 并保存SecurityContext
     * @return token
     */
    String create(Mono<UserDetails> userDetailsMono);

}
