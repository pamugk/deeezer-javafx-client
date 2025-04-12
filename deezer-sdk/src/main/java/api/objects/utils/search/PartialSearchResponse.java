package api.objects.utils.search;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.net.URL;
import java.util.List;

public record PartialSearchResponse<T>(
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        List<T> data,
        int total,
        URL prev,
        URL next
){
    public boolean hasPrev() { return prev != null; }
}
