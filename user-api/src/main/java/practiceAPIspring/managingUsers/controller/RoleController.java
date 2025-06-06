package practiceAPIspring.managingUsers.controller;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.role.RoleRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponListObject;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;
import practiceAPIspring.managingUsers.service.RoleService;

@RestController
@RequestMapping("api/roles")
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class RoleController {
    RoleService roleService;

    @PostMapping
    ResponseObject<?> create(@Validated @RequestBody RoleRequest roleRequest){
        return new ResponseObject<>(StatusMessage.SUCCESS, roleService.create(roleRequest));
    }
    @GetMapping
    ResponListObject<?> getAll(){
        return new ResponListObject<>(StatusMessage.SUCCESS, roleService.getAll());
    }

    @DeleteMapping("/{role}")
    ResponseObject<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return new ResponseObject<>(StatusMessage.SUCCESS, null);
    }
}
