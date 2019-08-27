package ltd.inmind.accelerator.config;

import ltd.inmind.accelerator.constants.UserConst;
import ltd.inmind.accelerator.shiro.AonModularRealmAuthenticator;
import ltd.inmind.accelerator.shiro.JwtFilter;
import ltd.inmind.accelerator.shiro.realm.JwtRealm;
import ltd.inmind.accelerator.shiro.realm.PasswordRealm;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        shiroFilterFactoryBean.setFilters(filterMap());
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap());

        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");


        return shiroFilterFactoryBean;
    }

    public Map<String, Filter> filterMap() {
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("jwtFilter", new JwtFilter());
        return filterMap;
    }


    /**
     * 拦截器链
     * @return
     */
    public Map<String, String> filterChainDefinitionMap(){
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/login/**", "anon");
        filterChainDefinitionMap.put("/oauth/2/access_token", "anon");


        //过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        filterChainDefinitionMap.put("/**", "jwtFilter");

        return filterChainDefinitionMap;
    }

    @Bean("securityManager")
    public SecurityManager securityManager(JwtRealm jwtRealm, PasswordRealm passwordRealm, SubjectDAO subjectDAO) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        AonModularRealmAuthenticator aonModularRealmAuthenticator = new AonModularRealmAuthenticator();
        aonModularRealmAuthenticator.setRealms(Arrays.asList(jwtRealm, passwordRealm));

        securityManager.setSubjectDAO(subjectDAO);
        securityManager.setAuthenticator(aonModularRealmAuthenticator);

        return securityManager;
    }

    /**
     * 关闭shiro自带的session，详情见文档
     * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
     * @return
     */
    @Bean
    public SubjectDAO subjectDAO(){
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        return subjectDAO;
    }

    /**
     * 使用shiro的Hashed凭证匹配器 在Realm中利用setter进行注入
     * @return HashedCredentialsMatcher
     */
    @Bean("credentialsMatcher")
    public CredentialsMatcher credentialsMatcher(){
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashIterations(UserConst.USER_PASSWORD_HASH_ITERATIONS);
        credentialsMatcher.setHashAlgorithmName(UserConst.USER_PASSWORD_ALGORITHM);
        return credentialsMatcher;
    }
}
