package ltd.inmind.user_service.controller.intf;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/api")
public class UserController {


    @GetMapping("/userinfo")
    public void userinfo() {
        //TODO coding

    }


}
