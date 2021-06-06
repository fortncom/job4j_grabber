package ru.job4j.grabber;

import ru.job4j.grabber.html.SqlRuParse;
import ru.job4j.grabber.model.Post;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("psql.driver"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("psql.url"),
                    cfg.getProperty("psql.login"),
                    cfg.getProperty("psql.password")
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
            "insert into posts(name, link, text, created) values(?, ?, ?, ?) on conflict do nothing;")) {
            statement.setString(1, post.getName());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getText());
            statement.setTimestamp(4, post.getCreated());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
            try (PreparedStatement statement = cnn.prepareStatement(
                    "select * from posts;")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        posts.add(new Post(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("link"),
                                resultSet.getString("text"),
                                resultSet.getTimestamp("created")
                        ));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return posts;
    }

    @Override
    public Post findById(String id) {
        if (!id.matches("[-+]?\\d+")) {
           throw new  IllegalArgumentException("arg id is not digit");
        }
        int idInt = Integer.parseInt(id);
        Post post = null;
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from posts where posts.id = ?;")) {
            statement.setInt(1, idInt);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                   post = new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("link"),
                            resultSet.getString("text"),
                            resultSet.getTimestamp("created")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws SQLException {
        Properties config = null;
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream(
                "rabbit.properties")) {
            config = new Properties();
            config.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        if (config != null) {
            PsqlStore store = new PsqlStore(config);
            SqlRuParse sqlPrs = new SqlRuParse();
            try (Connection cn = store.cnn) {
                List<Post> posts = sqlPrs.list("https://www.sql.ru/forum/job-offers/1");
                for (int i = 0; i < posts.size(); i++) {
                    if (!posts.get(i).getLink().equals("")) {
                        posts.set(i, sqlPrs.detail(posts.get(i).getLink()));
                        store.save(posts.get(i));
                    }
                }
                System.out.println(store.findById("1"));
                List<Post> all = store.getAll();
                for (Post post : all) {
                    System.out.println(post);
                }
            }
        }
    }
}
