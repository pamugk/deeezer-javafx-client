package api.objects.comments;

import api.objects.DeezerEntity;
import api.objects.utils.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment extends DeezerEntity {
    private String text;
    private long date;
    private Object object;
    private User author;
}
