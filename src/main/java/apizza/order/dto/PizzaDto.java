package apizza.order.dto;

import apizza.order.validation.NullOrNotBlank;
import apizza.order.validation.group.PatchCandidateGroup;
import apizza.order.validation.group.PostCandidateGroup;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PizzaDto {

    @Null(groups = {PostCandidateGroup.class, PatchCandidateGroup.class})
    private UUID id;

    @NotNull(groups = PostCandidateGroup.class)
    @NotBlank(groups = PostCandidateGroup.class)
    @NotBlank(groups = {PostCandidateGroup.class, PatchCandidateGroup.class})
    private String name;

    @NotNull(groups = PostCandidateGroup.class)
    @NotBlank(groups = PostCandidateGroup.class)
    @NullOrNotBlank(groups = PatchCandidateGroup.class)
    private String description;

    @NotNull(groups = PostCandidateGroup.class)
    @PositiveOrZero(groups = {PostCandidateGroup.class, PatchCandidateGroup.class})
    private Double price;

    private Boolean available;

}
