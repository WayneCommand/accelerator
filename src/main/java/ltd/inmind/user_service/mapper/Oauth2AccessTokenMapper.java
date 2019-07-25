package ltd.inmind.user_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.inmind.user_service.model.Oauth2AccessToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Oauth2AccessTokenMapper extends BaseMapper<Oauth2AccessToken> {

}
