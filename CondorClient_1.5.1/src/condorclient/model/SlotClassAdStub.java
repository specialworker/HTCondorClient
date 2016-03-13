
package condorclient.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author lianxiang
 */
public final class SlotClassAdStub implements ObservableSlotClassAd {

    public final SimpleStringProperty sampleId;
    public final SimpleStringProperty eachRun;

    public final SimpleStringProperty sampleSubmittedTime;
    public final SimpleStringProperty sampleRunTime;
    public final SimpleDoubleProperty sampleProcessStatus;
    public final SimpleStringProperty sampleJobStatus;
    public final SimpleStringProperty cpuId;
    public final SimpleStringProperty slotId;

    public SlotClassAdStub(String sampleId) {
        this(sampleId, null, null, null, 0.0, null, null, null);

    }

    SlotClassAdStub(String sampleId, String eachRun, String submittedTime, String runTime, Double processStatus, String jobStatus, String cpuId, String slotId) {
        this.sampleId = new SimpleStringProperty(sampleId);
        this.eachRun = new SimpleStringProperty(eachRun);
        this.sampleSubmittedTime = new SimpleStringProperty(submittedTime);
        this.sampleRunTime = new SimpleStringProperty(runTime);
        this.sampleProcessStatus = new SimpleDoubleProperty(processStatus);
        this.sampleJobStatus = new SimpleStringProperty(jobStatus);
        this.cpuId = new SimpleStringProperty(cpuId);
        this.slotId = new SimpleStringProperty(slotId);
    }

    public void setSampleId(String sampleId) {
        this.sampleId.set(sampleId);
    }

    public void setEachRun(String eachRun) {
        this.eachRun.set(eachRun);
    }

    public void setSampleSubmittedTime(String submittedTime) {
        this.sampleSubmittedTime.set(submittedTime);
    }

    public void setSampleRunTime(String runTime) {
        this.sampleRunTime.set(runTime);
    }

    public void setSampleProcessStatus(Double processStatus) {
        this.sampleProcessStatus.set(processStatus);
    }

    public void setSampleJobStatus(String jobStatus) {
        this.sampleJobStatus.set(jobStatus);
    }

    public void setCpuId(String cpuId) {
        this.cpuId.set(cpuId);
    }

    public void setSlotId(String slotId) {
        this.slotId.set(slotId);
    }

    @Override
    public ObservableValue<String> sampleSubmittedTimeProperty() {
        return sampleSubmittedTime;
    }

    @Override
    public ObservableValue<String> sampleRunTimeProperty() {
        return sampleRunTime;
    }

    @Override
    public ObservableValue<String> sampleJobStatusProperty() {
        return sampleJobStatus;
    }


    @Override
    public SimpleDoubleProperty sampleProcessStatusProperty() {
        return sampleProcessStatus;
    }


    @Override
    public Double getSampleProcessStatus() {
        return sampleProcessStatus.get();
    }

    @Override
    public String getSampleJobStatus() {
        return sampleJobStatus.get();
    }
        @Override
    public String getSampleId() {
        return sampleId.get();
    }

    @Override
    public String getEachRun() {
        return eachRun.get();
    }
        @Override
    public String getSampleRunTime() {
         return sampleRunTime.get();
    }
    
    @Override
    public String getSampleSubmittedTime() {
        return sampleSubmittedTime.get();
    }

    @Override
    public String getCpuId() {
        return cpuId.get();
    }

    @Override
    public String getSlotId() {
        return slotId.get();
        
    }

    @Override
    public ObservableValue<String> sampleIdProperty() {
         return sampleId;
    }

    @Override
    public ObservableValue<String> eachRunProperty() {
         return eachRun;
    }

    @Override
    public ObservableValue<String> cpuIdProperty() {
        return cpuId;
        
    }

    @Override
    public ObservableValue<String> slotIdProperty() {
        return slotId;
    }






}
