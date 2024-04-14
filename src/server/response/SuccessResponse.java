package server.response;

public class SuccessResponse {
    private final String successMessage;

    public SuccessResponse(String successMessage) {
        this.successMessage = successMessage;
    }

    @Override
    public String toString() {
        return "SuccessResponse{" +
                "successMessage='" + successMessage + '\'' +
                '}';
    }
}
