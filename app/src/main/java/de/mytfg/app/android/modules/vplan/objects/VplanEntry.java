package de.mytfg.app.android.modules.vplan.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.mytfg.app.android.modules.general.ApiObject;

public class VplanEntry extends VplanObject {
    private String lessons;
    private String plan;
    private String grade;
    private String substitution;
    private String comment;

    public static VplanEntry createFromJson(JSONObject plan) throws JSONException {
        VplanEntry entry = new VplanEntry();
        entry.lessons = plan.getString("lesson");
        entry.plan = plan.getString("plan");
        entry.comment = plan.getString("comment");
        entry.substitution = plan.getString("substitution");
        entry.grade = plan.getString("class");
        return entry;
    }

    private VplanEntry() {

    }

    public String getLessons() {
        return lessons;
    }

    public String getPlan() {
        return plan;
    }

    public String getGrade() {
        return grade;
    }

    public String getSubstitution() {
        return substitution;
    }

    public String getComment() {
        return comment;
    }
}


