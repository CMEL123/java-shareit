package shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDto {
    Long id;
    String name;
    @Email(message = "не валидный email")
    String email;
}