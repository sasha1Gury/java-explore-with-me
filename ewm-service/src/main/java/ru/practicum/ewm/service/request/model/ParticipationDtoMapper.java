package ru.practicum.ewm.service.request.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipationDtoMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static ParticipationRequestDto toParticipationRequestDto(Participation participation) {
        if (participation != null) {
            return ParticipationRequestDto.builder()
                    .id(participation.getId())
                    .created(participation.getCreated())
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
                participationRequestDtoList.add(toParticipationRequestDto(participation));
            }
            return participationRequestDtoList;
        } else {
            return null;
        }
    }
}
