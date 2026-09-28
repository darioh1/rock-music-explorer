package hr.dario.rockmusic;

import hr.dario.rockmusic.api.MusicBrainzClient;
import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ArtistSearchResponse;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.model.ReleaseGroupResponse;
import hr.dario.rockmusic.ui.ConsoleInput;
import hr.dario.rockmusic.ui.ConsoleOutput;

import java.util.Scanner;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MusicBrainzClient client = new MusicBrainzClient();
        ConsoleInput consoleInput = new ConsoleInput();
        ConsoleOutput consoleOutput = new ConsoleOutput();

        consoleOutput.printStart();
        consoleOutput.printArtistPrompt();

        String artistName = consoleInput.readArtistName(scanner);

        consoleOutput.printSearchingForArtist(artistName);

        ArtistSearchResponse response = client.searchArtist(artistName);
        Artist selectedArtist = null;
        if (response != null) {
            consoleOutput.printFoundArtists(response.getCount());

            int numberOfResults = Math.min(5, response.getArtists().size());

            consoleOutput.printArtists(response.getArtists(), numberOfResults);

            int choice = consoleInput.readChoice(scanner, 1, numberOfResults);

            selectedArtist = response.getArtists().get(choice - 1);

            consoleOutput.printSelectedArtist(selectedArtist);
        } else {
            consoleOutput.printError();
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
            consoleOutput.printAlbumsPrompt();

            int choice = consoleInput.readChoice(scanner, 1, 2);

            if (choice == 1){
                consoleOutput.printRegularAlbums(albums, selectedArtist);
            } else {
                consoleOutput.printAllAlbums(albums, selectedArtist);
            }
        } else {
            consoleOutput.printError();
        }
    }
}
