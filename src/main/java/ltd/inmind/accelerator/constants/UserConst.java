package ltd.inmind.accelerator.constants;

import ltd.inmind.accelerator.utils.KVPlusMap;

public class UserConst {
    /**
     * 用户密码签名算法
     */
    public static final String USER_PASSWORD_ALGORITHM = "SHA-256";


    public static final int USER_PASSWORD_HASH_ITERATIONS = 256;

    public static KVPlusMap<String, String> USER_JWT_REFRESH_CODE_MEM_CACHE = new KVPlusMap<>();

    public static final int USER_JWT_REFRESH_CODE_EXPIRED_TIME = 1000 * 60 * 60;





}
