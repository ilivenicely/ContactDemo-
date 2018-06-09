package dhruv.example.contactdemo.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import dhruv.example.contactdemo.R;

public class Fragment_ContactDetails extends Fragment {

    TextView name;
    ImageView contactImage;
    ListView phoneList;
    ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_contatdetails, container, false);

        name = view.findViewById(R.id.tv_contactName);
        contactImage = view.findViewById(R.id.img_contact);
        phoneList = view.findViewById(R.id.phoneNumber);

        return view;
    }

    public void getDetails(long id) {
        ContentResolver contentResolver = getActivity().getContentResolver();

        String cContactIdString = ContactsContract.Contacts._ID;
        Uri cCONTACT_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String cDisplayNameColumn = ContactsContract.Contacts.DISPLAY_NAME;
        String cContactColumn = ContactsContract.CommonDataKinds.Phone.NUMBER;

        String selection = cContactIdString + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        Cursor cursor = contentResolver.query(cCONTACT_CONTENT_URI, null, selection, selectionArgs, null);
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),
                ContentUris.withAppendedId(cCONTACT_CONTENT_URI, id));

        Bitmap photo = null;
        if (inputStream != null) {
            photo = BitmapFactory.decodeStream(inputStream);
            contactImage.setImageBitmap(photo);
        } else {
            contactImage.setImageResource(R.drawable.ic_launcher_background);
        }

        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            while ((cursor != null) && (cursor.isAfterLast() == false)) {
                if (cursor.getColumnIndex(cContactIdString) >= 0) {
                    if (String.valueOf(id).equals(cursor.getString(cursor.getColumnIndex(cContactIdString)))) {
                        String cname = cursor.getString(cursor.getColumnIndex(cDisplayNameColumn));
                        Log.e("Name --> ", cname);
                        name.setText(cname);
                        break;
                    }
                }
                cursor.moveToNext();
            }
        }


        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
            Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(id)}, null);
            ArrayList<String> list=new ArrayList<>();
            while (cursorInfo.moveToNext()){
                String phone = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.e("Number --> ", phone);
                //String[] v=new String[]{phone};
                list.add(phone);
            }
            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1,list);
            phoneList.setAdapter(arrayAdapter);
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}
