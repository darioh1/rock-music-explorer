package hr.dario.rockmusic.service;

import hr.dario.rockmusic.model.ReleaseGroup;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class AlbumService {
    public void sortByReleaseDate(List<ReleaseGroup> albums) {
        albums.sort(
                Comparator.comparing(
                        ReleaseGroup::getFirstReleaseDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
    }
    public List<ReleaseGroup> getRegularAlbums(List<ReleaseGroup> albums) {
        List<ReleaseGroup> regularAlbums = new ArrayList<>();
        for (ReleaseGroup album : albums) {
            if (album.getSecondaryTypes() == null || !album.getSecondaryTypes().contains("Compilation")){
                regularAlbums.add(album);
            }
        }
        return regularAlbums;
    }
}
