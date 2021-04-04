package ltd.inmind.accelerator.security.context;

import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

/**
 * Adapter for actual operation repository
 * @param <T> token
 * @author shenlanAZ
 * @since 1.0
 */
public interface TokenSecurityContextAdapter<T> {

    /**
     * 保存 context 并签发 token
     * @param securityContext 已认证的 context 不能为 null
     * @return token
     */
    T save(SecurityContext securityContext);

    /**
     * 根据 token 从缓存中取出 context
     * @return
     */
    SecurityContext load(T token);

    /**
     * 移除该 token 的 context
     */
    void remove(T token);

}
