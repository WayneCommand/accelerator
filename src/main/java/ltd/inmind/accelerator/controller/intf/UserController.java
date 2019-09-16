package ltd.inmind.accelerator.controller.intf;

import ltd.inmind.accelerator.model.intf.User;
import ltd.inmind.accelerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ltd.inmind.accelerator.model.User userByUsername = userService.getUserByUsername(authentication.getName());

        user.setUsername(userByUsername.getUsername());

        user.setuId(userByUsername.getUId());

        user.setEmail(userByUsername.getEmail());
        return user;

    }


}
