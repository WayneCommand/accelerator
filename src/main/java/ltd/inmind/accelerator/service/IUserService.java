package ltd.inmind.accelerator.service;

import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;

public interface IUserService {

    /**
     * 对外提供的注册接口
     * 同时注册account和profile
     */
    void signUp(String account, String password);

    UserProfile getProfileByAccount(String account);

    UserAccount getAccountByAccount(String account);

}
