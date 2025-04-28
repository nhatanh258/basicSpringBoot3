package practiceAPIspring.managingUsers.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class NullAwareBeanUtils {

    public static void copyNonNullProperties(Object source, Object target) {
        //Nhưng nhờ phần getNullPropertyNames(source) → bạn loại bỏ được các field đang null.
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        List<String> nullProperties = new ArrayList<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                nullProperties.add(pd.getName());
            }
        }
        return nullProperties.toArray(new String[0]);
    }
}
