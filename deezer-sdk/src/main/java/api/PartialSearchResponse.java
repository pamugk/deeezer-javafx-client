package api;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public record PartialSearchResponse<T>(
        List<T> data,
        Type type,
        int total,
        URL prev,
        URL next
){
    public PartialSearchResponse(Type type) {
        this(Collections.emptyList(), type, 0, null, null);
    }

    public boolean hasPrev() { return prev != null; }
}
