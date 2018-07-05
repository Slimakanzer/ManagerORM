import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args){
        Rays ray = new Rays(3, null);
        DatabaseProtocol.getData();

        ManagerORM<Rays> managerORM = new ManagerORM<>(Rays.class, DatabaseProtocol.url, DatabaseProtocol.login, DatabaseProtocol.password);
        managerORM.create();
        managerORM.update(new Rays(1, new Rays(2, null)));
        managerORM.insert(ray);
        ray.setName("Name 2");
        managerORM.update(ray);
        managerORM.dropTable();

        //managerORM.dropTable();

    }
}
