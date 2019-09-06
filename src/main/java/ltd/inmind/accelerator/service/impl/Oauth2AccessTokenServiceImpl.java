package ltd.inmind.accelerator.service.impl;

import ltd.inmind.accelerator.mapper.Oauth2AccessTokenMapper;
import ltd.inmind.accelerator.model.Oauth2AccessToken;
import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.service.Oauth2AccessTokenService;
import ltd.inmind.accelerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Oauth2AccessTokenServiceImpl implements Oauth2AccessTokenService {

    @Autowired
    private Oauth2AccessTokenMapper oauth2AccessTokenMapper;

    @Autowired
    private UserService userService;

    @Override
    public void addAccessTokenRecord(Integer clientPrimaryId, String username, String accessToken) {

        Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken();

        User userByUsername = userService.getUserByUsername(username);

        oauth2AccessToken.setuId(userByUsername.getUId());

        oauth2AccessToken.setAccessToken(accessToken);

        oauth2AccessToken.setClientPrimaryId(clientPrimaryId);
        oauth2AccessToken.setCreateTime(new Date());

        oauth2AccessTokenMapper.insert(oauth2AccessToken);
    }
}
