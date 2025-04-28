package practiceAPIspring.managingUsers.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.config.Pagination;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.User.UserRequest;
import practiceAPIspring.managingUsers.dto.request.User.UserStoreRequest;
import practiceAPIspring.managingUsers.dto.request.User.UserUpdateRequest;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponListObject;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.mapper.UserMapper;
import practiceAPIspring.managingUsers.service.UserService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Validated

public class UserController {
    public final UserService userService;
    private RoleMapper roleMapper;


    @RequestMapping("/user1")
    public Map<String, Object> user(OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();
        return oauth2User.getAttributes();
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponListObject<?> getList(){
        return new ResponListObject<>(StatusMessage.SUCCESS, userService.getAll());
    }


    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> get(@PathVariable String id){
        return new ResponseObject<>(HttpStatus.OK.toString(), userService.get(id));
    }

    @GetMapping("/page")
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> getPages(@ModelAttribute UserRequest request){
        return new Pagination<>(userService.getPages(request),
                new UserResponse(),  new UserMapper(roleMapper)::toUserResponse).handlePaginate();
    }
    /// /////////////////////////////////////
    @PostMapping("/register")// tao ban ghi moi
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> store(@Validated @RequestBody UserStoreRequest userStore) {
        return new ResponseObject<>(StatusMessage.SUCCESS, new UserMapper(roleMapper)
                .toUserResponse(userService.store(userStore)));
    }

    @PutMapping("/{id}")// cap nhat lai  mot ban ghi moi hoan toan
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> update(@PathVariable String id, @Validated @RequestBody UserUpdateRequest userUpdate) {
        return new ResponseObject<>(StatusMessage.SUCCESS, new UserMapper(roleMapper)
                .toUserResponse(userService.update(id, userUpdate)));
    }

    @DeleteMapping("/{id}")//xoa di ban ghi da tao
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> destroy(@PathVariable  String id) {
        return new ResponseObject<>(StatusMessage.SUCCESS, new UserMapper(roleMapper).
                toUserResponse(userService.destroy(id)));
    }

    @GetMapping("/myInfor")
    public ResponseObject<?> getMyInfor(){
        return new ResponseObject<>(StatusMessage.SUCCESS, new UserMapper(roleMapper)
                .toUserResponse(userService.getMyInfor()));
    }

}
