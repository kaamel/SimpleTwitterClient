package com.kaamel.simpletwitterclient.twitteritems;

import com.kaamel.simpletwitterclient.models.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

import org.parceler.Parcel;

/**
 * Created by kaamel on 10/6/17.
 */

@Table(database = TwitterDatabase.class, name = "Mention")
@Parcel(analyze={Tweet.class})
public class Mention extends Tweet {

    Mention() {

    }
}
