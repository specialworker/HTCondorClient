/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author lianxiang
 */
public final class DisplayedClassAdStub implements ObservableDisplayedClassAd {

    public final SimpleStringProperty clusterName;
    public final SimpleStringProperty clusterId;
    public final SimpleStringProperty infoFileName;
    public final SimpleStringProperty expFileName;
    public final SimpleStringProperty totalSampleNum;
    public final SimpleStringProperty submittedTime;
    public final SimpleStringProperty runTime;
    public final SimpleDoubleProperty processStatus;
    public final SimpleStringProperty jobStatus;
    // public final SimpleStringProperty owner;
    // public final SimpleStringProperty jobPrio;
    //public final SimpleStringProperty memorySize;
    // public final SimpleStringProperty cmd;
   public DisplayedClassAdStub(String clusterName,String  clusterId) {
        this(clusterName,clusterId,null,null,null,null,null,0.0,null);
        
    }
    DisplayedClassAdStub(String clusterName,String  clusterId,String infoFileName,String expFileName,String totalSampleNum,String submittedTime,String runTime,Double processStatus,String jobStatus){
        this.clusterName = new SimpleStringProperty(clusterName);
        this.infoFileName = new SimpleStringProperty(infoFileName);
        this.expFileName = new SimpleStringProperty(expFileName);
        this.totalSampleNum = new SimpleStringProperty(totalSampleNum);
        this.processStatus = new SimpleDoubleProperty(processStatus);
        this.jobStatus = new SimpleStringProperty(jobStatus);
        this.clusterId = new SimpleStringProperty(clusterId);
        this.submittedTime = new SimpleStringProperty(submittedTime);
        this.runTime = new SimpleStringProperty(runTime);
    }

   

   

   

    public void setClusterName(String clusterName) {
        this.clusterName.set(clusterName);
    }

    public void setClusterId(String clusterId) {
        this.clusterId.set(clusterId);
    }
 public void setJobStatus(String jobStatus) {
        this.jobStatus.set(jobStatus);
    }
  public void setInfoFileName(String infoFileName) {
        this.infoFileName.set(infoFileName);
    }
    public void setExpFileName(String expFileName) {
        this.expFileName.set(expFileName);
    }  
   public void setTotalSampleNum(String totalSampleNum) {
        this.totalSampleNum.set(totalSampleNum);
    }
    public void setProcessStatus(Double processStatus) {
        this.processStatus.set(processStatus);
    }
    public void setRunTime(String runTime) {
        this.runTime.set(runTime);
    }
    public void setSubmittedTime(String submittedTime) {
        this.submittedTime.set(submittedTime);
    }


    @Override
    public ObservableValue<String> submittedTimeProperty() {
        return submittedTime;
    }

    @Override
    public ObservableValue<String> runTimeProperty() {
        return runTime;
    }

    /*  @Override
     public ObservableValue<DisplayedClassAdStatus> statusProperty() {
     return status;
     }*/
    @Override
    public ObservableValue<String> jobStatusProperty() {
        return jobStatus;
    }


    
    @Override
    public ObservableValue<String> clusterIdProperty() {
        return clusterId;
    }

    @Override
    public ObservableValue<String> clusterNameProperty() {
       return clusterName;
    }

    @Override
    public ObservableValue<String> infoFileNameProperty() {
       return infoFileName;
    }

    @Override
    public ObservableValue<String> expFileNameProperty() {
        return expFileName;
    }

    @Override
    public ObservableValue<String> totalSampleNumProperty() {
        return totalSampleNum;
    }

    @Override
    public SimpleDoubleProperty processStatusProperty() {
        return processStatus;
    }
  @Override
    public String getClusterId() {
        return clusterId.get();
    }
    @Override
    public String getSubmittedTime() {
        return submittedTime.get();
    }

    @Override
    public String getRunTime() {
        return runTime.get();
    }
    @Override
    public String getClusterName() {
        return clusterName.get();
    }

    @Override
    public String getInfoFileName() {
        return infoFileName.get();
    }

    @Override
    public String getExpFileName() {
        return expFileName.get();
    }

    @Override
    public String getTotalSampleNum() {
        return totalSampleNum.get();
    }

    @Override
    public Double getProcessStatus() {
        return processStatus.get();
    }
    @Override
    public String getJobStatus() {
        return jobStatus.get();
    }

}
