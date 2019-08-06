package mydropbox;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class AwsLambdaInvoke {

    public String invokeLambda(String email) {
//        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAI33A2ZRM4GH5OP2A",
//                                                                  "PZKqlK37qO5Wvh1edcWDSImpDjNXdB90Qt9aNoru");

        AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                                                               .withCredentials(
                                                                       new InstanceProfileCredentialsProvider(false));
//                                                               .withRegion(Regions.US_EAST_2);

        AWSLambda awsLambda = builder.build();

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("dropboxlambda")
                .withPayload("{\n" +
                                     " \"name\": \"" + email + "\"\n" +
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
