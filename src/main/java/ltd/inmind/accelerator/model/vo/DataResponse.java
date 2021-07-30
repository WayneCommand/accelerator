package ltd.inmind.accelerator.model.vo;

import org.springframework.ui.ModelMap;

import java.util.Map;

public class DataResponse extends ModelMap {

    public static final String STATE = "state";
    public static final String STATE_SUCCESS = "success";
    public static final String STATE_FAILED = "failed";


    public DataResponse success() {
        addAttribute(STATE, STATE_SUCCESS);
        return this;
    }


    /**
     * 这里的失败如无特殊情况 指的是业务失败
     * 比如说密码不匹配
     */
    public DataResponse failed() {
        addAttribute(STATE, STATE_FAILED);
        return this;
    }

    public DataResponse msg(String msg) {
        addAttribute("msg", msg);
        return this;
    }

    public DataResponse data(String key, Object val) {
        addAttribute(key, val);
        return this;
    }

    public Map<String, Object> getRawData() {
        return this;
    }
}
