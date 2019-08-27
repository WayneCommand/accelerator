package ltd.inmind.accelerator.service;


import ltd.inmind.accelerator.constants.LoginConst.SignUpStatusEnum;
import ltd.inmind.accelerator.model.User;

public interface UserService {

    SignUpStatusEnum signUp(User user);

    User getUserByUsername(String username);

}
