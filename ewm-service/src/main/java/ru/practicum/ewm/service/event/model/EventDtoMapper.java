package ru.practicum.ewm.service.event.model;

import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.practicum.ewm.service.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class EventDtoMapper {
    private static final ModelMapper mapper = new ModelMapper();
    public static List<EventShortDto> toEventShortDtoList(Collection<Event> eventList) {
        if (eventList != null) {
            List<EventShortDto> eventShortDtoList = new ArrayList<>();
            for (Event event : eventList) {
                eventShortDtoList.add(mapper.map(event, EventShortDto.class));
            }
            return eventShortDtoList;
        } else {
            return null;
        }
    }
}
