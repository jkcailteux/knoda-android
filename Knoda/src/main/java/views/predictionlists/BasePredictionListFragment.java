package views.predictionlists;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.knoda.knoda.R;

import java.util.ArrayList;

import adapters.PagingAdapter;
import adapters.PredictionAdapter;
import butterknife.InjectView;
import listeners.PredictionSwipeListener;
import models.KnodaScreen;
import models.Prediction;
import models.ServerError;
import networking.NetworkCallback;
import networking.NetworkListCallback;
import views.core.BaseListFragment;
import views.core.MainActivity;
import views.details.DetailsFragment;

/**
 * Created by nick on 2/3/14.
 */
public class BasePredictionListFragment extends BaseListFragment implements PredictionSwipeListener.PredictionCellCallbacks, PagingAdapter.PagingAdapterDatasource<Prediction> {

    PredictionSwipeListener swipeListener;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public PagingAdapter getAdapter() {
        return new PredictionAdapter(getActivity(), this, networkingManager.getImageLoader(), bus);
    }

    @Override
    public AbsListView.OnScrollListener getOnScrollListener() {
        return swipeListener.makeScrollListener();
    }

    @Override
    public void onListViewCreated(ListView listView) {
        swipeListener = new PredictionSwipeListener(listView, this);
        listView.setOnTouchListener(swipeListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });

    }

    public void onItemClicked(int position) {
        Prediction prediction = (Prediction)adapter.getItem(position-1);
        if (prediction != null) {
            DetailsFragment fragment = DetailsFragment.newInstance(prediction);
            pushFragment(fragment);
        }
    }


    @Override
    public void getObjectsAfterObject(Prediction object, final NetworkListCallback<Prediction> callback) {
        int lastId = object == null ? 0 : object.id;

        boolean firstLaunch = sharedPrefManager.getFirstLaunch();
        if(false || firstLaunch){
            NetworkListCallback<Prediction> callback2 = new NetworkListCallback<Prediction>() {
                @Override
                public void completionHandler(ArrayList<Prediction> object, ServerError error) {
                    FlurryAgent.logEvent("First_Screen_Overlay");
                    //overlay.setVisibility(View.VISIBLE);
                    sharedPrefManager.setFirstLaunch(false);
                }
            };
            networkingManager.getPredictionsAfter(lastId, callback2);
        }else{
            networkingManager.getPredictionsAfter(lastId, callback);
        }




    }


    @Override
    public void onPredictionAgreed(final PredictionListCell cell) {
        cell.setAgree(true);

        networkingManager.agreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null)
                    errorReporter.showError(error);
                else {
                    cell.prediction = object;
                    cell.update();
                    ((MainActivity) getActivity()).checkBadges();
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Agree");
    }

    @Override
    public void onPredictionDisagreed(final PredictionListCell cell) {
        cell.setAgree(false);

        networkingManager.disagreeWithPrediction(cell.prediction.id, new NetworkCallback<Prediction>() {
            @Override
            public void completionHandler(Prediction object, ServerError error) {
                if (error != null) {
                    errorReporter.showError(error);
                } else {
                    cell.prediction = object;
                    cell.update();
                    ((MainActivity) getActivity()).checkBadges();
                }
            }
        });
        FlurryAgent.logEvent("Swiped_Disagree");
    }

    @Override
    public void onProfileTapped(final PredictionListCell cell) {
        if (cell.prediction.userId.equals(userManager.getUser().id)) {
            ((MainActivity)getActivity()).showFrament(KnodaScreen.KnodaScreenOrder.PROFILE);
        } else {
            AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(cell.prediction.userId);
            pushFragment(fragment);
        }
    }

    @Override
    public String noContentString() {
        return "";
    }
}
