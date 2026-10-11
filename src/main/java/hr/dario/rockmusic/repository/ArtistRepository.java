package hr.dario.rockmusic.repository;

import hr.dario.rockmusic.model.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepository {
    public boolean save(Connection connection, Artist artist) throws SQLException {
        String sql = """
                INSERT INTO artists (id, name, type, country)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, artist.getId());
            statement.setString(2, artist.getName());
            statement.setString(3, artist.getType());
            statement.setString(4, artist.getCountry());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Artist> findByName(
            Connection connection,
            String name
    ) throws SQLException{
        List<Artist> artists = new ArrayList<>();

        String sql = """
        SELECT id, name, type, country
        FROM artists
        WHERE name ILIKE ?
        ORDER BY name
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, "%" + name + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Artist artist = new Artist();

                    artist.setId(resultSet.getString("id"));
                    artist.setName(resultSet.getString("name"));
                    artist.setType(resultSet.getString("type"));
                    artist.setCountry(resultSet.getString("country"));

                    artists.add(artist);
                }
            }
        }
        return artists;
    }
}
