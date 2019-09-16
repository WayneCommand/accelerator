package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.model.dto.ResponseResult;
import ltd.inmind.accelerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 注册接口
     *
     * @param user 用户名 密码
     * @return
     */
    @PostMapping("/signUp")
    public ResponseResult signUp(User user) {

        try {

            userService.signUp(user);
            return ResponseResult.success();

        } catch (AcceleratorException e) {
            return ResponseResult.failure(e);
        }
    }

    /**
     * jwt token 续期
     * @param token 将要过期的token
     * @return
     */
    @PostMapping("/refreshJwtToken")
    public void refreshJwtToken(String token) {

        //TODO 续期逻辑
    }

}
