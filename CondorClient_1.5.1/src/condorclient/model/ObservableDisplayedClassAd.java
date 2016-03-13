/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author lianxiang
 */
public interface ObservableDisplayedClassAd extends DisplayedClassAd {

    public ObservableValue<String> clusterNameProperty();

    public ObservableValue<String> clusterIdProperty();

    public ObservableValue<String> infoFileNameProperty();//?

    public ObservableValue<String> expFileNameProperty();//?

    public ObservableValue<String> totalSampleNumProperty();//?

    public ObservableValue<String> submittedTimeProperty();//?

    public ObservableValue<String> runTimeProperty();//?public ObservableValue<String> processStatusProperty();

    public SimpleDoubleProperty processStatusProperty();

    public ObservableValue<String> jobStatusProperty();
      // public ObservableValue<String> ownerProperty();
    //  public ObservableValue<DisplayedClassAdStatus> statusProperty();//..
    // public ObservableValue<String> jobPrioProperty();
    // public ObservableValue<String> memorySizeProperty();//?
    // public ObservableValue<String> cmdProperty();
}
