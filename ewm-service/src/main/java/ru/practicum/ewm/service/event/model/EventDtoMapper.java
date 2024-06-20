package ru.practicum.ewm.service.event.model;

import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.practicum.ewm.service.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.practicum.ewm.service.event.service.EventService.localDateTimeToStringConverter;
import static ru.practicum.ewm.service.event.service.EventService.stringToLocalDateTimeConverter;

@NoArgsConstructor
public class EventDtoMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static List<EventShortDto> toEventShortDtoList(Collection<Event> eventList) {
        mapper.addConverter(localDateTimeToStringConverter);
        mapper.addConverter(stringToLocalDateTimeConverter);
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
