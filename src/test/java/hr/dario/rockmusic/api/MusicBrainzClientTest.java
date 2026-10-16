package hr.dario.rockmusic.api;

import hr.dario.rockmusic.model.ArtistSearchResponse;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MusicBrainzClientTest {

    @Test
    void shouldRetryWhenMusicBrainzReturns503() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        MusicBrainzClient musicBrainzClient = new MusicBrainzClient(httpClient, 0);

        @SuppressWarnings("unchecked")
        HttpResponse<String> unavailableResponse = mock(HttpResponse.class);

        @SuppressWarnings("unchecked")
        HttpResponse<String> successResponse = mock(HttpResponse.class);

        when(unavailableResponse.statusCode()).thenReturn(503);
        when(successResponse.statusCode()).thenReturn(200);
        when(successResponse.body()).thenReturn("{\"count\":0,\"artists\":[]}");
        when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(
                unavailableResponse,
                unavailableResponse,
                successResponse
        );

        ArtistSearchResponse artistSearchResponse = musicBrainzClient.searchArtist("Pink Floyd");

        assertNotNull(artistSearchResponse);
        verify(httpClient, times(3)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldNotRetryWhenMusicBrainzReturns400() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        MusicBrainzClient musicBrainzClient = new MusicBrainzClient(httpClient, 0);

        @SuppressWarnings("unchecked")
        HttpResponse<String> badRequestResponse = mock(HttpResponse.class);

        when(badRequestResponse.statusCode()).thenReturn(400);
        when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(badRequestResponse);

        ArtistSearchResponse result = musicBrainzClient.searchArtist("Pink Floyd");
        assertNull(result);
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldStopRetryingAfterThreeFailedAttempts() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        MusicBrainzClient musicBrainzClient = new MusicBrainzClient(httpClient, 0);

        @SuppressWarnings("unchecked")
        HttpResponse<String> unavailableResponse = mock(HttpResponse.class);

        when(unavailableResponse.statusCode()).thenReturn(503);
        when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(unavailableResponse);

        ArtistSearchResponse result = musicBrainzClient.searchArtist("Pink Floyd");
        assertNull(result);
        verify(httpClient, times(3)).send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        );
    }

    @Test
    void shouldRetryWhenMusicBrainzReturns429() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        MusicBrainzClient musicBrainzClient = new MusicBrainzClient(httpClient, 0);

        @SuppressWarnings("unchecked")
        HttpResponse<String> tooManyRequestsResponse = mock(HttpResponse.class);

        @SuppressWarnings("unchecked")
        HttpResponse<String> successResponse = mock(HttpResponse.class);

        when(tooManyRequestsResponse.statusCode()).thenReturn(429);

        when(successResponse.statusCode()).thenReturn(200);
        when(successResponse.body()).thenReturn("{\"count\":0,\"artists\":[]}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(tooManyRequestsResponse, successResponse);

        ArtistSearchResponse result = musicBrainzClient.searchArtist("Pink Floyd");

        assertNotNull(result);

        verify(httpClient, times(2)).send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        );
    }
}
