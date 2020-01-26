package api;

import api.objects.utils.search.SearchResponse;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

@Getter
@Setter
public class PartialSearchResponse<T> extends SearchResponse<T> {
    private Type type;
    private int total;
    private URL prev;
    private URL next;

    PartialSearchResponse(Type type) {
        this.type = type;
        setData(null);
    }

    public PartialSearchResponse(List<T> data) {
        setData(data);
    }

    public boolean hasPrev() { return prev != null; }
    public boolean hasNext() { return next != null; }
}
