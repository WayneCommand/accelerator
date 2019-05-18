package ltd.inmind.user_service.shiro.token;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UserPasswordToken extends UsernamePasswordToken {

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
