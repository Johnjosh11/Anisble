package productapi.category;

public class ProductApiInvalidRequestException extends RuntimeException {

    public ProductApiInvalidRequestException(String message) {
        super(message);
    }
}
