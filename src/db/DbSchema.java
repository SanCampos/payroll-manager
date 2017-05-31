package db;

/**
 * Created by thedr on 5/31/2017.
 */
public class DbSchema {

    public static String name = "test";

    public static class TEST_TABLE {
        public static String name = "test";

        public static class cols {
            public static String first_name = "first_name";
            public static String last_name = "last_name";
            public static String age       = "age";
            public static String salary = "salary";
            public static String id        = "id";
        }
    }
}
