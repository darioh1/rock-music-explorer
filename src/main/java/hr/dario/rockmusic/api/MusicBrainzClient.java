package hr.dario.rockmusic.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MusicBrainzClient {
    public void searchArtist(String artistName){
        String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        HttpClient client = HttpClient.newHttpClient();
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

            System.out.println("Status code: " + response.statusCode());
            System.out.println(response.body());

        } catch (Exception e) {
            System.out.println("Error while contacting MusicBrainz.");
            e.printStackTrace();
        }
    }
}
