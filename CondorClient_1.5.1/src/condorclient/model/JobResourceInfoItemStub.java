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
public class JobResourceInfoItemStub implements ObservableJobResourceInfoItem {

    public final SimpleStringProperty jobName;
    public final SimpleStringProperty jobCpu;
    public final SimpleStringProperty jobMem;

    public JobResourceInfoItemStub(String jobName) {
        this(jobName, null, null);

    }

    JobResourceInfoItemStub(String jobName, String jobCpu, String jobMem) {
        this.jobName = new SimpleStringProperty(jobName);
        this.jobCpu = new SimpleStringProperty(jobCpu);
        this.jobMem = new SimpleStringProperty(jobMem);

    }

    public void setJobName(String jobName) {
        this.jobName.set(jobName);
    }

    public void setJobCpu(String jobCpu) {
        this.jobCpu.set(jobCpu);
    }

    public void setJobMem(String jobMem) {
        this.jobMem.set(jobMem);
    }

    @Override
    public ObservableValue<String> jobNameProperty() {
        return jobName;

    }

    @Override
    public ObservableValue<String> jobCpuProperty() {
        return jobCpu;

    }

    @Override
    public ObservableValue<String> jobMemProperty() {
        return jobMem;
    }

    @Override
    public String getJobName() {
        return jobName.get();
    }

    @Override
    public String getJobCpu() {
        return jobCpu.get();
    }

    @Override
    public String getJobMem() {
        return jobMem.get();
    }

}
