package api.objects.utils;

import java.net.URL;

public record Genre(
        long id,
        String type,
        String name,
        URL picture,
        URL picture_small,
        URL picture_medium,
        URL picture_big,
        URL picture_xl
) {
}
