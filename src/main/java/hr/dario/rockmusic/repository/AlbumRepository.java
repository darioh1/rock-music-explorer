package hr.dario.rockmusic.repository;

import hr.dario.rockmusic.model.ReleaseGroup;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class AlbumRepository {
    public boolean save( Connection connection,
                         ReleaseGroup album, String artistId) throws SQLException{
        String sql = """
                INSERT INTO albums (id, artist_id, title, first_release_date, primary_type, secondary_types)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, album.getId());
            statement.setString(2, artistId);
            statement.setString(3, album.getTitle());
            statement.setString(4, album.getFirstReleaseDate());
            statement.setString(5, album.getPrimaryType());
            List<String> secondaryTypes = album.getSecondaryTypes();

            if (secondaryTypes != null) {
                Array sqlArray = connection.createArrayOf("text",
                        secondaryTypes.toArray(new String[0]));
                statement.setArray(6, sqlArray);
            } else {
                statement.setNull(6, Types.ARRAY);
            }
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

    public List<ReleaseGroup> findByArtistId(Connection connection, String artistId)
        throws SQLException {
        List<ReleaseGroup> albums = new ArrayList<>();

        String sql = """
                SELECT id, title, first_release_date, primary_type, secondary_types
                FROM albums
                WHERE artist_id = ?
                ORDER BY first_release_date
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, artistId);

            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    ReleaseGroup album = new ReleaseGroup();
                    album.setId(resultSet.getString("id"));
                    album.setTitle(resultSet.getString("title"));
                    album.setFirstReleaseDate(resultSet.getString("first_release_date"));
                    album.setPrimaryType(resultSet.getString("primary_type"));
                    Array sqlArray = resultSet.getArray("secondary_types");

                    if (sqlArray != null) {
                        String[] types = (String[]) sqlArray.getArray();
                        album.setSecondaryTypes(Arrays.asList(types));
                        sqlArray.free();

                    }

                    albums.add(album);
                }
            }
        }
        return albums;
    }
}
