package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nick on 2/1/14.
 */
public class ActivityItem extends BaseModel {

    public Integer id;

    @SerializedName("prediction_id")
    public Integer predictionId;

    @SerializedName("activity_type")
    public ActivityItemType type;

    @SerializedName("user_id")
    public Integer userId;

    //@SerializedName("created_at")
    public String creationDate;

    @SerializedName("title")
    public String title;

    @SerializedName("body")
    public String body;

    @SerializedName("target")
    public String target;

    @SerializedName("seen")
    public boolean seen;

    @SerializedName("image_url")
    public String image_url;

    @SerializedName("shareable")
    public boolean shareable = true;


//    public String getCreationString() {
//        return "made " + DateUtil.getPeriodString(creationDate) + " ago";
//    }
}
