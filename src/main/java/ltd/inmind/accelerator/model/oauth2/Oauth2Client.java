package ltd.inmind.accelerator.model.oauth2;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("oauth2_clients")
public class Oauth2Client {

    @TableId(type = IdType.AUTO)
    private int id;

    private String clientId;

    private String clientName;

    private String clientSecret;

    private transient String logo;

    private transient String description;

    private String callbackUrl;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Date modifyTime;
}
