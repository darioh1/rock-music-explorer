package hr.dario.rockmusic.repository;

import hr.dario.rockmusic.database.DatabaseConnection;
import hr.dario.rockmusic.model.ReleaseGroup;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AlbumRepositoryTest {

    @Test
    void shouldReturnEmptyListWhenArtistHasNoAlbums() throws SQLException {
        AlbumRepository repository = new AlbumRepository();

        try (Connection connection = DatabaseConnection.getTestConnection()) {
            assertTrue(
                    repository.findByArtistId(connection, "nonexistent-artist").isEmpty()
            );
        }
    }

    @Test
    void shouldReadSavedAlbumWithSecondaryTypes() throws SQLException {
        AlbumRepository repository = new AlbumRepository();

        try (Connection connection = DatabaseConnection.getTestConnection()) {
            connection.setAutoCommit(false);

            try {
                String artistId = UUID.randomUUID().toString();

                String sql = """
                    INSERT INTO artists (id, name, type, country)
                    VALUES (?, ?, ?, ?)
                    """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, artistId);
                    statement.setString(2, "Test Artist");
                    statement.setString(3, "Group");
                    statement.setString(4, "GB");

                    statement.executeUpdate();
                }

                ReleaseGroup album = new ReleaseGroup();
                album.setId(UUID.randomUUID().toString());
                album.setTitle("Test Compilation");
                album.setFirstReleaseDate("2000-01-01");
                album.setPrimaryType("Album");
                album.setSecondaryTypes(List.of("Compilation", "Live"));

                repository.save(connection, album, artistId);
                List<ReleaseGroup> albums = repository.findByArtistId(connection, artistId);

                assertEquals(1, albums.size());
                assertEquals("Test Compilation", albums.getFirst().getTitle());
                assertEquals(album.getId(), albums.getFirst().getId());
                assertEquals(List.of("Compilation", "Live"), albums.getFirst().getSecondaryTypes());

            } finally {
                connection.rollback();
            }
        }
    }
}