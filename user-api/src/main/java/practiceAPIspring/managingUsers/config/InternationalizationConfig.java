package practiceAPIspring.managingUsers.config;

// Import các class cần thiết từ Spring Framework
import org.springframework.context.MessageSource; // Interface để truy cập nội dung message (đa ngôn ngữ)
import org.springframework.context.annotation.Bean; // Đánh dấu phương thức là một bean Spring
import org.springframework.context.annotation.Configuration; // Đánh dấu đây là class cấu hình
import org.springframework.context.support.ReloadableResourceBundleMessageSource; // Cho phép đọc file messages đa ngôn ngữ
import org.springframework.web.servlet.LocaleResolver; // Interface xác định locale (ngôn ngữ) hiện tại
import org.springframework.web.servlet.config.annotation.InterceptorRegistry; // Dùng để đăng ký interceptor
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Interface cho cấu hình Spring MVC
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor; // Interceptor dùng để thay đổi locale từ request param
import org.springframework.web.servlet.i18n.SessionLocaleResolver; // Xác định locale và lưu nó trong session

import java.util.Locale; // Class Locale đại diện cho ngôn ngữ và vùng

// Đánh dấu đây là một class cấu hình Spring
@Configuration
public class InternationalizationConfig {

    // Bean để xác định locale mặc định (ví dụ: ENGLISH) và lưu locale hiện tại trong session
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver(); // Sử dụng session để lưu locale người dùng
        slr.setDefaultLocale(Locale.ENGLISH); // Đặt locale mặc định là tiếng Anh
        return slr;
    }

    // Bean để tạo interceptor giúp thay đổi ngôn ngữ theo request param (ví dụ: ?lang=vi)
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // Tên tham số truyền trên URL để đổi ngôn ngữ, ví dụ ?lang=vi
        return interceptor;
    }

    // Bean để chỉ định nguồn messages (file .properties) và encoding UTF-8 để đọc file tiếng Việt có dấu
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages"); // Chỉ định prefix file messages: messages.properties, messages_vi.properties,...
        messageSource.setDefaultEncoding("UTF-8"); // Đảm bảo đọc đúng tiếng Việt có dấu
        return messageSource;
    }

    // Cấu hình Spring MVC để đăng ký interceptor vào hệ thống
    @Bean
    public WebMvcConfigurer webMvcConfigurer(LocaleChangeInterceptor lci) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(lci); // Đăng ký interceptor vào hệ thống
            }
        };
    }
}
