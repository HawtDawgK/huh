package db;

import api.ClientWrapper;
import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import post.PostResolvable;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class PostRepository {

    private static final String DB_URL;
    private static final String DB_NAME;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties properties = new Properties();
        InputStream inputResource = ClientWrapper.class.getClassLoader().getResourceAsStream("db.properties");

        try {
            properties.load(inputResource);
            DB_URL = properties.getProperty("url");
            DB_NAME = properties.getProperty("db");
            USER = properties.getProperty("user");
            PASSWORD = properties.getProperty("password");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static List<PostResolvable> getFavorites(long userId) throws SQLException {
        try (
                Connection con = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
                Statement stmt = con.createStatement();
        ) {
            ResultSet rs = stmt.executeQuery("select * from post where user_id = " + userId);
            List<PostResolvable> favorites = new ArrayList<>();

            while (rs.next()) {
                long postId = rs.getLong(2);
                String site = rs.getString(3);

                favorites.add(new PostResolvable(postId, PostSite.findByName(site).getPostApi()));
            }

            return favorites;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static int addFavorite(long userId, long postId, PostSite site) throws SQLException {
        try (
                Connection con = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
                Statement stmt = con.createStatement();
        ) {
            if (hasFavorite(userId, postId, site)) {
                return 0;
            }

            String query = String.format("INSERT INTO post (user_id, post_id, site_name) VALUES (%d, %d, \"%s\")",
                    userId, postId, site.getName());

            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static int removeFavorite(long userId, long postId, PostSite site) throws SQLException {
        try (
                Connection con = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
                Statement stmt = con.createStatement();
        ) {
            if (!hasFavorite(userId, postId, site)) {
                return 0;
            }

            String query = String.format("DELETE FROM post " +
                            "WHERE user_id = %d AND post_id = %d AND site_name = \"%s\";",
                    userId, postId, site.getName());

            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static boolean hasFavorite(long userId, long postId, PostSite site) throws SQLException {
        try (
                Connection con = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
                Statement stmt = con.createStatement();
        ) {
            String query = String.format("SELECT * FROM post " +
                            " WHERE user_id = %d AND post_id = %d AND site_name = \"%s\";",
                    userId, postId, site.getName());

            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
