package views.contacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

import adapters.PagingAdapter;
import adapters.UserContactAdapter;
import models.GroupInvitation;
import models.UserContact;
import networking.NetworkListCallback;
import views.core.BaseListFragment;

public class FindFriendsContactsFragment extends BaseListFragment implements PagingAdapter.PagingAdapterDatasource<UserContact> {
    boolean pageLoaded = false;
    FindFriendsActivity parent;

    public FindFriendsContactsFragment() {
    }

    public static FindFriendsContactsFragment newInstance(FindFriendsActivity parent) {
        FindFriendsContactsFragment fragment = new FindFriendsContactsFragment();
        fragment.parent = parent;
        return fragment;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FlurryAgent.logEvent("FindFriendsContacts");
        pListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public PagingAdapter getAdapter() {
        UserContactAdapter adapter1 = new UserContactAdapter(FindFriendsListCellHeader.CONTACTS, getActivity(), this, parent.networkingManager.getImageLoader(), parent);
        return adapter1;

    }

    @Override
    public void getObjectsAfterObject(UserContact object, final NetworkListCallback<UserContact> callback) {
        if (parent.localContacts == null)
            return;
        parent.networkingManager.matchPhoneContacts(parent.localContacts, callback);
    }

    @Override
    public void onListViewCreated(ListView listView) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //final Contest contest = (Contest) adapter.getItem(i - 1);
//                //i needs to be greater than list of known knoda friends +1 because of headers
//                final UserContact userContact = (UserContact) adapter.getItem(i - 2);
//                userContact.selected = true;
//                adapter.setItem(i - 2, userContact);
//                if (userContact.knodaInfo == null) {
//                    //if they have multiple forms of id
//                    ArrayList<String> contactTypes = new ArrayList<String>();
//                    if (userContact.emails != null)
//                        for (String s : userContact.emails) {
//                            contactTypes.add(s);
//                        }
//                    if (userContact.phones != null)
//                        for (String s : userContact.phones) {
//                            contactTypes.add(s);
//                        }
//
//                    if (contactTypes.size() > 1) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        final CharSequence[] array = contactTypes.toArray(new CharSequence[0]);
//                        builder.setTitle("Choose a contact method for " + userContact.contact_id);
//                        builder.setItems(array, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                addInvitation(array[which].toString());
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                        builder.show();
//                    } else if (contactTypes.size() == 1) {
//                        addInvitation(contactTypes.get(0));
//                    }
//                }
//            }
//        });
    }

//    private void addInvitation(String s) {
//        GroupInvitation invitation = new GroupInvitation();
//        if (s.indexOf("@") != -1) {
//            invitation.email = s;
//            parent.inviting.add(invitation);
//        } else if (isNumeric(s)) {
//            invitation.phoneNumber = s;
//            parent.inviting.add(invitation);
//        }
//        parent.setSubmitBtnText();
//        System.out.println("Inviting " + s);
//    }

    @Override
    public String noContentString() {
        return "There are no contacts";
    }


}