package dropbox.lambda;

public class MyLambdaRequest {
    private String email;
    private String url;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MyLambdaRequest{" +
                "email='" + email + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
