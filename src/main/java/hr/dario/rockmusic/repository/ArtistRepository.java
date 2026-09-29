package hr.dario.rockmusic.repository;

import hr.dario.rockmusic.model.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArtistRepository {
    public boolean save(Connection connection, Artist artist) throws SQLException {
        String sql = """
                INSERT INTO artists (id, name, type, country)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, artist.getId());
            statement.setString(2, artist.getName());
            statement.setString(3, artist.getType());
            statement.setString(4, artist.getCountry());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
