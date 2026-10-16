package hr.dario.rockmusic;

import hr.dario.rockmusic.api.MusicBrainzClient;
import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ArtistSearchResponse;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.service.*;
import hr.dario.rockmusic.ui.ConsoleInput;
import hr.dario.rockmusic.ui.ConsoleOutput;

import java.util.Scanner;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MusicBrainzClient client = new MusicBrainzClient();
        ConsoleInput consoleInput = new ConsoleInput();
        ConsoleOutput consoleOutput = new ConsoleOutput();
        AlbumService albumService = new AlbumService();
        DatabaseService databaseService = new DatabaseService();
        AlbumPaginationService paginationService = new AlbumPaginationService(client);
        MusicService musicService = new MusicService(databaseService, paginationService);

        consoleOutput.printStart();
        consoleOutput.printArtistPrompt();

        String artistName = consoleInput.readArtistName(scanner);

        consoleOutput.printSearchingForArtist(artistName);

        ArtistSearchResponse response = client.searchArtist(artistName);
        if (response == null) {
            consoleOutput.printError();
            return;
        }
        if (response.getArtists() == null || response.getArtists().isEmpty()){
            consoleOutput.printNoArtistsFound();
            return;
        }
        consoleOutput.printFoundArtists(response.getCount());

        int numberOfResults = Math.min(5, response.getArtists().size());

        consoleOutput.printArtists(response.getArtists(), numberOfResults);

        int artistChoice = consoleInput.readChoice(scanner, 1, numberOfResults, "artist");

        Artist selectedArtist = response.getArtists().get(artistChoice - 1);

        consoleOutput.printSelectedArtist(selectedArtist);

        AlbumLoadResult result;

        try {
            result = musicService.getAlbumsForArtist(selectedArtist);
        } catch (SQLException e) {
            consoleOutput.printArtistSaveFailed();
            e.printStackTrace();
            return;
        }

        if (result == null) {
            consoleOutput.printError();
            return;
        }

        List<ReleaseGroup> albums = result.albums();
        consoleOutput.printAlbumSource(result.source());

        if (albums.isEmpty()) {
            consoleOutput.printNoAlbumsFound();
            return;
        }

        albums = new ArrayList<>(albums);
        albumService.sortByReleaseDate(albums);
        consoleOutput.printAlbumsPrompt();

        int albumsChoice = consoleInput.readChoice(scanner, 1, 2, "album option");

        if (albumsChoice == 1){
            List<ReleaseGroup> regularAlbums = albumService.getRegularAlbums(albums);

            consoleOutput.printAlbums(regularAlbums, selectedArtist, "Regular");
        } else {
            consoleOutput.printAlbums(albums, selectedArtist, "All");
        }
    }
}
