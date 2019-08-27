package ltd.inmind.accelerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.inmind.accelerator.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
