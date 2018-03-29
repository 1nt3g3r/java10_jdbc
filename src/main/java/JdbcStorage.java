import entity.Cat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcStorage {
    private String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private String SERVER_PATH = "localhost";
    private String DB_NAME = "my_first_db";
    private String DB_LOGIN = "root";
    private String DB_PASSWORD = "integer231992";

    private Connection connection;
    private Statement st;

    private PreparedStatement createSt;
    private PreparedStatement updateSt;
    private PreparedStatement searchSt;

    public JdbcStorage() {
        initDbDriver();
        initConnection();
        initPreparedStatements();
    }

    private void initDbDriver() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initConnection() {
        String connectionUrl = "jdbc:mysql://" + SERVER_PATH + "/" + DB_NAME;
        connectionUrl += "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        try {
            connection = DriverManager.getConnection(connectionUrl, DB_LOGIN, DB_PASSWORD);

            st = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initPreparedStatements() {
        try {
            createSt =
            connection.prepareStatement("INSERT INTO Cat (cat_name, weight, sex) VALUES(?, ?, ?)");

            updateSt =
            connection.prepareStatement("UPDATE Cat SET cat_name=?, weight=?, sex=? WHERE id=?");

            searchSt =
            connection.prepareStatement("SELECT id, cat_name, weight, sex FROM Cat WHERE" +
                    " cat_name like ?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cat getCatById(long catId) {
        String selectSql =
                "SELECT id, cat_name, weight, sex, owner_id " +
                "FROM Cat " +
                "WHERE id=" + catId;

        ResultSet rs = null;
        try {
            rs = st.executeQuery(selectSql);

            if (rs.first()) {
                Cat cat = new Cat();

                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(rs.getLong("owner_id"));

                return cat;
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
           closeResultSet(rs);
        }
    }

    public void createCat(Cat cat) {
        try {
            createSt.setString(1, cat.getCatName());
            createSt.setFloat(2, cat.getWeight());
            createSt.setBoolean(3, cat.getSex());

            createSt.executeUpdate();

            cat.setId(getLastCatId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCat(Cat cat) {
        try {
            updateSt.setString(1, cat.getCatName());
            updateSt.setFloat(2, cat.getWeight());
            updateSt.setBoolean(3, cat.getSex());
            updateSt.setLong(4, cat.getId());

            updateSt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCatById(long id) {
        String sql = "DELETE FROM Cat WHERE id=" + id;
        try {
          st.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getLastCatId() {
        String sql = "select id from cat order by id desc limit 1";

        ResultSet rs = null;
        try {
            rs = st.executeQuery(sql);

            if (rs.first()) {
                return rs.getLong("id");
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeResultSet(rs);
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void clear() {
        String sql = "DELETE FROM Cat";

        try {
            st.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Cat> getAllCats() {
        List<Cat> result = new ArrayList<Cat>();

        String sql = "SELECT id, cat_name, weight, sex FROM Cat";

        ResultSet rs = null;

        try {
            rs = st.executeQuery(sql);

            while(rs.next()) {
                Cat cat = new Cat();

                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));

                result.add(cat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }

        return result;
    }

    public List<Cat> search(String part) {
        List<Cat> result = new ArrayList<Cat>();

        ResultSet rs = null;

        try {
            searchSt.setString(1, "%" + part + "%");
            rs = searchSt.executeQuery();

            while(rs.next()) {
                Cat cat = new Cat();

                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));

                result.add(cat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }

        return result;
    }

    public static void main(String[] args) {
        JdbcStorage storage = new JdbcStorage();

        List<Cat> cats = storage.search("а");

        System.out.println("Cat count: " + cats.size());

        for(Cat cat: cats) {
            System.out.println(cat);
        }
    }
}
