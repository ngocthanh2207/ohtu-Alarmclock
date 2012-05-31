package ohtu.beddit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Toast;
import ohtu.beddit.R;
import ohtu.beddit.alarm.AlarmService;
import ohtu.beddit.alarm.AlarmServiceImpl;
import ohtu.beddit.alarm.AlarmTimePicker;
import ohtu.beddit.views.timepicker.CustomTimePicker;
import ohtu.beddit.io.PreferenceService;

public class MainActivity extends Activity
{

    private AlarmService alarmService;
    private AlarmTimePicker alarmTimePicker;
    private Button addAlarmButton;
    private Button deleteAlarmButton;


    /** Called when the alarm is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setAlarmService(new AlarmServiceImpl(this));

        //initialize default values for settings if called for the first time
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        setUI();

        // Update buttons and clock handles
        updateButtons();
        setClockHands();

        boolean debugWeb = false;
        String token = PreferenceService.getSettingString(this, R.string.pref_key_userToken);
        if (token != null){
            Log.v("Token:", token);
        }
        if ( (token == null || token.equals("")) && debugWeb) {
            Intent myIntent = new Intent(this, AuthActivity.class);
            startActivityForResult(myIntent,2);
        }

    }

    private void setUI() {
        //Set clock, buttons and listeners
        alarmTimePicker = (CustomTimePicker)this.findViewById(R.id.alarmTimePicker);

        addAlarmButton = (Button) findViewById(R.id.setAlarmButton);
        addAlarmButton.setOnClickListener(new AlarmSetButtonClickListener());
        deleteAlarmButton = (Button)findViewById(R.id.deleteAlarmButton);
        deleteAlarmButton.setOnClickListener(new AlarmDeleteButtonClickListener());

        //Set background color
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
        layout.setBackgroundColor(Color.WHITE);

    }

    private void setClockHands() {
        Log.v("hours",""+alarmService.getAlarmHours(this));
        Log.v("mins",""+alarmService.getAlarmMinutes(this));
        Log.v("interval",""+alarmService.getAlarmInterval(this));

        alarmTimePicker.setHours(alarmService.getAlarmHours(this));
        alarmTimePicker.setMinutes(alarmService.getAlarmMinutes(this));
        alarmTimePicker.setInterval(alarmService.getAlarmInterval(this));
    }

    @Override
    public void onResume(){
        super.onResume();
        updateButtons();
    }



    public class AlarmSetButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            alarmService.addAlarm(MainActivity.this, alarmTimePicker.getHours(), alarmTimePicker.getMinutes(), alarmTimePicker.getInterval());
            MainActivity.this.updateButtons();
            // Tell the user about what we did.
            Toast.makeText(MainActivity.this, getString(R.string.toast_alarmset), Toast.LENGTH_LONG).show();

        }
    }


    public class AlarmDeleteButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            alarmService.deleteAlarm(MainActivity.this);
            MainActivity.this.updateButtons();
            Toast.makeText(MainActivity.this, getString(R.string.toast_alarmremoved), Toast.LENGTH_LONG).show();
        }
    }

    public class backButtonlisten {
        public void onBack(View view) {
            MainActivity.this.finish();
        }
    }


    // Set buttons to on/off
    public void updateButtons(){
        if (alarmService.isAlarmSet(this.getApplicationContext())){
            addAlarmButton.setEnabled(false);
            deleteAlarmButton.setEnabled(true);
        } else {
            addAlarmButton.setEnabled(true);
            deleteAlarmButton.setEnabled(false);
        }
        Log.v("User Interface", "Buttons updated");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem settings = menu.findItem(R.id.settings_menu_button);
        settings.setIntent(new Intent(this.getApplicationContext(), SettingsActivity.class));

        MenuItem help = menu.findItem(R.id.help_menu_button);
        help.setIntent(new Intent(this.getApplicationContext(), HelpActivity.class));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu_button:
                startActivity(item.getIntent());
                break;
            case R.id.help_menu_button:
                startActivity(item.getIntent());
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (2) : {
                if (resultCode == Activity.RESULT_OK) {
                    Log.v("MainActivity", "We got message to finish main.");
                    this.finish();
                } else {
                    DialogSetUserName();
                }
                break;
            }
        }
    }

    public void DialogSetUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog dialog = builder.create();
        dialog.setTitle(getString(R.string.dialog_username_title));
        dialog.setMessage(getString(R.string.dialog_username_msg));
        final EditText input = new EditText(this);
        dialog.setView(input);
        DialogInterface.OnClickListener listenerAccept = new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Great! Welcome.", Toast.LENGTH_SHORT).show();
                PreferenceService.setSetting(MainActivity.this, R.string.pref_key_username, input.getText().toString());
            }
        };
        dialog.setButton(getString(R.string.dialog_username_button), listenerAccept);
        dialog.setCancelable(false);
        dialog.show();
    }


    // These methods are for tests

    public void setAlarmService(AlarmService alarmService) {
        this.alarmService = alarmService;

    }

    public AlarmService getAlarmService() {
        return alarmService;
    }

}
