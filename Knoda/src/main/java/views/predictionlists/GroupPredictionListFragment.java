package views.predictionlists;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;

import adapters.GroupPredictionAdapter;
import adapters.PagingAdapter;
import factories.GsonF;
import models.Group;
import models.Prediction;
import networking.NetworkListCallback;
import pubsub.ChangeGroupEvent;
import views.group.GroupLeaderboardsFragment;
import views.group.GroupPredictionListHeader;
import views.group.GroupSettingsFragment;

public class GroupPredictionListFragment extends BasePredictionListFragment implements GroupPredictionListHeader.GroupPredictionListHeaderDelegate {
    public Group group;
    //private GroupPredictionListHeader headerview;

    public static GroupPredictionListFragment newInstance(Group group) {
        GroupPredictionListFragment fragment = new GroupPredictionListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GROUP", GsonF.actory().toJson(group));
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = GsonF.actory().fromJson(getArguments().getString("GROUP"), Group.class);
        bus.register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(group.name.toUpperCase());
        final GroupPredictionAdapter a = ((GroupPredictionAdapter)adapter);
        a.setGroup(group);
        /*
        headerview = new GroupPredictionListHeader(getActivity(), this);
        headerview.group = group;
        final GroupPredictionAdapter a = ((GroupPredictionAdapter)adapter);
        a.setLoadFinishedListener(new PagingAdapter.PagingAdapaterPageLoadFinishListener<Prediction>() {
            @Override
            public void adapterFinishedLoadingPage(int page) {
                a.header = headerview;
            }
        });
        */
        FlurryAgent.logEvent("Group_Prediction_List");
    }

    @Override
    public void onRankings() {
        GroupLeaderboardsFragment fragment = GroupLeaderboardsFragment.newInstance(group);
        pushFragment(fragment);
    }

    @Override
    public void onSettings() {
        GroupSettingsFragment fragment = GroupSettingsFragment.newInstance(group, null);
        pushFragment(fragment);
    }

    @Override
    public void onListViewCreated(ListView listView) {
        super.onListViewCreated(listView);
    }



    @Override
    public PagingAdapter getAdapter() {
        GroupPredictionAdapter a = new GroupPredictionAdapter(getActivity(), this, networkingManager.getImageLoader());
        a.delegate = this;
        return a;
    }

    @Override
    public void getObjectsAfterObject(Prediction object, NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        networkingManager.getPredictionsForGroupAfter(group.id, lastId, callback);
    }

    @Override
    public void onItemClicked(int position) {
        position = position - 1;
        if (position <= 0) {
            return;
        }
        super.onItemClicked(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.post(new ChangeGroupEvent(group));

    }

    @Override
    public void onPause() {
        super.onPause();
        bus.post(new ChangeGroupEvent(null));
    }

}
