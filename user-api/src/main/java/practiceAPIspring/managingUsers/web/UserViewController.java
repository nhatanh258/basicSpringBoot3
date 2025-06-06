package practiceAPIspring.managingUsers.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import practiceAPIspring.managingUsers.dto.request.User.UserStoreRequest;
import practiceAPIspring.managingUsers.dto.response.user.UserResponse;
import practiceAPIspring.managingUsers.mapper.RoleMapper;
import practiceAPIspring.managingUsers.mapper.UserMapper;
import practiceAPIspring.managingUsers.service.UserService;


@Slf4j
@Controller
@RequestMapping
@AllArgsConstructor
@Validated
public class UserViewController {
    public final UserService userService;
    private RoleMapper roleMapper;

    @PostMapping("/register")
    public String registerFormSubmit(
            @ModelAttribute("user") @Validated UserStoreRequest userStore,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "errorRegister";  // Lỗi validate form
        }

        try {
            UserResponse response = new UserMapper(roleMapper)
                    .toUserResponse(userService.store(userStore));

            model.addAttribute("successMessage", "User registered successfully!");
            model.addAttribute("jsonResponse", response);
            return "table";

        } catch (DataIntegrityViolationException e) {
            // Bắt lỗi vi phạm ràng buộc DB, ví dụ duplicate key
            model.addAttribute("errorMessage", "Duplicate entry: The number ID or email already exists.");
            return "errorRegister"; // Trả về trang lỗi
        } catch (Exception e) {
            // Bắt các lỗi khác
            model.addAttribute("errorMessage", "Something went wrong: " + e.getMessage());
            return "errorRegister";
        }
    }

}
