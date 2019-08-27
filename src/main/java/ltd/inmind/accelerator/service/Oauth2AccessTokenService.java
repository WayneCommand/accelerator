package ltd.inmind.accelerator.service;

public interface Oauth2AccessTokenService {

    void addAccessTokenRecord(Integer clientPrimaryId, String username, String accessToken);

}
