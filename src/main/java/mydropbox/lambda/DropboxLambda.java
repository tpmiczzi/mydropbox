package mydropbox.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Date;
import java.util.UUID;

public class DropboxLambda implements RequestHandler<MyLambdaRequest, MyLambdaResponse> {

    @Override
    public MyLambdaResponse handleRequest(MyLambdaRequest input, Context context) {
        context.getLogger().log("Input: " + input);
        MyLambdaResponse lambdaResponse = new MyLambdaResponse();
        try {
            lambdaResponse.setResponseMessage("Hello " + input.getName() + " Response Time : " + new Date());
            lambdaResponse.setTransactionID(UUID.randomUUID().toString());
        } catch (Exception e) {
            e.printStackTrace();
            lambdaResponse.setResponseMessage(e.getMessage());
        }
        context.getLogger().log("Response : " + lambdaResponse);
        return lambdaResponse;
    }
}
