package api.objects.comments;

import api.objects.utils.User;

public record Comment(
        long id,
        String type,
        String text,
        long date,
        Object object,
        User author
) {
}
