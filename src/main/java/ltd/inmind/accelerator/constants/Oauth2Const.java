package ltd.inmind.accelerator.constants;

import ltd.inmind.accelerator.utils.KVPlusMap;

public interface Oauth2Const {
    long EXPIRED_TIME = 1000 * 60 * 10; //10 min

    KVPlusMap<String, String> OAUTH2_MEM_CACHE = new KVPlusMap<>();

}
