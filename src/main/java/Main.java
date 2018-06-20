import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args){
        DatabaseProtocol.getData();

        ManagerORM<Rays> managerORM = new ManagerORM<>(Rays.class, DatabaseProtocol.url, DatabaseProtocol.login, DatabaseProtocol.password);

        managerORM.create();
        managerORM.insert(new Rays(1, new Rays(2, null)));
//        managerORM.dropTable();

    }
}
