package hr.dario.rockmusic.ui;

import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ReleaseGroup;

import java.util.List;

public class ConsoleOutput {
    public void printStart() {
        System.out.println("=================================");
        System.out.println("       ROCK MUSIC EXPLORER");
        System.out.println("=================================");
        System.out.println();
    }

    public void printArtistPrompt() {
        System.out.println("Enter artist name: ");
    }

    public void printSearchingForArtist(String artistName){
        System.out.println();
        System.out.println("Searching for: " + artistName);
    }

    public void printFoundArtists(int results){
        System.out.println("Found: " + results + " artists");
        //System.out.println("Showing: " + response.getArtists().size() + " artists");
    }

    public void printArtists(List<Artist> artists, int numberOfResults) {
        int maxNameLength = 0;
        int maxTypeLength = 0;
        int maxCountryLength = 0;
        for (int i = 0; i < numberOfResults; i++) {
            Artist artist = artists.get(i);
            if (artist.getName().length() > maxNameLength) {
                maxNameLength = artist.getName().length();
            }
            if (artist.getType().length() > maxTypeLength){
                maxTypeLength = artist.getType().length();
            }
            String country = artist.getCountry() != null ? artist.getCountry() : "Unknown";
            if (country.length() > maxCountryLength){
                maxCountryLength = country.length();
            }
        }
        for (int i = 0; i < numberOfResults; i++) {
            Artist artist = artists.get(i);
            String country = artist.getCountry() != null ? artist.getCountry() : "Unknown";
            System.out.printf(
                    "%d. %-" + maxNameLength + "s | %-"
                            + maxTypeLength + "s | %-"
                            + maxCountryLength + "s | %d%n",
                    i + 1,
                    artist.getName(),
                    artist.getType(),
                    country,
                    artist.getScore()
            );
        }
        System.out.println();
    }

    public void printSelectedArtist(Artist selectedArtist) {
        System.out.println("Selected artist: " + selectedArtist.getName());
        // System.out.println("MusicBrainz ID: " + selectedArtist.getId());
    }

    public void printError(){
        System.out.println("Error");
    }

    public void printNoArtistsFound() {
        System.out.println("No artists found.");
    }

    public void printNoAlbumsFound(){
        System.out.println("No albums found.");
    }

    public void printAlbumsPrompt (){
        System.out.println();
        System.out.println("1. Regular albums");
        System.out.println("2. All albums");
    }

    public void printAlbums(List<ReleaseGroup> albums, Artist selectedArtist, String albumType) {
        System.out.println();
        System.out.println(albumType + " albums of " + selectedArtist.getName() + ":");
        System.out.println();
        int maxTitleLength = 0;
        for (ReleaseGroup album : albums) {
            if (album.getTitle().length() > maxTitleLength) {
                maxTitleLength = album.getTitle().length();
            }
        }
        for (ReleaseGroup album : albums){
            String releaseDate = album.getFirstReleaseDate() != null
                    ? album.getFirstReleaseDate() : "Unknown";

            System.out.printf(
                    "%-" + maxTitleLength + "s | %s%n",
                    album.getTitle(),
                    releaseDate
            );
        }
    }

    public void printDatabaseSuccessful(){
        System.out.println("Database connection successful!");
        System.out.println();
    }

    public void printDatabaseFail(){
        System.out.println("Database connection failed!");
        System.out.println();
    }

    public void printArtistSaved(){
        System.out.println("Artist saved to database!");
        System.out.println();
    }

    public void printArtistSaveFailed(){
        System.out.println("Failed to save artist to database!");
        System.out.println();
    }

    public void printArtistAlreadyExists(){
        System.out.println("Artist already exists in database.");
        System.out.println();
    }
}
