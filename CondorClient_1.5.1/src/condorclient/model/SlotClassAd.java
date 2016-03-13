/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient.model;

/**
 *
 * @author lianxiang
 */
public interface SlotClassAd {

    public String getSampleId();

    public String getEachRun();

    public String getSampleSubmittedTime();//?

    public String getSampleRunTime();//?

    public Double getSampleProcessStatus();

    public String getSampleJobStatus();

    public String getCpuId();

    public String getSlotId();

}
