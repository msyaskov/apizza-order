package apizza.order.dto;

import apizza.order.entity.OrderStatus;
import apizza.order.validation.group.PatchCandidateGroup;
import apizza.order.validation.group.PostCandidateGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @Null(groups = {PostCandidateGroup.class, PatchCandidateGroup.class})
    private UUID id;

    @Null(groups = {PostCandidateGroup.class, PatchCandidateGroup.class})
    private UUID userId;

    @Null(groups = {PostCandidateGroup.class, PatchCandidateGroup.class})
    private LocalDateTime date;

    @Null(groups = {PostCandidateGroup.class, PatchCandidateGroup.class})
    private Double price;

    @Null(groups = PostCandidateGroup.class)
    @NotNull(groups = PatchCandidateGroup.class)
    private OrderStatus status;

    @NotEmpty(groups = PostCandidateGroup.class)
    @Null(groups = PatchCandidateGroup.class)
    private Collection<UUID> pizzas;

}
