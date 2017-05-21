package io.github.theangrydev.travelcardcalculator;

import android.app.Application;
import net.danlew.android.joda.JodaTimeAndroid;

public class TravelCardCalculatorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
