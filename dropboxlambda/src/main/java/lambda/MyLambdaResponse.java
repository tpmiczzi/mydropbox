package dropbox.lambda;

public class MyLambdaResponse {
    String responseMessage;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "MyLambdaResponse{" +
                "responseMessage='" + responseMessage + '\'' +
                '}';
    }
}
