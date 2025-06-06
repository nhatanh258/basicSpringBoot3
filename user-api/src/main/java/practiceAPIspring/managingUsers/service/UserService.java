package practiceAPIspring.managingUsers.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContextException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.request.User.UserRequest;
import practiceAPIspring.managingUsers.dto.request.User.UserStoreRequest;
import practiceAPIspring.managingUsers.dto.request.User.UserUpdateRequest;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.mapper.UserMapper;
import practiceAPIspring.managingUsers.model.Role;
import practiceAPIspring.managingUsers.model.User;
import practiceAPIspring.managingUsers.repository.RoleRepo;
import practiceAPIspring.managingUsers.repository.UserRepo;
import practiceAPIspring.managingUsers.utils.NullAwareBeanUtils;


import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Repository


public class UserService {



    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private UserRepo userRepo;// dung khi truy van native
    private RoleRepo roleRepo;
    private final UserMapper userMapper;


    @Transactional
    public List<UserResponse> getAll() {
            var listUsers = this.userRepo.findAll();
            return listUsers.stream().map(
                    userRespon -> new UserMapper(roleMapper).toUserResponse(userRespon)
            ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> getPages(UserRequest request) {
        try {
            return userRepo.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error get all users: " + e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(value = "users", key = "#id")
    public Optional<UserResponse> get(String id) {
        try {
            Thread.sleep(4000);
            User user = this.userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            return Optional.ofNullable(new UserMapper(roleMapper).toUserResponse(user));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error get: " + e.getMessage(), e);
        }
    }
    // xoa du lieu trong cache cu
    @CacheEvict(value = "users", key = "#userId")// keyid rat quan trong no phai trung voi id trong cache user truoc thi moi xoa dc
    public void deleteUser(String userId) {
    }
    // co the dung cacheput nhung lau hon cache
    // xao toan bo cache
    @CacheEvict(value = "users", allEntries = true)
    public void deleteAllUsers() {
    }
    //xoa cache theo conditions //#result: là giá trị trả về của method updateUser(...).
    @CacheEvict(value = "users", condition = "#result != null && #result.email != null", key = "#id")
    public UserResponse updateUser(String id, UserUpdateRequest userUpdateRequest) {
        try {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            user.setFullName(userUpdateRequest.getFullName());
            user.setEmail(userUpdateRequest.getEmail());
            user.setNumberId(userUpdateRequest.getNumberId());
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
            User updatedUser = userRepo.save(user);
            return new UserMapper(roleMapper).toUserResponse(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error update user: " + e.getMessage(), e);
        }
    }

    /// //////////////////////////////////////

    @Transactional //Nếu store chạy thành công → commit //Nếu ném exception → rollback
    public User store(UserStoreRequest request) {
            System.out.println("====================================");
            User result = User.builder()
                    .fullName(request.getFullName())
                    .numberId(request.getNumberId())
                    .phoneNumber(request.getPhoneNumber())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            // tao voi role
            Role role = roleRepo.findByName("USER")// tra ve 1 role
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            result.setRoles(roles);
            //event se duoc nguoi dung tao sau khi dang nhap thanh cong
            return this.userRepo.save(result);
    }

    @Autowired
    private NullAwareBeanUtils nullAwareBeanUtils;

//    @Transactional(propagation = Propagation.SUPPORTS)
//@Transactional(propagation = Propagation.NOT_SUPPORTED)
//@Transactional(propagation = Propagation.MANDATORY)
@Transactional(propagation = Propagation.NEVER)
//    @Transactional
    public User update(String id, UserUpdateRequest userUpdateRequest) {
        User result = User.builder()
                .fullName(userUpdateRequest.getFullName())
                .email(userUpdateRequest.getEmail())
                .numberId(userUpdateRequest.getNumberId())
                .phoneNumber(userUpdateRequest.getPhoneNumber())
                .build();

        User entity = userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found : " + id));
        nullAwareBeanUtils.copyNonNullProperties(result, entity);// CÓ TRANSACTIONAL

        return this.userRepo.save(entity);
    }


    public User getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = this.userRepo.findByEmail(email).orElseThrow(() ->
                new ApplicationContextException(StatusMessage.NOT_FOUND));
        return user;
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



//    @Scheduled(cron = "50 0 0 * * ?")
//    public void annaence(){
//        String formattedDateTime = LocalDateTime.now().format(formatter);
//        System.out.println("thong bao chay sau moi 50 giay timenow :" + formattedDateTime);
//    }
//    @Scheduled(fixedDelay = 4000)
//    public void fixDelay(){
//        System.out.println("thong bao chay sau moi 4000ms of old task time now: " + LocalDateTime.now().format(formatter));
//    }
//    @Scheduled(fixedRate = 5000)
//    public void taskFixRate(){
//        String formattedDateTime = LocalDateTime.now().format(formatter);
//        System.out.println("FIXED RATE chạy độc lập sau 5000ms time now: " + formattedDateTime);
//    }
//    @Scheduled(initialDelay = 3000,fixedRate = 5000)
//    public void taskInitialFixRate(){
//        String formattedDateTime = LocalDateTime.now().format(formatter);
//        System.out.println("FĩXED RATE and initial Task each 3000ms time now:" + formattedDateTime);
//    }

    public User destroy(String id) {
        try {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            this.userRepo.deleteById(id);
            return user;
        }catch (Exception e){
            throw  new IllegalCallerException("xoa that bai");
        }
    }
    ///  thu transactional trong save user
    @Transactional
    public User saveTransactional(UserStoreRequest request) {
            String temasss = request.getPassword();
            System.out.println("ass mk la: " + temasss);
            System.out.println("ass fullname la: " + request.getFullName());


            System.out.println("====================================");
            User result = User.builder()
                    .fullName(request.getFullName())
                    .numberId(request.getNumberId())
                    .phoneNumber(request.getPhoneNumber())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            // tao voi role

            Role role = roleRepo.findByName("USER")// tra ve 1 role
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            result.setRoles(roles);

            //event se duoc nguoi dung tao sau khi dang nhap thanh cong

            System.out.println("user have already created ! ");
            return this.userRepo.save(result);
    }
/// //////////////////////////////////////////
    // // dung hql manual query

@Cacheable(value = "users", key = "#email")
public UserResponse getUserByEmail(String email)
{
    User user = userRepo.findByEmailManual(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    return userMapper.toUserResponse(user);
}

    public List<UserResponse> getUsersByNameContains(String namePart) {
        return userRepo.findByFullNameContains(namePart).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRoleName(String roleName) {
        return userRepo.findByRoleName(roleName).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByEventId(Long eventId) {
        return userRepo.findUsersByEventId(eventId).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsersSortedAsc() {
        return userRepo.findAllUsersSortedByFullNameAsc().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsersSortedDesc() {
        return userRepo.findAllUsersSortedByFullNameDesc().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
    public List<UserResponse> findUsersByRoleName(String roleName) {
        return userRepo.findUsersByRoleName(roleName).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
    @Transactional  //cực kỳ quan trọng! Nó giữ nguyên Hibernate session để sử dụng cache nội bộ của session.
    public UserResponse findByNumberIdAndPhoneNumber(String numberId, String phoneNumber) {
        User user = userRepo.findByNumberIdAndPhoneNumber(numberId, phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with given ID and phone number"));
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> findAllUsersWithOrWithoutRolesNative() {
        return userRepo.findAllUsersWithOrWithoutRolesNative().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findAllUsersWithOrWithoutRoles() {
        return userRepo.findAllUsersWithOrWithoutRoles().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findRolesWithOrWithoutUsers() {
        return userRepo.findRolesWithOrWithoutUsers().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findAllUsersAndRoles() {
        return userRepo.findAllUsersAndRoles().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> crossJoinUsersAndRoles() {
        return userRepo.crossJoinUsersAndRoles().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findAllSubordinatesOfManager(String managerEmail) {
        return userRepo.findAllSubordinatesOfManager(managerEmail).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }



    public List<UserResponse> findAllUsersSortedByFullNameAscNative() {
        return userRepo.findAllUsersSortedByFullNameAscNative().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findAllUsersSortedByFullNameDescNative() {
        return userRepo.findAllUsersSortedByFullNameDescNative().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public Page<UserResponse> findAllUsersWithPagingNative(Pageable pageable) {
        return userRepo.findAllUsersWithPagingNative(pageable)
                .map(userMapper::toUserResponse);
    }

    public Page<UserResponse> findAllUsersWithPagingHQL(Pageable pageable) {
        return userRepo.findAllUsersWithPagingHQL(pageable)
                .map(userMapper::toUserResponse);
    }


    public List<UserResponse> findAllUsersWithPhoneNumberStartingWith123Native() {
        return userRepo.findAllUsersWithPhoneNumberStartingWith123Native().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findAllUsersWithPhoneNumberStartingWith123HQL() {
        return userRepo.findAllUsersWithPhoneNumberStartingWith123HQL().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }





    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //dung slice lấy trang theo ten
    public Slice<User> getUsersSlice(String fullName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepo.findByFullNameContaining(fullName, pageable);
    }

    //khong theo ten
    public Slice<User> getUsersSlice2(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepo.findAllBy(pageable);
    }
}
