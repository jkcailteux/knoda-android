package views.predictionlists;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import java.util.regex.Pattern;

import models.Prediction;
import views.core.MainActivity;

/**
 * Created by nick on 1/27/14.
 */
public class PredictionListCell extends RelativeLayout {

    public TextView usernameTextView;
    public TextView bodyTextView;
    public NetworkImageView avatarImageView;
    public TextView timeStampsTextView;
    public ImageView voteImageView;

    public RelativeLayout usernameView;
    public RelativeLayout bodyView;

    public TextView commentCountTextView;
    public TextView resultTextView;
    public Prediction prediction;

    public RelativeLayout agreeView;
    public RelativeLayout disagreeView;
    public ImageView verifiedCheckmark;

    public RelativeLayout groupView;
    public TextView groupTextView;
    public ImageView groupIcon;

    public RelativeLayout walkthroughView;
    public RelativeLayout container;

    public LinearLayout textContainer;

    private boolean agreed;
    private boolean disagreed;

    public PredictionListCell(Context context) {
        super(context);
        initView(context);
    }

    public PredictionListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_cell_predictions, this);
        usernameTextView = (TextView) findViewById(R.id.prediction_cell_username_textview);
        bodyTextView = (TextView) findViewById(R.id.prediction_cell_body_textview);
        avatarImageView = (NetworkImageView) findViewById(R.id.prediction_cell_avatar_imageview);
        timeStampsTextView = (TextView) findViewById(R.id.prediction_cell_timestamps_textview);
        bodyView = (RelativeLayout) findViewById(R.id.prediction_cell_body_view);
        voteImageView = (ImageView) findViewById(R.id.prediction_cell_vote_image);
        usernameView = (RelativeLayout) findViewById(R.id.prediction_cell_top_container);
        commentCountTextView = (TextView) findViewById(R.id.prediction_cell_comment_textview);
        resultTextView = (TextView) findViewById(R.id.prediction_cell_result_textview);
        agreeView = (RelativeLayout) findViewById(R.id.prediction_cell_agreeview);
        disagreeView = (RelativeLayout) findViewById(R.id.prediction_cell_disagreeview);
        verifiedCheckmark = (ImageView) findViewById(R.id.prediction_cell_verified_checkmark);
        groupView = (RelativeLayout) findViewById(R.id.prediction_cell_group_container);
        groupTextView = (TextView) findViewById(R.id.prediction_cell_group_textview);
        walkthroughView = (RelativeLayout) findViewById(R.id.prediction_cell_swipe_walkthrough);
        container = (RelativeLayout) findViewById(R.id.prediction_cell_container);
        textContainer = (LinearLayout) findViewById(R.id.prediction_cell_username_textview_container);
        groupIcon = (ImageView) findViewById(R.id.prediction_cell_group_icon);


    }

    public void setAgree(boolean agree) {
        int resId = agree ? R.drawable.agree_marker : R.drawable.disagree_marker;
        voteImageView.setImageResource(resId);
        if (agree && agreed)
            return;
        if (!agree && disagreed)
            return;

        if (agree) {
            if (disagreed) {
                disagreed = false;
                prediction.disagreedCount--;
            }

            agreed = true;
            prediction.agreedCount++;
        } else {
            if (agreed) {
                agreed = false;
                prediction.agreedCount--;
            }

            disagreed = true;
            prediction.disagreedCount++;
        }
    }

    public void setPrediction(Prediction prediction, MainActivity mainActivity, boolean enablehashtags) {
        this.prediction = prediction;
        if (prediction == null)
            return;
        update(mainActivity, enablehashtags);
    }


    public void update(final MainActivity mainActivity, boolean enableHashtags) {

        if (enableHashtags) {
            SpannableString spannableString =
                    new SpannableString(prediction.body);
            bodyTextView.setText(spannableString);
            Pattern hashtagPattern = Pattern.compile("[#]+[A-Za-z0-9-_]+\\b");
            String hashtagScheme = "content://com.knoda.knoda.hashtag/";
            Linkify.addLinks(bodyTextView, hashtagPattern, hashtagScheme);

            Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
            String mentionScheme = "content://com.knoda.knoda.hashtag/";
            Linkify.addLinks(bodyTextView, mentionPattern, mentionScheme);

            Pattern webPattern = Patterns.WEB_URL;
            String webScheme = "content://com.knoda.knoda.hashtag/";
            Linkify.addLinks(bodyTextView, webPattern, webScheme);

            stripUnderlines(bodyTextView);
        } else
            bodyTextView.setText(prediction.body);

        usernameTextView.setText(prediction.username);
        timeStampsTextView.setText(prediction.getMetdataString());
        commentCountTextView.setText(prediction.commentCount.toString());

        if (prediction.verifiedAccount)
            verifiedCheckmark.setVisibility(VISIBLE);
        else
            verifiedCheckmark.setVisibility(INVISIBLE);

        agreed = (prediction.challenge != null && prediction.challenge.agree);
        disagreed = (prediction.challenge != null && !prediction.challenge.agree);

        updateVoteImage();

        if (prediction.contest_name != null) {
            groupView.setVisibility(VISIBLE);
            groupTextView.setText(prediction.contest_name.toString());
            groupIcon.setImageResource(R.drawable.contest_prediction_badge);
        } else if (prediction.groupName != null) {
            groupView.setVisibility(VISIBLE);
            groupTextView.setText(prediction.groupName.toString());
        } else if (prediction.embedLocations != null && prediction.embedLocations.size() > 0) {
            groupView.setVisibility(VISIBLE);
            groupTextView.setText(prediction.embedLocations.get(0).domain);
            groupIcon.setImageResource(R.drawable.embed_prediction_badge);
        } else {
            groupView.setVisibility(GONE);
        }

        avatarImageView.setTag(prediction.userId);
        textContainer.setTag(prediction.userId);

    }


    private void updateVoteImage() {

        if (prediction.isReadyForResolution && (prediction.challenge != null && prediction.challenge.isOwn) && !prediction.settled)
            voteImageView.setImageResource(R.drawable.prediction_alert);
        else if (prediction.challenge != null) {

            if (prediction.challenge.isOwn) {
                voteImageView.setImageBitmap(null);
            } else {
                voteImageView.setImageResource(getVoteImage());
            }
        } else {
            voteImageView.setImageBitmap(null);
        }
        if (prediction.challenge == null || !prediction.settled) {
            resultTextView.setText("");
            return;
        }

        boolean win = false;
        if (prediction.challenge.agree)
            win = prediction.outcome ? true : false;
        else
            win = prediction.outcome ? false : true;

        if (win) {
            resultTextView.setText("W");
            resultTextView.setTextColor(getResources().getColor(R.color.knodaLightGreen));
        } else {
            resultTextView.setText("L");
            resultTextView.setTextColor(getResources().getColor(R.color.red));

        }
    }

    private int getVoteImage() {
        if (prediction.challenge == null)
            return 0;

        if (prediction.challenge.agree)
            return R.drawable.agree_marker;
        else if (!prediction.challenge.agree)
            return R.drawable.disagree_marker;

        return 0;
    }

    public int testVoteImage(Prediction p) {
        prediction = p;
        return getVoteImage();
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL(), getContext());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }


    private class URLSpanNoUnderline extends URLSpan {
        Context context;

        public URLSpanNoUnderline(String url, Context c) {
            super(url);
            context = c;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(context.getResources().getColor(R.color.knodaLightGreen));
            if (getURL().contains("@")) {
                ds.setFakeBoldText(true);
            } else if (getURL().contains(("#"))) {
                ds.setFakeBoldText(true);
            } else
                ds.setFakeBoldText(true);
        }
    }

    //THIS CODE IS FOR WE WANT BOTH SWIPE AND HASHTAGS
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView widget = (TextView) this.bodyTextView;
        Object text = widget.getText();
        if (text instanceof Spanned) {
            Spannable buffer = (Spannable) text;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off,
                        ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);//do hashtag and mention logic here
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }
                    return true;
                }
            }

        }

        return false;
    }
    */

}