package condorclient.utilities;

import birdbath.ClassAd;
import birdbath.Schedd;
import birdbath.Transaction;
import condor.ClassAdStructAttr;
import condor.FileInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.rpc.ServiceException;

public class TransferFileThread extends Thread {

    XMLHandler handler = new XMLHandler();
    // private int init=0;
    private Map<String, ObservableList<String>> transMap = new TreeMap<>();

    public TransferFileThread() {
        setDaemon(true);
        setName("Thread haha");

    }

    @Override
    public void run() {
        // String[] statusName = {"", "Idle", "Running", "Removed", "Completed", "Held"};
        String[] statusName = {"", "等待", "运行", "移除", "完成", "挂起"};

        //ClassAdStructAttr[]是元素类型
        int i = 0;
        XMLHandler handler = new XMLHandler();

        while (!this.isInterrupted()) {

            // UI updaten
            Platform.runLater(new Runnable() {
                ClassAd ad = null;//birdbath.ClassAd;
                Schedd schedd = null;
                ClassAdStructAttr[][] classAdArray = null;
                URL scheddURL = null;
                XMLHandler handler = new XMLHandler();
                int i = 0;
                Transaction xact;// = schedd.createTransaction();
                String scheddStr = handler.getURL("schedd");
                String condoruser=handler.getUser();
                int transfer = 0;
                String transClusterId = "";
                String transJobId = "";
                int status = 0;

                @Override
                public void run() {
                    //每个3秒执行的过程
                    ///start

                    i++;

                    try {
                        scheddURL = new URL(scheddStr);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        schedd = new Schedd(scheddURL);
                    } catch (ServiceException ex) {
                        Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    xact = schedd.createTransaction();
                    System.out.println("==" + i);
                    //s1
                    try {
                        classAdArray = schedd.getJobAds("owner==\""+condoruser+"\"");
                    } catch (RemoteException ex) {
                        Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("classAdArray:" + classAdArray.length);
                    for (ClassAdStructAttr[] x : classAdArray) {

                        ad = new ClassAd(x);
                        transClusterId = ad.get("ClusterId");
                        transJobId = ad.get("ProcId");
                        //提交时间 运行时间
                        status = Integer.valueOf(ad.get("JobStatus"));
                        ObservableList<String> tmplist = FXCollections.observableArrayList();
                        transMap.putIfAbsent(transClusterId, tmplist);
                        transMap.get(transClusterId).add(transJobId + "-" + status);
                        //if(ad.get(""))
                    }
                    System.out.println("transMap:" + transMap.size());

                    int allC = 1;
                    for (String id : transMap.keySet()) {
                        System.out.println("id:" + id);
                        for (String jis : transMap.get(id)) {
                            String[] ars = jis.split("-");
                            if (!ars[1].equals("4")) {
                                allC = 0;
                                break;

                            }

                        }
                        if (allC == 1 && handler.getTransfer(id) == 0) {//

                            int currentClusterId = Integer.parseInt(id);
                            int currentJobId;
                            String clusterDir=handler.getTaskDir(id);
                            for (String jis : transMap.get(id)) {
                                String[] ars = jis.split("-");
                                System.out.println("jis:" + jis);
                                //ss
                                try {
                                    xact.begin(30);
                                } catch (RemoteException ex) {
                                    Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                FileInfo[] files = null;//存放所有提交的文件
                                currentJobId = Integer.parseInt(ars[0]);

                                try {
                                    files = xact.listSpool(currentClusterId, currentJobId);
                                } catch (RemoteException ex) {
                                    Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if (files != null) {
                                    for (FileInfo file : files) {
                                       /* try {
//                                            xact.getFile(currentClusterId, currentJobId, file.getName(), (int) file.getSize(), new File(clusterDir+"\\"+currentJobId+"\\" + file.getName()));
                                            System.out.print(file.getName() + "\n");
                                        } catch (FileNotFoundException ex) {
                                            Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                            Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                                        }*/
                                    }
                                }
                                try {
                                    xact.commit();
                                } catch (RemoteException ex) {
                                    Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                //e

                            }
                            handler.setTransfer(id);//设置已经传输了文件

                        }

                    }

                }

            }
            );

            try {
                sleep(TimeUnit.SECONDS.toMillis(3));
            } catch (InterruptedException ex) {
                Logger.getLogger(TransferFileThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
