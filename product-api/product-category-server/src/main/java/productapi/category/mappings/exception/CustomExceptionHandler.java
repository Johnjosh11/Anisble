package productapi.category.mappings.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import productapi.category.ProductApiInvalidRequestException;

@ControllerAdvice
public class CustomExceptionHandler {
    static Logger logger = LogManager.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(ProductApiInvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(ProductApiInvalidRequestException e) {
        logger.info("ProductApiInvalidRequestException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}