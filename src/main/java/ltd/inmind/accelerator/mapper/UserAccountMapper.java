package ltd.inmind.accelerator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.inmind.accelerator.model.po.UserAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
}
