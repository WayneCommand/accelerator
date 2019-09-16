package ltd.inmind.accelerator.service;

import ltd.inmind.accelerator.model.User;

public interface UserService {

    void signUp(User user);

    User getUserByUsername(String username);

}
