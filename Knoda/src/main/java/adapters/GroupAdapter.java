package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;

import helpers.AdapterHelper;
import models.Group;
import views.group.CreateGroupHeaderView;
import views.group.GroupListCell;

public class GroupAdapter extends PagingAdapter<Group> {

    public GroupAdapter(Context context, PagingAdapterDatasource<Group> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0)
            return getHeaderView(convertView);


        if (position-1 >= objects.size())
            return super.getView(position-1, convertView, parent);

        GroupListCell listItem = (GroupListCell) AdapterHelper.getConvertViewSafely(convertView, GroupListCell.class);
        if (listItem == null)
            listItem = (GroupListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_group, null);

        Group group = getItem(position-1);

        listItem.setGroup(group);
        if (group.avatar != null && group.avatar.small != null)
            listItem.avatarImageView.setImageUrl(group.avatar.small, imageLoader);
        return listItem;
    }

    View getHeaderView(View convertView) {

        CreateGroupHeaderView header = (CreateGroupHeaderView) AdapterHelper.getConvertViewSafely(convertView, CreateGroupHeaderView.class);

        if (header == null)
            header = new CreateGroupHeaderView(context);
        return header;
    }
}

