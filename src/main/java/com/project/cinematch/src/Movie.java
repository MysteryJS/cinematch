public class Movie {
    private String title;
    private int year;
    private int traktId;

    public Movie(String title, int year, int traktId) {
        this.title = title;
        this.year = year;
        this.traktId = traktId;
    }

    public String getTitle() { return title; }
    public int getYear() { return year; }
    public int getTraktId() { return traktId; }

    @Override
    public String toString() {
        return title + " (" + year + ") [trakt=" + traktId + "]";
    }
}
