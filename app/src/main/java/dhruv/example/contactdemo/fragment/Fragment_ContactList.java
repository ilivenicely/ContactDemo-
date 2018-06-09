package dhruv.example.contactdemo.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dhruv.example.contactdemo.MainActivity;
import dhruv.example.contactdemo.Model.ContactModel;
import dhruv.example.contactdemo.R;

public class Fragment_ContactList extends ListFragment {

    public List<ContactModel> contactModels;
    onContactSelectedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_contactlist, container, false);
        contactModels = new ArrayList<>();
        //new getAllContacts().execute("");
        EnableRuntimePermission();
        return view;
    }

    public void EnableRuntimePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getAllContacts();
            setListAdapter(new contactListAdapter());
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_CONTACTS}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getAllContacts();
                setListAdapter(new contactListAdapter());
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mListener.onContactSelected(Long.parseLong(contactModels.get(position).getId()));
        Log.e("Id --> ", id + " ");
    }

/*

    public class getAllContacts extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
*/

    public void getAllContacts() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        String sortOrder = ContactsContract.Contacts.Entity.DISPLAY_NAME + " ASC";
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortOrder);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    Bitmap photo = null;
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }
                    while (cursorInfo.moveToNext()) {
                        ContactModel info = new ContactModel();

                        String whereName = ContactsContract.Data.MIMETYPE + " = ?";
                        String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                        Cursor nameCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                        while (nameCur.moveToNext()) {
                            String given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                            String family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                            String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                            info.name = given;
                            info.lname = family;
                        }
                        nameCur.close();

                        info.id = id;
                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        info.photo = photo;
                        info.photoURI = pURI;

                        /*for (int i=0;i<contactModels.size();i++){
                            if(!info.getId().equals(id)){


                            }
                        }*/
                        contactModels.add(info);
                    }

                    cursorInfo.close();
                }
            }
            cursor.close();
        }
    }

    private class contactListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"NewApi", "LocalSuppress", "ViewHolder"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.row_contact, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvFname.setText(contactModels.get(position).getName());
            viewHolder.tvLname.setText(contactModels.get(position).getLname());
            viewHolder.tvNumber.setText(contactModels.get(position).getMobileNumber());

            if (contactModels.get(position).getPhoto() != null) {
                viewHolder.imgContact.setImageBitmap(contactModels.get(position).getPhoto());
            } else {
                viewHolder.imgContact.setImageResource(R.drawable.ic_launcher_background);
            }

            return convertView;
        }

        class ViewHolder {
            TextView tvFname, tvLname, tvNumber;
            ImageView imgContact;

            ViewHolder(View view) {
                tvFname = view.findViewById(R.id.row_tvFname);
                tvLname = view.findViewById(R.id.row_tvLname);
                tvNumber = view.findViewById(R.id.row_tvPhoneNumber);
                imgContact = view.findViewById(R.id.row_imgContact);
            }
        }
    }

    public interface onContactSelectedListener {
        void onContactSelected(long id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onContactSelectedListener) {
            mListener = (onContactSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnStudentAddedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
