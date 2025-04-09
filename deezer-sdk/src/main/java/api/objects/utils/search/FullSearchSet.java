package api.objects.utils.search;

import api.PartialSearchResponse;
import api.objects.playables.*;
import api.objects.utils.User;

public record FullSearchSet(
        PartialSearchResponse<Album> albumResponse,
        PartialSearchResponse<Artist> artistResponse,
        PartialSearchResponse<Playlist> playlistResponse,
        PartialSearchResponse<TrackSearch> trackResponse,
        PartialSearchResponse<User> userResponse
) {
}
