package views.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knoda.knoda.R;

/**
 * Created by nick on 2/13/14.
 */
public class DetailsActionbar extends LinearLayout {

    public interface DetailsActionBarDelegate {
        void onComments();
        void onTally();
        void onSimilar();
        void onShare();
    }

    private DetailsActionBarDelegate delegate;

    public ImageView commentImageView;
    public TextView commentTextView;

    public ImageView tallyImageView;
    public TextView tallyTextView;

    public ImageView similarImageView;
    public ImageView shareImageView;

    public DetailsActionbar(Context context) {
        super(context);
        initView(context);
    }

    public DetailsActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DetailsActionbar(Context context, DetailsActionBarDelegate delegate) {
        super(context);
        initView(context);
        this.delegate = delegate;
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_details_actionbar, this);

        commentImageView = (ImageView)findViewById(R.id.details_action_comment_imageview);
        commentTextView = (TextView)findViewById(R.id.details_action_comment_textview);

        tallyImageView = (ImageView)findViewById(R.id.details_action_tally_imageview);
        tallyTextView = (TextView)findViewById(R.id.details_action_tally_textview);

        similarImageView = (ImageView)findViewById(R.id.details_action_similar_imageview);
        shareImageView = (ImageView)findViewById(R.id.details_action_share_imageview);

        setBackgroundColor(getResources().getColor(R.color.lightGray));

        commentImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onComments();
            }
        });

        tallyImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onTally();
            }
        });

        shareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onShare();
            }
        });

        similarImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.onSimilar();
            }
        });

    }
}