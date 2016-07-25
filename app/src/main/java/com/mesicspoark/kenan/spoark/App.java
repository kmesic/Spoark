package com.mesicspoark.kenan.spoark;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by kenme_000 on 11/15/2015.
 */
public class App extends Application {
     @Override public void onCreate() {
         super.onCreate();

         // Enable Local Datastore.
         Parse.enableLocalDatastore(this);

         Parse.initialize(new Parse.Configuration.Builder(this)
                 .applicationId("spoark")
                 .clientKey(null)
                 .server("http://192.168.1.181:1337/parse/")
                 .build()
         );
         //Parse.initialize(this, "whpfOJPCSABe6gyl9znlDSivZZfnOrnhLIPYxDvz", "6MCNggdLaTNf3P3fI9Z5hHaN2J0ZNb4of1hbwJ65");
     }
}
