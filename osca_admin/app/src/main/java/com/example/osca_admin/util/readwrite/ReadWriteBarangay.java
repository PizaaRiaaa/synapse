package com.example.osca_admin.util.readwrite;

public class ReadWriteBarangay {

    public String brgy;

    public ReadWriteBarangay(){}

    public ReadWriteBarangay(String textBrgy){
        this.brgy = textBrgy;
    }

    public String getBrgy() {
        return brgy;
    }

    public void setBrgy(String brgy) {
        this.brgy = brgy;
    }
}
