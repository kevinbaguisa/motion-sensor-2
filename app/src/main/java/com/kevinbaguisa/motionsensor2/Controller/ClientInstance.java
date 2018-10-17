package com.kevinbaguisa.motionsensor2.Controller;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientInstance {

//    private static final String BASEURL = "http://10.1.1.10:3000/";
    private static final String BASEURL = "http://10.0.3.2:3000/";

    private static ClientInstance mInstance;
    private static Retrofit retrofit;

    public ClientInstance(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized ClientInstance getInstance(){
        if(mInstance == null){
            mInstance = new ClientInstance();
        }
        return mInstance;
    }

    public RESTAPI getAPI(){
        return retrofit.create(RESTAPI.class);
    }

}
