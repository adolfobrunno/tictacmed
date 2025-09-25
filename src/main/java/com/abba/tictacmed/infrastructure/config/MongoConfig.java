package com.abba.tictacmed.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        Converter<Date, OffsetDateTime> dateToOffsetDateTime = new Converter<>() {
            @Override
            public OffsetDateTime convert(Date source) {
                return source.toInstant().atOffset(ZoneOffset.UTC);
            }
        };

        Converter<OffsetDateTime, Date> offsetDateTimeToDate = new Converter<>() {
            @Override
            public Date convert(OffsetDateTime source) {
                return Date.from(source.toInstant());
            }
        };

        return new MongoCustomConversions(List.of(dateToOffsetDateTime, offsetDateTimeToDate));
    }
}
