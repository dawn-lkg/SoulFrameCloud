package com.clm.common.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author 陈黎明
 * @date 2025/3/2 上午12:12
 */
@Slf4j
@JsonComponent
public class JacksonConfiguration {
    private static final String TIME_ZONE = "GMT+8";

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                if (source.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(source);
                } catch (Exception e) {
                    return LocalDate.parse(source, DateTimeFormatter.ofPattern(DATE_PATTERN));
                }
            }
        };
    }

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDateTime.parse(source);
                } catch (Exception e) {
                    return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
                }
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        
        return builder -> {
            builder.modules(javaTimeModule);
            builder.simpleDateFormat(DATE_TIME_PATTERN);
        };
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.setTimeZone(TimeZone.getTimeZone(TIME_ZONE)); // 指定时区为：中国时
        
        // 提高性能的配置
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 针对实体映射时不匹配类型或序列化的处理配置：即找不到、不可用也不抛出异常。
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // 启用缓存机制，提高性能
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        
        // 禁用日期时间格式化为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 对Null值进行处理
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                // 根据不同类型进行处理
                if (o instanceof Collection) {
                    // 空集合返回[]
                    jsonGenerator.writeStartArray();
                    jsonGenerator.writeEndArray();
                } else if (o instanceof Map || o instanceof Object[]) {
                    // 空对象或对象数组返回{}
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeEndObject();
                } else {
                    // 其他类型返回空字符串
                    jsonGenerator.writeString("");
                }
            }
        });
        
        // 注册Java8日期时间模块
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    /**
     * LocalDateTime的自定义序列化器
     */
    public static class LocalDateTimeJsonSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            String formattedValue = value.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            gen.writeString(formattedValue);
        }
    }
}
