package dhruv.example.contactdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import dhruv.example.contactdemo.fragment.Fragment_ContactDetails;
import dhruv.example.contactdemo.fragment.Fragment_ContactList;

public class MainActivity extends AppCompatActivity implements Fragment_ContactList.onContactSelectedListener {

    Fragment_ContactDetails contactDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContactSelected(long id) {
        contactDetails = (Fragment_ContactDetails) getSupportFragmentManager().findFragmentById(R.id.frag_contactDetail);
        contactDetails.getDetails(id);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment_ContactList list = (Fragment_ContactList) getFragmentManager().findFragmentById(R.id.frag_contactList);
        list.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
