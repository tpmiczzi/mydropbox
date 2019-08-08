package mydropbox;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class AwsLambdaInvoke {

    public String invokeLambda(String email, String url) {
        AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                                                               .withCredentials(
                                                                       new InstanceProfileCredentialsProvider(false));

        AWSLambda awsLambda = builder.build();

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("dropboxlambda")
                .withPayload("{\n" +
                                     " \"email\": \"" + email + ",\"\n" +
                                     " \"url\": \"" + url + "\"\n" +
                                     "}");

        InvokeResult invokeResult = null;

        try {
            invokeResult = awsLambda.invoke(invokeRequest);

            String json = new String(invokeResult.getPayload().array());
            JSONObject mainObj = new JSONObject(json);

            return (String) mainObj.get("responseMessage");
        } catch (Exception e) {
            throw e;
        }
    }
}
