package shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentCreateDto {
    @NotBlank(message = "Текст комментария должен быть указан")
    String text;
    LocalDateTime created = LocalDateTime.now();
}
