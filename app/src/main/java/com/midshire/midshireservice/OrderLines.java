package com.midshire.midshireservice;

import java.util.ArrayList;

public class OrderLines {
    private ArrayList<PartsModel> partsList;

    public OrderLines() {
        partsList = new ArrayList<>();
    }

    public ArrayList<PartsModel> getPartsList() {
        return partsList;
    }

    public void setPartsList(ArrayList<PartsModel> partsList) {
        this.partsList = partsList;
    }

    public void addPart(PartsModel part) {
        partsList.add(part);
    }

    public void removePart(PartsModel part) {
        partsList.remove(part);
    }

    public int getSize(){
        return partsList.size();
    }
}

