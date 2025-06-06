package practiceAPIspring.managingUsers.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import practiceAPIspring.managingUsers.dto.request.event.EventRequest;
import practiceAPIspring.managingUsers.dto.response.event.EventResponse;
import practiceAPIspring.managingUsers.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Event toEvent(EventRequest request);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    EventResponse toEventResponse(Event event);
}
