package ltd.inmind.accelerator.service.impl;

import ltd.inmind.accelerator.mapper.UserProfileMapper;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.service.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Autowired
    private UserProfileMapper userProfileMapper;


    @Override
    public UserProfile getByUId(Long uId) {

        return userProfileMapper.selectById(uId);
    }
}
