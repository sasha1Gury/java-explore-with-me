package ru.practicum.ewm.service.request.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.practicum.ewm.service.error_handler.ErrorHandler.DATE_TIME_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipationDtoMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static ParticipationRequestDto toParticipationRequestDto(Participation participation) {
        if (participation != null) {
            return ParticipationRequestDto.builder()
                    .id(participation.getId())
                    .created(participation.getCreated().format(DATE_TIME_FORMATTER))
                    .requester(participation.getUser().getId())
                    .event(participation.getEvent().getId())
                    .status(participation.getStatus())
                    .build();
        } else {
            return null;
        }
    }

    public static List<ParticipationRequestDto>
    toParticipationRequestDtoList(Collection<Participation> participationList) {

        if (participationList != null) {
            List<ParticipationRequestDto> participationRequestDtoList = new ArrayList<>();
            for (Participation participation : participationList) {
                participationRequestDtoList.add(mapper.map(participation, ParticipationRequestDto.class));
            }
            return participationRequestDtoList;
        } else {
            return null;
        }
    }
}
