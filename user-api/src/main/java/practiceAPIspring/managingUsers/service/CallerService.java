package practiceAPIspring.managingUsers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import practiceAPIspring.managingUsers.repository.UserRepo;


@Service
public class CallerService {

    @Autowired
    private ApplicationContext applicationContext;

    @Transactional(propagation = Propagation.MANDATORY)
    public void callWithNeverPropagation() {
        System.out.println("⚠️ This method must not be called inside a transaction");
        applicationContext.getBean(TransactionalService.class).doSomethingTransactional();
    }

    public void callWithNeverPropagationNotTran() {
        System.out.println("⚠️ This method must not be called inside a transaction");
        applicationContext.getBean(TransactionalService.class).doSomethingTransactional23();
    }

    @Autowired
    private UserRepo userRepo;

    @Transactional(timeout = 3)  // timeout 3 giây
    public void longRunningTransaction() {
        try {
            System.out.println("Transaction bắt đầu...");
            Thread.sleep(5000);  // Giả lập thao tác tốn 5 giây (vượt quá timeout)
            userRepo.count();
            System.out.println("Transaction kết thúc");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

