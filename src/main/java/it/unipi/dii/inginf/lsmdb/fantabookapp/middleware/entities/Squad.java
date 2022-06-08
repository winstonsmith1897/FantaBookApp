package it.unipi.dii.inginf.lsmdb.fantabookapp.middleware.entities;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.Value;

public class Squad {
    private String owner;
    private String ID = null;
    private String name;
    private int current_position = 4;
    private String urlImage = null;
    private boolean isFavourite = false;

    public Squad(String username, String id, int i) {
    }

    public Squad(String squad) {
        this.owner = owner;
        this.name = "My squad";
        this.current_position = 4;
    }

    public Squad(String owner,
                    String name) {
        this.owner = owner;
        this.name = name;
    }

    public Squad(String owner,
                    String ID,
                    String name) {
        this.owner = owner;
        this.ID = ID;
        this.name = name;
    }

    public Squad(String owner,
                    String ID,
                    String name,
                    String urlImage, int current_position) {
        this.owner = owner;
        this.ID = ID;
        this.name = name;
        this.urlImage = urlImage;
        this.current_position = current_position;
    }

    public Squad(Document mongoDocument) {
        String json = mongoDocument.toJson();
        JSONObject jsonObject = new JSONObject(json);
        System.out.println(jsonObject);
        //JSONArray arrJson = jsonObject.getJSONArray("createdSquads");
        //String[] arr = new String[arrJson.length()];
        //for (int i = 0; i < arrJson.length(); i++){
            ID = jsonObject.getString("squadId");
            //ID = arrJson.getJSONObject(i).getString("squadId");
            name  = jsonObject.getString("name");
            //name = arrJson.getJSONObject(i).getString("name");
        current_position = jsonObject.getInt("current_position");
        //current_position = arrJson.getJSONObject(i).getInt("current_position");
            if (jsonObject.has("isFavourite"))
                 isFavourite = true;
            if (jsonObject.has("urlImage"))
                urlImage = jsonObject.getString("urlImage");

            //System.out.println("ID");
            //System.out.println(ID);
            System.out.println("NAME");
            System.out.println(name);
            System.out.println("POSITION");
            System.out.println(current_position);
            //System.out.println("IMAGE");
            //System.out.println(urlImage);
    //}

    }

    public Squad(Document mongoDocument,
                    String owner){
        this(mongoDocument);
        this.owner = owner;
    }

    public Squad(Value squadNeo4jValue) {
        ID = squadNeo4jValue.get("squadId").asString();
        name = squadNeo4jValue.get("name").asString();
        urlImage = squadNeo4jValue.get("urlImage").asString();
        //current_position = squadNeo4jValue.get("current_position").asInt();
    }

    public Document toBsonDocument() {
        Document document = new Document("squadId", ID);

        document.append("name", name);
        if (isFavourite)
            document.append("isFavourite", true);
        if (urlImage != null)
            document.append("urlImage", urlImage);
        document.append("current_position", current_position);

        return document;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
    public void setCurrent_position(int current_position){
        this.current_position = current_position;
    }
    public int getCurrent_position() {
        return current_position;
    }
}