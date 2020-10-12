package ltd.inmind.accelerator.service.impl;

import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.mapper.Oauth2AccessTokenMapper;
import ltd.inmind.accelerator.model.oauth2.Oauth2AccessToken;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.service.IUserAccountService;
import ltd.inmind.accelerator.service.Oauth2AccessTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class Oauth2AccessTokenServiceImpl implements Oauth2AccessTokenService {

    private final Oauth2AccessTokenMapper oauth2AccessTokenMapper;

    private final IUserAccountService userAccountService;

    @Override
    public void addAccessTokenRecord(Integer clientPrimaryId, String username, String accessToken) {
        UserAccount byAccount = userAccountService.getByAccount(username);

        Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken();
        oauth2AccessToken.setUId(byAccount.getUId());
        oauth2AccessToken.setAccessToken(accessToken);
        oauth2AccessToken.setClientPrimaryId(clientPrimaryId);
        oauth2AccessToken.setCreateTime(new Date());

        oauth2AccessTokenMapper.insert(oauth2AccessToken);
    }
}
