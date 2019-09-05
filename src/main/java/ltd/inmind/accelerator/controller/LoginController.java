package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.constants.LoginConst.SignUpStatusEnum;
import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ltd.inmind.accelerator.constants.LoginConst.LoginStatusEnum.FAILED;

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
        //TODO spring security

        Map<String, String> result = new HashMap<>();

        if (StringUtils.isAnyBlank(username, password)) {
            result.put("status", FAILED.getValue());
            result.put("message", "参数格式错误.");
            return result;
        }


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
