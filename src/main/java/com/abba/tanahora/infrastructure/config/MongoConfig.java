package com.abba.tanahora.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConfig {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        Converter<Date, OffsetDateTime> dateToOffsetDateTime = new Converter<>() {
            @Override
            public OffsetDateTime convert(Date source) {
                // Read Date (UTC instant) and represent it with Brazil's current offset
                return source.toInstant().atZone(BRAZIL_ZONE).toOffsetDateTime();
            }
        };

        Converter<OffsetDateTime, Date> offsetDateTimeToDate = new Converter<>() {
            @Override
            public Date convert(OffsetDateTime source) {
                // Persist as Instant (Mongo stores UTC under the hood)
                return Date.from(source.toInstant());
            }
        };

        return new MongoCustomConversions(List.of(dateToOffsetDateTime, offsetDateTimeToDate));
    }
}
