package ecorayen.interfaces;

import java.util.ArrayList;

public interface iservices<T> {
    boolean add(T t);
    boolean delete(T t);
    boolean update(T t);
    ArrayList<T> getAll();
    Object getById(int id);
    ArrayList<T> getByField(String field);
    ArrayList<T> getByField(String field, String value);



}
