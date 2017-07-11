package main.java.db;

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
             public static String avatar_id = "avatar_id";
         }
    }

    static class table_userAvatars {
         static final String name = "user_avatars";

         static class cols {
             static final String id = "id";
             static final String path = "path";
         }
    }
    
    static class table_childrenAvatars {
        static final String name = "children_avatars";
    
        static class cols {
            static final String id = "id";
            static final String path = "path";
        }
    }
    
    static class table_children {
        static final String name = "children";
        
        static class cols {
            static final String fname = "fname";
            static final String lname = "lname";
            static final String nickname = "nickname";
            static final String age = "age";
            static final String place_of_birth = "place_of_birth";
            static final String id = "id";
            static final String description = "description";
            static final String gender = "gender";
        }
    }
    
    static class table_places_of_birth {
        static final String name = "places_of_birth";
        
        static class cols {
            static final String birthPlace = "birthPlace";
            static final String id = "id";
        }
    }
    
    static class table_genders {
        static final String name = "genders";
        
        static class cols {
            static final String gender = "gender";
            static final String id =  "id";
        }
    }
}
