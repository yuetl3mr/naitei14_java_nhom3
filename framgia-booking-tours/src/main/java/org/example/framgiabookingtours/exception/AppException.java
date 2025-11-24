package org.example.framgiabookingtours.exception;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class AppException extends RuntimeException {
    private Object[] args;

    private ErrorCode errorCode;
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public AppException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.args = args;
    }


}
