package mydropbox.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class MyAmazonSES {
    private static final String FROM = "tpm.kiev.ua@gmail.com";

    private static final String SUBJECT = "Random file";

    private static final String HTMLBODY = "<h1>Hello my dear friends</h1>"
            + "<p>Random file, please download <a href='%s'>here</a></p>";

    public String sendEmail(String email, String url) {

        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                                                         .withRegion(Regions.US_EAST_1).build();

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(email))
                    .withMessage(new Message()
                                         .withBody(new Body()
                                                           .withHtml(new Content()
                                                                             .withCharset("UTF-8").withData(getBody(url))))
                                         .withSubject(new Content()
                                                              .withCharset("UTF-8").withData(SUBJECT)))
                    .withSource(FROM);
            client.sendEmail(request);

            return "Email sent!";
        } catch (Exception ex) {
            return "The email was not sent. Error message: " + ex.getMessage();
        }
    }

    private String getBody(String url) {

        return String.format(HTMLBODY, url);
    }
}
