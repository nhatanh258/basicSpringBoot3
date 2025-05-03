package practiceAPIspring.managingUsers.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateUserSuccessfullyWithBuilder() {
        User user = User.builder()
                .email("test@example.com")
                .fullName("Test User")
                .numberId("123456789")
                .phoneNumber("0123456789")
                .password("secret123")
                .build();

        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertEquals("secret123", user.getPassword());
    }

    @Test
    void shouldFailValidationWhenEmailIsNull() {
        User user = User.builder()
                .email(null)
                .password("secret123")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasEmailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertTrue(hasEmailViolation);
    }

    @Test
    void shouldFailValidationWhenPasswordIsNull() {
        User user = User.builder()
                .email("test@example.com")
                .password(null)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        boolean hasPasswordViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        assertTrue(hasPasswordViolation);
    }

    @Test
    void shouldPassValidationWhenEmailAndPasswordAreProvided() {
        User user = User.builder()
                .email("test@example.com")
                .password("pass")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        User user = new User();
        user.setEmail("abc@xyz.com");
        user.setFullName("Jane Doe");
        user.setNumberId("99999");
        user.setPhoneNumber("0999999999");
        user.setPassword("password");

        assertEquals("abc@xyz.com", user.getEmail());
        assertEquals("Jane Doe", user.getFullName());
        assertEquals("99999", user.getNumberId());
        assertEquals("0999999999", user.getPhoneNumber());
        assertEquals("password", user.getPassword());
    }
}
