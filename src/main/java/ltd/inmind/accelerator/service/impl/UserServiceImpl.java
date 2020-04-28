package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.constants.PlatformEnum;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.mapper.UserMapper;
import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;


    @Override
    public void signUp(User user) {
        User byUsername = getUserByUsername(user.getUsername());
        if (byUsername != null)
            throw new AcceleratorException(PlatformEnum.USER_NAME_ALREADY_EXIST);

        String password = passwordEncoder.encode(user.getPassword());

        user.setPassword(password);

        user.setCreateTime(new Date());

        userMapper.insert(user);
    }

    @Override
    public User getUserByUsername(String username) {

        return userMapper.selectOne(Wrappers
                .<User>lambdaQuery()
                .eq(User::getUsername, username));
    }

}
