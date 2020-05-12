package ltd.inmind.accelerator.service.impl;

import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.mapper.UserProfileMapper;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.service.IUserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ltd.inmind.accelerator.constants.ExceptionConst.SYSTEM_BUG;

@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Autowired
    private UserProfileMapper userProfileMapper;


    @Override
    public UserProfile getByUId(Long uId) {

        return userProfileMapper.selectById(uId);
    }

    @Override
    public void register(UserProfile userProfile) {
        Long uId = userProfile.getUId();

        if (uId == null || uId == 0)
            throw new AcceleratorException(SYSTEM_BUG);

        userProfileMapper.insert(userProfile);
    }
}
