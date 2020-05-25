package ltd.inmind.accelerator.model.vo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonComponent
public class DataResponse {

    private Map<String, Object> data = new HashMap<>();

    public static final String STATE = "state";
    public static final String STATE_SUCCESS = "success";
    public static final String STATE_FAILED = "failed";


    public DataResponse success() {
        data.put(STATE, STATE_SUCCESS);
        return this;
    }


    /**
     * 这里的失败如无特殊情况 指的是业务失败
     * 比如说密码不匹配
     */
    public DataResponse failed() {
        data.put(STATE, STATE_FAILED);
        return this;
    }

    public DataResponse msg(String msg) {
        data.put("msg", msg);
        return this;
    }

    public DataResponse data(String key, Object val) {
        data.put(key, val);
        return this;
    }

    public Map<String, Object> getRawData() {
        return data;
    }


    public static class Serializer extends JsonSerializer<DataResponse> {
        @Override
        public void serialize(DataResponse dataResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            jsonGenerator.writeObject(dataResponse.getRawData());
        }
    }
}
