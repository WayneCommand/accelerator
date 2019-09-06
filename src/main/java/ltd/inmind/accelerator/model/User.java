package ltd.inmind.accelerator.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("t_user")
@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Integer uId;
    private String username;
    private String password;
    private String email;
    @TableField(exist = false)
    private Date lastLogin;
    private Date createTime;
    private Date updateTime;

}
