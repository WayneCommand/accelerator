package ltd.inmind.accelerator.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AcceleratorException extends RuntimeException {

    private String msg;
    private String code;

    public AcceleratorException() {

    }

    public AcceleratorException(String _code) {
        this.code = _code;
    }
}
