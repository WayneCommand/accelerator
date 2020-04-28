package ltd.inmind.accelerator.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
@Deprecated
public enum PlatformEnum {

    USER_NAME_ALREADY_EXIST("LOGIN.USER_NAME_ALREADY_EXIST", "用户名已存在");

    private String key;
    private String defaultDesc;
}
