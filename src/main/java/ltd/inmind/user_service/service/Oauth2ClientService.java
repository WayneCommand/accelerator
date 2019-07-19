package ltd.inmind.user_service.service;

import ltd.inmind.user_service.model.Oauth2Client;

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
     * @return
     */
    String grantCode(String username);

    String accessToken(String client_id, String client_secret, String code);

}
