package ltd.inmind.accelerator.constants;

public class LoginConst {

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    public enum SignUpStatusEnum{
        SUCCESS("S"),
        FAILED("F"),
        USER_NAME_ALREADY_EXIST("UNAE");
        private String value;

        SignUpStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
