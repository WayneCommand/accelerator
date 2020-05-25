package ltd.inmind.accelerator.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("u_user_profile")
public class UserProfile {

  @TableId
  private Long uId;

  private String nickname;

  private String avatar;

  private String email;

  private String phone;

  private String theme;

  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  @TableField(fill = FieldFill.UPDATE)
  private Date modifyTime;

}
