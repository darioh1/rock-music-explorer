package hr.dario.rockmusic.service;

import hr.dario.rockmusic.api.MusicBrainzClient;
import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.model.ReleaseGroupResponse;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicServiceTest {
    @Test
    void shouldReturnAlbumsFromDatabaseWithoutCallingApi() throws SQLException {
        DatabaseService databaseService = mock(DatabaseService.class);
        AlbumPaginationService paginationService = mock(AlbumPaginationService.class);
        MusicService musicService = new MusicService(databaseService, paginationService);
        Artist artist = new Artist();
        artist.setId("artist-123");

        ReleaseGroup album = new ReleaseGroup();
        album.setTitle("The Dark Side of the Moon");

        List<ReleaseGroup> savedAlbums = List.of(album);

        when(databaseService.areAlbumsFetched("artist-123")).thenReturn(true);
        when(databaseService.getSavedAlbums("artist-123")).thenReturn(savedAlbums);

        List<ReleaseGroup> result = musicService.getAlbumsForArtist(artist);

        assertEquals(1, result.size());
        assertEquals("The Dark Side of the Moon", result.getFirst().getTitle());
        verifyNoInteractions(paginationService);
    }

    @Test
    void shouldFetchAndSaveAlbumsWhenNotInDatabase() throws SQLException {
        DatabaseService databaseService = mock(DatabaseService.class);
        AlbumPaginationService paginationService = mock(AlbumPaginationService.class);
        MusicService musicService = new MusicService(databaseService, paginationService);
        Artist artist = new Artist();
        artist.setId("artist-123");

        ReleaseGroup album = new ReleaseGroup();
        album.setTitle("The Dark Side of the Moon");

        List<ReleaseGroup> albums = List.of(album);

        ReleaseGroupResponse response = new ReleaseGroupResponse();
        response.setReleaseGroups(albums);
        response.setCount(1);

        when(databaseService.areAlbumsFetched("artist-123")).thenReturn(false);
        when(paginationService.getAllAlbumsByArtist("artist-123")).thenReturn(response);

        List<ReleaseGroup> result = musicService.getAlbumsForArtist(artist);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("The Dark Side of the Moon", result.getFirst().getTitle());
        verify(databaseService, times(1)).saveArtistWithAlbums(artist, albums);
        verify(paginationService, times(1)).getAllAlbumsByArtist("artist-123");
    }

    @Test
    void shouldNotSaveAlbumsWhenApiFails() throws SQLException {
        DatabaseService databaseService = mock(DatabaseService.class);
        AlbumPaginationService paginationService = mock(AlbumPaginationService.class);
        MusicService musicService = new MusicService(databaseService, paginationService);
        Artist artist = new Artist();
        artist.setId("artist-123");

        when(databaseService.areAlbumsFetched("artist-123")).thenReturn(false);
        when(paginationService.getAllAlbumsByArtist("artist-123")).thenReturn(null);

        List<ReleaseGroup> result = musicService.getAlbumsForArtist(artist);

        assertNull(result);
        verify(databaseService, never()).saveArtistWithAlbums(any(), any());
    }

    @Test
    void shouldPropagateExceptionWhenDatabaseFails() throws SQLException {
        DatabaseService databaseService = mock(DatabaseService.class);
        AlbumPaginationService paginationService = mock(AlbumPaginationService.class);

        MusicService musicService =
                new MusicService(databaseService, paginationService);

        Artist artist = new Artist();
        artist.setId("artist-123");

        when(databaseService.areAlbumsFetched("artist-123")).thenThrow(new SQLException("Database unavailable"));
        assertThrows(SQLException.class, () -> {
            musicService.getAlbumsForArtist(artist);
        });
        verifyNoInteractions(paginationService);
    }
}
