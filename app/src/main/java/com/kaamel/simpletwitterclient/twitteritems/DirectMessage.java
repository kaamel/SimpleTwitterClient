package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;
import com.kaamel.simpletwitterclient.models.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.parceler.Parcel;

/**
 * Created by kaamel on 10/8/17.
 */

@Table(database = TwitterDatabase.class)
@Parcel(analyze={DirectMessage.class})
public class DirectMessage {

    /*
    "id": 240136858829479936,
    "text": "booyakasha"
     */

    @Column
    @SerializedName("text")
    String body;

    @PrimaryKey
    @Column
    @SerializedName("text")
    long id;

    @Column
    @SerializedName("created_at")
    String createdAt;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("recipient")
    User recipient;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("sender")
    User sender;

    DirectMessage() {

    }

    public long getId () {
        return id;
    }

    public String getBody() {
        return body;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}
