public class Main {
    public static void main(String[] args){
        Rays ray = new Rays(3, null);
        DatabaseProtocol.getData();

        ManagerORM<Rays> managerORM = new ManagerORM<>(Rays.class, DatabaseProtocol.url, DatabaseProtocol.login, DatabaseProtocol.password);
        //Create table in DB
        managerORM.create();

        //This exaple of recoursive reference
        managerORM.insert(new Rays(1, new Rays(2, null)));


        managerORM.insert(ray);
        //Example of update in ORM
        ray.setName("Name 2");
        managerORM.update(ray);

        //Drop table in DB
        managerORM.dropTable();


    }
}
