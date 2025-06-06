package practiceAPIspring.managingUsers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import practiceAPIspring.managingUsers.service.CallerService;
import practiceAPIspring.managingUsers.service.DemoService;


@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionalController {

    private final DemoService demoService;

    private final CallerService timeoutService;

    @GetMapping("/test")
    public String test() {
        demoService.methodWithTransaction(); // Gọi method có transaction, bên trong gọi NEVER -> ném lỗi
        return "Done";
    }
    @GetMapping("/testMandentory")
    public String test2() {
        demoService.methodWithTransactionNotTRan(); // Gọi method có transaction, bên trong gọi NEVER -> ném lỗi
        return "Done";
    }

    @GetMapping("/timeout")
    public String testTimeout() {
        timeoutService.longRunningTransaction();
        return "Done";
    }
}

