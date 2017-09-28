package com.kaamel.simpletwitterclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kaamel on 9/28/17.
 */

public class TwitterErrors {
    //{"errors":[{"message":"Rate limit exceeded","code":88}]}

    @SerializedName("errors")
    List<TwitterError> errors;

    class TwitterError {

        @SerializedName("message")
        String errorMessage;

        @SerializedName("code")
        int code;
    }

}
