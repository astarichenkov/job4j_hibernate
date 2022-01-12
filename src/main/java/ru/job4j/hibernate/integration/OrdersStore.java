package ru.job4j.hibernate.integration;


import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrdersStore {
    private final BasicDataSource pool;

    public OrdersStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Order save(Order order) {
        try (Connection con = pool.getConnection();
             PreparedStatement pr = con.prepareStatement(
                     "INSERT INTO orders(name, description, created) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            pr.setString(1, order.getName());
            pr.setString(2, order.getDescription());
            pr.setTimestamp(3, order.getCreated());
            pr.execute();
            ResultSet id = pr.getGeneratedKeys();
            if (id.next()) {
                order.setId(id.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public boolean update(Order order) {
        int rsl = 0;
        try (Connection con = pool.getConnection();
             PreparedStatement pr = con.prepareStatement(
                     "UPDATE orders SET name = ?, description = ?, created = ? WHERE id = ?",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            pr.setString(1, order.getName());
            pr.setString(2, order.getDescription());
            pr.setTimestamp(3, order.getCreated());
            pr.setInt(4, order.getId());
            rsl = pr.executeUpdate();
            ResultSet id = pr.getGeneratedKeys();
            if (id.next()) {
                order.setId(id.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl == 1;
    }

    public Collection<Order> findAll() {
        List<Order> rsl = new ArrayList<>();
        try (Connection con = pool.getConnection();
             PreparedStatement pr = con.prepareStatement("SELECT * FROM orders")) {
            try (ResultSet rs = pr.executeQuery()) {
                while (rs.next()) {
                    rsl.add(
                            new Order(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("description"),
                                    rs.getTimestamp(4)
                            )
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    public Order findById(int id) {
        Order rsl = null;
        try (Connection con = pool.getConnection();
             PreparedStatement pr = con.prepareStatement("SELECT * FROM orders WHERE id = ?")) {
            pr.setInt(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                rsl = new Order(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp(4)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    public List<Order> findByName(String name) {
        List<Order> rsl = new ArrayList<>();
        try (Connection con = pool.getConnection();
             PreparedStatement pr = con.prepareStatement("SELECT * FROM orders WHERE name = ?")) {
            pr.setString(1, name);
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                rsl.add(new Order(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp(4)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

//    @Override
//    public List<Item> findByName(String key) {
//        Session session = sf.openSession();
//        session.beginTransaction();
//        List<Item> result = session.createQuery("from ru.job4j.tracker.model.Item WHERE name = :param")
//                .setParameter("param", key).list();
//        session.getTransaction().commit();
//        session.close();
//        return result;
//    }
}