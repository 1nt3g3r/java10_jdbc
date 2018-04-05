package hibtest;

import entity.Cat;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class HibernateStorage {
    private SessionFactory sessionFactory;

    public HibernateStorage() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public void createCat(Cat cat) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(cat);
        transaction.commit();
        session.close();
    }

    public Cat getCatById(long id) {
        Cat cat = null;

        Session session = sessionFactory.openSession();
        cat = session.get(Cat.class, id);
        session.close();

        return cat;
    }

    public void updateCat(Cat cat) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.update(cat);
        transaction.commit();
        session.close();
    }

    public void deleteCat(Cat cat) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(cat);
        transaction.commit();
        session.close();
    }

    public List<Cat> getAllCats() {
        List<Cat> result = null;

        Session session = sessionFactory.openSession();
        result = session.createQuery("from Cat", Cat.class).list();
        session.close();

        return result;
    }

    public List<Cat> search(String catName) {
        List<Cat> result = null;

        Session session = sessionFactory.openSession();
        String hql = "from Cat c " +
                "where c.catName like '%" + catName + "%' " +
                "order by c.weight desc";

        String sql = "select id, cat_name, weight, sex from Cat where cat_name like '%" + catName + "%'";

        System.out.println(hql);
        System.out.println(sql);

        result = session.createQuery(hql, Cat.class).list();
        session.close();

        return result;
    }

    public static void main(String[] args) {
        HibernateStorage storage = new HibernateStorage();

        List<Cat> cats = storage.search("а");
        System.out.println(cats);
    }
}
