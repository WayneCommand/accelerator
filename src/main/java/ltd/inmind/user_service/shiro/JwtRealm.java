package ltd.inmind.user_service.shiro;

import ltd.inmind.user_service.utils.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

@Component
public class JwtRealm extends AuthorizingRealm {


    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = JwtUtil.getUsername(principals.toString());

        return new SimpleAuthorizationInfo();
    }

    /**
     * 认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String credentials = (String) token.getCredentials();

        String username = JwtUtil.getUsername(credentials);

        if (username == null)
            throw new AuthenticationException("token error");

        if (username.isEmpty())
            throw new AuthenticationException("user not found");

        final String PASSWORD = "000000";

        if (!JwtUtil.verify(credentials,username,PASSWORD))
            throw new AuthenticationException("password error");

        return new SimpleAuthenticationInfo(credentials, credentials, "jwtRealm");
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
}
