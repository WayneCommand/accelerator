package ltd.inmind.accelerator.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("u_user_profile")
public class UserProfile {

  private Long uId;
  private String nickname;
  private String avatar;
  private String theme;
  private Date createTime;
  private Date modifyTime;
}
