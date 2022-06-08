package middleware.dao;


import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.SquadDAOImpl;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.User;

import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SquadDAOImplTest {
    private it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.SquadDAO SquadDAO;

    @Test
    public void WHEN_getSquad_has_SquadID_null_THEN_return_null() {
        try {
            Assertions.assertNull(SquadDAO.getSquad(null));
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WHEN_getFavourite_has_user_null_THEN_return_null() {
        try {
            Assertions.assertNull(SquadDAO.getFavourite(null));
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WHEN_isSongFavourite_has_user_null_THEN_return_false() {
        Assertions.assertFalse(SquadDAO.isPlayerFavourite(null, new Player()));
    }

    @Test
    public void WHEN_isSongFavourite_has_song_null_THEN_return_false() {
        Assertions.assertFalse(SquadDAO.isPlayerFavourite(new User(""), null));
    }

    @Test
    public void WHEN_getAllSongs_has_Squad_null_THEN_return_empty_list() {
        try {
            Assertions.assertEquals(0, SquadDAO.getAllPlayers(null).size());
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WHEN_getSuggestedSquads_has_user_null_THEN_return_empty_list() {
        try {
            Assertions.assertEquals(0, SquadDAO.getSuggestedSquads(null).size());
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WHEN_getSuggestedSquads_has_limit_non_positive_THEN_return_empty_list() {
        try {
            Assertions.assertEquals(0, SquadDAO.getSuggestedSquads(new User(""), -1).size());
        } catch (ActionNotCompletedException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void init(){
        SquadDAO = new SquadDAOImpl();
    }
}
