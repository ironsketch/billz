package com.linuxduck.billz;

import java.util.ArrayList;

public class GroupList {
    Group group;
    ArrayList<Bill> bills;
    public GroupList(){}
    public GroupList(Group group, ArrayList<Bill> bills){
        this.group = group;
        this.bills = bills;
    }

    public Group getGroup(){
        return group;
    }

    public ArrayList<Bill> getBills(){
        return bills;
    }
}
