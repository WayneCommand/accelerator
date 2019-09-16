package ltd.inmind.accelerator;

import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.service.UserService;
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
        user.setUsername("shenlan");
        user.setPassword("123456");
        //user.setEmail("123@321.com");

        userService.signUp(user);

    }


}
