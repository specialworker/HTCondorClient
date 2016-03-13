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
public interface ResourceInfoItem {

    /**
     *
     * @author lianxiang
     */
    public String getMachineId();

    public String getIp();

    public String getCpu();

    public String getMem();

    public String getDisk();

    public String getSlotNum();//?

    public String getConnectInfo();//?


}
