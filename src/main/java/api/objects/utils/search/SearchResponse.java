package api.objects.utils.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse<T> {
    private List<T> data;
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data == null ? new ArrayList<>() : data; }

    public SearchResponse() {
        data = new ArrayList<>();
    }
}
