package practiceAPIspring.managingUsers.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.config.LifeCycleUser;
import practiceAPIspring.managingUsers.config.Pagination;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.User.UserRequest;
import practiceAPIspring.managingUsers.dto.request.User.UserStoreRequest;
import practiceAPIspring.managingUsers.dto.request.User.UserUpdateRequest;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponListObject;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.mapper.UserMapper;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.service.DynamicSchedulerService;
import practiceAPIspring.managingUsers.service.UserService;


import java.util.List;
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
    @GetMapping("/evitEmpty/{id}")// cap nhat lai  mot ban ghi moi hoan toan
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> updateByCACHEeVIT(@PathVariable String id) {
         userService.deleteUser(id);
         return new ResponseObject<>(StatusMessage.SUCCESS, null);
    }

    @PutMapping("/evit/{id}")// cap nhat lai  mot ban ghi moi hoan toan
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> updateByCACHEeVIT(@PathVariable String id, @Validated @RequestBody UserUpdateRequest userUpdate) {
        return new ResponseObject<>(StatusMessage.SUCCESS, userService.updateUser(id, userUpdate));
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


    /// //////////////////////
    @Autowired
    private LifeCycleUser user;

    @GetMapping("/set/{name}")
    public String setName(@PathVariable String name) {
        user.setName(name);
        return "Đã set tên: " + name;
    }

    @GetMapping("/get")
    public String getName() {
        return "Tên hiện tại: " + user.getName();
    }
    ///
    @PostMapping("/tryRegister")// tao ban ghi moi
    @ResponseStatus(HttpStatus.OK)
    public ResponseObject<?> TrySore(@Validated @RequestBody UserStoreRequest userStore) {
        return new ResponseObject<>(StatusMessage.SUCCESS, new UserMapper(roleMapper)
                .toUserResponse(userService.saveTransactional(userStore)));
    }

    /// / scheduled future
    @Autowired
    private DynamicSchedulerService schedulerService;

    public void SchedulerController(DynamicSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
        this.schedulerService.startScheduler(); // khởi động khi app chạy
    }

// / test voi scheduler

    @GetMapping("/cron")
    public void getCurrentCron() {
        schedulerService.startScheduler();
    }

    // cron update time future
    @PostMapping("/update")
    public String updateCron(@RequestParam String cron) {
        schedulerService.updateCron(cron);
        return "Updated cron to: " + cron;
    }

    @PostMapping("/stop")
    public String stop() {
        schedulerService.stopScheduler();
        return "Scheduler stopped";
    }
    /// ////////////////////////////////////
    // dung hql manual query

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/name-contains/{namePart}")
    public ResponseEntity<List<UserResponse>> getUsersByNameContains(@PathVariable String namePart) {
        return ResponseEntity.ok(userService.getUsersByNameContains(namePart));
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserResponse>> getUsersByRoleName(@PathVariable String roleName) {
        return ResponseEntity.ok(userService.getUsersByRoleName(roleName));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<UserResponse>> getUsersByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(userService.getUsersByEventId(eventId));
    }

    @GetMapping("/sorted/asc")
    public ResponseEntity<List<UserResponse>> getAllUsersSortedAsc() {
        return ResponseEntity.ok(userService.getAllUsersSortedAsc());
    }

    @GetMapping("/sorted/desc")
    public ResponseEntity<List<UserResponse>> getAllUsersSortedDesc() {
        return ResponseEntity.ok(userService.getAllUsersSortedDesc());
    }
    @GetMapping("/role2/{roleName}")
    public ResponseEntity<List<UserResponse>> findUsersByRoleName(@PathVariable String roleName) {
        return ResponseEntity.ok(userService.findUsersByRoleName(roleName));
    }

    @GetMapping("/id-phone")
    public ResponseEntity<UserResponse> findByNumberIdAndPhoneNumber(
            @RequestParam String numberId, @RequestParam String phoneNumber) {
        return ResponseEntity.ok(userService.findByNumberIdAndPhoneNumber(numberId, phoneNumber));
    }

    @GetMapping("/with-or-without-roles/native")
    public ResponseEntity<List<UserResponse>> findAllUsersWithOrWithoutRolesNative() {
        return ResponseEntity.ok(userService.findAllUsersWithOrWithoutRolesNative());
    }

    @GetMapping("/with-or-without-roles")
    public ResponseEntity<List<UserResponse>> findAllUsersWithOrWithoutRoles() {
        return ResponseEntity.ok(userService.findAllUsersWithOrWithoutRoles());
    }

    @GetMapping("/roles-with-or-without-users")
    public ResponseEntity<List<UserResponse>> findRolesWithOrWithoutUsers() {
        return ResponseEntity.ok(userService.findRolesWithOrWithoutUsers());
    }

    @GetMapping("/all-users-and-roles")
    public ResponseEntity<List<UserResponse>> findAllUsersAndRoles() {
        return ResponseEntity.ok(userService.findAllUsersAndRoles());
    }

    @GetMapping("/cross-join-users-and-roles")
    public ResponseEntity<List<UserResponse>> crossJoinUsersAndRoles() {
        return ResponseEntity.ok(userService.crossJoinUsersAndRoles());
    }

    @GetMapping("/subordinates")
    public ResponseEntity<List<UserResponse>> findAllSubordinatesOfManager(@RequestParam String managerEmail) {
        return ResponseEntity.ok(userService.findAllSubordinatesOfManager(managerEmail));
    }

    @GetMapping("/sorted/native/asc")
    public ResponseEntity<List<UserResponse>> findAllUsersSortedByFullNameAscNative() {
        return ResponseEntity.ok(userService.findAllUsersSortedByFullNameAscNative());
    }

    @GetMapping("/sorted/native/desc")
    public ResponseEntity<List<UserResponse>> findAllUsersSortedByFullNameDescNative() {
        return ResponseEntity.ok(userService.findAllUsersSortedByFullNameDescNative());
    }

    @GetMapping("/paging/native")
    public ResponseEntity<Page<UserResponse>> findAllUsersWithPagingNative(
            @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.findAllUsersWithPagingNative(pageable));
    }

    @GetMapping("/paging/hql")
    public ResponseEntity<Page<UserResponse>> findAllUsersWithPagingHQL(
            @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.findAllUsersWithPagingHQL(pageable));
    }


    @GetMapping("/phone/starts-with-123/native")
    public ResponseEntity<List<UserResponse>> findAllUsersWithPhoneNumberStartingWith123Native() {
        return ResponseEntity.ok(userService.findAllUsersWithPhoneNumberStartingWith123Native());
    }

    @GetMapping("/phone/starts-with-123/hql")
    public ResponseEntity<List<UserResponse>> findAllUsersWithPhoneNumberStartingWith123HQL() {
        return ResponseEntity.ok(userService.findAllUsersWithPhoneNumberStartingWith123HQL());
    }





    /// /////////////////////////////////
    //phan trang voi slice
    @GetMapping("/users/slice")
    public Slice<User> getUsersSlice(
            @RequestParam String fullName,
            @RequestParam int page,
            @RequestParam int size) {
        return userService.getUsersSlice(fullName, page, size);
    }
    @GetMapping("/slice")
    public Slice<User> getSlice(
            @RequestParam int page,
            @RequestParam int size) {
        return userService.getUsersSlice2(page, size);
    }

}
