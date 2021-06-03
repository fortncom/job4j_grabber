package ru.job4j.grabber.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse {

    List<Post> posts = new ArrayList<>();

    public void parse() {
        for (int i = 1; i < 5; i++) {
            System.out.println("Страница №" + i);
            try {
                Document doc = Jsoup.connect(String.format(
                        "https://www.sql.ru/forum/job-offers/%s", i)).get();
                Elements rows = doc.select(".forumTable > tbody > tr");

                for (Element tr : rows) {
                    Post post = new Post();
                    Element td = tr.children().get(1);
                    String href = td.getElementsByTag("a").attr("href");
                    if (href != null) {
                        post.setPath(href);
                        posts.add(post);
                    }
                }
                loadPosts();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPosts() throws IOException {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        for (Post p : posts) {
            if (!p.getPath().equals("")) {
                Document pst = Jsoup.connect(p.getPath()).get();
                Elements dataPost = pst.select(".msgTable > tbody");
                Element name = dataPost.first().child(0).child(0);
                if (name != null) {
                    p.setName(name.text());
                }
                Element text = dataPost.first().child(1).child(1);
                if (text != null) {
                    p.setText(text.text());
                }
                Element date = dataPost.first().child(2).child(0);
                if (date != null) {
                    p.setCreated(Timestamp.valueOf(parser.parse(date.text())));
                }
            }
            System.out.println(p);
        }
    }

    public static void main(String[] args) {
        SqlRuParse ruParse = new SqlRuParse();
        ruParse.parse();
    }
}
