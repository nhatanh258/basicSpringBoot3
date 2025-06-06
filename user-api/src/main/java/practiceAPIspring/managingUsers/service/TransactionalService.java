package practiceAPIspring.managingUsers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import practiceAPIspring.managingUsers.repository.UserRepo;


@Service
public class TransactionalService {
    @Autowired
    private UserRepo userRepo;

    @Transactional(propagation = Propagation.REQUIRED)
    public void doSomethingTransactional() {
        userRepo.count();
        System.out.println("✅ Doing something inside a REQUIRED transaction");
    }

    public void doSomethingTransactional23() {
        userRepo.count();
        System.out.println("✅ Doing something inside a REQUIRED transaction");
    }

    @Transactional(timeout = 5)  // timeout 5 giây
    public void someTransactionalMethod() {
        // logic trong transaction
    }


}
