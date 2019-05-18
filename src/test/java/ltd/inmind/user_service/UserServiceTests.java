package ltd.inmind.user_service;

import ltd.inmind.user_service.model.User;
import ltd.inmind.user_service.service.UserService;
import ltd.inmind.user_service.utils.UUIDUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {


    @Autowired
    private UserService userService;


    @Test
    public void testSignUp(){

        User user = new User();
        user.setUsername("test_" + UUIDUtil.generateShortUuid());
        user.setPassword("123456");
        //user.setEmail("123@321.com");

        System.out.println(userService.signUp(user));


    }


}
