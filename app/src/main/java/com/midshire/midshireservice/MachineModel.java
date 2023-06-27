package com.midshire.midshireservice;

public class MachineModel {

    public String machineMake;
    public String machineModel;
    public String machineSerialNo;

    public MachineModel(String machineMake, String machineModel, String machineSerialNo) {
        this.machineMake = machineMake;
        this.machineModel = machineModel;
        this.machineSerialNo = machineSerialNo;
    }

    public MachineModel() {
    }

    public String getMachineMake() {
        return machineMake;
    }

    public void setMachineMake(String machineMake) {
        this.machineMake = machineMake;
    }

    public String getMachineModel() {
        return machineModel;
    }

    public void setMachineModel(String machineModel) {
        this.machineModel = machineModel;
    }

    public String getMachineSerialNo() {
        return machineSerialNo;
    }

    public void setMachineSerialNo(String machineSerialNo) {
        this.machineSerialNo = machineSerialNo;
    }
}
