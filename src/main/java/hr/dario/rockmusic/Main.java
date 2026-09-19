package hr.dario.rockmusic;

import hr.dario.rockmusic.api.MusicBrainzClient;
import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ArtistSearchResponse;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.model.ReleaseGroupResponse;
import hr.dario.rockmusic.ui.ConsoleInput;

import java.util.Scanner;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MusicBrainzClient client = new MusicBrainzClient();
        ConsoleInput consoleInput = new ConsoleInput();

        System.out.println("=================================");
        System.out.println("       ROCK MUSIC EXPLORER");
        System.out.println("=================================");
        System.out.println();
        System.out.println("Enter artist name: ");
        String artistName = scanner.nextLine();
        System.out.println();
        System.out.println("Searching for: " + artistName);

        ArtistSearchResponse response = client.searchArtist(artistName);
        Artist selectedArtist = null;
        if (response != null) {
            System.out.println("Found: " + response.getCount() + " artists");
            //System.out.println("Showing: " + response.getArtists().size() + " artists");

            int numberOfResults = Math.min(5, response.getArtists().size());
            int maxNameLength = 0;
            int maxTypeLength = 0;
            int maxCountryLength = 0;
            for (int i = 0; i < numberOfResults; i++) {
                Artist artist = response.getArtists().get(i);
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
                Artist artist = response.getArtists().get(i);
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
            int choice = consoleInput.readChoice(scanner, 1, numberOfResults);

            selectedArtist = response.getArtists().get(choice - 1);
            System.out.println("Selected artist: " + selectedArtist.getName());
           // System.out.println("MusicBrainz ID: " + selectedArtist.getId());
        } else {
            System.out.println("Error");
        }
        ReleaseGroupResponse albumsResponse = client.getAlbumsByArtist(selectedArtist.getId());
        if (albumsResponse != null){
            List<ReleaseGroup> albums = albumsResponse.getReleaseGroups();
            albums.sort(
                    Comparator.comparing(
                            ReleaseGroup::getFirstReleaseDate,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    )
            );
            System.out.println();
            System.out.println("1. Regular albums");
            System.out.println("2. All albums");
            int choice = consoleInput.readChoice(scanner, 1, 2);
            if (choice == 1){
                System.out.println();
                System.out.println("Regular albums of " + selectedArtist.getName() + ":");
                System.out.println();
                int maxTitleLength = 0;
                for (ReleaseGroup album : albums) {
                    if (album.getSecondaryTypes() != null && album.getSecondaryTypes().contains("Compilation")) {
                        continue;
                    }
                    if (album.getTitle().length() > maxTitleLength) {
                        maxTitleLength = album.getTitle().length();
                    }
                }
                for (ReleaseGroup album : albums) {
                    if (album.getSecondaryTypes() != null && album.getSecondaryTypes().contains("Compilation")) {
                        continue;
                    } else {
                        String releaseDate = album.getFirstReleaseDate() != null
                                ? album.getFirstReleaseDate() : "Unknown";
                        System.out.printf(
                                "%-" + maxTitleLength + "s | %s%n",
                                album.getTitle(),
                                releaseDate
                        );
                    }
                }
            } else {
                System.out.println();
                System.out.println("All albums of " + selectedArtist.getName() + ":");
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
                            album.getFirstReleaseDate()
                    );
                }
            }
        } else {
            System.out.println("Error");
        }
    }
}
