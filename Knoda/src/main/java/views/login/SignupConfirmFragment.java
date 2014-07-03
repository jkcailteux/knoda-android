package views.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.knoda.knoda.R;

import butterknife.InjectView;
import butterknife.OnClick;
import models.User;
import pubsub.LoginFlowDoneEvent;
import views.core.BaseDialogFragment;

/**
 * Created by nick on 6/11/14.
 */
public class SignupConfirmFragment extends BaseDialogFragment {

    @InjectView(R.id.confirm_imageview)
    NetworkImageView imageView;

    @InjectView(R.id.confirm_username)
    TextView textView;

    public SignupConfirmFragment() {
    }

    public static SignupConfirmFragment newInstance() {
        SignupConfirmFragment fragment = new SignupConfirmFragment();
        return fragment;
    }

    @OnClick(R.id.confirm_start_predicting_button)
    void onClick() {
        try {
            sharedPrefManager.setShouldShowVotingWalkthrough(true);
            bus.post(new LoginFlowDoneEvent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        dismissFade();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_confirm, container, false);
        updateBackground();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        User u = userManager.getUser();

        if (u == null)
            return;

        textView.setText(u.username + ",");

        imageView.setImageUrl(u.avatar.small, networkingManager.getImageLoader());
    }

}