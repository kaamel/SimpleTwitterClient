package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kaamel on 9/28/17.
 */

public class TwitterErrors {
    //{"errors":[{"message":"Rate limit exceeded","code":88}]}

    @SerializedName("errors")
    public List<TwitterError> errors;

    public class TwitterError {

        @SerializedName("message")
        public String errorMessage;

        @SerializedName("code")
        public int code;
    }

}
