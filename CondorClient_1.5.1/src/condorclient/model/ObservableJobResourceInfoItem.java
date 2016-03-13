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
public interface ObservableJobResourceInfoItem extends JobResourcesInfoItem {

    public ObservableValue<String> jobNameProperty();

    public ObservableValue<String> jobCpuProperty();

    public ObservableValue<String> jobMemProperty();

}
