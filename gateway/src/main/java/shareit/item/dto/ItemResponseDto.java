package shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemResponseDto {
    @NotBlank(message = "Имя должно быть указано")
    String name;

    @NotBlank(message = "Описание должно быть указано")
    String description;

    @NotNull(message = "Доступность должна быть указана")
    Boolean available;

    Long requestId;
}