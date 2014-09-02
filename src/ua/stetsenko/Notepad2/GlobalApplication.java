package ua.stetsenko.Notepad2;

import android.app.Application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GlobalApplication extends Application {

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm dd-MM-yyyy ", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
