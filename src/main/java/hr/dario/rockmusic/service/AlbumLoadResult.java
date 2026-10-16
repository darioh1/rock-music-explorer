package hr.dario.rockmusic.service;

import hr.dario.rockmusic.model.ReleaseGroup;

import java.util.List;

public record AlbumLoadResult(
        List<ReleaseGroup> albums,
        AlbumSource source
) {
}
