package practiceAPIspring.managingUsers.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practiceAPIspring.managingUsers.dto.request.event.EventRequest;
import practiceAPIspring.managingUsers.dto.response.event.EventResponse;
import practiceAPIspring.managingUsers.dto.response.permission.PermissionResponse;
import practiceAPIspring.managingUsers.mapper.EventMapper;
import practiceAPIspring.managingUsers.model.Event;
import practiceAPIspring.managingUsers.repository.EventRepo;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j

public class EventService {
    private final EventRepo eventRepo;
    private final EventMapper eventMapper;
    public EventResponse create(EventRequest request){
        Event event = eventMapper.toEvent(request);
        event = eventRepo.save(event);
        System.out.println("xxx1 "+event.getName());
        EventResponse as = eventMapper.toEventResponse(event);
        System.out.println("xxx2 "+as.getName());

        // dua vao token xac dinh nguoi dung
        //dua vao nguoi dung them vao event
        // dua vao event them vao nguoi dung

        return  as;
    }

    public List<PermissionResponse> getAll(){
//        var permission = permissionRepository.findAll();
//        return permission.stream().map(permissionMapper::toPermissionResponse).toList();
        return  null;
    }

    public void delete(String permission){
//        permissionRepository.deleteById(permission);
    }
}
