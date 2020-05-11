package ltd.inmind.accelerator.service;

import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;

public interface IUserService {

    void signUp(String account, String password);

    UserProfile getProfileByAccount(String account);

    UserAccount getAccountByAccount(String account);

}
