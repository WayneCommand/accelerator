package ltd.inmind.user_service.controller.intf;

import ltd.inmind.user_service.model.intf.User;
import ltd.inmind.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/oauth/2/api")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/userinfo")
    public User userinfo(HttpServletResponse response) {
        User user = new User();

        user.setuId(1);
        user.setEmail("shenlanluck@gmail.com");
        user.setUsername("shenlan");

        //response.setHeader("Cache-Control", "no-store");
        //response.setHeader("Pragma", "no-cache");

        //TODO real user info
        return user;

    }


}
