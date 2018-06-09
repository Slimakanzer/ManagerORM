import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args){
        DatabaseProtocol.getData();
        ManagerORM<Rays> manager = new ManagerORM<>(Rays.class, DatabaseProtocol.url,DatabaseProtocol.login,DatabaseProtocol.password);
        manager.create();
        manager.insert(new Rays());
    }
}
