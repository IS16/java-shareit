package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    private JacksonTester<UserDto> json;
    private Validator validator;

    public UserDtoTest(@Autowired JacksonTester<UserDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testJsonUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "admin", "admin@shareit.ru");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("admin");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("admin@shareit.ru");
    }

    @Test
    void shouldThrowNameNotBlankValidationError() {
        UserDto userDto = new UserDto(1L, " ", "admin@shareit.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }

    @Test
    void shouldThrowNameNullValidationError() {
        UserDto userDto = new UserDto(1L, null, "admin@shareit.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }

    @Test
    void shouldThrowEmailNotBlankValidationError() {
        UserDto userDto = new UserDto(1L, "admin", " ");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }

    @Test
    void shouldThrowEmailNullValidationError() {
        UserDto userDto = new UserDto(1L, "admin", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must not be blank'");
    }

    @Test
    void shouldThrowEmailInvalidValidationError() {
        UserDto userDto = new UserDto(1L, "admin", "abc");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='must be a well-formed email address'");
    }
}
