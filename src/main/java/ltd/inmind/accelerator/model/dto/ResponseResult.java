package ltd.inmind.accelerator.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import ltd.inmind.accelerator.exception.AcceleratorException;

@AllArgsConstructor
@Builder
@Deprecated
public class ResponseResult {
    private boolean isSucceeded;
    private String message;

    public static ResponseResult success() {
        return ResponseResult.builder()
                .isSucceeded(true)
                .build();
    }

    public static ResponseResult success(String message) {
        return ResponseResult.builder()
                .isSucceeded(true)
                .message(message)
                .build();
    }

    public static ResponseResult failure(AcceleratorException ex) {
        return ResponseResult.builder()
                .isSucceeded(false)
                .message(ex.getPlatformEnum().getDefaultDesc())
                .build();
    }

}
