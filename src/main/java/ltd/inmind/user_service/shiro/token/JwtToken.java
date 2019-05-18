package ltd.inmind.user_service.shiro.token;

import ltd.inmind.user_service.utils.JwtUtil;
import org.apache.shiro.authc.AuthenticationToken;

import static ltd.inmind.user_service.utils.JwtUtil.JWT_USERNAME;

public class JwtToken implements AuthenticationToken {
    /**
     * jwt生成的token
     * principal包含在token里
     */
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return JwtUtil.getClaim(token,JWT_USERNAME);
    }

    @Override
    public Object getCredentials() {
        return new byte[0];
    }

    public String getToken() {
        return token;
    }
}
