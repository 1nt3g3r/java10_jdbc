package storage;

import entity.Cat;
import entity.Owner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JdbcStorage {
    private static final String[] CAT_NAMES = {
            "Ліза",
            "Лизун",
            "Барсик",
            "Байт",
            "Кицьондра",
            "Платон",
            "Кузя",
            "Барсик",
            "Нюра",
            "Ксюша",
            "Том",
            "Інга",
            "Мурчик"
    };

    private String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private String SERVER_PATH = "localhost";
    private String DB_NAME = "my_first_db";
    private String DB_LOGIN = "root";
    private String DB_PASSWORD = "integer231992";

    private Connection connection;
    private Statement st;

    private PreparedStatement createCatSt;
    private PreparedStatement updateCatSt;
    private PreparedStatement searchCatSt;
    private PreparedStatement selectCatsByOwnerId;

    private PreparedStatement createOwnerSt;
    private PreparedStatement selectOwnerSt;

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
            createCatSt =
            connection.prepareStatement("INSERT INTO Cat (cat_name, weight, sex) VALUES(?, ?, ?)");

            updateCatSt =
            connection.prepareStatement("UPDATE Cat SET cat_name=?, weight=?, sex=?, owner_id=? WHERE id=?");

            searchCatSt =
            connection.prepareStatement("SELECT id, cat_name, weight, sex, owner_id FROM Cat WHERE" +
                    " cat_name like ?");

            createOwnerSt = connection.prepareStatement("INSERT INTO Owner(FIRST_NAME, LAST_NAME) VALUES(?, ?)");

            selectOwnerSt = connection.prepareStatement("SELECT first_name, last_name from Owner where id=?");

            selectCatsByOwnerId = connection.prepareStatement("SELECT id, cat_name, weight, sex from Cat where owner_id=?");
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

    public void createCasts(List<Cat> cats) throws SQLException {
        connection.setAutoCommit(false);

        for (Cat cat : cats) {
            createCatSt.setString(1, cat.getCatName());
            createCatSt.setFloat(2, cat.getWeight());
            createCatSt.setBoolean(3, cat.getSex());

            createCatSt.addBatch();
        }

        createCatSt.executeBatch();

        connection.setAutoCommit(true);
    }

    public void createOwner(Owner owner) {
        try {
            createOwnerSt.setString(1, owner.getFirstName());
            createOwnerSt.setString(2, owner.getLastName());

            createOwnerSt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Owner getOwnerById(long id) {
        ResultSet rs = null;
        try {
            selectOwnerSt.setLong(1, id);

            rs = selectOwnerSt.executeQuery();

            if (rs.first()) {
                Owner owner = new Owner();
                owner.setId(id);
                owner.setFirstName(rs.getString("first_name"));
                owner.setLastName(rs.getString("last_name"));

                return owner;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }

        return null;
    }

    public void createCat(Cat cat) {
        try {
            createCatSt.setString(1, cat.getCatName());
            createCatSt.setFloat(2, cat.getWeight());
            createCatSt.setBoolean(3, cat.getSex());

            createCatSt.executeUpdate();

            cat.setId(getLastCatId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCats(List<Cat> cats) throws SQLException {
        connection.setAutoCommit(false);

        for(Cat cat: cats) {
            updateCatSt.setString(1, cat.getCatName());
            updateCatSt.setFloat(2, cat.getWeight());
            updateCatSt.setBoolean(3, cat.getSex());
            updateCatSt.setLong(4, cat.getOwnerId());
            updateCatSt.setLong(5, cat.getId());

            updateCatSt.addBatch();
        }

        updateCatSt.executeBatch();
        connection.setAutoCommit(true);
    }

    public void updateCat(Cat cat) {
        try {
            updateCatSt.setString(1, cat.getCatName());
            updateCatSt.setFloat(2, cat.getWeight());
            updateCatSt.setBoolean(3, cat.getSex());
            updateCatSt.setLong(4, cat.getOwnerId());
            updateCatSt.setLong(5, cat.getId());

            updateCatSt.executeUpdate();
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

    public List<Cat> getAllCatsForOwner(Owner owner) {
        List<Cat> result = new ArrayList<Cat>();

        ResultSet rs = null;
        try {
            selectCatsByOwnerId.setLong(1, owner.getId());

            rs = selectCatsByOwnerId.executeQuery();

            while(rs.next()) {
                Cat cat = new Cat();

                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(owner.getId());

                result.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Cat> getAllCats() {
        List<Cat> result = new ArrayList<Cat>();

        String sql = "SELECT id, cat_name, weight, sex, owner_id FROM Cat";

        ResultSet rs = null;

        try {
            rs = st.executeQuery(sql);

            while(rs.next()) {
                Cat cat = new Cat();

                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(rs.getLong("owner_id"));

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
            searchCatSt.setString(1, "%" + part + "%");
            rs = searchCatSt.executeQuery();

            while(rs.next()) {
                Cat cat = new Cat();

                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(rs.getLong("owner_id"));

                result.add(cat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }

        return result;
    }

    private Cat randomCat() {
        Random r = new Random();

        String catName = CAT_NAMES[r.nextInt(CAT_NAMES.length)];

        Cat cat = new Cat();
        cat.setCatName(catName);
        cat.setWeight(r.nextFloat());
        cat.setSex(r.nextBoolean());

        return cat;
    }

    public void setOwnerForCat(Cat cat, Owner owner) {
        cat.setOwnerId(owner.getId());

        updateCat(cat);
    }

    public void deleteOwner(Owner owner) {
        String deleteSql = "delete from Owner where owner.id=" + owner.getId();

        try {
            st.executeUpdate(deleteSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        JdbcStorage storage = new JdbcStorage();

        storage.deleteOwner(storage.getOwnerById(4));
    }

}
