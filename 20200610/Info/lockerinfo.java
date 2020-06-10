package com.example.login.Info;

public class lockerinfo {
    private String Local;
    private String userID;

    public lockerinfo(){ }

    public lockerinfo(String Local,String userID){
        this.Local = Local;
        this.userID = userID;
    }

    public String getLocal(){
        return this.Local;
    }
    public void setLocal(){
        this.Local = Local;
    }
    public String getUserID(){
        return this.userID;
    }
    public void setUserID(){
        this.userID = userID;
    }

}
