package hr.dario.rockmusic.repository;

import hr.dario.rockmusic.database.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArtistRepositoryTest {

    @Test
    void shouldReturnTrueWhenAlbumsAreFetched() throws SQLException {
        ArtistRepository repository = new ArtistRepository();

        try (Connection connection = DatabaseConnection.getTestConnection()) {
            connection.setAutoCommit(false);

            try {
                String artistId = UUID.randomUUID().toString();

                String sql = """
                        INSERT INTO artists (id, name, type, country, albums_fetched)
                        VALUES (?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)){
                    statement.setString(1, artistId);
                    statement.setString(2, "Pink Floyd");
                    statement.setString(3, "Group");
                    statement.setString(4, "unknown");
                    statement.setBoolean(5, true);

                    statement.executeUpdate();
                }

                assertTrue(repository.areAlbumsFetched(connection, artistId));

            } finally {
                connection.rollback();
            }
        }
    }

    @Test
    void shouldReturnFalseWhenAlbumsAreNotFetched() throws SQLException {
        ArtistRepository repository = new ArtistRepository();

        try (Connection connection = DatabaseConnection.getTestConnection()) {
            connection.setAutoCommit(false);

            try {
                String artistId = UUID.randomUUID().toString();

                String sql = """
                        INSERT INTO artists (id, name, type, country, albums_fetched)
                        VALUES (?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)){
                    statement.setString(1, artistId);
                    statement.setString(2, "Pink Floyd");
                    statement.setString(3, "Group");
                    statement.setString(4, "unknown");
                    statement.setBoolean(5, false);

                    statement.executeUpdate();
                }

                assertFalse(repository.areAlbumsFetched(connection, artistId));

            } finally {
                connection.rollback();
            }
        }
    }

    @Test
    void shouldReturnFalseWhenArtistDoesNotExist() throws SQLException {
        ArtistRepository repository = new ArtistRepository();

        try (Connection connection = DatabaseConnection.getTestConnection()) {
            String artistId = UUID.randomUUID().toString();

            assertFalse(repository.areAlbumsFetched(connection, artistId));
        }
    }
}