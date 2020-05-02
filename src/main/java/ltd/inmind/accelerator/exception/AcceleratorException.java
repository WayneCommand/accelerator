package ltd.inmind.accelerator.exception;


import lombok.Data;

@Data
public class AcceleratorException extends RuntimeException {

    private String msg;
    private String code;

    public AcceleratorException() {

    }

    public AcceleratorException(String _code) {
        this.code = _code;
    }
}
