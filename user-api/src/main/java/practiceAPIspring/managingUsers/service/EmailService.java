package practiceAPIspring.managingUsers.service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.*;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // Gửi email đơn giản
    @Async
    public void sendSimpleEmail(String to, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");
        message.setTo(to);
        message.setSubject(title);
        message.setText(text);
        mailSender.send(message);
    }

    // Gửi email với file đính kèm
    public void sendEmailWithAttachment(String to, String subject, String text, File file) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(file.getName(), file);
        mailSender.send(message);
    }

    // Gửi nhiều người nhận
    public void sendBulkEmail(List<String> recipients, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(recipients.toArray(new String[0]));
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    // Gửi email theo template Thymeleaf
    public void sendTemplateEmail(String to, String subject, String username) throws MessagingException {
        Context context = new Context(); // doi tuong cua thymleaf de toa ra ngu canh
        context.setVariable("username", username); // gan bien username vao context tỏng trang html
        System.out.println("TemplateEngine: " + templateEngine);
        String html = templateEngine.process("email-template", context);// su dung template email-template.html trong resources/templates

        MimeMessage message = mailSender.createMimeMessage(); // tạo một đối tượng MimeMessage  để hỗ trợ gửi email với định dạng đa phương tiện
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // MimeMessageHelper giúp dễ dàng thiết lập các thuộc tính của email như người nhận, tiêu đề, nội dung, đính kèm, v.v.
        helper.setTo(to); // thiết lập người nhận email
        helper.setSubject(subject); // thiết lập tiêu đề email
        helper.setText(html, true); // thiết lập nội dung email, với tham số thứ hai là true để chỉ định rằng nội dung là HTML
        mailSender.send(message); // gửi email thông qua JavaMailSender
    }

    // Gửi email có CC và BCC
    public void sendEmailWithCcAndBcc(String to, String cc, String bcc, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setCc(cc);
        helper.setBcc(bcc);
        helper.setSubject(subject);
        helper.setText(text, true);
        mailSender.send(message);
    }

    // Gửi OTP
    public void sendOtp(String to, String otp) {
        String subject = "Xác minh OTP";
        String content = "Mã OTP của bạn là: " + otp + ". Có hiệu lực trong 5 phút.";
        sendSimpleEmail(to, subject, content);
    }

//     Gửi tự động theo lịch
//    @Scheduled(cron = "*/1 * * * * ?") // chạy mỗi 30 giây
//    public void sendDailyReport() {
//        log.info("tu dong gui den email admin");
//        try {
//            sendSimpleEmail("nguyenthuytrang090904@gmail.com", "trang lololoooll ngu vãi vãi", "trang ngu như bò tót");
//        } catch (Exception e) {
//            log.error("Không gửi được email: ", e);
//        }
//    }


    // Đọc email từ Gmail qua IMAP
    public List<String> readEmails() {
        // Tạo danh sách chứa nội dung các email được đọc
        List<String> emailContents = new ArrayList<>();
        try {
            // Khởi tạo đối tượng Properties để cấu hình phiên làm việc mail
            Properties props = new Properties();
            // Thiết lập giao thức sử dụng để lấy mail (IMAPS - IMAP qua SSL)
            props.setProperty("mail.store.protocol", "imaps");

            // Tạo phiên làm việc mail với cấu hình vừa thiết lập, không cần Authenticator (null)
            Session session = Session.getInstance(props, null);
            // Lấy Store (kho lưu trữ mail) từ phiên làm việc
            Store store = session.getStore();
            // Kết nối đến server IMAP của Gmail với email và mật khẩu ứng dụng (App Password)
            store.connect("imap.gmail.com", "nhatanhn686@gmail.com", "iadn fbfk ldbn zgdv");

            // Lấy thư mục "INBOX" (hộp thư đến)
            Folder inbox = store.getFolder("INBOX");
            // Mở thư mục ở chế độ chỉ đọc (READ_ONLY)
            inbox.open(Folder.READ_ONLY);

            // Lấy tất cả các email trong hộp thư đến
            Message[] messages = inbox.getMessages();

            // Duyệt qua từng 5  email
            int count = 0;
            for (Message msg : messages) {
                // Lấy tiêu đề email
                String subject = msg.getSubject();
                // Lấy thông tin người gửi (địa chỉ)
                Address[] from = msg.getFrom();
                // Lấy nội dung email (có thể là String hoặc Multipart)
                Object content = msg.getContent();

                // Khởi tạo biến chứa nội dung thân email
                String body = "";

                // Nếu nội dung là kiểu String (email thuần văn bản)
                if (content instanceof String) {
                    body = (String) content;
                }
                // Nếu nội dung là kiểu Multipart (có thể chứa nhiều phần như text, hình ảnh, file đính kèm)
                else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    // Duyệt từng phần của Multipart
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart part = multipart.getBodyPart(i);
                        // Nếu phần này là text thuần hoặc HTML thì lấy nội dung đó
                        if (part.isMimeType("text/plain") || part.isMimeType("text/html")) {
                            body = part.getContent().toString();
                            break; // Lấy phần đầu tiên thỏa điều kiện rồi dừng
                        }
                    }
                }

                // Định dạng thông tin email thành chuỗi: người gửi, tiêu đề và nội dung
                String info = String.format("Từ: %s\nTiêu đề: %s\nNội dung:\n%s\n====================",
                        Arrays.toString(from), subject, body);

                // Thêm chuỗi thông tin email vào danh sách kết quả
                emailContents.add(info);
                if(++count >= 5) break; // Dừng sau khi đọc 5 email
            }

            // Đóng thư mục inbox (false: không xoá thư khi đóng)
            inbox.close(false);
            // Đóng kết nối đến store
            store.close();

        } catch (Exception e) {
            // Ghi log lỗi nếu có lỗi xảy ra khi đọc email
            log.error("Lỗi khi đọc email: ", e);
        }
        // Trả về danh sách chuỗi chứa thông tin các email đã đọc
        return emailContents;
    }

    // cau hinh bat dong bo trong chay email
    @Async("taskExecutor") // dùng executor tên "taskExecutor"
    public void sendEmail(String to, String subject, String body) {
        System.out.println("Sending to " + to + " on thread: " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000); // mô phỏng gửi email
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done sending to " + to);
    }

}
