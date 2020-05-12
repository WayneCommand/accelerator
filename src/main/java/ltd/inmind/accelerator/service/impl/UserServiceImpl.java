package ltd.inmind.accelerator.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.service.IUserAccountService;
import ltd.inmind.accelerator.service.IUserProfileService;
import ltd.inmind.accelerator.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IUserProfileService userProfileService;

    @Override
    @Transactional
    public void signUp(String account, String password) {
        UserAccount userAccount = new UserAccount();
        userAccount.setAccount(account);
        userAccount.setPassword(password);

        userAccountService.register(userAccount);

        UserProfile userProfile = new UserProfile();
        userProfile.setUId(userAccount.getUId());

        userProfileService.register(userProfile);
    }

    @Override
    public UserProfile getProfileByAccount(String account) {
        UserAccount userAccount = userAccountService.getByAccount(account);
        if (userAccount == null)
            return null;

        return userProfileService.getByUId(userAccount.getUId());
    }

    @Override
    public UserAccount getAccountByAccount(String account) {
        return userAccountService.getByAccount(account);
    }
}
