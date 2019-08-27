package ltd.inmind.accelerator.constants;

import ltd.inmind.accelerator.utils.KVPlusMap;

public class Oauth2Const {
    public static final long EXPIRED_TIME = 1000 * 60 * 10; //10 min

    public static KVPlusMap<String, String> OAUTH2_MEM_CACHE = new KVPlusMap<>();

}
