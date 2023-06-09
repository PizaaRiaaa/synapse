package com.example.osca_admin.util.readwrite;

public class ReadWriteTotalReminders {

    public float appointment;
    public float games;
    public float medication;
    public float physical;

    public ReadWriteTotalReminders(){}


    public ReadWriteTotalReminders(float textAppointment, float textGames, float textMedication, float textPhysical){
        this.appointment = textAppointment;
        this.games = textGames;
        this.medication = textMedication;
        this.physical = textPhysical;
    }

    public float getAppointment() {
        return appointment;
    }

    public void setAppointment(float appointment) {
        this.appointment = appointment;
    }

    public float getGames() {
        return games;
    }

    public void setGames(float games) {
        this.games = games;
    }

    public float getMedication() {
        return medication;
    }

    public void setMedication(float medication) {
        this.medication = medication;
    }

    public float getPhysical() {
        return physical;
    }

    public void setPhysical(float physical) {
        this.physical = physical;
    }
}
