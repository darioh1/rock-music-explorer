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
    private final HttpClient client;
    private final long retryDelayMillis;

    public MusicBrainzClient() {
        this(HttpClient.newHttpClient(), 2000);
    }

    public MusicBrainzClient(HttpClient client, long retryDelayMillis) {
        this.client = client;
        this.retryDelayMillis = retryDelayMillis;
    }

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
            HttpResponse<String> response = sendRequestWithRetry(request);
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

    public ReleaseGroupResponse getAlbumsByArtist(String artistId, int limit, int offset){
        String encodedArtistId = URLEncoder.encode(artistId, StandardCharsets.UTF_8);
        String url = "https://musicbrainz.org/ws/2/release-group?artist="
                + encodedArtistId
                + "&type=album&fmt=json"
                + "&limit=" + limit
                + "&offset=" + offset;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "RockMusicExplorer/1.0")
                .GET()
                .build();
        try {
            HttpResponse<String> response = sendRequestWithRetry(request);
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

    private HttpResponse<String> sendRequestWithRetry(HttpRequest request)
            throws IOException, InterruptedException {

        int maxAttempts = 3;
        int attempt = 0;
        HttpResponse<String> response = null;

        while (attempt < maxAttempts) {
            attempt++;
            response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                return response;
            }

            if (statusCode != 503 && statusCode != 429) {
                return response;
            }

            if (attempt < maxAttempts) {
                Thread.sleep(retryDelayMillis);
            }
        }
        return response;
    }
}
