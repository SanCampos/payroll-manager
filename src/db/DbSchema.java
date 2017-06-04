package db;

/**
 * Created by thedr on 5/31/2017.
 */
public class DbSchema {

    public static String name = "test";

    public static class table_employees {
        public static String name = "employees";

        public static class cols {
            public static String first_name = "first_name";
            public static String last_name = "last_name";
            public static String age       = "age";
            public static String salary = "salary";
            public static String id        = "id";
        }
    }

    public static class table_users {
        public static String name = "users";

        public static class cols {
            public static String username = "username";
            public static String password = "password";
            public static String id = "id";
            public static String prvlg_lvl = "prvl_lvl"; //For application privileges. Will be used later on
        }
    }
}
