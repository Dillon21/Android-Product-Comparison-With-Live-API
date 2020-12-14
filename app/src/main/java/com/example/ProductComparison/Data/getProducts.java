package com.example.ProductComparison.Data;

import okhttp3.Request;

/**
 * @author Dillon Scott 1604465
 */

public class getProducts {

    private Request request;
    public String url;

    //build okhttp request
    public getProducts(String barcode) {
        url = "https://product-data1.p.rapidapi.com/lookup?upc=";
        url = url.concat(barcode);

        request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", "a599abcaadmsh008ffcc80509f00p169676jsn61f9fd5418e3")
                .addHeader("x-rapidapi-host", "product-data1.p.rapidapi.com")
                .build();
    }

    public Request getRequest(){
        return request;
    }
}
