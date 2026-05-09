package pl.skyroster.skyroster_backend.application.pilot;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.skyroster.skyroster_backend.domain.model.Pilot;
import pl.skyroster.skyroster_backend.domain.port.PilotRepository;
import pl.skyroster.skyroster_backend.generated.model.PagedPilotResponse;
import pl.skyroster.skyroster_backend.infrastructure.mappers.PilotMapper;

@Service
@RequiredArgsConstructor
public class GetPilotUseCase {

  private final PilotRepository pilotRepository;

  public PagedPilotResponse getPilots(Integer page, Integer size, String sort) {
    Pageable pageable = createPageable(page, size, sort);

    Page<Pilot> pilotsPage = pilotRepository.findAllWithRelations(pageable);

    return new PagedPilotResponse()
        .content(
            pilotsPage.getContent()
                .stream()
                .map(PilotMapper::toResponse)
                .toList()
        )
        .page(pilotsPage.getNumber())
        .size(pilotsPage.getSize())
        .totalElements(pilotsPage.getTotalElements())
        .totalPages(pilotsPage.getTotalPages())
        .first(pilotsPage.isFirst())
        .last(pilotsPage.isLast());
  }

  private Pageable createPageable(Integer page, Integer size, String sort) {
    int resolvedPage = page == null ? 0 : page;
    int resolvedSize = size == null ? 20 : Math.min(size, 100);

    Sort resolvedSort = Sort.by(Sort.Direction.ASC, "lastName");

    if (sort != null && !sort.isBlank()) {
      String[] parts = sort.split(",");
      String property = parts[0];

      Sort.Direction direction = parts.length > 1
          ? Sort.Direction.fromOptionalString(parts[1]).orElse(Sort.Direction.ASC)
          : Sort.Direction.ASC;

      resolvedSort = Sort.by(direction, property);
    }

    return PageRequest.of(resolvedPage, resolvedSize, resolvedSort);
  }
}