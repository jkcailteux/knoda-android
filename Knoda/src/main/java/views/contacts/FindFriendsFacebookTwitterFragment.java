package views.contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.knoda.knoda.R;

import adapters.PagingAdapter;
import adapters.UserContactAdapter;
import butterknife.OnClick;
import models.ServerError;
import models.SocialAccount;
import models.User;
import models.UserContact;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import unsorted.Logger;
import views.core.BaseListFragment;

public class FindFriendsFacebookTwitterFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<UserContact> {

    FindFriendsActivity parent;
    String filter;
    boolean folllowedAll = false;

    public FindFriendsFacebookTwitterFragment() {
    }

    public static FindFriendsFacebookTwitterFragment newInstance(FindFriendsActivity parent, String filter) {
        FindFriendsFacebookTwitterFragment fragment = new FindFriendsFacebookTwitterFragment();
        fragment.parent = parent;
        fragment.filter = filter;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent.bus.register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("FindFriends" + filter);
        pListView.setMode(PullToRefreshBase.Mode.DISABLED);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public PagingAdapter getAdapter() {
        int type = 0;
        if (filter.equals("facebook"))
            type = FindFriendsListCellHeader.FACEBOOK;
        else if (filter.equals("twitter"))
            type = FindFriendsListCellHeader.TWITTER;
        return new UserContactAdapter(type, getActivity(), this, parent.networkingManager.getImageLoader(), parent);
    }

    @Override
    public void getObjectsAfterObject(UserContact userContact, final NetworkListCallback<UserContact> callback) {
        parent.networkingManager.getFriendsOnKnoda(filter, callback);
    }

    @Override
    public String noContentString() {
        if (filter.equals("facebook"))
            return "No Facebook friends on Knoda. Get your friends on Knoda!";
        else if (filter.equals("twitter"))
            return "No one you follow on twitter is on Knoda. Get your friends on Knoda!";
        else
            return "No friends on Knoda found. Get your friends on Knoda!";
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!folllowedAll && isVisibleToUser)
            if (adapter != null) {
                ((UserContactAdapter) adapter).followAll(true);
                folllowedAll = true;
            }
    }


}