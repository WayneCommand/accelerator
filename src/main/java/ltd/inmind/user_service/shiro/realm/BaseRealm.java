package ltd.inmind.user_service.shiro.realm;


import ltd.inmind.user_service.model.User;
import ltd.inmind.user_service.service.UserService;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 提供公用的方法和通用的授权
 */
@Component
public abstract class BaseRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }

    protected User getUser(String principal){
        //从数据库里查找用户
        User user = userService.getUserByUsername(principal);
        if (user == null)
            throw new UnknownAccountException("User not found error");

        return user;
    }
}
