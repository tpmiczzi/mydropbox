package mydropbox.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Date;

public class DropboxLambda implements RequestHandler<MyLambdaRequest, MyLambdaResponse> {

    @Override
    public MyLambdaResponse handleRequest(MyLambdaRequest myLambdaRequest, Context context) {
        context.getLogger().log("Input: " + myLambdaRequest);
        MyLambdaResponse lambdaResponse = new MyLambdaResponse();
        try {

            MyAmazonSES myAmazonSES = new MyAmazonSES();
            String rez = myAmazonSES.sendEmail(myLambdaRequest.getEmail(), myLambdaRequest.getUrl());

            lambdaResponse.setResponseMessage("Status - " + rez + " Response Time : " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
            lambdaResponse.setResponseMessage(e.getMessage());
        }
        context.getLogger().log("Response : " + lambdaResponse);
        return lambdaResponse;
    }
}
