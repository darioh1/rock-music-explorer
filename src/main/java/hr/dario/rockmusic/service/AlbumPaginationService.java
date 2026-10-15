package hr.dario.rockmusic.service;

import hr.dario.rockmusic.api.MusicBrainzClient;
import hr.dario.rockmusic.model.ReleaseGroup;
import hr.dario.rockmusic.model.ReleaseGroupResponse;

import java.util.ArrayList;
import java.util.List;

public class AlbumPaginationService {
    private final MusicBrainzClient client;
    private final long delayMillis;

    public AlbumPaginationService(MusicBrainzClient client) {
        this(client, 1100);
    }

    public AlbumPaginationService(MusicBrainzClient client, long delayMillis){
        this.client = client;
        this.delayMillis = delayMillis;
    }

    public ReleaseGroupResponse getAllAlbumsByArtist(String artistId) {
        int limit = 100;
        int offset = 0;

        List<ReleaseGroup> allAlbums = new ArrayList<>();

        while (true) {
            ReleaseGroupResponse response =
                    client.getAlbumsByArtist(artistId, limit, offset);

            if (response == null || response.getReleaseGroups() == null) {
                return null;
            }

            List<ReleaseGroup> currentPage = response.getReleaseGroups();

            if (currentPage.isEmpty()) {
                if (offset < response.getCount()) {
                    return null;
                }
                break;
            }

            allAlbums.addAll(currentPage);
            offset += currentPage.size();

            if (offset >= response.getCount()) {
                break;
            }

            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        ReleaseGroupResponse result = new ReleaseGroupResponse();
        result.setReleaseGroups(allAlbums);
        result.setCount(allAlbums.size());

        return result;
    }
}
