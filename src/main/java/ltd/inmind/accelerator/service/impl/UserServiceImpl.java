package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ltd.inmind.accelerator.constants.LoginConst.SignUpStatusEnum;
import ltd.inmind.accelerator.mapper.UserMapper;
import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.service.UserService;
import ltd.inmind.accelerator.utils.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;


    @Override
    public SignUpStatusEnum signUp(User user) {

        try {

            return doSignUp(user);
        } catch (Exception e) {

            LOG.error("user service sign up error", e);
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

        //TODO spring security

        User byUsername = getUserByUsername(user.getUsername());
        if (byUsername != null)
            return SignUpStatusEnum.USER_NAME_ALREADY_EXIST;

        String password = user.getPassword();

        String salt = UUIDUtil.generateShortUuid();


        user.setCreateTime(new Date());

        user.setPassword("");

        user.setSalt(salt);

        userMapper.insert(user);

        return SignUpStatusEnum.SUCCESS;
    }



}
