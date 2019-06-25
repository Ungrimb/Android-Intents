package smartlock.example.intents;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Calendar calendar;
    private TextView dateView, timeView, url, phone, activityText;
    private int year, month, day, hour, minute;
    private String webPage;
    private ImageView imageView, takePictureButton, foto_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateView = findViewById(R.id.textView2);
        timeView = findViewById(R.id.textView3);
        url = findViewById(R.id.textWeb);
        phone = findViewById(R.id.phone);
        activityText = findViewById((R.id.textPass));
        webPage = "";

        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        showTime(hour, minute);

        imageView = findViewById(R.id.imageView4);
        takePictureButton = findViewById(R.id.imageView3);
        foto_gallery = findViewById(R.id.imageView5);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imageView.setEnabled(true);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        } else if (id == 998) {
            return new TimePickerDialog(this,
                    myDateListener2, hour, minute, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2 + 1, arg3);
                }
            };
    private TimePickerDialog.OnTimeSetListener myDateListener2 =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    showTime(hourOfDay, minute);
                }
            };

    private void showDate(int arg1, int arg2, int arg3) {
        year = arg1;
        month = arg2-1;
        day = arg3;
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month+1).append("/").append(year));
    }

    private void showTime(int arg1, int arg2) {
        hour = arg1;
        minute = arg2;
        timeView.setText(new StringBuilder().append(hour).append(":")
                .append(minute));
    }

    public void setTime(View view) {
        showDialog(998);
    }

    public void openWebPage(View view) {
        webPage = url.getText().toString();
        if (!webPage.startsWith("http://") || !webPage.startsWith("https://")) {
            webPage = "https://" + webPage;
        }
        if (!webPage.contains(".")) {
            webPage = webPage + ".com";
        }
        Uri webPage2 = Uri.parse(webPage);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage2);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void dialPhoneNumber(View view) {
        String phoneNumber = phone.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void pastText(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("variable_string", activityText.getText().toString());
        startActivity(intent);
    }

    public void takePicture(View view) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    public void openGallery(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 101);
    }

    public void eventoCalendario(View view) {
        calendar.set(year, month, day, hour, minute);
        Intent intentCalendar = new Intent(Intent.ACTION_EDIT);
        intentCalendar.setData(CalendarContract.Events.CONTENT_URI);

        //intentCalendar.putExtra(CalendarContract.Events.CALENDAR_ID, 1);
        intentCalendar.putExtra(CalendarContract.Events.TITLE, "PRUEBA DE EVENTO");
        intentCalendar.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
        intentCalendar.putExtra(CalendarContract.Events.HAS_ALARM, 1);

        startActivity(intentCalendar);
        }

    }