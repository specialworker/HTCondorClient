/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient.model;

import javafx.beans.value.ObservableValue;

/**
 *
 * @author lianxiang
 */
public interface ObservableResourceInfoItem extends ResourceInfoItem {

    public ObservableValue<String> machineIdProperty();

    public ObservableValue<String> ipProperty();

    public ObservableValue<String> cpuProperty();

    public ObservableValue<String> memProperty();

    public ObservableValue<String> diskProperty();

    public ObservableValue<String> slotNumProperty();

    public ObservableValue<String> connectInfoProperty();

}
