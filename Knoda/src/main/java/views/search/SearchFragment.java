package views.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.knoda.knoda.R;

import adapters.PagingAdapter;
import adapters.SearchAdapter;
import adapters.TagAdapter;
import butterknife.InjectView;
import models.Prediction;
import models.Tag;
import models.User;
import networking.NetworkListCallback;
import views.core.BaseFragment;
import views.details.DetailsFragment;
import views.predictionlists.AnotherUsersProfileFragment;
import views.predictionlists.CategoryFragment;

public class SearchFragment extends BaseFragment implements SearchView.SearchViewCallbacks, PagingAdapter.PagingAdapterDatasource<Tag>,
        SearchAdapter.SearchAdapterDatasource, SearchAdapter.SearchAdapterCallbacks {

    @InjectView(R.id.search_listview)
    ListView listview;

    TagAdapter tagAdapter;
    SearchAdapter searchAdapter;
    SearchView searchView;

    private String searchTerm = null;
    private boolean keyboardHandled = false;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        hideKeyboard();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        setTitle("");
        menu.clear();
        menu.removeGroup(R.id.default_menu_group);
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setCallbacks(this);
        if (searchTerm == null && !keyboardHandled) {
            showKeyboard(searchView.searchField);
            keyboardHandled = true;
        } else if (searchTerm != null) {
            searchView.searchField.setText(searchTerm);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        if (tagAdapter == null)
            tagAdapter = new TagAdapter(getActivity(), this, null);

        searchAdapter = new SearchAdapter(getActivity(), this, this, networkingManager.getImageLoader());
//        if (searchAdapter == null)
//            searchAdapter = new SearchAdapter(getActivity(), this, this, networkingManager.getImageLoader());
//
        if (searchAdapter.items.size() > 0)
            listview.setAdapter(searchAdapter);
        else
            onCancel();

        if (searchTerm != null) {
            onSearch(searchTerm);
//            Handler h = new Handler();
//            h.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    searchView.searchField.setText(searchTerm.toString());
//                }
//            }, 1000);
        } else {
            if (searchView != null && !keyboardHandled) {
                showKeyboard(searchView.searchField);
                keyboardHandled = true;
            }
        }
    }

    @Override
    public void onSearch(String string) {
        hideKeyboard();
        searchTerm = string;
        listview.setAdapter(searchAdapter);
        searchAdapter.loadForSearchTerm(string);
        listview.setOnItemClickListener(searchAdapter.makeOnItemClickListeners());
        if (!searchView.searchField.getText().toString().equals(searchTerm))
            searchView.searchField.setText(searchTerm);
    }

    @Override
    public void onCancel() {
        listview.setAdapter(tagAdapter);

        tagAdapter.loadPage(0);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Tag tag = tagAdapter.getItem(position);
                if (tag != null) {
                    CategoryFragment fragment = CategoryFragment.newInstance(tag.name);
                    pushFragment(fragment);
                }
            }
        });
    }

    @Override
    public void getObjectsAfterObject(Tag object, NetworkListCallback<Tag> callback) {
        networkingManager.getTags(callback);
    }

    @Override
    public void getUsers(String searchTerm, NetworkListCallback<User> callback) {
        networkingManager.searchForUsers(searchTerm, callback);
    }

    @Override
    public void getPredictions(String searchTerm, NetworkListCallback<Prediction> callback) {
        networkingManager.searchForPredictions(searchTerm, callback);
    }


    @Override
    public void onUserSelected(User user) {
        AnotherUsersProfileFragment fragment = AnotherUsersProfileFragment.newInstance(user.id);
        pushFragment(fragment);
    }

    @Override
    public void onPredictionSelected(Prediction prediction) {
        DetailsFragment fragment = DetailsFragment.newInstance(prediction);
        pushFragment(fragment);
    }

    @Override
    public String noContentString() {
        return "Please try again later.";
    }
}
