package api.objects.utils.search;
import api.objects.DeezerEntity;

import java.util.List;

public class SearchResponse<T extends DeezerEntity> {
    private List<T> data;
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
}
