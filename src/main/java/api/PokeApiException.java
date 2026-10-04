package api;

public class PokeApiException extends RuntimeException {
    private final ErrorType error;

    public PokeApiException(ErrorType error, String message) {
        super(message);
        this.error = error;
    }

    public ErrorType getError() {
        return error;
    }
}
