package api.objects.utils.search;

import java.util.List;

public record SearchResponse<T>(
        List<T> data
) {
}
