package ltd.inmind.accelerator.model.oauth2;

import lombok.Data;

@Data
public class AccessTokenResult {
    private String access_token;

    private String scope;

    private String token_type = "bearer";

    private String message;

    private String expires_in;

    private String refresh_token;
}

