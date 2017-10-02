package com.kaamel.simpletwitterclient.models;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by kaamel on 10/1/17.
 */

@Database(name = TwitterDatabase.NAME, version = TwitterDatabase.VERSION)
public class TwitterDatabase {

    public static final String NAME = "RestClientDatabase";
    public static final int VERSION = 1;
}

