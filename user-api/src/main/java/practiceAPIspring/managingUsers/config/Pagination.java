package practiceAPIspring.managingUsers.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.comonRequest.PaginateParamsRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.PaginateResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class Pagination<T, R> {
    private List<T> data;// danh sach cac doi tuong can phan trang
    private final int PAGE = 1;
    private final int LIMIT = 10;
    private final boolean IS_PAGINATE = false;
    private Function<T, R> responseFunction;//dể chuyển đổi các đối tượng T thành R (ví dụ, từ một entity sang DTO).
    private PaginateParamsRequest paginateParams = new PaginateParamsRequest();

    public Pagination(List<T> data, R response, Function<T, R> responseFunction) {
        this.data = data != null ? data : new ArrayList<>();
        if (response != null) this.validRequestParams(response);
        this.responseFunction = responseFunction;
    }

//    public <T, R> Pagination(List<UserResponse> pages, UserResponse userResponse, Function<T,R>  toUserResponse) {
//    }


    private void validRequestParams(R request) {
        BeanUtils.copyProperties(request, this.paginateParams);// để sao chép các giá trị từ đối tượng yêu cầu (request) vào đối tượng paginateParams
        this.paginateParams.setPage(this.paginateParams.getPage() > 0 ? this.paginateParams.page : PAGE);
        this.paginateParams.setLimit(this.paginateParams.getLimit() > 0 ? this.paginateParams.getLimit() : LIMIT);
        this.paginateParams.isPaginate = true;//phan trang
    }

    private List<R> toMapperResponse(List<T> dataPage) {
        return dataPage.stream().map(responseFunction).collect(Collectors.toList());// collector chuyen doi
    }

    public ResponseObject<?> handlePaginate() {
        if (!this.paginateParams.isPaginate) {// neu khong phan trang
            return new ResponseObject<>(StatusMessage.SUCCESS, toMapperResponse(this.data));
        }

        /// //////////////////////////////////////////
        int totalPage = (int) Math.ceil((float) this.data.size() / this.paginateParams.limit);
        List<T> dataPage = new ArrayList<>();
        if (this.paginateParams.page <= totalPage && this.data.size() > 0) {
            int offset = (this.paginateParams.page - 1) * this.paginateParams.limit;
            int endIndex = Math.min(offset + this.paginateParams.limit, this.data.size());
            dataPage = this.data.subList(offset, endIndex);
        }
        return new ResponseObject<>(StatusMessage.SUCCESS, new PaginateResponse<>(this.paginateParams.page, this.paginateParams.limit, this.data.size(), totalPage, toMapperResponse(dataPage)));
    }
}
