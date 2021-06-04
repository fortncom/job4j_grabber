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

public class SqlRuParse implements Parse {


    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements rows = doc.select(".forumTable > tbody > tr");
            for (Element tr : rows) {
                Post post = new Post();
                Element td = tr.children().get(1);
                String href = td.getElementsByTag("a").attr("href");
                if (href != null) {
                    post.setLink(href);
                    posts.add(post);
                }
            }
            return posts;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @Override
    public Post detail(String link) {
        Post post = new Post();
        try {
            if (!link.equals("")) {
                Document pst = Jsoup.connect(link).get();
                Elements dataPost = pst.select(".msgTable > tbody");
                Element name = dataPost.first().child(0).child(0);
                if (name != null) {
                    post.setName(name.text());
                }
                Element text = dataPost.first().child(1).child(1);
                if (text != null) {
                    post.setText(text.text());
                }
                Element date = dataPost.first().child(2).child(0);
                if (date != null) {
                    post.setCreated(Timestamp.valueOf(new SqlRuDateTimeParser().parse(date.text())));
                }
            }
            return post;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        SqlRuParse ruParse = new SqlRuParse();
        List<Post> posts = ruParse.list("https://www.sql.ru/forum/job-offers/1");
        for (int i = 0; i < posts.size(); i++) {
            posts.set(i, ruParse.detail(posts.get(i).getLink()));
            System.out.println(posts.get(i));
        }

    }

}
