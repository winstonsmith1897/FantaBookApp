package it.unipi.dii.inginf.lsmdb.fantabookapp.databasePopulation;

import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.dao.PlayerDAOImpl;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities.Player;
import it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.exception.ActionNotCompletedException;
import org.bson.types.ObjectId;
import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerPopulation {
    public static void main(String[] args) throws ActionNotCompletedException, IOException {
            String player_list = "C:\\Users\\marco\\OneDrive\\Desktop\\playerlist.txt";
            populateWithPlayer(player_list);
    }
    private static void populateWithPlayer(String file) throws ActionNotCompletedException, IOException {

        JSONObject player;
        Player playerToInsert = new Player();
        try {
            File myObj = new File(file);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                player = new JSONObject(data);
                //System.out.println("\n\n");
                System.out.println(player);
                playerToInsert.setID(new ObjectId().toString());
                //playerToInsert.setID(player.getString("_id"));
                playerToInsert.setName(player.getString("name"));
                String Position = playerToInsert.setPosition(player.getString("position"));
                //System.out.println(Position);
                playerToInsert.setImageUrl(player.getString("imageUrl"));
                playerToInsert.setNationality(player.getString("nationality"));
                playerToInsert.setTeam(player.getString("team"));
                playerToInsert.setMinutes(player.getString("minutes"));
                playerToInsert.setShirtNumber(player.getString("shirt_number"));
                playerToInsert.setAppearances(player.getString("appearances"));
                playerToInsert.setLeague(player.getString("league"));

                if (Position.equals("Goalkeeper")) {
                    try {
                        playerToInsert.setConceded(player.getString("conceded"));
                    } catch (Exception e) {
                        playerToInsert.setConceded("0");
                    }
                    playerToInsert.setClean_sheets(player.getInt("clean-sheets"));
                } else {
                    try {
                        playerToInsert.setAssists(player.getString("assists"));
                    } catch (Exception e) {
                        playerToInsert.setAssists("0");
                    }
                    playerToInsert.setGoals(player.getInt("goals"));
                }
                String Value = playerToInsert.setValue(player.getString("value"));
                playerToInsert.setAge(player.getString("age"));
                playerToInsert.setRedCard(player.getInt("red_card"));
                playerToInsert.setRating(player.getDouble("rating"));
                playerToInsert.setYellowCard(player.getInt("yellow"));
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(Value);
                int Likes = 0;
                while (m.find()) {
                    Likes = Integer.parseInt(m.group());
                    break;
                }
                playerToInsert.setLikeCount(Likes);
                //System.out.println(playerToInsert);

                new PlayerDAOImpl().createPlayer(playerToInsert);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
    }
