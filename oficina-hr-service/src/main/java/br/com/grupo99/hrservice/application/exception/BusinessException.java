package br.com.grupo99.hrservice.application.exception;

/**
 * Exceção para erros de negócio.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
