package db;

/**
 * Created by thedr on 5/31/2017.
 */
class DbSchema {

    static String name = "test";

     static class table_employees {
         static final String name = "employees";

         static class cols {
             static final String first_name = "first_name";
             static final String last_name = "last_name";
             static final String age       = "age";
             static final String salary = "salary";
             static final String id        = "id";
        }
    }

     static class table_users {
         static final String name = "users";

         static class cols {
             static final String username = "username";
             static final String hash_pw = "hash_pw";
             static final String id = "id";
             static final String salt = "salt";
             static final String prvlg_lvl = "prvlg_lvl"; //For application privileges. Will be used later on
        }
    }
}
