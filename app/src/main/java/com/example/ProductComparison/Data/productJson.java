package com.example.ProductComparison.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Dillon Scott 1604465
 */


public class productJson {
    public String json;
    public JSONObject getItems;
    public JSONArray tasksArray;

    /**
     * takes in json respons from api and grabs inner array of products found
     * @param json
     */
    public productJson(String json){
        this.json = json;
    }

    public JSONArray getPriceList(){

        try {
            JSONObject jsonObject = new JSONObject(json);
             getItems = jsonObject.getJSONObject("items");
             tasksArray = getItems.getJSONArray("pricing");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasksArray;
    }

    /**
     * gets item object containing name field for the product searched
     * @return
     */
    public JSONObject getProductName(){
        try {
            JSONObject jsonObject = new JSONObject(json);
            getItems = jsonObject.getJSONObject("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getItems;
    }


}
