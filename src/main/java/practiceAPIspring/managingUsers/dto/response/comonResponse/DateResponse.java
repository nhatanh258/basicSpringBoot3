package practiceAPIspring.managingUsers.dto.response.comonResponse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateResponse extends StdSerializer<Date> {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public DateResponse() {
        this(null);
    }

    public DateResponse(Class t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(formatter.format(value));
    }
//    Nó chứa thông tin & dịch vụ để serialize object — ví dụ như:
//    Lấy custom serializers khác nếu bạn serialize nested object.
//    Xác định kiểu dữ liệu của property.
//    Cung cấp các cấu hình liên quan đến serialization (ví dụ ObjectMapper settings).
//    Serialize giá trị null hoặc default.


}
