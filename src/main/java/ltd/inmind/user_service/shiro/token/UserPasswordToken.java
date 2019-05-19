package ltd.inmind.user_service.shiro.token;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UserPasswordToken extends UsernamePasswordToken {

    private String secret;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
