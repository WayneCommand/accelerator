package ltd.inmind.user_service.service;

import ltd.inmind.user_service.model.Oauth2Client;

public interface Oauth2ClientService {

    /**
     * 创建一个客户端
     * @param oauth2Client clientName,callbackUrl
     * @return clientID clientSecret
     */
    boolean newClient(Oauth2Client oauth2Client);

}
