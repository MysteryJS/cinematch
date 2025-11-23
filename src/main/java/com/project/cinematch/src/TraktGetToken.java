import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TraktGetToken {

    private static final String CLIENT_ID = "7a5a358035e2b7c737c3b52d004d76342096dd989a9285445bb42aefbde02e91";
    private static final String CLIENT_SECRET = "d67091b777d54a41f9f8d8e9d19f81cade852f15d8e96f182c24101e146320d5";
    private static final String REDIRECT_URI = "http://localhost:8080/callback";

    // Βάλε εδώ τον κωδικό που πήρες από το callback
    private static final String AUTH_CODE = "a84300134b32b33e315d7567fc02b8f34fedb8c70eb1664a3827de0e02e8d42c";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String body = """
            {
              "code": "%s",
              "client_id": "%s",
              "client_secret": "%s",
              "redirect_uri": "%s",
              "grant_type": "authorization_code"
            }
            """.formatted(AUTH_CODE, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.trakt.tv/oauth/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body:\n" + response.body());
    }
}