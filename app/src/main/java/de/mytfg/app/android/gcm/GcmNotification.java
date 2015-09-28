package de.mytfg.app.android.gcm;

import android.os.Bundle;
import android.util.Log;

/**
 * Represents a GCM Notification.
 */
public class GcmNotification implements Comparable {
    private String type;
    private String grouper;
    private String title;
    private String message;


    public GcmNotification(Bundle data) {
        this.type = data.getString("type");
        this.grouper = data.getString("grouper");
        this.title = data.getString("title");
        this.message = data.getString("message");
    }

    public String getType() {
        return type;
    }

    public String getGrouper() {
        return grouper;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof GcmNotification) {
            return grouper.compareTo(((GcmNotification)o).grouper);
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof GcmNotification) {
            return ((GcmNotification) o).grouper == grouper;
        }
        return false;
    }



    @Override
    public int hashCode() {
        return grouper.hashCode();
    }

    @Override
    public String toString() {
        return grouper;
    }
}
