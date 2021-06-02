package ru.job4j.grabber.utils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlRuDateTimeParserTest {

    @Test
    public void whenParseYesterday() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        LocalDate localDate = LocalDate.now().minusDays(1);
        LocalTime localTime = LocalTime.of(16, 15);
        LocalDateTime expected = LocalDateTime.of(localDate, localTime);
        LocalDateTime result = parser.parse(localDate.format(DateTimeFormatter.ofPattern("dd MMMM yy")).concat(", ")
                .concat(localTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(result, is(expected));
    }

    @Test
    public void whenParseToday() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.of(16, 15);
        LocalDateTime expected = LocalDateTime.of(localDate, localTime);
        LocalDateTime result = parser.parse(localDate.format(DateTimeFormatter.ofPattern("dd MMMM yy")).concat(", ")
                .concat(localTime.format(DateTimeFormatter.ofPattern("HH:mm"))));
        assertThat(result, is(expected));
    }
}