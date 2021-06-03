package ru.job4j.grabber.utils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class SqlRuDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        String[] fullDate = parse.split(",");
        if (fullDate.length < 2) {
            return null;
        }
        String onlyDate;
            switch (fullDate[0].trim().toLowerCase()) {
                case "сегодня" : onlyDate = LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd MMMM yy"));
                break;
                case "вчера" : onlyDate = LocalDate.now().minusDays(1).format(
                        DateTimeFormatter.ofPattern("dd MMMM yy"));
                break;
                default : onlyDate = fullDate[0].trim();
                break;
        }

        try {
            Date date = getFormatter().parse(onlyDate.concat(fullDate[1]));
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    private SimpleDateFormat getFormatter() {
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.getDefault());
        String[] shortMonths = {
                "янв", "фев", "мар", "апр", "май", "июн",
                "июл", "авг", "сен", "окт", "ноя", "дек"};
        dfs.setShortMonths(shortMonths);
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd MMM yy HH:mm", Locale.getDefault());
        formatter.setDateFormatSymbols(dfs);
        return formatter;
    }
}
