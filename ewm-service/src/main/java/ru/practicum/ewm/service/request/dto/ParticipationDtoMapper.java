package ru.practicum.ewm.service.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.practicum.ewm.service.request.model.Participation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipationDtoMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(Collection<Participation> participationList) {
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
