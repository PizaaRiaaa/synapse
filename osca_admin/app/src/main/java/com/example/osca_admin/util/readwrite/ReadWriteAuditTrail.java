package com.example.osca_admin.util.readwrite;

import com.google.firebase.database.PropertyName;

public class ReadWriteAuditTrail {

    public String ActionMade;
    public String ActionMadeBy;
    public String Info;
    public String Name;
    public Long Timestamp;
    public String Type;
    public String UserID;

    public ReadWriteAuditTrail(){}

    public ReadWriteAuditTrail(String textActionMade, String textActionMadeBy, String textInfo,
                               String textName, Long textTimestamp, String textType, String textUserID) {

        this.ActionMade = textActionMade;
        this.ActionMadeBy = textActionMadeBy;
        this.Info = textInfo;
        this.Name = textName;
        this.Timestamp = textTimestamp;
        this.Type = textType;
        this.UserID = textUserID;

    }
    @PropertyName("ActionMade")
    public String getActionMade() {
        return ActionMade;
    }

    @PropertyName("ActionMade")
    public void setActionMade(String ActionMade) {
        this.ActionMade = ActionMade;
    }

    @PropertyName("ActionMadeBy")
    public String getActionMadeBy() {
        return ActionMadeBy;
    }

    @PropertyName("ActionMadeBy")
    public void setActionMadeBy(String ActionMadeBy) {
        this.ActionMadeBy = ActionMadeBy;
    }

    @PropertyName("Info")
    public String getInfo() {
        return Info;
    }

    @PropertyName("Info")
    public void setInfo(String Info) {
        this.Info = Info;
    }

    @PropertyName("Name")
    public String getName() {
        return Name;
    }

    @PropertyName("Name")
    public void setName(String Name) {
        this.Name = Name;
    }

    @PropertyName("Timestamp")
    public Long getTimestamp() {
        return Timestamp;
    }

    @PropertyName("Timestamp")
    public void setTimestamp(Long Timestamp) {
        this.Timestamp = Timestamp;
    }

    @PropertyName("Type")
    public String getType() {
        return Type;
    }

    @PropertyName("Type")
    public void setType(String Type) {
        this.Type = Type;
    }

    @PropertyName("UserID")
    public String getUserID() {
        return UserID;
    }

    @PropertyName("UserID")
    public void setUserID(String UserID) {
        this.UserID = UserID;
    }
}
