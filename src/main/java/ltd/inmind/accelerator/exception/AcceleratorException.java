package ltd.inmind.accelerator.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ltd.inmind.accelerator.constants.PlatformEnum;

@Getter
@AllArgsConstructor
@Deprecated
public class AcceleratorException extends RuntimeException {

    private PlatformEnum platformEnum;
}
