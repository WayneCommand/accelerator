package ltd.inmind.accelerator.model.vo;

import lombok.Data;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

@Data
public class LoginResp {

    private String state;

    private String msg;


    public static LoginResp success() {
        LoginResp loginResp = new LoginResp();

        loginResp.setState("success");

        return loginResp;
    }

    public static LoginResp failed(AuthenticationException exception) {
        LoginResp loginResp = new LoginResp();


        if (exception instanceof BadCredentialsException){
            loginResp.setMsg("账号或密码错误!");
        }

        return loginResp;
    }
}
