package middleware.dao;


import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.UserDAOImpl;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class UserDAOImplTest {

    private UserDAOImpl user;

    @Test
    public void WHEN_getUserByUsername_has_username_null_THEN_return_null() {
        try {
            Assertions.assertNull(user.getUserByUsername(null));
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void WHEN_getUserByPartialUsername_has_username_null_THEN_return_empty_list() {
        try {
            Assertions.assertEquals(0, user.getUserByPartialUsername(null).size());
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WHEN_getSuggestedUsers_has_limit_not_positive_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.getSuggestedUsers(new User("test"), 0));
    }

    @Test
    public void WHEN_getSuggestedUsers_has_user_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.getSuggestedUsers(null));
    }

    @Test
    public void WHEN_checkUserPassword_has_wrong_credential_THEN_return_false() {
        Assertions.assertFalse(user.checkUserPassword("ciccio", "caputo"));
    }

    @Test
    public void WHEN_checkUserPassword_has_a_combination_of_parameters_null_THEN_return_false() {
        Assertions.assertFalse(user.checkUserPassword(null, null));
        Assertions.assertFalse(user.checkUserPassword("test", null));
        Assertions.assertFalse(user.checkUserPassword(null, "test"));
    }

    @Test
    public void WHEN_followUser_has_both_parameters_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.followUser(null, null));
    }

    @Test
    public void WHEN_followUser_has_first_parameter_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.followUser(null, new User("test")));
    }

    @Test
    public void WHEN_followUser_has_second_parameter_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.followUser(new User("test"), null));
    }


    @BeforeEach
    public void init(){
        user = new UserDAOImpl();

    }

}