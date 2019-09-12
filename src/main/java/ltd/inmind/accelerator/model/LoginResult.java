package ltd.inmind.accelerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResult {
    private boolean isSucceeded;
    private String message;

    public static LoginResult getResult(LoginStatusEnum statusEnum) {
        switch (statusEnum) {
            case SUCCESS:
                return new LoginResult(true, "登陆成功");

            case TOKEN_EXPIRED:
                return new LoginResult(false, "凭证已过期");

            case USER_NOT_FOUND:
                return new LoginResult(false, "用户未找到");

            case PASSWORD_NOT_MATCH:
                return new LoginResult(false, "密码错误");

            default:
                throw new RuntimeException("status not match");
        }

    }

    public enum LoginStatusEnum{
        SUCCESS,
        USER_NOT_FOUND,
        PASSWORD_NOT_MATCH,
        TOKEN_EXPIRED,
    }
}
