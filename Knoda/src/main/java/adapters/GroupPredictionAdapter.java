package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import helpers.AdapterHelper;
import models.Group;
import models.Prediction;
import pubsub.NewPredictionEvent;
import views.core.MainActivity;
import views.group.GroupPredictionListHeader;

public class GroupPredictionAdapter extends PredictionAdapter {


    public Group group;
    public GroupPredictionListHeader header;
    public GroupPredictionListHeader.GroupPredictionListHeaderDelegate delegate;

    public GroupPredictionAdapter(Context context, PagingAdapter.PagingAdapterDatasource<Prediction> datasource, ImageLoader imageLoader, Bus bus, MainActivity mainActivity) {
        super(context, datasource, imageLoader, bus, null, mainActivity);
        bus.register(this);
    }

    @Subscribe
    public void newPrediction(NewPredictionEvent event) {
        if (event.prediction.groupId != null && event.prediction.groupId.equals(group.id))
            insertAt(event.prediction, 0);
    }

    @Override
    public int getCount() {
        if (group == null)
            return super.getCount();

        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (group == null)
            return super.getView(position, convertView, parent);

        if (position == 0)
            return getHeaderView(convertView);

        return super.getView(position - 1, convertView, parent);

    }

    public void setGroup(Group group) {
        this.group = group;
        notifyDataSetChanged();
    }

    View getHeaderView(View convertView) {
        header = (GroupPredictionListHeader) AdapterHelper.getConvertViewSafely(convertView, GroupPredictionListHeader.class);
        if (header == null)
            header = new GroupPredictionListHeader(context, delegate);
        try {
            ((TextView) header.findViewById(R.id.group_prediction_list_leader_textview)).setText(group.leader.username.toString());
        } catch (Exception e) {
        }
        return header;
    }
}
