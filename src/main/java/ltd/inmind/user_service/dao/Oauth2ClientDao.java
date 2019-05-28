package ltd.inmind.user_service.dao;

import ltd.inmind.user_service.model.Oauth2Client;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface Oauth2ClientDao {

    @Insert("")
    int insert(@Param("oauth2Client") Oauth2Client oauth2Client);


}
