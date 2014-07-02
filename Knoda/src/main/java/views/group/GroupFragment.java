package views.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import adapters.GroupAdapter;
import adapters.PagingAdapter;
import butterknife.InjectView;
import models.Group;
import networking.NetworkListCallback;
import views.core.BaseFragment;
import views.predictionlists.GroupPredictionListFragment;

public class GroupFragment extends BaseFragment implements PagingAdapter.PagingAdapterDatasource<Group> {

    @InjectView(R.id.group_list_view)
    ListView listView;

    PagingAdapter adapter;

    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("Groups_Screen");
        setTitle("GROUPS");

        adapter = new GroupAdapter(getActivity(), this, networkingManager.getImageLoader());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BaseFragment fragment;
                if (view instanceof CreateGroupHeaderView) {
                    fragment = AddGroupFragment.newInstance();
                } else {
                    GroupListCell v = ((GroupListCell) view);
                    fragment = GroupPredictionListFragment.newInstance(v.group);
                }
                pushFragment(fragment);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.loadPage(0);
    }

    @Override
    public void getObjectsAfterObject(Group object, NetworkListCallback<Group> callback) {
        //networkingManager.getGroups(callback);
        callback.completionHandler(userManager.groups, null);
    }


    @Override
    public String noContentString() {
        return "You are not in any groups. Create a group with your friends to start making private predictions.";
    }
}


