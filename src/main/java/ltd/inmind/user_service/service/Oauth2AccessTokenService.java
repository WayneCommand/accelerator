package ltd.inmind.user_service.service;

public interface Oauth2AccessTokenService {

    void addAccessTokenRecord(Integer clientPrimaryId, String username, String accessToken);

}
