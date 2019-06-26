package ltd.inmind.user_service.model;

public class AccessTokenResult {
    private String access_token;

    private String scope;

    private String token_type;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
    public void setTokenType(TOKEN_TYPE tokenType){
        setToken_type(tokenType.getValue());
    }

    public enum TOKEN_TYPE{
        BEARER("bearer");

        private String value;

        TOKEN_TYPE(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
