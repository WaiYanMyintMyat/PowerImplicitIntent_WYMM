package com.wymm.powerimpliintent;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_CONTACT = 2;
    private Toolbar toolbar;
    private MaterialButton btnCreateATimer, btnAddACalendarEvent, btnCaptureAVideo, btnSelectAContact, btnWebSearch;
    private VideoView videoView;
    private TextView tvSelectAContact;
    private EditText etWebSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpFindViewById();
        setSupportActionBar(toolbar);

        btnCreateATimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createATimer("Create A Timer", 0);
            }
        });

        btnAddACalendarEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addACalendarEvent("Event Title", "Mingalar Taung Nyunt", 0L, 100L);
            }
        });

        btnCaptureAVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureVideo();
            }
        });

        btnSelectAContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContact();
            }
        });

        btnWebSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etWebSearch.getText().toString())){
                    performWebSearch(etWebSearch.getText().toString());
                } else{
                    Toast.makeText(MainActivity.this, "Please Enter Search Item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createATimer(String message, int seconds) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void addACalendarEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
    }

    private void performWebSearch(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void setUpFindViewById() {
        toolbar = findViewById(R.id.toolbar);
        btnCreateATimer = findViewById(R.id.btn_create_a_timer);
        btnAddACalendarEvent = findViewById(R.id.btn_add_a_calendar_event);
        btnCaptureAVideo = findViewById(R.id.btn_capture_a_video);
        videoView = findViewById(R.id.video_view);
        btnSelectAContact = findViewById(R.id.btn_select_a_contact);
        tvSelectAContact = findViewById(R.id.tv_select_a_contact);
        btnWebSearch = findViewById(R.id.btn_web_search);
        etWebSearch = findViewById(R.id.et_web_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri recordUri = data.getData();
                videoView.setVideoURI(recordUri);
                videoView.start();
            } else {
                Toast.makeText(this, "There is No Data !!!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contactUri = data.getData();
                if (contactUri != null) {
                    Cursor cursor = getContentResolver().query(contactUri, null,
                            null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        String name = cursor.getString(nameIndex);
                        tvSelectAContact.setText(name);

                        String contactId =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        Cursor phoneDetailColumn = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                        if (phoneDetailColumn != null) {
                            while (phoneDetailColumn.moveToNext()) {
                                String phoneNumber = phoneDetailColumn.getString(phoneDetailColumn.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                tvSelectAContact.append(" Phone: " + phoneNumber);
                            }
                            phoneDetailColumn.close();
                        }

                        cursor.close();
                    } else {
                        Toast.makeText(this, "Please Try Again !!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Please Try Again !!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "There is No Data !!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "There is No Data !!!", Toast.LENGTH_SHORT).show();
        }
    }
}
