package com.example.synapse.screen.util;

public class ReadWriteAppointment {
    private String Specialist;
    private String AppointmentType;
    private String DrName;
    private String Concern;
    private String Time;
    private Long RequestCode;

    public ReadWriteAppointment() { }

    public ReadWriteAppointment(String textSpecialist, String textAppointmentType, String textDrName,
                                String textConcern, String textTime, Long textRequestCode) {

        this.Specialist = textSpecialist;
        this.AppointmentType = textAppointmentType;
        this.DrName = textDrName;
        this.Concern = textConcern;
        this.Time = textTime;
        this.RequestCode = textRequestCode;
    }

    public String getSpecialist(){ return Specialist; }
    public void setSpecialist(String Specialist){ this.Specialist = Specialist; }

    public String getAppointmentType(){ return AppointmentType; }
    public void setAppointmentType(String AppointmentType){ this.AppointmentType = AppointmentType; }

    public String getDrName(){ return DrName; }
    public void setDrName(String DrName){ this.DrName = DrName; }

    public String getConcern(){ return Concern; }
    public void setConcern(String Concern){ this.Concern = Concern; }

    public String getTime(){ return Time; }
    public void setTime(String Time){ this.Time = Time; }

    public Long getRequestCode(){ return RequestCode; }
    public void setRequestCode(Long requestCode){ this.RequestCode = requestCode; }
}
