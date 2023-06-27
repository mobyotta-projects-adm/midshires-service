package com.midshire.midshireservice;

import com.midshire.midshireservice.MachineModel;
import com.midshire.midshireservice.PartsModel;

import java.util.ArrayList;

public class MachineLines {

    private ArrayList<MachineModel> machineModelArrayList;

    public MachineLines() {
        machineModelArrayList = new ArrayList<>();
    }

    public ArrayList<MachineModel> getMachinesList() {
        return machineModelArrayList;
    }

    public void setMachinesList(ArrayList<MachineModel> machineModels) {
        this.machineModelArrayList = machineModels;
    }

    public void addMachine(MachineModel machine) {
        machineModelArrayList.add(machine);
    }

    public void removeMachine(MachineModel machine) {
        machineModelArrayList.remove(machine);
    }

    public int getSize(){
        return machineModelArrayList.size();
    }
}
