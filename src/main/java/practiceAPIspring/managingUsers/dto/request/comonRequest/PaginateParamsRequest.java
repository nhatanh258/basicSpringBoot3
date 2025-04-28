package practiceAPIspring.managingUsers.dto.request.comonRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginateParamsRequest {
    @NotNull
    @Min(1)
    public int page;

    @NotNull
    @Min(5)
    public int limit;

    public String search;

    public Boolean isPaginate = false;
}