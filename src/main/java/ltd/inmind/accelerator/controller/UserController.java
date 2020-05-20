package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.model.vo.MyHomePage;
import ltd.inmind.accelerator.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("my_info")
    public DataResponse myInfo(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();


        return new DataResponse()
                .success()
                .data("info", userService.getMyInfo(principal.getUsername()));
    }

    @GetMapping("my_safety")
    public DataResponse mySafety(Authentication authentication) {

        return new DataResponse()
                .success()
                .data("safety", userService.getMySafety(authentication.getName()));
    }

    @GetMapping("my_home_page")
    public DataResponse myHomePage(Authentication authentication) {

        MyHomePage myHomePage = userService.getMyHomePage(authentication.getName());

        return new DataResponse()
                .success()
                .data("homePage", myHomePage);
    }

    @PostMapping("update_profile")
    public DataResponse updateProfile(UserProfile profile, Authentication authentication) {

        userService.updateUserProfile(profile, authentication.getName());

        return new DataResponse().success();
    }

    @PostMapping("verify_password")
    public DataResponse verifyPassword(UserAccount account, Authentication authentication) {

        if (StringUtils.isBlank(account.getPassword()))
            return new DataResponse().failed();

        if (userService.verifyPassword(authentication.getName(),account.getPassword()))
            return new DataResponse().success();

        return new DataResponse().failed();
    }

    @PostMapping("change_password")
    public DataResponse changePassword(UserAccount account, Authentication authentication) {

        if (StringUtils.isBlank(account.getPassword()))
            return new DataResponse().failed();

        userService.changePassword(authentication.getName(), account.getPassword());

        return new DataResponse().success();
    }
}
