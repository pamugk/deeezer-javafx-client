package api.objects.utils;

import java.net.URL;
import java.util.Date;

public record User(
        long id,
        String type,
        String name,
        String lastname,
        String firstname,
        String email,
        int status,
        Date birthday,
        Date inscription_date,
        String gender,
        URL link,
        URL picture,
        URL picture_small,
        URL picture_medium,
        URL picture_big,
        URL picture_xl,
        String country,
        String lang,
        boolean is_kid,
        String explicit_content_level,
        String[] explicit_content_levels_available,
        URL tracklist
) {
}
