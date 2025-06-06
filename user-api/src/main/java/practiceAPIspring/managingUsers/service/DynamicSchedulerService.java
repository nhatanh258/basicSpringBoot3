package practiceAPIspring.managingUsers.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicSchedulerService {
    private final TaskScheduler taskScheduler;// inject dua vao bean service
    private ScheduledFuture<?> scheduledFuture;
    private String currentCron = "0/10 * * * * *"; // default: mỗi 10s

    public DynamicSchedulerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void startScheduler() {
        stopScheduler(); // nếu đang chạy thì dừng lại

        CronTrigger cronTrigger = new CronTrigger(currentCron); //Tạo một CronTrigger dựa trên biểu thức cron
        scheduledFuture = taskScheduler.schedule(() -> {
            System.out.println("Cron đang chạy tại " + LocalDateTime.now());
        }, cronTrigger);// Lên lịch tác vụ với CronTrigger

        System.out.println("Scheduler started with cron: " + currentCron);
    }

    public void stopScheduler() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {// kiem tra task shceduler
            scheduledFuture.cancel(true);
            System.out.println("Scheduler đã dừng");
        }
    }

    public void updateCron(String newCron) {
        this.currentCron = newCron;
        startScheduler();
    }

    public String getCurrentCron() {
        return this.currentCron;
    }
}

