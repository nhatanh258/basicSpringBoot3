package practiceAPIspring.managingUsers.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContextException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Repository


public class UserService {



    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private UserRepo userRepo;// dung khi truy van native
    private RoleRepo roleRepo;


    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        try {
            return this.userRepo.findAll().stream().map(
                    userRespon -> new UserMapper(roleMapper).toUserResponse(userRespon)
            ).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error get all users: " + e.getMessage(), e);
        }
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

    @Transactional(readOnly = true)
    public Optional<UserResponse> get(String id) {
        try {
            User user = this.userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            return Optional.ofNullable(new UserMapper(roleMapper).toUserResponse(user));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error get: " + e.getMessage(), e);
        }
    }
    /// //////////////////////////////////////


    public User store(UserStoreRequest request) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while saving the entity: " + e.getMessage(), e);
        }
    }



    public User update(String id, UserUpdateRequest userUpdateRequest) {
        User result = User.builder()
                .fullName(userUpdateRequest.getFullName())
                .email(userUpdateRequest.getEmail())
                .numberId(userUpdateRequest.getNumberId())
                .phoneNumber(userUpdateRequest.getPhoneNumber())
                .build();

        var roles = roleRepo.findAllById(Collections.singleton(id));
        result.setRoles(new HashSet<>(roles));
        User entity = userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found : " + id));
        NullAwareBeanUtils.copyNonNullProperties(result, entity);

        return this.userRepo.save(result);
    }


    public User getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = this.userRepo.findByEmail(email).orElseThrow(() ->
                new ApplicationContextException(StatusMessage.NOT_FOUND));
        return user;
    }


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

}
