package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
