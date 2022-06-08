package middleware.dao;


import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.PlayerDAOImpl;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PlayerDAOImplTest {

    private PlayerDAOImpl Player;

    @Test
    public void WHEN_cratePlayer_has_Player_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Player.createPlayer(null));
    }


    @Test
    public void WHEN_filterPlayer_has_maxNumber_non_positive_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Player.filterPlayer("test", 0, "name"));
    }

    @Test
    public void WHEN_filterPlayer_has_attributeField_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Player.filterPlayer("test", 3, null));
    }

    @Test
    public void WHEN_filterPlayer_has_attributeField_that_does_not_exist_THEN_return_empty_list() {
        try {
            Assertions.assertEquals(0, Player.filterPlayer("test", 3, "test").size());
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WHEN_findArtistsWithMostNumberOfHit_has_maxNumber_non_positive_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Player.findPlayersWithMostNumberOfGoal(2, 0));
    }

    @Test
    public void WHEN_getPlayerByNAME_has_PlayerName_Null_THEN_return_NULL() {
        Assertions.assertNull(Player.getPlayerByName(null));
    }

    @Test
    public void WHEN_incrementLikeCount_has_Player_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Player.incrementLikeCount(null));
    }

    @Test
    public void WHEN_decrementLikeCount_has_Player_null_THEN_throws_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Player.decrementLikeCount(null));
    }

    @BeforeEach
    public void init(){
        Player = new PlayerDAOImpl();

    }


}
