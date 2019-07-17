package ltd.inmind.user_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.inmind.user_service.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
