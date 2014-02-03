package adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import java.util.ArrayList;

import core.managers.NetworkingManager;
import core.networking.NetworkListCallback;
import models.BaseModel;
import models.ServerError;

/**
 * Created by nick on 2/1/14.
 */
public class PagingAdapter<T extends BaseModel> extends BaseAdapter {

    protected ArrayList<T> objects = new ArrayList<T>();
    protected PagingAdapterDatasource datasource;

    public LayoutInflater inflater;
    public ImageLoader imageLoader;

    public Integer currentPage;
    public boolean loading;

    public interface PagingAdapterDatasource <T extends BaseModel> {
        void getObjectsAfterObject(T object, NetworkListCallback<T> callback);
    }

    public PagingAdapter(LayoutInflater inflater, PagingAdapterDatasource datasource, ImageLoader imageLoader) {
        this.inflater = inflater;
        this.datasource = datasource;
        this.imageLoader = imageLoader;
        this.currentPage = 0;
    }

    @Override
    public int getCount() {

        if (objects.size() == 0)
            return 1;

        if (canLoadNextPage())
            return objects.size() + 1;

        return objects.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= objects.size())
            return null;
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.list_cell_loading, null);
        return view;
    }

    public void loadPage(final int page) {

        if (loading)
            return;

        T object = objects.size() == 0 ? null : objects.get(objects.size() - 1);
        loading = true;

        datasource.getObjectsAfterObject(object, new NetworkListCallback<T>() {
            @Override
            public void completionHandler(ArrayList<T> objectsToAdd, ServerError error) {
                if (error != null || objectsToAdd == null || objectsToAdd.size() == 0)
                    return;

                if (page == 0)
                    objects = objectsToAdd;
                else
                    objects.addAll(objectsToAdd);

                currentPage = page;
                loading = false;
                notifyDataSetChanged();
            }
        });
    }

    public boolean canLoadNextPage() {
        double div = (double)objects.size() / (double) NetworkingManager.PAGE_LIMIT;

        if (div >= Math.ceil(div))
            return true;

        return false;
    }

    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisible, int visibleCount, int totalCount) {
                boolean shouldLoadMore = firstVisible + visibleCount >= totalCount;

                if (shouldLoadMore)
                    loadPage(currentPage + 1);
            }
        };
    }

}