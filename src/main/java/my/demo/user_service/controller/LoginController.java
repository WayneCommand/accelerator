package my.demo.user_service.controller;

import my.demo.user_service.model.User;
import my.demo.user_service.shiro.JwtToken;
import my.demo.user_service.utils.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping("/byPassword")
    public String byPassword(String username, String password) {
        Subject subject = SecurityUtils.getSubject();

        String token = JwtUtil.sign(username, password);

        JwtToken jwtToken = new JwtToken(token);

        subject.login(jwtToken);

        return token;

    }

    /**
     * 注册接口
     * @param user 用户名 密码
     * @return
     */
    public String sign(User user) {



        return null;
    }

}
