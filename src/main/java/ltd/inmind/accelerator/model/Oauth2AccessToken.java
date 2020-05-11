package ltd.inmind.accelerator.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_oauth2_access_token")
public class Oauth2AccessToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String accessToken;

    private Long uId;

    private Integer clientPrimaryId;

    private Date expiredTime;

    private Date createTime;

}
