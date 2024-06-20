package ru.practicum.ewm.service.configuration;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.model.Participation;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
@EnableAutoConfiguration
public class ModelMapperConfig {

    public static final Converter<LocalDateTime, String> localDateTimeToStringConverter = new AbstractConverter<LocalDateTime, String>() {
        @Override
        protected String convert(LocalDateTime source) {
            return source == null ? null : source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    };

    public static final Converter<String, LocalDateTime> stringToLocalDateTimeConverter = new AbstractConverter<String, LocalDateTime>() {
        @Override
        protected LocalDateTime convert(String source) {
            try {
                return source == null ? null : LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing date: " + e.getMessage());
                return null;
            }
        }
    };

    public static final Converter<User, Long> userLongConverter = new AbstractConverter<>() {
        @Override
        protected Long convert(User source) {
            return source.getId();
        }
    };

    public static final Converter<Event, Long> eventLongConverter = new AbstractConverter<>() {
        @Override
        protected Long convert(Event source) {
            return source.getId();
        }
    };

    public static final PropertyMap<Participation, ParticipationRequestDto> propertyMap = new PropertyMap<>() {
        @Override
        protected void configure() {
            using(userLongConverter).map(source.getUser(), destination.getRequester());
        }
    };

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(localDateTimeToStringConverter);
        mapper.addConverter(stringToLocalDateTimeConverter);
        mapper.addConverter(eventLongConverter);
        mapper.addConverter(userLongConverter);
        mapper.addMappings(propertyMap);
        return mapper;
    }
}
