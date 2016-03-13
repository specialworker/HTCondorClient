/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package condorclient.model;

import javafx.collections.ObservableList;

/**
 *
 * @author lianxiang
 */
public interface ManagingJobClassAdService {
    public ObservableList<String>  getClassAdIds(String jobStatusName);//ObservableList<String>
    public ObservableList<String> getJobStatusNames();//getProjectNames
   // public ObservableDisplayedClassAd getDisplayedClassAd(String clusterId);//tickectId? 
   // public ObservableDisplayedClassAd createDisplayedClassAdFor(String jobStatusName,String showClusterId);
    public void deleteDisplayedClassAd(String classAdId);

    //delete ?
 /*   public void saveDisplayedClassAd(String classAdId, DisplayedClassAd.DisplayedClassAdStatus status,
            String owner, String submittedTime ,String runTime ,String jobStatus ,String jobPrio ,String memorySize,String cmd);//delete ?
   */

    
}
