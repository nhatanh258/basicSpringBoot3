package practiceAPIspring.managingUsers.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.constant.StatusMessage;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponListObject;
import practiceAPIspring.managingUsers.dto.response.comonResponse.ResponseObject;
import practiceAPIspring.managingUsers.dto.response.event.EventResponse;
import practiceAPIspring.managingUsers.service.EventService;


@RestController
@RequestMapping("api/event")
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventController {
    EventService eventService;

    @PostMapping
    ResponseObject<?> create(@Validated @RequestBody EventResponse request){
//        return new ResponseObject<>(StatusMessage.SUCCESS, eventService.create(request));
        return null;
    }
    @GetMapping
    ResponListObject<?> getAll(){
        return new ResponListObject<>(StatusMessage.SUCCESS, eventService.getAll());
    }

    @DeleteMapping("/{eventId}")
    ResponseObject<Void> delete(@PathVariable String eventId){
        eventService.delete(eventId);
        return new ResponseObject<>(StatusMessage.SUCCESS, null);
    }
}
