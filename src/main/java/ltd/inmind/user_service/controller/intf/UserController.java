package ltd.inmind.user_service.controller.intf;

import ltd.inmind.user_service.model.intf.User;
import ltd.inmind.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/2/api")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/userinfo")
    public User userinfo() {
        User user = new User();

        user.setuId(1);
        user.setEmail("shenlanluck@gmail.com");
        user.setUsername("shenlan");

        return user;

    }


}
