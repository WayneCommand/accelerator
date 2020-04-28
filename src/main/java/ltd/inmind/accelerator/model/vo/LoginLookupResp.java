package ltd.inmind.accelerator.model.vo;

import lombok.Data;

@Data
public class LoginLookupResp {
    private String isExist;
    private String msg;


    public static LoginLookupResp exist(){
        LoginLookupResp lookupResp = new LoginLookupResp();

        lookupResp.setIsExist("true");

        return lookupResp;
    }

    public static LoginLookupResp notExist(String msg){
        LoginLookupResp lookupResp = new LoginLookupResp();

        lookupResp.setIsExist("false");
        lookupResp.setMsg(msg);

        return lookupResp;
    }

}
