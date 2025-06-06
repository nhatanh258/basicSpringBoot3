package practiceAPIspring.managingUsers.dto.response.comonResponse;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PaginateResponse<T> {
    public int page;
    public int limit;
    public long totalItems;
    public int totalPage;
    public List<T> data;

    public PaginateResponse(int page, int limit, long totalItems, int totalPage, List<T> data) {
        this.page = page;
        this.limit = limit;
        this.totalItems = totalItems;
        this.totalPage = totalPage;
        this.data = data;
    }
}
