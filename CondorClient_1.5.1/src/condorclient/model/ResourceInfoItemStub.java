/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author lianxiang
 */
public class ResourceInfoItemStub implements ObservableResourceInfoItem {

    public final SimpleStringProperty machineId;
    public final SimpleStringProperty ip;
    public final SimpleStringProperty cpu;
    public final SimpleStringProperty mem;
    public final SimpleStringProperty disk;
    public final SimpleStringProperty slotNum;
    public final SimpleStringProperty connectInfo;

    public ResourceInfoItemStub(String ip) {
        this(null, ip, null, null, null, null, null);

    }

    ResourceInfoItemStub(String machineId, String ip, String cpu, String mem, String disk, String slotNum, String connectInfo) {
        this.machineId = new SimpleStringProperty(machineId);
        this.ip = new SimpleStringProperty(ip);
        this.cpu = new SimpleStringProperty(cpu);
        this.mem = new SimpleStringProperty(mem);
        this.disk = new SimpleStringProperty(disk);
        this.slotNum = new SimpleStringProperty(slotNum);
        this.connectInfo = new SimpleStringProperty(connectInfo);
  
    }
    
    public void setMachineId(String machineId) {
        this.machineId.set(machineId);
    }
    
    public void setIp(String ip) {
        this.ip.set(ip);
    }
    
    public void setCpu(String cpu) {
        this.cpu.set(cpu);
    }
    
    public void setMem(String mem) {
        this.mem.set(mem);
    }
    
    public void setDisk(String disk) {
        this.disk.set(disk);
    }
    
    public void setSlotNum(String slotNum) {
        this.slotNum.set(slotNum);
    }
    
    
    public void setConnectInfo(String connectInfo) {
        this.connectInfo.set(connectInfo);
    }
    
    
    

    @Override
    public ObservableValue<String> machineIdProperty() {
         return machineId;

    }

    @Override
    public ObservableValue<String> ipProperty() {
         return ip;

    }

    @Override
    public ObservableValue<String> cpuProperty() {
 return cpu;
    }

    @Override
    public ObservableValue<String> memProperty() {
 return mem;
    }

    @Override
    public ObservableValue<String> diskProperty() {
 return disk;
    }

    @Override
    public ObservableValue<String> slotNumProperty() {
 return slotNum;
    }

    @Override
    public ObservableValue<String> connectInfoProperty() {
 return connectInfo;
    }

    @Override
    public String getMachineId() {
return machineId.get();
    }

    @Override
    public String getIp() {
return ip.get();
    }

    @Override
    public String getCpu() {
return cpu.get();
    }

    @Override
    public String getMem() {
return mem.get();
    }

    @Override
    public String getDisk() {
return disk.get();
    }

    @Override
    public String getSlotNum() {
return slotNum.get();
    }

    @Override
    public String getConnectInfo() {
return connectInfo.get();
    }

}
