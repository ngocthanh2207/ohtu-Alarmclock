package ohtu.beddit.io;

import android.content.*;
import android.util.Log;
import com.google.gson.JsonParser;
import ohtu.beddit.R;

import java.io.*;
import java.util.Scanner;


public class FileHandler {

    public static final String ALARMS_FILENAME = "beddit_alarms";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    private Context context;

    public FileHandler(Context context){
        this.context = context;
    }

    public boolean writeToFile(String filename, String writable){
        FileOutputStream fos = null;
        try{
            fos = context.openFileOutput(filename,Context.MODE_PRIVATE);
            fos.write(writable.getBytes());
            fos.close();
        }catch(Exception e){
            Log.v("Filehandler", "Writing crashed");
            return false;
        }
        return true;
    }

    public String readStringFromFile(String filename){
        try{
            Scanner scanner = new Scanner(context.openFileInput(filename));
            String line = scanner.nextLine();
            return line;
        }catch(Exception e){
            Log.v("Filehandler", "File not found");
            return "";
        }
    }

    public boolean saveAlarm(int hour, int minute, int interval, boolean enabled){
        int alarmSet = enabled ? 1 : -1;
        String towrite = ""+alarmSet+'#'+hour+'#'+minute+'#'+interval;
        return writeToFile(ALARMS_FILENAME, towrite);
    }


    // Disables alarm, but keeps the wake up time in memory
    public boolean disableAlarm(){
        int[] oldData = getAlarm();
        return saveAlarm(oldData[1], oldData[2], oldData[3], false);
    }

    /*
        returns int[4] in the form of   0: if alarm exists
                                        1: hours
                                        2: minutes
                                        3: interval
     */
    public int[] getAlarm(){
        String[] alarmData = readStringFromFile(ALARMS_FILENAME).split("#");

        int[] alarmValues = new int[4];
        try{
            alarmValues[0] = Integer.parseInt(alarmData[0]);
            alarmValues[1] = Integer.parseInt(alarmData[1]);
            alarmValues[2] = Integer.parseInt(alarmData[2]);
            alarmValues[3] = Integer.parseInt(alarmData[3]);
        }catch (Exception e){
            Log.v("Exception", e.getMessage());
            //  Possible exceptions: parseInt fails, or if there was no alarm data, ArrayOutOfBoundsException
            alarmValues[0] = -1;
            for (int i = 1; i < alarmValues.length; i++){
                alarmValues[i] = 0;
            }
            saveAlarm(0, 0, 0, false);
        }
        return alarmValues;
    }

    public String getClientInfo(String request) {
        String json= "";
        try{
            InputStream inputStream = context.getResources().openRawResource(R.raw.secret);
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNext()) { json+=scanner.next();}
        }catch(Exception e){
            Log.v("Filehandler", "File not found");
            return "";
        }
        String clientid=new JsonParser().parse(json).getAsJsonObject().get(request).getAsString();
        return clientid;
    }
}
