package ltd.inmind.accelerator.constants;

import ltd.inmind.accelerator.utils.KVPlusMap;

public class UserConst {

    public static KVPlusMap<String, String> USER_JWT_REFRESH_CODE_MEM_CACHE = new KVPlusMap<>();

    public static final int USER_JWT_REFRESH_CODE_EXPIRED_TIME = 1000 * 60 * 60;

}
