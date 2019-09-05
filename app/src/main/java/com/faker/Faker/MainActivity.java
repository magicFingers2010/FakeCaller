package com.faker.Faker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

import pub.devrel.easypermissions.EasyPermissions;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;


public class MainActivity extends AppCompatActivity {

    RelativeLayout defaultLayout;
    RelativeLayout chooserLayout;
    String name;
    String number;
    private static int PICK_CONTACT = 1;
    EditText phoneNumberContainer;
    EditText nameContainer;
    RadioGroup radioGroup;
    RadioButton firstOption;
    RadioButton secondOption;
    RadioButton thirdOption;
    Button setButton;
    private static final String TAG = "MyActivity";
    int getSelectedAnswer = 0;
    final static String PREF_FILE_1 = "pref_file_1";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref =getSharedPreferences(PREF_FILE_1, Context.MODE_PRIVATE);
        String inProgress = sharedPref.getString("inProcess","0");

        assert inProgress != null;
        if (inProgress.equals("true")){
            Intent intents = new Intent(this, inProcessActivity.class);
            startActivity(intents);
        }

        String firstInstall = sharedPref.getString("firstInstall","0");

        assert firstInstall != null;
        if (!firstInstall.equals("false")){
            configureTapTarget();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       defaultLayout = (RelativeLayout) findViewById(R.id.defaultLayout);
       chooserLayout = (RelativeLayout) findViewById(R.id.chooserLayout);
       defaultLayout.setVisibility(View.GONE);

        CardView createNewNum = (CardView) findViewById(R.id.createNewNumber);
        CardView chooseContact = (CardView) findViewById(R.id.chooseFromContact);

        phoneNumberContainer = (EditText) findViewById(R.id.phoneNumberContainer);
        nameContainer = (EditText) findViewById(R.id.nameContainer);

        radioGroup = (RadioGroup) findViewById(R.id.timerContainer);

        int getRadioGroup = radioGroup.getCheckedRadioButtonId();

        Log.d(TAG, String.valueOf(getSelectedAnswer));
        firstOption = (RadioButton)findViewById(R.id.thirty);
        secondOption = (RadioButton)findViewById(R.id.sixty);
        thirdOption = (RadioButton)findViewById(R.id.minutes);
        firstOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getSelectedAnswer = getSelectedAnswer(v.getId());
            }
        });
        secondOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedAnswer = getSelectedAnswer(v.getId());
            }
        });
        thirdOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedAnswer = getSelectedAnswer(v.getId());
            }
        });

        setButton = (Button) findViewById(R.id.setButton);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkErrors()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 10);
                    long currentFakeTime =  calendar.getTimeInMillis();

                    setUpAlarm(getSelectedAnswer, nameContainer.getText().toString(), phoneNumberContainer.getText().toString());
                }
            }
        });

        createNewNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout(v);
            }
        });

        chooseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout(v);
            }
        });
        requirePermissions();

    }



    private void requirePermissions() {
        String[] perms = {
                Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this,"Please Allow this permissions to make the app function properly",
                    PICK_CONTACT, perms);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeLayout(View v){
        if (v.getId() == R.id.createNewNumber){
            chooserLayout.setVisibility(View.GONE);
            defaultLayout.setVisibility(View.VISIBLE);
        }else{
            chooseContact();
        }
    }

    public void chooseContact(){
        requirePermissions();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        //intent.setType(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_TYPE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK){
            Uri contactDetails = data.getData();

            name = getContactNameFromURI(contactDetails);
            number = getContactPhoneFromURI(contactDetails);
            chooserLayout.setVisibility(View.GONE);
            defaultLayout.setVisibility(View.VISIBLE);

            nameContainer.setText(name);
            phoneNumberContainer.setText(number);

        }


    }

    public String getContactNameFromURI(Uri uri){
        String contactName = null;
        // querying contact data store
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        return contactName;
    }

    public String getContactIdFromURI(Uri uri){
        String contactID = null;

        // getting contacts "ID" that we need for stuff
        Cursor cursorID = getContentResolver().query(uri,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();

        return contactID;
    }

    public String getContactPhoneFromURI(Uri uri){
        String contactNumber = null;
        String contactID = getContactIdFromURI(uri);

        // Using the contact ID now we will get contact phone number
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursor.moveToFirst()) {
            contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursor.close();
        return contactNumber;
    }

    private Boolean checkErrors(){
        String phoneNumbercheck = phoneNumberContainer.getText().toString();
        String nameCheck = nameContainer.getText().toString();
        int error = 0;
        if(radioGroup.getCheckedRadioButtonId() == -1){
                Toast.makeText(MainActivity.this, "You must select a time", Toast.LENGTH_LONG).show();
            error ++;
        }
        if(TextUtils.isEmpty(phoneNumbercheck)){
            phoneNumberContainer.setError("Please Input a Number");
            error ++;
        }
        if (error > 0){
            return false;
        }
        return true;
    }

    public void setUpAlarm(long selectedTimeInMilliseconds, String fakeName, String fakeNumber){

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, FakeCallReceiver.class);

        intent.putExtra("FAKENAME", fakeName);
        intent.putExtra("FAKENUMBER", fakeNumber);

        PendingIntent fakePendingIntent = PendingIntent.getBroadcast(this, 0,  intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (selectedTimeInMilliseconds * 1000) , fakePendingIntent);
        Toast.makeText(getApplicationContext(), "Your fake call time has been set", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPref =getSharedPreferences(PREF_FILE_1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("inProcess","true");
        editor.apply();
        Intent intents = new Intent(this, inProcessActivity.class);
        intents.putExtra("TRY",selectedTimeInMilliseconds * 1000);
        startActivity(intents);

    }
    private int getSelectedAnswer(int radioSelected){
        int answerSelected = 0;
        if(radioSelected == R.id.thirty){
            answerSelected = 60;        }
        if(radioSelected == R.id.sixty){
            answerSelected = 300;
        }
        if(radioSelected == R.id.minutes){
            answerSelected = 600;
        }
        return answerSelected;
    }
    private void configureTapTarget() {
        new MaterialTapTargetSequence()

            .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(R.id.addButton)
                .setPrimaryText("Set a number")
                .setSecondaryText("set a number to display in the fake call manually")
                 .setIcon(R.drawable.ic_add_circle_black_24dp)
                .create(),7000)
            .addPrompt(new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(R.id.chooseContact)
                .setPrimaryText("Contacts")
                .setSecondaryText("Choose a contact as the fake caller")
                .setIcon(R.drawable.ic_contact_phone_black_24dp)
                .setAnimationInterpolator(new LinearOutSlowInInterpolator()))
            .show();
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString("firstInstall","false");
        edit.apply();

    }

}
