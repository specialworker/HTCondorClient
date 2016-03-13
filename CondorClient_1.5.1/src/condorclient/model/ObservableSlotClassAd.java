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
public interface ObservableSlotClassAd extends SlotClassAd {

    public ObservableValue<String> sampleIdProperty();

    public ObservableValue<String> eachRunProperty();

    public ObservableValue<String> sampleSubmittedTimeProperty();//?

    public ObservableValue<String> sampleRunTimeProperty();//?public ObservableValue<String> processStatusProperty();

    public SimpleDoubleProperty sampleProcessStatusProperty();

    public ObservableValue<String> sampleJobStatusProperty();

    public ObservableValue<String> cpuIdProperty();

    public ObservableValue<String> slotIdProperty();

}
