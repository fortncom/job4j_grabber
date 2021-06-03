package ru.job4j.grabber.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

public class SqlRuParse {

    public static void main(String[] args) throws Exception {
        for (int i = 1; i < 6; i++) {
            System.out.println("Страница №" + i);
            Document doc = Jsoup.connect(String.format("https://www.sql.ru/forum/job-offers/%s", i)).get();
            Elements rows = doc.select(".forumTable > tbody > tr");
            SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
            for (Element td : rows) {
                Element href = td.children().get(1);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                String dt = td.children().get(5).text();
                System.out.println(parser.parse(dt));
            }
        }
    }
}
