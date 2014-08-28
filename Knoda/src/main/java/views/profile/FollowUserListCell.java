package views.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import models.User;

/**
 * Created by jeff on 7/31/2014.
 */
public class FollowUserListCell extends RelativeLayout {


    public TextView username;
    public ImageView verified;
    public Button follow;
    public NetworkImageView avatar;
    public View buttonCover;

    public FollowUserListCell(Context context) {
        super(context);
    }

    public FollowUserListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        username = (TextView) findViewById(R.id.follow_user_username_textview);
        verified = (ImageView) findViewById(R.id.follow_user_verified_checkmark);
        follow = (Button) findViewById(R.id.follow_user_button);
        avatar = (NetworkImageView) findViewById(R.id.follow_user_avatar_imageview);
        buttonCover = findViewById(R.id.follow_user_button_cover);
    }

    public void setUser(final User user, ImageLoader imageLoader, final FollowFeedFragment followFeedFragment) {
        buttonCover.setVisibility(GONE);
        username.setText(user.username);
        if (user.verified)
            verified.setVisibility(VISIBLE);
        else
            verified.setVisibility(INVISIBLE);
        if (user.following_id != null)
            follow.setBackgroundResource(R.drawable.follow_btn_active);
        else
            follow.setBackgroundResource(R.drawable.follow_btn);
        if (user.avatar != null)
            avatar.setImageUrl(user.avatar.small, imageLoader);
        follow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCover.setVisibility(VISIBLE);
                follow.setEnabled(false);
                followFeedFragment.followUser(user, follow, buttonCover);
            }
        });

    }
}
