package api;

import api.objects.utils.search.SearchResponse;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

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

    URL getPrev() { return prev; }
    void setPrev(URL prev) { this.prev = prev; }
    URL getNext() { return next; }
    void setNext(URL next) { this.next = next; }

    public boolean hasPrev() { return prev != null; }
    public boolean hasNext() { return next != null; }

    public int getTotal() { return total; }
    void setTotal(int total) { this.total = total; }

    Type getType() { return type; }
    void setType(Type type) { this.type = type; }
}
