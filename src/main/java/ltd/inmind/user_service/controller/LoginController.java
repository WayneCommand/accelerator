package ltd.inmind.user_service.controller;

import ltd.inmind.user_service.constants.LoginConst.SignUpStatusEnum;
import ltd.inmind.user_service.model.User;
import ltd.inmind.user_service.service.UserService;
import ltd.inmind.user_service.shiro.token.UserPasswordToken;
import ltd.inmind.user_service.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ltd.inmind.user_service.constants.LoginConst.LoginStatusEnum.FAILED;
import static ltd.inmind.user_service.constants.LoginConst.LoginStatusEnum.SUCCESS;


@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/byPassword")
    public Map<String, String> byPassword(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        Map<String, String> result = new HashMap<>();

        if (StringUtils.isAnyBlank(username, password)) {
            result.put("status", FAILED.getValue());
            result.put("message", "参数格式错误.");
            return result;
        }

        UserPasswordToken token = new UserPasswordToken();
        token.setUsername(username);
        token.setPassword(password.toCharArray());

        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
            result.put("status", FAILED.getValue());
            result.put("message", "未知的账户");
        } catch (LockedAccountException e) {
            result.put("status", FAILED.getValue());
            result.put("message", "账户已锁定");
        } catch (DisabledAccountException e) {
            result.put("status", FAILED.getValue());
            result.put("message", "账户已禁用");
        } catch (IncorrectCredentialsException e) {
            result.put("status", FAILED.getValue());
            result.put("message", "密码错误");
        } catch (ShiroException e){
            result.put("status", FAILED.getValue());
            result.put("message", "系统错误");
        }

        if (FAILED.getValue().equals(result.get("status"))) {
            return result;
        }

        String jwt = "Bearer " + JwtUtil.sign(username, token.getSecret());

        result.put("status", SUCCESS.getValue());
        result.put("token", jwt);

        return result;
    }

    /**
     * 注册接口
     *
     * @param user 用户名 密码
     * @return
     */
    @PostMapping("/signUp")
    public Map<String, String> signUp(User user) {

        SignUpStatusEnum signUpStatusEnum = userService.signUp(user);
        Map<String, String> result = new HashMap<>();

        switch (signUpStatusEnum) {
            case USER_NAME_ALREADY_EXIST:{
                result.put("status", SignUpStatusEnum.FAILED.getValue());
                result.put("message", "用户名已存在");
                break;
            }

            case FAILED:{
                result.put("status", SignUpStatusEnum.FAILED.getValue());
                break;
            }
            case SUCCESS:{
                result.put("status", SignUpStatusEnum.SUCCESS.getValue());
                break;
            }
        }

        return result;
    }

    /**
     * jwt token 续期
     * @param token 将要过期的token
     * @return
     */
    @PostMapping("/refreshJwtToken")
    public Map<String, String> refreshJwtToken(String token) {

        //TODO 续期逻辑
        return Collections.emptyMap();
    }

}
