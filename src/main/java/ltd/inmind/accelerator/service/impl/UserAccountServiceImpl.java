package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.mapper.UserAccountMapper;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.service.IUserAccountService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import static ltd.inmind.accelerator.constants.ExceptionConst.USER_ALREADY_EXIST;
import static ltd.inmind.accelerator.constants.ExceptionConst.USER_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements IUserAccountService {

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final UserAccountMapper userAccountMapper;


    @Override
    public UserAccount getByAccount(String account) {

        return userAccountMapper.selectOne(Wrappers.<UserAccount>lambdaQuery()
                .eq(UserAccount::getAccount, account));
    }

    @Override
    public void register(UserAccount userAccount) {

        UserAccount _userAccount = getByAccount(userAccount.getAccount());
        if (_userAccount != null)
            throw new AcceleratorException(USER_ALREADY_EXIST);

        String password = passwordEncoder.encode(userAccount.getPassword());
        userAccount.setPassword(password);

        userAccount.setPhoneToLogin(0L);
        userAccount.setTwoStepVerify(0L);

        userAccountMapper.insert(userAccount);
    }

    @Override
    public Boolean verifyPassword(String account, String password) {
        UserAccount userAccount = getByAccount(account);
        if (userAccount == null)
            return false;

        return passwordEncoder.matches(password, userAccount.getPassword());
    }

    @Override
    public void changePassword(String account, String password) {
        UserAccount _userAccount = getByAccount(account);

        if (_userAccount == null)
            throw new AcceleratorException(USER_NOT_EXIST);

        String encodePassword = passwordEncoder.encode(password);

        UserAccount userAccount = new UserAccount();
        userAccount.setUId(_userAccount.getUId());
        userAccount.setPassword(encodePassword);
        userAccount.setPasswordModifyTime(new Date());

        userAccountMapper.update(userAccount, Wrappers.<UserAccount>lambdaQuery()
                .eq(UserAccount::getUId, _userAccount.getUId()));
    }

    @Override
    public void updateUserAccount(UserAccount userAccount) {
        userAccountMapper.update(userAccount, Wrappers.<UserAccount>lambdaQuery()
                .eq(UserAccount::getAccount, userAccount.getAccount()));
    }
}
