package com.example.synapse.screen.util.readwrite;

public class ReadWriteGames {
    private String Game;
    private String Time;
    private Long RequestCode;

    public ReadWriteGames() { }

    public ReadWriteGames(String textGame, String textTime, Long textRequestCode) {

        this.Game = textGame;
        this.Time = textTime;
        this.RequestCode = textRequestCode;

    }

    public String getGame(){ return Game; }
    public void setGame(String Game){ this.Game = Game; }

    public String getTime(){ return Time; }
    public void setTime(String Time){ this.Time = Time; }

    public Long getRequestCode(){ return RequestCode; }
    public void setRequestCode(Long requestCode){ this.RequestCode = requestCode; }
}
