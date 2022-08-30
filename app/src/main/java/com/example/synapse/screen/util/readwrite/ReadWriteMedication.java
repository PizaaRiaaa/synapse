package com.example.synapse.screen.util.readwrite;

public class ReadWriteMedication {
    private String Name;
    private String Dose;
    private String Shape;
    private String Color;
    private String Dosage;
    private String Description;
    private String Quantity;
    private Long RequestCode;
    private String Time;

    public ReadWriteMedication() { }

    public ReadWriteMedication(String textName, String textDose, String textShape, String textColor,
                                    String textDosage, String textDescription, String textQuantity,
                                    Long textRequestCode, String textTime) {

        this.Name = textName;
        this.Dose = textDose;
        this.Shape = textShape;
        this.Color = textColor;
        this.Dosage = textDosage;
        this.Description = textDescription;
        this.Quantity = textQuantity;
        this.RequestCode = textRequestCode;
        this.Time = textTime;
    }

    public String getName(){ return Name; }
    public void setName(String Name){ this.Name = Name; }

    public String getDose(){ return Dose; }
    public void setDose(String Dose){ this.Dose = Dose; }

    public String getShape(){ return Shape; }
    public void setShape(String Shape){ this.Shape = Shape; }

    public String getColor(){ return Color; }
    public void setColor(String Color){ this.Color = Color; }

    public String getDosage(){ return Dosage; }
    public void setDosage(String Dosage){ this.Dosage = Dosage; }

    public String getDescription(){ return Description; }
    public void setDescription(String Description){ this.Description = Description; }

    public String getQuantity(){ return Quantity; }
    public void setQuantity(String Quantity){ this.Quantity = Quantity; }

    public Long getRequestCode(){ return RequestCode; }
    public void setRequestCode(Long RequestCode){ this.RequestCode = RequestCode; }

    public String getTime(){ return Time; }
    public void setTime(String Time){ this.Time = Time; }

}
