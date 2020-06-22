package ltd.inmind.accelerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.inmind.accelerator.model.oauth2.Oauth2AccessToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Oauth2AccessTokenMapper extends BaseMapper<Oauth2AccessToken> {

}
