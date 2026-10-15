package hr.dario.rockmusic.service;

import hr.dario.rockmusic.model.Artist;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.model.ReleaseGroupResponse;

import java.sql.SQLException;
import java.util.List;

public class MusicService {
    private final DatabaseService databaseService;
    private final AlbumPaginationService paginationService;

    public MusicService(DatabaseService databaseService,
                        AlbumPaginationService paginationService) {
        this.databaseService = databaseService;
        this.paginationService = paginationService;
    }

    public List<ReleaseGroup> getAlbumsForArtist(Artist artist)
            throws SQLException {

        String artistId = artist.getId();

        if (databaseService.areAlbumsFetched(artistId)) {
            return databaseService.getSavedAlbums(artistId);
        }

        ReleaseGroupResponse response = paginationService.getAllAlbumsByArtist(artistId);

        if (response == null || response.getReleaseGroups() == null) {
            return null;
        }


        List<ReleaseGroup> albums = response.getReleaseGroups();
        databaseService.saveArtistWithAlbums(artist, albums);
        return albums;
    }
}