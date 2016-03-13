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
public interface DisplayedClassAd {

    public String getClusterName();
    

    public String getClusterId();

    public String getInfoFileName();

    public String getExpFileName();

    public String getTotalSampleNum();

    public String getSubmittedTime();//?

    public String getRunTime();//?

    public Double getProcessStatus();
     public String getJobStatus();

    //public DisplayedClassAdStatus getStatus();//..

   

   // public String getJobPrio();
    // public String getMemorySize();//?
    //public String getCmd();
  

}
