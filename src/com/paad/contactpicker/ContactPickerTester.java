package com.paad.contactpicker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import com.paad.ad2.R;

public class ContactPickerTester extends Activity {
    public static final int PICK_CONTACT = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactpickertester);
    }

    public void pickContact(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts/"));
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        switch (reqCode) {
            case (PICK_CONTACT): {
                if (resCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                    cursor.moveToFirst();

                    String nameColumnIdx = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
                    String name = cursor.getString(cursor.getColumnIndex(nameColumnIdx));
                    cursor.close();

                    TextView tv = (TextView) findViewById(R.id.selected_contact_textview);
                    tv.setText(name);
                }
                break;
            }
            default:
                break;
        }
    }
}