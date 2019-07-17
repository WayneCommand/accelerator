package ltd.inmind.user_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.inmind.user_service.model.Oauth2Client;
import org.apache.ibatis.annotations.*;

@Mapper
public interface Oauth2ClientMapper extends BaseMapper<Oauth2Client> {

}
