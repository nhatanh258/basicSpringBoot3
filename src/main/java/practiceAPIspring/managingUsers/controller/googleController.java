package practiceAPIspring.managingUsers.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;

@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class googleController {
    @RequestMapping("/")
    public String home(){
        return "Welcome";
    }

    @RequestMapping("/user")
    public Principal user(Principal user){
        return user;
    }
}
