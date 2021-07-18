package com.linuxduck.billz;

public class Group {
    private Integer gid;
    private String name;
    private Double amount;
    public Group(Integer gid, String name, Double amount) {
        this.gid = gid;
        this.name = name;
        this.amount = amount;
    }

    public Integer getGid() { return gid; }

    public String getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(Double amount){
        this.amount = amount;
    }
}
