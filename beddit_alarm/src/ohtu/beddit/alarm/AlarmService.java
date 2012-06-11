package ohtu.beddit.alarm;

import android.content.Context;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: joosakur
 * Date: 22.5.2012
 * Time: 10:16
 * To change this template use File | Settings | File Templates.
 */
public interface AlarmService {
    void addAlarm(int hours, int minutes, int interval);

    void addWakeUpAttempt(Calendar time);

    void deleteAlarm();

    public boolean isAlarmSet();

    public int getAlarmHours();

    public int getAlarmMinutes();

    public int getAlarmInterval();

    void changeAlarm(int hours, int minutes, int interval);
}
