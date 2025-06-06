package practiceAPIspring.managingUsers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // số luồng tối thiểu
        executor.setMaxPoolSize(10); // số luồng tối đa
        executor.setQueueCapacity(100); // hàng đợi
        executor.setThreadNamePrefix("AsyncThread-"); // prefix cho tên luồng
        executor.initialize();
        return executor;
    }
}
