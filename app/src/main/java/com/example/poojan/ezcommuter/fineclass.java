package com.example.poojan.ezcommuter;

public class fineclass {
    String officer;
    String email;
    String type;
    String amt;
    fineclass(){}
    fineclass(String officer,String email,String type,String amt){
        this.officer = officer;
        this.email = email;
        this.type = type;
        this.amt = amt;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }
}
