package views.predictionlists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.knoda.knoda.R;

import java.util.ArrayList;

import butterknife.InjectView;
import core.networking.NetworkListCallback;
import models.Prediction;
import models.ServerError;
import views.core.BaseFragment;

public class HomeFragment extends BaseFragment {

    @InjectView(R.id.home_listview) ListView listView;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner.show();

        networkingManager.getPredictionsAfter(0, new NetworkListCallback<Prediction>() {
            @Override
            public void completionHandler(ArrayList<Prediction> predictions, ServerError error) {
                spinner.hide();
                if (error != null)
                    errorReporter.showError(error);
                else
                    listView.setAdapter(new BasePredictionListAdapter(getActivity().getLayoutInflater(), predictions));

            }
        });
    }
}
