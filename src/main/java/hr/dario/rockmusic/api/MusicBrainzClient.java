package hr.dario.rockmusic.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.dario.rockmusic.model.ArtistSearchResponse;
import hr.dario.rockmusic.model.ReleaseGroupResponse;

public class MusicBrainzClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();
    public ArtistSearchResponse searchArtist(String artistName){
        String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String url = "https://musicbrainz.org/ws/2/artist/?query=artist:"
                + encodedArtistName
                + "&fmt=json";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "RockMusicExplorer/1.0")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() != 200) {
                System.out.println("MusicBrainz returned status code: " + response.statusCode());
                return null;
            }
            ArtistSearchResponse searchResponse =
                    objectMapper.readValue(
                            response.body(),
                            ArtistSearchResponse.class
                    );
            return searchResponse;
        } catch (IOException e) {
            System.out.println("Error while contacting or reading data from MusicBrainz.");
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Request to MusicBrainz was interrupted.");
            return null;
        }
    }
    public ReleaseGroupResponse getAlbumsByArtist(String artistId){
        String encodedArtistId = URLEncoder.encode(artistId, StandardCharsets.UTF_8);
        String url = "https://musicbrainz.org/ws/2/release-group?artist="
                + encodedArtistId
                + "&type=album&fmt=json";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "RockMusicExplorer/1.0")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() != 200) {
                System.out.println("MusicBrainz returned status code: " + response.statusCode());
                return null;
            }
            ReleaseGroupResponse releaseGroupResponse =
                    objectMapper.readValue(
                            response.body(),
                            ReleaseGroupResponse.class
                    );
            return releaseGroupResponse;
        } catch (IOException e) {
            System.out.println("Error while contacting or reading data from MusicBrainz.");
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Request to MusicBrainz was interrupted.");
            return null;
        }
    }
}
