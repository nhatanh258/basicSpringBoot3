package practiceAPIspring.managingUsers.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.permission.PermissionRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponListObject;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;
import practiceAPIspring.managingUsers.service.PermissionService;

import java.util.UUID;

@RestController
@RequestMapping("api/permissions")
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ResponseObject<?> create(@Validated @RequestBody PermissionRequest request){
        return new ResponseObject<>(StatusMessage.SUCCESS, permissionService.create(request));
    }
    @GetMapping
    ResponListObject<?> getAll(){
        return new ResponListObject<>(StatusMessage.SUCCESS, permissionService.getAll());
    }

    @DeleteMapping("/{permission}")
    ResponseObject<Void> delete(@PathVariable String permission){
        permissionService.delete(permission);
        return new ResponseObject<>(StatusMessage.SUCCESS, null);
    }

}
