package ltd.inmind.user_service.dao;

import ltd.inmind.user_service.model.Oauth2Client;
import org.apache.ibatis.annotations.*;

@Mapper
public interface Oauth2ClientDao {

    @Insert("insert into t_oauth2_client(client_id,client_name,client_secret,callback_url)" +
            " values(#{oauth2Client.clientId},#{oauth2Client.clientName},#{oauth2Client.clientSecret},#{oauth2Client.callbackUrl})")
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    int insert(@Param("oauth2Client") Oauth2Client oauth2Client);

    @Select("select * from t_oauth2_client where id = #{id}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "client_id", property = "clientId"),
            @Result(column = "client_name", property = "clientName"),
            @Result(column = "client_secret", property = "clientSecret"),
            @Result(column = "callback_url", property = "callbackUrl")
    })
    Oauth2Client selectById(Integer id);

    @Select("select * from t_oauth2_client where client_id = #{clientId}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "client_id", property = "clientId"),
            @Result(column = "client_name", property = "clientName"),
            @Result(column = "client_secret", property = "clientSecret"),
            @Result(column = "callback_url", property = "callbackUrl")
    })
    Oauth2Client selectByClientId(String clientId);



}
