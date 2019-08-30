package cn.itcast.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义类型转换器,实现接口：Converter<请求数据的类型（固定）,要转换的目标对象>
 */
public class StringToDateConverter implements Converter<String,Date> {
    @Override
    public Date convert(String source) {
        try {
            // 判断:import org.springframework.util.StringUtils;
            if (StringUtils.isEmpty(source)){
                return null;
            }
            // 转换
            return new SimpleDateFormat("yyyy-MM-dd").parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
