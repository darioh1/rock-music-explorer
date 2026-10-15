package hr.dario.rockmusic.service;

import hr.dario.rockmusic.api.MusicBrainzClient;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.model.ReleaseGroupResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlbumPaginationServiceTest {
    @Test
    void shouldFetchAllAlbumPages() {
        MusicBrainzClient client = mock(MusicBrainzClient.class);
        AlbumPaginationService service = new AlbumPaginationService(client, 0);
        ReleaseGroupResponse firstPage = new ReleaseGroupResponse();
        ReleaseGroupResponse secondPage = new ReleaseGroupResponse();
        List<ReleaseGroup> firstAlbums = new ArrayList<>();
        List<ReleaseGroup> secondAlbums = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            ReleaseGroup album = new ReleaseGroup();
            album.setTitle("Album " + i);
            firstAlbums.add(album);
        }

        for (int i = 101; i <= 150; i++) {
            ReleaseGroup album = new ReleaseGroup();
            album.setTitle("Album " + i);
            secondAlbums.add(album);
        }

        firstPage.setReleaseGroups(firstAlbums);
        firstPage.setCount(150);
        secondPage.setReleaseGroups(secondAlbums);
        secondPage.setCount(150);
        when(client.getAlbumsByArtist("artist-id", 100, 0)).thenReturn(firstPage);
        when(client.getAlbumsByArtist("artist-id", 100, 100)).thenReturn(secondPage);
        ReleaseGroupResponse result = service.getAllAlbumsByArtist("artist-id");

        assertNotNull(result);
        assertEquals(150, result.getReleaseGroups().size());
        assertEquals("Album 1", result.getReleaseGroups().getFirst().getTitle());
        assertEquals("Album 150", result.getReleaseGroups().getLast().getTitle());
        verify(client, times(1)).getAlbumsByArtist("artist-id", 100, 0);
        verify(client, times(1)).getAlbumsByArtist("artist-id", 100, 100);
    }

    @Test
    void shouldReturnNullWhenSecondPageFails() {
        MusicBrainzClient client = mock(MusicBrainzClient.class);
        AlbumPaginationService service = new AlbumPaginationService(client, 0);

        List<ReleaseGroup> firstAlbums = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            ReleaseGroup album = new ReleaseGroup();
            album.setTitle("Album " + i);
            firstAlbums.add(album);
        }

        ReleaseGroupResponse firstPage = new ReleaseGroupResponse();
        firstPage.setCount(150);
        firstPage.setReleaseGroups(firstAlbums);

        when(client.getAlbumsByArtist("artist-id", 100, 0)).thenReturn(firstPage);
        when(client.getAlbumsByArtist("artist-id", 100, 100)).thenReturn(null);
        ReleaseGroupResponse result = service.getAllAlbumsByArtist("artist-id");
        assertNull(result);
        verify(client, times(1)).getAlbumsByArtist("artist-id", 100, 100);
    }

    @Test
    void shouldReturnNullWhenPageIsEmptyBeforeAllAlbumsAreFetched() {
        MusicBrainzClient client = mock(MusicBrainzClient.class);
        AlbumPaginationService service = new AlbumPaginationService(client, 0);

        List<ReleaseGroup> firstAlbums = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            ReleaseGroup album = new ReleaseGroup();
            album.setTitle("Album " + i);
            firstAlbums.add(album);
        }

        ReleaseGroupResponse firstPage = new ReleaseGroupResponse();
        firstPage.setCount(150);
        firstPage.setReleaseGroups(firstAlbums);

        ReleaseGroupResponse secondPage = new ReleaseGroupResponse();
        secondPage.setCount(150);
        secondPage.setReleaseGroups(List.of());

        when(client.getAlbumsByArtist("artist-id", 100, 0)).thenReturn(firstPage);
        when(client.getAlbumsByArtist("artist-id", 100, 100)).thenReturn(secondPage);
        ReleaseGroupResponse result = service.getAllAlbumsByArtist("artist-id");
        assertNull(result);
    }
}
