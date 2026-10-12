package hr.dario.rockmusic.service;

import hr.dario.rockmusic.model.ReleaseGroup;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlbumServiceTest {
    @Test
    void shouldExcludeCompilations() {
        AlbumService albumService = new AlbumService();
        ReleaseGroup regularAlbum = new ReleaseGroup();

        regularAlbum.setTitle("The Dark Side of the Moon");
        regularAlbum.setSecondaryTypes(List.of());

        ReleaseGroup compilation = new ReleaseGroup();
        compilation.setTitle("Greatest Hits");
        compilation.setSecondaryTypes(List.of("Compilation"));

        List<ReleaseGroup> albums = List.of(regularAlbum, compilation);
        List<ReleaseGroup> result = albumService.getRegularAlbums(albums);

        assertEquals(1, result.size());
        assertEquals("The Dark Side of the Moon", result.getFirst().getTitle());
    }

    @Test
    void shouldHandleNullSecondaryTypes() {
        AlbumService albumService = new AlbumService();
        ReleaseGroup album = new ReleaseGroup();

        album.setTitle("Animals");
        album.setSecondaryTypes(null);

        List<ReleaseGroup> albums = List.of(album);
        List<ReleaseGroup> result = albumService.getRegularAlbums(albums);

        assertEquals(1, result.size());
        assertEquals("Animals", result.getFirst().getTitle());
    }
}
