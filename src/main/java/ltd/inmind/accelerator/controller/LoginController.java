package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.model.dto.ResponseResult;
import ltd.inmind.accelerator.model.vo.LoginLookupResp;
import ltd.inmind.accelerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/lookup")
    public LoginLookupResp lookup(User user) {
        User byUsername = userService.getUserByUsername(user.getUsername());

        if (byUsername == null) {
            return LoginLookupResp.notExist("我们未找到您的账户, 请检查您的账户名。");
        }

        return LoginLookupResp.exist();
    }
}
