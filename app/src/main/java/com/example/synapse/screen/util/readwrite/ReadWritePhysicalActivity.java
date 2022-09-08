package com.example.synapse.screen.util.readwrite;

import com.google.firebase.database.DatabaseReference;

public class ReadWritePhysicalActivity {
    private String Activity;
    private String Duration;
    private String Time;
    private String Description;
    private String RepeatMode;
    private Long RequestCode;

    public ReadWritePhysicalActivity() { }

    public ReadWritePhysicalActivity(String textActivity, String textDuration,
                                     String textTime, String textDescription,
                                     String textRepeatMode, Long textRequestCode) {

        this.Activity = textActivity;
        this.Duration = textDuration;
        this.Time = textTime;
        this.Description = textDescription;
        this.RepeatMode = textRepeatMode;
        this.RequestCode = textRequestCode;
    }

    public String getActivity(){ return Activity; }
    public void setActivity(String Activity){ this.Activity = Activity; }

    public String getDuration(){ return Duration; }
    public void setDuration(String Duration){ this.Duration = Duration; }

    public String getTime(){ return Time; }
    public void setTime(String Time){ this.Time = Time; }

    public String getDescription(){ return Description; }
    public void setDescription(String Description){ this.Description = Description; }

    public String getRepeatMode(){ return RepeatMode; }
    public void setRepeatMode(String RepeatMode){ this.RepeatMode = RepeatMode; }

    public Long getRequestCode(){ return RequestCode; }
    public void setRequestCode(Long requestCode){ this.RequestCode = requestCode; }
}
