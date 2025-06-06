package practiceAPIspring.managingUsers.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoService {

    @Autowired
    private CallerService callerService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void methodWithTransaction() {
        System.out.println("🚀 Transaction started in methodWithTransaction");
        callerService.callWithNeverPropagation(); // Đây sẽ gọi method NEVER trong transaction active -> lỗi
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodWithTransactionNotTRan() {
        System.out.println("🚀 Transaction started in methodWithTransaction");
        callerService.callWithNeverPropagation(); // Đây sẽ gọi method NEVER trong transaction active -> lỗi
    }
}
