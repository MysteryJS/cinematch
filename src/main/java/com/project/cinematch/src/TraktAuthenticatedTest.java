import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TraktAuthenticatedTest {

    private static final String CLIENT_ID = "7a5a358035e2b7c737c3b52d004d76342096dd989a9285445bb42aefbde02e91";
    private static final String ACCESS_TOKEN = "129587faae6ca7327a4dc8fcd3ce733cfda10f29eba2f355f6fda1d8b319a965";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.trakt.tv/users/me"))
                .header("Content-Type", "application/json")
                .header("trakt-api-version", "2")
                .header("trakt-api-key", CLIENT_ID)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Response:\n" + response.body());
    }
}