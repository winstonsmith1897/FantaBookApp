package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities;

import org.bson.Document;
import org.neo4j.driver.Record;

import java.util.concurrent.ThreadLocalRandom;


public class Player {
    private String ID;
    private String name;
    private String firstName;
    private String secondName;
    private String nationality;
    private String position;
    private String team;
    private String league;
    private String age;
    private double rating;
    private int likeCount;
    private String Assists;
    private int Goals;
    private int RedCard;
    private int YellowCard;
    private String ImageUrl;
    private String Appearances;
    private String Value;
    private String minutes;
    private String ShirtNumber;
    private String conceded;
    private int clean_sheets;


    public Player() {

    }

    /**
     * Constructs a player given a Neo4j Record initializing only fields that correspond to player Node properties.
     * @param playerNeo4jRecord
     */
    public Player(Record playerNeo4jRecord) {

        ID = playerNeo4jRecord.get("playerId").asString();
        name = playerNeo4jRecord.get("name").asString();
        position = playerNeo4jRecord.get("position").asString();
        nationality = playerNeo4jRecord.get("nationality").asString();
        ImageUrl = playerNeo4jRecord.get("imageUrl").asString();
    }
    /**
     * Constructs a player given a json string.
     * @param playerDocument
     */


    public Player(Document playerDocument){

        ID = playerDocument.getString("_id");
        nationality = playerDocument.getString("nationality");
        name = playerDocument.getString("name");
        position = playerDocument.getString("position");
        rating = playerDocument.getDouble("rating");
        likeCount = playerDocument.getInteger("likeCount");
        team = playerDocument.getString("team");
        league = playerDocument.getString("league");
        age = playerDocument.getString("age");
        if (!position.equals("Goalkeeper")) {
            Assists = playerDocument.getString("assists");
            Goals = playerDocument.getInteger("goals");
        }
        RedCard = playerDocument.getInteger("RedCard");
        YellowCard = playerDocument.getInteger("YellowCard");
        ImageUrl = playerDocument.getString("imageUrl");
        Appearances = playerDocument.getString("Appearances");
        Value = playerDocument.getString("Value");
        ShirtNumber = playerDocument.getString("ShirtNumber");
        minutes = playerDocument.getString("minutes");
        if (position.equals("Goalkeeper")) {
            clean_sheets = playerDocument.getInteger("clean-sheets");
            conceded = playerDocument.getString("conceded");
        }
    }

    /**
     * @return
     */

    public Document toBsonDocument() {

        Document playerDocument = new Document("_id", ID)
                .append("name", name);

        Document Position = playerDocument.append("position", position);
        //System.out.println(Position.containsValue("Goalkeeper"));

        if (Position.containsValue("Goalkeeper")) {
            playerDocument.append("conceded", conceded);

            playerDocument.append("clean-sheets", clean_sheets);

        }

        else {
            playerDocument.append("goals", Goals);

            playerDocument.append("assists", Assists);
        }

        playerDocument.append("age", age);

        playerDocument.append("rating", rating);

        playerDocument.append("nationality", nationality);

        playerDocument.append("likeCount", likeCount);


        playerDocument.append("team", team);

        playerDocument.append("league", league);

        playerDocument.append("RedCard", RedCard);

        playerDocument.append("YellowCard", YellowCard);

        playerDocument.append("Appearances", Appearances);

        playerDocument.append("minutes", minutes);

        playerDocument.append("Value", Value);

        playerDocument.append("ShirtNumber", ShirtNumber);

        playerDocument.append("imageUrl", ImageUrl);



        return playerDocument;
    }


    public Player(String ID,
         String name,
         String firstName,
         String secondName,
         String nationality,
         String position,
         String team,
         String league,
                  String age,
                  Double rating,
         int likeCount,
                  String Assists,
                  int Goals,
                  int RedCard,
                  int YellowCard,
          String Appearances,
          String Value,
          String minutes,
          String ShirtNumber,
          String ImageUrl,
                  String conceded,
                  int clean_sheets) {
        this.ID = ID;
        this.name = name;
        this.firstName = firstName;
        this.secondName = secondName;
        this.nationality = nationality;
        this.position = position;
        this.rating = rating;
        this.likeCount = likeCount;
        this.team = team;
        this.league = league;
        this.age = age;
        this.Assists = Assists;
        this.Goals = Goals;
        this.RedCard = RedCard;
        this.YellowCard = YellowCard;
        this.ImageUrl = ImageUrl;
        this.Appearances = Appearances;
        this.Value = Value;
        this.minutes = minutes;
        this.ShirtNumber = ShirtNumber;
        this.conceded = conceded;
        this.clean_sheets = clean_sheets;
    }

    public String getID() { return ID; }

    public void setID(String ID) { this.ID = ID; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppearances() {
        return Appearances;
    }

    public void setAppearances(String Appearances) {
        this.Appearances = Appearances;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getValue() {
        return Value;
    }

    public String setValue(String Value) {
        this.Value = Value;
        return Value;
    }

    public String getShirtNumber() {
        return ShirtNumber;
    }

    public void setShirtNumber(String ShirtNumber) {
        this.ShirtNumber = ShirtNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }


    public String getPosition() {
        return position;
    }

    public String setPosition(String position) {
        this.position = position;
        return position;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getGoals() {
        return Goals;
    }

    public void setGoals(int Goals) {
        this.Goals = Goals;
    }

    public String getAssists() {
        return Assists;
    }

    public void setAssists(String Assists) {
        this.Assists = Assists;
    }

    public int getRedCard() {
        return RedCard;
    }

    public void setRedCard(int RedCard) {
        this.RedCard = RedCard;
    }

    public int getYellowCard() {
        return YellowCard;
    }

    public void setYellowCard(int YellowCard) {
        this.YellowCard = YellowCard;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int i) {
        int randomNum = ThreadLocalRandom.current().nextInt(i * 10, i * 100 + 1);
        this.likeCount = randomNum;
    }

    public String getImageUrl() {
        System.out.println("IMAGE");
        System.out.println(ImageUrl);
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getConceded() {
        return conceded;
    }

    public void setConceded(String conceded) {
        this.conceded = conceded;
    }

    public int getCleanSheets() {
        return clean_sheets;
    }

    public void setClean_sheets(int clean_sheets) {
        this.clean_sheets = clean_sheets;
    }


}
