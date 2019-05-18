package ltd.inmind.user_service.shiro.realm;

import ltd.inmind.user_service.model.User;
import ltd.inmind.user_service.shiro.token.JwtToken;
import ltd.inmind.user_service.utils.JwtUtil;
import org.apache.shiro.authc.*;
import org.springframework.stereotype.Component;

/**
 * 根据Jwt Token提供认证
 */
@Component
public class JwtRealm extends BaseRealm {

    private static final String REALM_NAME = "jwtRealm";

    /**
     * 认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;

        User user = getUser((String) jwtToken.getPrincipal());

        if (!JwtUtil.verify(jwtToken.getToken(), (String) jwtToken.getPrincipal(), user.getPassword()))
            throw new UnknownAccountException("password error");

        //这个地方再去对比就没有什么意义了
        return new SimpleAuthenticationInfo(user.getUsername(), new byte[0], REALM_NAME);
    }



    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }



}
