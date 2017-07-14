package main.java.db;

/**
 * Created by thedr on 5/31/2017.
 */
public class DbSchema {

    public static String name = "test";

     public static class table_employees {
         public static final String name = "employees";

         public static class cols {
             public static final String first_name = "first_name";
             public static final String last_name = "last_name";
             public static final String age       = "age";
             public static final String salary = "salary";
             public static final String id        = "id";
         }
    }

     public static class table_users {
         public static final String name = "users";

         public static class cols {
             public static final String username = "username";
             public static final String hash_pw = "hash_pw";
             public static final String id = "id";
             public static final String salt = "salt";
             public static final String prvlg_lvl = "prvlg_lvl"; //For application privileges. Will be used later on
             public static String avatar_id = "avatar_id";
         }
    }

    public static class table_userAvatars {
         public static final String name = "users_avatars";

         public static class cols {
             public static final String id = "id";
             public static final String path = "path";
         }
    }
    
    public static class table_childrenAvatars {
        public static final String name = "children_avatars";
    
        public static class cols {
            public static final String id = "id";
            public static final String path = "path";
        }
    }
    
    public static class table_children {
        public static final String name = "children";
        
        public static class cols {
            public static final String fname = "fname";
            public static final String lname = "lname";
            public static final String nickname = "nickname";
            public static final String birth_date = "birth_date";
            public static final String place_of_birth = "place_of_birth";
            public static final String id = "id";
            public static final String description = "description";
            public static final String gender = "gender";
        }
    }
    
    public static class table_places_of_birth {
        public static final String name = "locations";
        
        public static class cols {
            public static final String location = "location";
            public static final String id = "id";
        }
    }
    
    public static class table_genders {
        public static final String name = "genders";
        
        public static class cols {
            public static final String gender = "gender";
            public static final String id =  "id";
        }
    }
}
