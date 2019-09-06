package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.constants.LoginConst.SignUpStatusEnum;
import ltd.inmind.accelerator.mapper.UserMapper;
import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    private UserMapper userMapper;


    @Override
    public SignUpStatusEnum signUp(User user) {

        try {

            return doSignUp(user);
        } catch (Exception e) {

            log.error("user service sign up error", e);
            return SignUpStatusEnum.FAILED;
        }
    }

    @Override
    public User getUserByUsername(String username) {

        return userMapper.selectOne(Wrappers
                .<User>lambdaQuery()
                .eq(User::getUsername, username));
    }

    private SignUpStatusEnum doSignUp(User user) {

        User byUsername = getUserByUsername(user.getUsername());
        if (byUsername != null)
            return SignUpStatusEnum.USER_NAME_ALREADY_EXIST;

        String password = passwordEncoder.encode(user.getPassword());

        user.setPassword(password);

        user.setCreateTime(new Date());

        userMapper.insert(user);

        return SignUpStatusEnum.SUCCESS;
    }



}
