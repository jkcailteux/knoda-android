package views.group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import models.Group;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import pubsub.NewGroupEvent;
import views.avatar.GroupAvatarChooserActivity;
import views.core.BaseFragment;

public class AddGroupFragment extends BaseFragment {

    private static final int PHOTO_RESULT_CODE = 123123129;
    @InjectView(R.id.add_group_name_edittext)
    EditText nameEditText;
    @InjectView(R.id.add_group_description_edittext)
    EditText descriptionEditText;
    @InjectView(R.id.add_group_avatar_imageview)
    ImageView avatarImageView;
    private File avatarFile;

    public AddGroupFragment() {
    }

    public static AddGroupFragment newInstance() {
        AddGroupFragment fragment = new AddGroupFragment();
        return fragment;
    }

    @OnClick(R.id.add_group_avatar_imageview)
    void onClickAvatar() {
        getActivity().findViewById(R.id.add_group_avatar_imageview).setEnabled(false);
        Intent intent = new Intent(getActivity(), GroupAvatarChooserActivity.class);
        intent.putExtra("cancelable", true);
        startActivityForResult(intent, PHOTO_RESULT_CODE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setTitle("CREATE GROUP");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.submit, menu);
        menu.removeGroup(R.id.default_menu_group);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.action_submit)
            submitGroup();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideKeyboard();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }


    private void submitGroup() {

        hideKeyboard();
        if (!validate())
            return;

        Group group = new Group();
        FlurryAgent.logEvent("CREATE_GROUP_START");

        group.name = nameEditText.getText().toString();
        group.description = descriptionEditText.getText().toString();

        spinner.show();

        networkingManager.submitGroup(group, new NetworkCallback<Group>() {
            @Override
            public void completionHandler(Group object, ServerError error) {
                if (avatarFile != null) {
                    networkingManager.uploadGroupAvatar(object.id, avatarFile, new NetworkCallback<Group>() {
                        @Override
                        public void completionHandler(Group group, ServerError error) {
                            if (error != null) {
                                errorReporter.showError(error);
                                spinner.hide();
                            } else {
                                finish(group);
                                FlurryAgent.logEvent("CREATE_GROUP_SUCCESS");
                            }
                        }
                    });
                } else {
                    finish(object);
                }
            }
        });
    }

    private void finish(final Group group) {
        userManager.refreshGroups(new NetworkListCallback<Group>() {
            @Override
            public void completionHandler(ArrayList<Group> object, ServerError error) {
                bus.post(new NewGroupEvent(group));
                popFragment();
                pushFragment(GroupSettingsFragment.newInstance(group, null));
                spinner.hide();
            }
        });
    }

    private boolean validate() {
        String errorMessage = null;
        if (nameEditText.getText().length() == 0)
            errorMessage = "Please enter a name.";
        else if (descriptionEditText.getText().length() == 0)
            errorMessage = "Please select a description.";
        if (errorMessage != null) {
            errorReporter.showError(errorMessage);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        avatarImageView.setEnabled(true);
        if (data == null)
            return;
        if (data.getExtras().containsKey(MediaStore.EXTRA_OUTPUT) && resultCode == Activity.RESULT_OK) {
            String avatarPath = data.getExtras().getString(MediaStore.EXTRA_OUTPUT);
            avatarFile = new File(avatarPath);
            Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
            avatarImageView.setImageBitmap(bitmap);
        }
    }
}
