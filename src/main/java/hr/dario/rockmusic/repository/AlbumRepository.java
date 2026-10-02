package hr.dario.rockmusic.repository;

import hr.dario.rockmusic.model.ReleaseGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AlbumRepository {
    public boolean save( Connection connection,
                         ReleaseGroup album, String artistId) throws SQLException{
        String sql = """
                INSERT INTO albums (id, artist_id, title, first_release_date, primary_type)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, album.getId());
            statement.setString(2, artistId);
            statement.setString(3, album.getTitle());
            statement.setString(4, album.getFirstReleaseDate());
            statement.setString(5, album.getPrimaryType());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public int saveAll(Connection connection, List<ReleaseGroup> albums,
                       String artistId) throws SQLException{
        int savedAlbums = 0;

        for (ReleaseGroup album : albums) {
            if (save(connection, album, artistId)) {
                savedAlbums++;
            }
        }
        return savedAlbums;
    }
}
