package ltd.inmind.user_service.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/test")
    public String test(){

        Object principal = SecurityUtils.getSubject().getPrincipal();

        System.out.println(principal);

        return String.format("hello, %s.", principal);
    }
}
