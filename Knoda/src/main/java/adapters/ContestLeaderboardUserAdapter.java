package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.knoda.knoda.R;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import helpers.AdapterHelper;
import managers.UserManager;
import models.ContestUser;
import views.contests.ContestUserListCell;

public class ContestLeaderboardUserAdapter extends PagingAdapter<ContestUser> {

    @Inject
    public UserManager userManager;
    public Bus bus;

    public ContestLeaderboardUserAdapter(Context context, PagingAdapterDatasource<ContestUser> datasource, ImageLoader imageLoader) {
        super(context, datasource, imageLoader);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position >= objects.size())
            return super.getView(position, convertView, parent);


        ContestUserListCell listItem = (ContestUserListCell) AdapterHelper.getConvertViewSafely(convertView, ContestUserListCell.class);
        if (listItem == null)
            listItem = (ContestUserListCell) LayoutInflater.from(context).inflate(R.layout.list_cell_contestuser, null);
        final ContestUser contestUser = objects.get(position);
        if (contestUser != null) {
            listItem.setContestUser(contestUser);
            if (contestUser.avatar != null)
                listItem.avatarImageView.setImageUrl(contestUser.avatar.small, imageLoader);
        }


        return listItem;
    }

    @Override
    protected View getNoContentView() {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cell_no_content_contest, null);
        ((TextView) view.findViewById(R.id.no_content_textview)).setText(datasource.noContentString());
        return view;
    }

    public boolean canLoadNextPage() {
        return false;
    }
}

