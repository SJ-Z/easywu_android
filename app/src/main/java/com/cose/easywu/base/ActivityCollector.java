package com.cose.easywu.base;

import android.app.Activity;

import com.cose.easywu.app.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }

    public static Activity getLastActivity() {
        return activities.get(activities.size() - 2);
    }

    public static int getActivitiesLength() {
        return activities.size();
    }
}
