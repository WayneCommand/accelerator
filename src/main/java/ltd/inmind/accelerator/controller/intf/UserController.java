package ltd.inmind.accelerator.controller.intf;

import ltd.inmind.accelerator.model.intf.User;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/2/api")
public class UserController {

    @Autowired
    private IUserService userService;


    @GetMapping("/userinfo")
    public User userinfo(Authentication authentication) {
        UserProfile userProfile = userService.getProfileByAccount(authentication.getName());

        User user = new User();

        user.setUId(userProfile.getUId());
        user.setUsername(userProfile.getNickname());

        return user;
    }


}
