package ltd.inmind.accelerator.service;

import ltd.inmind.accelerator.model.oauth2.Oauth2Client;

import java.util.List;

public interface Oauth2ClientService {

    /**
     * 创建一个客户端
     * @param oauth2Client clientName,callbackUrl
     * @return
     */
    boolean newClient(Oauth2Client oauth2Client);

    /**
     * 根据 验证client id的有效性
     *
     * @param clientId
     * @return
     */
    boolean verifyClientId(String clientId);

    /**
     * 发放授权码
     *
     * @return
     */
    String grantCode(String client_id, String username);

    String accessToken(String client_id, String client_secret, String code);

    List<Oauth2Client> list();
}
