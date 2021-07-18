package com.linuxduck.billz;

import java.util.Date;

public class Bill {
    private Integer bid;
    private String name;
    private Double amount;
    private Boolean deleted;
    private String duedate;

    public Bill(Integer bid, String name, Double amount, Boolean deleted, String duedate){
        this.bid = bid;
        this.name = name;
        this.amount = amount;
        this.deleted = deleted;
        this.duedate = duedate;
    }

    public Integer getBid() { return bid; }

    public String getName(){
        return name;
    }

    public Double getAmount(){
        return amount;
    }

    public Boolean getDeleted() { return deleted; }

    public String getDuedate() { return duedate; }

    public void setName(String name){
        this.name = name;
    }

    public void setAmount(Double amount){
        this.amount = amount;
    }

    public void setDeleted() {
        this.deleted = true;
    }
}
