import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        // Βάλε εδώ το client id σου (ΟΧΙ το secret)
        String CLIENT_ID = "7a5a358035e2b7c737c3b52d004d76342096dd989a9285445bb42aefbde02e91";

        // Για trending ΔΕΝ είναι υποχρεωτικό το access token.
        // Μπορείς να το αφήσεις κενό αν θέλεις.
        String ACCESS_TOKEN = "";
        // ή αν θες authenticated calls: βάλε αυτό που πήρες με TraktGetToken.

        TraktClient client = new TraktClient(CLIENT_ID, ACCESS_TOKEN);

        List<Movie> trending = client.getTrendingMovies();

        for (Movie m : trending) {
            System.out.println(m);
        }
    }
}
