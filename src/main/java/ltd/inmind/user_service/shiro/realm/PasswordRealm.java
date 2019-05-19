package ltd.inmind.user_service.shiro.realm;

import ltd.inmind.user_service.model.User;
import ltd.inmind.user_service.shiro.token.UserPasswordToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 根据主体和密码提供认证
 */
@Component
public class PasswordRealm extends BaseRealm {

    private static final String REALM_NAME = "passwordRealm";

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UserPasswordToken passwordToken = (UserPasswordToken) token;

        User user = getUser((String) passwordToken.getPrincipal());

        passwordToken.setSecret(user.getPassword());

        return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), ByteSource.Util.bytes(user.getSalt()), REALM_NAME);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UserPasswordToken;
    }

    @Autowired
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        super.setCredentialsMatcher(credentialsMatcher);
    }
}
