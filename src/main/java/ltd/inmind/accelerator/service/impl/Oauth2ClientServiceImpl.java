package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ltd.inmind.accelerator.constants.Oauth2Const;
import ltd.inmind.accelerator.mapper.Oauth2ClientMapper;
import ltd.inmind.accelerator.model.oauth2.Oauth2Client;
import ltd.inmind.accelerator.service.Oauth2AccessTokenService;
import ltd.inmind.accelerator.service.Oauth2ClientService;
import ltd.inmind.accelerator.utils.UUIDUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ltd.inmind.accelerator.constants.Oauth2Const.EXPIRED_TIME;
import static ltd.inmind.accelerator.constants.Oauth2Const.OAUTH2_MEM_CACHE;

@Service
public class Oauth2ClientServiceImpl implements Oauth2ClientService {

    @Autowired
    private Oauth2ClientMapper oauth2ClientMapper;

    @Autowired
    private Oauth2AccessTokenService accessTokenService;

   @Override
    public boolean newClient(Oauth2Client oauth2Client) {
        try {

            //生成clientID clientSecret
            String clientId = UUIDUtil.generateShortUuid();
            String secret = clientId + "_" + oauth2Client.getCallbackUrl() + "_" + System.currentTimeMillis();
            String clientSecret = DigestUtils.sha256Hex(secret);

            oauth2Client.setClientId(clientId);
            oauth2Client.setClientSecret(clientSecret);

            oauth2ClientMapper.insert(oauth2Client);
        } catch (Exception e) {

            //LOG
            return false;
        }

        return true;
    }

    @Override
    public boolean verifyClientId(String clientId) {
        try {

            Oauth2Client oauth2Client = oauth2ClientMapper.selectOne(Wrappers.<Oauth2Client>lambdaQuery()
            .eq(Oauth2Client::getClientId,clientId));
            return oauth2Client != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 授权码的kv结构：
     * clientId_code = username
     */
    @Override
    public String grantCode(String client_id, String username) {
        try {
            String code = UUIDUtil.generateShortUuid();
            Oauth2Const.OAUTH2_MEM_CACHE.put(client_id + "_" + code, username, EXPIRED_TIME);
            return code;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String accessToken(String client_id, String client_secret, String code) {

        Oauth2Client oauth2Client = oauth2ClientMapper.selectOne(
                Wrappers.<Oauth2Client>lambdaQuery()
                        .eq(Oauth2Client::getClientId, client_id));

        if (oauth2Client == null)
            throw new RuntimeException("client id error");

        if (!oauth2Client.getClientSecret().equals(client_secret))
            throw new RuntimeException("secret invalid");
        String codeKey = client_id + "_" + code;
        String username = OAUTH2_MEM_CACHE.get(codeKey);

        if (username == null)
            throw new RuntimeException("code expired");

        String token = client_id + "_" + username + "_" + UUIDUtil.generateShortUuid();

        String accessToken = DigestUtils.sha256Hex(token);

        OAUTH2_MEM_CACHE.remove(codeKey);

        accessTokenService.addAccessTokenRecord(oauth2Client.getId(), username, accessToken);

        return accessToken;
    }

}
