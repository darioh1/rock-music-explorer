package hr.dario.rockmusic;

import hr.dario.rockmusic.api.MusicBrainzClient;
import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ArtistSearchResponse;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.model.ReleaseGroupResponse;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================");
        System.out.println("       ROCK MUSIC EXPLORER");
        System.out.println("=================================");
        System.out.println();
        System.out.println("Enter artist name: ");
        String artistName = scanner.nextLine();
        System.out.println();
        System.out.println("Searching for: " + artistName);
        MusicBrainzClient client = new MusicBrainzClient();
        ArtistSearchResponse response = client.searchArtist(artistName);
        Artist selectedArtist = null;
        if (response != null) {
            System.out.println("Found: " + response.getCount() + " artists");
            //System.out.println("Showing: " + response.getArtists().size() + " artists");

            int numberOfResults = Math.min(5, response.getArtists().size());
            for (int i = 0; i < numberOfResults; i++) {
                Artist artist = response.getArtists().get(i);
                String country = artist.getCountry() != null ? artist.getCountry() : "Unknown";
                System.out.println(
                        (i + 1) + ". "
                                + artist.getName() + " | "
                                + artist.getType() + " | "
                                + country + " | "
                                + artist.getScore()
                );
            }
            int choice;
            while (true) {
                System.out.println("Choose artist [1-" + numberOfResults + "]: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice <= numberOfResults && choice >= 1) {
                        break;
                    } else {
                        System.out.println("Please chose a number between 1 and " + numberOfResults + ".");
                    }
                } else {
                    System.out.println("Please enter a number.");
                    scanner.next();
                }
            }

            selectedArtist = response.getArtists().get(choice - 1);
            System.out.println("Selected artist: " + selectedArtist.getName());
            System.out.println("MusicBrainz ID: " + selectedArtist.getId());
        } else {
            System.out.println("Error");
        }
        ReleaseGroupResponse albumsResponse = client.getAlbumsByArtist(selectedArtist.getId());
        if (albumsResponse != null){
            System.out.println();
            System.out.println("Albums: ");
            System.out.println();
            for (ReleaseGroup album : albumsResponse.getReleaseGroups()){
                System.out.println(
                        album.getTitle() + " | "
                        + album.getFirstReleaseDate()
                );
            }
        } else {
            System.out.println("Error");
        }
    }
}
