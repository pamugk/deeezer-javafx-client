package api.objects.utils.search;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.List;

public record SearchResponse<T>(
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        List<T> data) {
}
