package hr.dario.rockmusic.service;

import hr.dario.rockmusic.database.DatabaseConnection;
import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.repository.AlbumRepository;
import hr.dario.rockmusic.repository.ArtistRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DatabaseService {
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    public DatabaseService() {
        this.artistRepository = new ArtistRepository();
        this.albumRepository = new AlbumRepository();
    }

    public SaveResult saveArtistWithAlbums(
            Artist artist,
            List<ReleaseGroup> albums
    ) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                boolean artistSaved = artistRepository.save(connection, artist);
                int savedAlbums = albumRepository.saveAll(connection, albums, artist.getId());

                connection.commit();

                return new SaveResult(artistSaved, savedAlbums);

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
}
