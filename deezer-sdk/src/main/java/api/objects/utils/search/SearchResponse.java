package api.objects.utils.search;

import java.util.ArrayList;
import java.util.List;

public record SearchResponse<T>(
        List<T> data
) {
    public SearchResponse() {
        this(new ArrayList<>());
    }
}
