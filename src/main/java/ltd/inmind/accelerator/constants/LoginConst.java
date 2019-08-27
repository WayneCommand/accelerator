package ltd.inmind.accelerator.constants;

public class LoginConst {

    public enum LoginStatusEnum{
        SUCCESS("S"),
        FAILED("F");


        private String value;

        LoginStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

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
