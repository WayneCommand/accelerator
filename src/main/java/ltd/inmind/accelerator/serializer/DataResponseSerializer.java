package ltd.inmind.accelerator.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ltd.inmind.accelerator.model.vo.DataResponse;

import java.lang.reflect.Type;

public class DataResponseSerializer implements JsonSerializer<DataResponse> {

    @Override
    public JsonElement serialize(DataResponse src, Type typeOfSrc, JsonSerializationContext context) {

        return context.serialize(src.getRawData());
    }

}
