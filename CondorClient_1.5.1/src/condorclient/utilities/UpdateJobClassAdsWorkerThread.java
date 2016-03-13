package condorclient.utilities;

import birdbath.ClassAd;
import birdbath.Schedd;
import condor.ClassAdStructAttr;
import condorclient.CondorClient;
import condorclient.model.DisplayedClassAdStub;
import condorclient.model.ObservableDisplayedClassAd;
import condorclient.model.ObservableSlotClassAd;
import condorclient.model.SlotClassAdStub;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.FocusModel;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javax.xml.rpc.ServiceException;

public class UpdateJobClassAdsWorkerThread extends Thread {

    final ObservableMap<String, ObservableList<String>> showClassAdStatusMap;

    {//静态程序块？
        final Map<String, ObservableList<String>> map = new TreeMap<>();
        showClassAdStatusMap = FXCollections.observableMap(map);
        for (String s : newList("等待", "运行", "移除", "完成", "挂起", "异常")) {
            showClassAdStatusMap.put(s, FXCollections.<String>observableArrayList());
        }

        // showClassAdStatusMap.addListener(showClassAdStatusMapChangeListener);
    }

    URL scheddURL = null;

    {//静态程序块？可以用于全局声明URL
        XMLHandler handler = new XMLHandler();
        String scheddStr = handler.getURL("schedd");
        try {
            scheddURL = new URL(scheddStr);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String showClusterId;
    String showJobId;

    String showSubmittedTime = "";
    String showRunTime = "";
    int status = 0;
    String showJobStatus;
    String showJobPrio;
    String showMemoSize = "";
    String showCmd;
    // private ObservableList<DisplayedClassAdStub> displayedClassAds = FXCollections.observableArrayList();//用于填充相应的表格
    private ObservableList<DisplayedClassAdStub> displayedClusters = FXCollections.observableArrayList();//存放显示某种状态的cluster，即table中的内容
    private Map<String, ObservableList<DisplayedClassAdStub>> id2JobClassAdsMap = new TreeMap<>();//存放每个clusterId到其对应的所有job
    private TableView<ObservableDisplayedClassAd> table;
    private int showSampleTab;
    public int oneSelectedBoxNo = 0;
    public TableView<ObservableSlotClassAd> sampleTab;
    private int clusterToShow;
    private int toShowOrNot = 0;
    private final ListChangeListener<ObservableDisplayedClassAd> tableSelectionChanged
            = new ListChangeListener<ObservableDisplayedClassAd>() {

                @Override
                public void onChanged(ListChangeListener.Change<? extends ObservableDisplayedClassAd> c) {

                    //System.out.println("tableSelectionChanged\n");
                    updateWhichClustertoShow();
                    updateModifyButtonState();
                    updateRemoveButtonState();
                    updatePauseButtonState();
                    updateRunButtonState();
                    updateStopButtonState();
                    updateGoonButtonState();
//                    updateDownloadButtonState();
                    updateCheckdButtonState();

                }
            };
    private final ChangeListener<String> clusterStatusItemSelectedListener = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            statusUnselected(oldValue);
            statusSelected(newValue);
            System.out.print("oldValue:" + oldValue + "\nnewValue:" + newValue + "\n");
        }
    };
    private final ListChangeListener<ObservableDisplayedClassAd> whichClusterStatusListToShowListener = new ListChangeListener<ObservableDisplayedClassAd>() {

        @Override
        public void onChanged(ListChangeListener.Change<? extends ObservableDisplayedClassAd> c) {
            if (table == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (ObservableDisplayedClassAd p : c.getAddedSubList()) {
                        table.getItems().add(p);
                    }
                }
                if (c.wasRemoved() || c.wasReplaced()) {
                    for (ObservableDisplayedClassAd p : c.getRemoved()) {
                        ObservableDisplayedClassAd removed = p;

                        if (removed != null) {
                            table.getItems().remove(removed);
                        }
                    }
                }
            }
        }
    };
    private ListView<String> statusListView;
    private ListView<String> countListView;

    private VBox statusVBox;
    private Label total_label = new Label();
    private Label completed_label = new Label();

    private Label running_label = new Label();

    private Label idle_label = new Label();

    private Label held_label = new Label();
    private Label error_label = new Label();

    private Label removed_label = new Label();
//9 ge
    private Button create_button; // Value injected by FXMLLoader

    private Button modify_button; // Value injected by FXMLLoader

    private Button remove_button; // Value injected by FXMLLoader

    private Button run_button; // Value injected by FXMLLoader

    private Button stop_button; // Value injected by FXMLLoader

    private Button pause_button; // Value injected by FXMLLoader

    private Button goon_button; // Value injected by FXMLLoader

    //   private Button download_button; // Value injected by FXMLLoader
    private Button check_button; // Value injected by FXMLLoader
    XMLHandler handler = new XMLHandler();
    // private int init=0;

    public UpdateJobClassAdsWorkerThread(TableView<ObservableDisplayedClassAd> table, TableView<ObservableSlotClassAd> sampleTab, ListView<String> statusListView, ListView<String> countListView, ToolBar toolBar) {
        setDaemon(true);
        setName("Thread haha");
        this.table = table;
        this.sampleTab = sampleTab;
        this.statusListView = statusListView;
        this.countListView = countListView;
        this.table.setId("table");
        //countListView.getSelectionModel().setSelectionMode(null);
        ObservableList<String> tmpobslist = FXCollections.observableArrayList();//tmpobslist此处只是做中转变量
        //tmpobslist.addAll(showClassAdStatusMap.keySet());
        tmpobslist.addAll("全部", "完成", "运行", "等待", "挂起", "异常", "移除");
        // FXCollections.sort(tmpobslist);
        statusListView.setItems(tmpobslist);
        statusListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ObservableList<String> countlist = FXCollections.observableArrayList();//tmpobslist此处只是做中转变量
        // countlist.addAll(total_label,completed_label, running_label, idle_label,held_label,error_label ,removed_label);
        countlist.addAll("(0)", "(0)", "(0)", "(0)", "(0)", "(0)", "(0)");
        this.countListView.setItems(countlist);
        // countListView.selectionModelProperty().bind(statusListView.selectionModelProperty());
        // this.countListView.setSelectionModel(null);
        //countListView.selectionModelProperty().bind(statusListView.getSelectionModel().getSelectedItems());
        create_button = (Button) toolBar.getItems().get(0);
        modify_button = (Button) toolBar.getItems().get(1);
        remove_button = (Button) toolBar.getItems().get(2);
        run_button = (Button) toolBar.getItems().get(3);
        stop_button = (Button) toolBar.getItems().get(4);
        pause_button = (Button) toolBar.getItems().get(5);
        goon_button = (Button) toolBar.getItems().get(6);
        // download_button = (Button) toolBar.getItems().get(7);
        check_button = (Button) toolBar.getItems().get(7);
    
        final ObservableList<ObservableDisplayedClassAd> tableSelection = table.getSelectionModel().getSelectedItems();
        tableSelection.addListener(tableSelectionChanged);
        //s
        final ContextMenu cm = new ContextMenu();
        MenuItem cmItem1 = new MenuItem("打开结果目录");
        cmItem1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                //e

                String openClusterDir = "";
                final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
                for (Object o : selectedNodeList) {
                    if (o instanceof ObservableDisplayedClassAd) {

                        openClusterDir = handler.getTaskDir(((ObservableDisplayedClassAd) o).getClusterId());
                        try {
                            Desktop.getDesktop().open(new File(openClusterDir));
                        } catch (IOException ex) {
                            Logger.getLogger(UpdateJobClassAdsWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                //s

            }
        });

        cm.getItems().add(cmItem1);
        //e
        table.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (e.getButton() == MouseButton.SECONDARY) {
                            cm.show(table, e.getScreenX(), e.getScreenY());
                        }
                    }
                });
        statusListView.getSelectionModel().selectedItemProperty().addListener(clusterStatusItemSelectedListener);
//statusListView.getItems().s
        //statusListView
        FocusModel<String> f = new FocusModel() {

            @Override
            protected int getItemCount() {
                return 1;
            }

            @Override
            protected Object getModelItem(int index) {
                return null;
            }
        };
        f.focus(0);
        //  this.statusListView.setFocusModel(f);
        // this.statusListView.focusModelProperty().set(f);

    }

    private void statusUnselected(String oldStatus) {
        if (oldStatus != null) {
            displayedClusters.removeListener(whichClusterStatusListToShowListener);
            displayedClusters = null;
            table.getSelectionModel().clearSelection();
            table.getItems().clear();

        }

    }

    private void statusSelected(String newStatus) {
        // System.out.println(newStatus + "1showClassAdStatusMap ids:" + showClassAdStatusMap.get("挂起"));
        if (newStatus != null) {
            // System.out.println(newStatus + "2showClassAdStatusMap ids:" + showClassAdStatusMap.get("挂起"));
            ObservableList<DisplayedClassAdStub> getjobslist = FXCollections.observableArrayList();
            if (newStatus.equals("全部")) {
                for (String s : id2JobClassAdsMap.keySet()) {
                    getjobslist.add(id2JobClassAdsMap.get(s).get(0));
                }

            } else {
                // System.out.println(newStatus + "ddddshowClassAdStatusMap ids:" + showClassAdStatusMap.get(newStatus));
                // if (showClassAdStatusMap.get(newStatus).size()!=0) {

                for (String s : showClassAdStatusMap.get(newStatus)) {
                    getjobslist.add(id2JobClassAdsMap.get(s).get(0));

                }
                // System.out.println("getjobslist:" + getjobslist.toString());
                // }
            }
            displayedClusters = getjobslist;
            table.getItems().clear();
            table.getItems().addAll(displayedClusters);

            displayedClusters.addListener(whichClusterStatusListToShowListener);
            if (create_button != null) {
                create_button.setDisable(false);
            }

        }
    }

    private void updateCheckdButtonState() {
        boolean disable = true;
        if (check_button != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (check_button != null) {
            check_button.setDisable(disable);
        }
    }

    /* private void updateDownloadButtonState() {
     boolean disable = true;
     if (download_button != null && table != null) {
     final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
     disable = nothingSelected;
     }
     if (download_button != null) {
     download_button.setDisable(disable);
     }
     }*/
    private void updateGoonButtonState() {
        boolean disable = true;
        if (goon_button != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (goon_button != null) {
            goon_button.setDisable(disable);
        }
    }

    private void updateStopButtonState() {
        boolean disable = true;
        if (stop_button != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (stop_button != null) {
            stop_button.setDisable(disable);
        }
    }

    private void updatePauseButtonState() {
        boolean disable = true;
        if (pause_button != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (pause_button != null) {
            pause_button.setDisable(disable);
        }
    }

    private void updateRunButtonState() {
        boolean disable = true;
        if (run_button != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (run_button != null) {
            run_button.setDisable(disable);
        }
    }

    private void updateRemoveButtonState() {
        boolean disable = true;
        if (remove_button != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (remove_button != null) {
            remove_button.setDisable(disable);
        }
    }

    private void updateModifyButtonState() {
        //  System.out.println("modify");
        boolean disable = true;
        if (modify_button != null && table != null) {
            final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (modify_button != null) {
            modify_button.setDisable(disable);
        }
    }

    private <T> List<T> newList(T... items) {
        return Arrays.asList(items);
    }

    public void updateWhichClustertoShow() {

        final boolean nothingSelected = table.getSelectionModel().getSelectedItems().isEmpty();
        if (nothingSelected) {
            //toShowOrNot = 0;
        } else {
            toShowOrNot = 1;
            final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
            for (Object o : selectedNodeList) {
                if (o instanceof ObservableDisplayedClassAd) {
                    clusterToShow = Integer.parseInt(((ObservableDisplayedClassAd) o).getClusterId());
                }
            }

        }
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
                int i = 0;
                String condoruser=handler.getUser();

                @Override
                public void run() {
                    //每个3秒执行的过程
                    ///start

                    ClassAd ad = null;//birdbath.ClassAd;
                    Schedd schedd = null;
                    
                    try {
                        schedd = new Schedd(scheddURL);
                    } catch (ServiceException ex) {
                        Logger.getLogger(UpdateJobClassAdsWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ClassAdStructAttr[][] classAdArray = null;

                    i++;

                    try {
                        classAdArray = schedd.getJobAds("owner==\""+condoruser+"\"");
                    } catch (RemoteException ex) {
                        Logger.getLogger(UpdateJobClassAdsWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    showClassAdStatusMap.get("等待").clear();
                    showClassAdStatusMap.get("运行").clear();
                    showClassAdStatusMap.get("移除").clear();
                    showClassAdStatusMap.get("完成").clear();
                    showClassAdStatusMap.get("挂起").clear();
                    showClassAdStatusMap.get("异常").clear();

                    id2JobClassAdsMap.clear();
                    String showJobName;

                    for (ClassAdStructAttr[] x : classAdArray) {
                        DisplayedClassAdStub updateTableClassAd = new DisplayedClassAdStub("id", "Name");//其中的clusterId字段存放的是JobId

                        ad = new ClassAd(x);
                        //System.out.print(ad.toString());
                        showClusterId = ad.get("ClusterId");
                        showJobId = ad.get("ProcId");
                        //提交时间 运行时间
                        status = Integer.valueOf(ad.get("JobStatus"));
                        showJobStatus = statusName[status];
                        showJobPrio = ad.get("JobPrio");
                        //内存大小
                        showCmd = ad.get("Cmd");

                        showJobName = handler.getJobName(showClusterId);

                        // System.out.println("showJobName:" + showJobName);
                        updateTableClassAd.setClusterId(showClusterId);//clusterId中放的是jobId
                        updateTableClassAd.setClusterName(showJobName);
                        String ar = ad.get("Arguments");

                        String[] ars = ar.split(" ");
                        if (ars.length > 2) {
                            updateTableClassAd.setExpFileName(ars[1]);
                            updateTableClassAd.setInfoFileName(ars[0]);
                        }
                        updateTableClassAd.setJobStatus(showJobStatus);
                        Long runtime;// = (Long.parseLong(ad.get("CommittedTime")) - Long.parseLong(ad.get("CommittedSuspensionTime")));
                        Double progress = 0.0;

                        //sdd
                        // 
                        //EnteredCurrentStatus
                        if (showJobStatus.equals("运行")) {//处于运行状态
                            runtime = (Long.parseLong(ad.get("ServerTime")) - Long.parseLong(ad.get("EnteredCurrentStatus")));
                            progress = Double.valueOf(runtime) / 20;
                        } else {//其他状态
                            runtime = (Long.parseLong(ad.get("CommittedTime")) - Long.parseLong(ad.get("CommittedSuspensionTime")));
                        }
                        Long hh = runtime / (60 * 60);
                        String hhs = "" + hh;
                        if (hh < 10) {
                            hhs = "0" + hh;
                        }
                        Long mm = (runtime % (60 * 60)) / 60;
                        String mms = "" + mm;
                        if (mm < 10) {
                            mms = "0" + mm;
                        }
                        Long ss = runtime % 60;
                        String sss = "" + ss;
                        if (ss < 10) {
                            sss = "0" + ss;
                        }
                        String t = "" + hhs + ":" + mms + ":" + sss;
                        updateTableClassAd.setRunTime(t);
                        //e

                        if (!showJobStatus.equals("完成")) {
                            if (showJobStatus.equals("等待")) {
                                updateTableClassAd.setProcessStatus(-0.1);
                            } else if (progress < 0.9) {
                                updateTableClassAd.setProcessStatus(progress);
                            }
                        } else {
                            updateTableClassAd.setProcessStatus(1.0);
                        }
                        // updateTableClassAd.setProcessStatus(0.5);
                        updateTableClassAd.setTotalSampleNum("20");//要用度过来的？
                        String str = ad.get("QDate");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long m = Long.parseLong(str) * 1000;
                        String QDate = sdf.format(new Date(m));
                        //System.out.print("QDate:" + QDate + "\n");//提交
                        updateTableClassAd.setSubmittedTime(QDate);

                        ObservableList<DisplayedClassAdStub> tmplist = FXCollections.observableArrayList();
                        id2JobClassAdsMap.putIfAbsent(showClusterId, tmplist);
                        id2JobClassAdsMap.get(showClusterId).add(updateTableClassAd);
                        //对已完成任务的结果文件回传处理

                    }
                    // System.out.println("id2JobClassAdsMap.keySet():" + id2JobClassAdsMap.keySet());
                    // System.out.println("id2JobClassAdsMap.status:" + id2JobClassAdsMap.get("101").get(0).getJobStatus());
                    for (String idstr : id2JobClassAdsMap.keySet()) {
                        //get得到的是id2JobClassAdsMap数据拷贝不是引用

                        DisplayedClassAdStub get = id2JobClassAdsMap.get(idstr).get(0);//id2JobClassAdsMap.get(idstr)第0个是cluster的状况，综合和了每个job的信息
                        //设置每个任务的进度
                        Double clusterProgress = 0.0;
                        String totaljobstatus = "";
                        int hcount = 0;
                        int icount = 0;
                        int xcount = 0;
                        int rcount = 0;
                        int ccount = 0;
                        int jcount = id2JobClassAdsMap.get(idstr).size();
                        for (DisplayedClassAdStub p : id2JobClassAdsMap.get(idstr)) {//判断整个任务的状态
                            clusterProgress = clusterProgress + p.getProcessStatus();
                            if (p.getJobStatus().equals("等待")) {
                                icount++;
                            } else if (p.getJobStatus().equals("挂起")) {
                                hcount++;
                            } else if (p.getJobStatus().equals("移除")) {
                                xcount++;
                            } else if (p.getJobStatus().equals("运行")) {
                                rcount++;
                            }else if (p.getJobStatus().equals("完成")) {
                                ccount++;
                            }
                        }
                        if (xcount == jcount) {
                            get.setJobStatus("移除");
                        } else if ((hcount != 0) && (hcount != jcount)) {
                            get.setJobStatus("异常");//一个任务中的部分节点被挂了
                        } else if (icount == jcount) {
                            get.setJobStatus("等待");
                        } else if ((ccount !=jcount)&& (rcount>0||ccount>0)) {//部分运行或完成，而部分在等待的状况
                            get.setJobStatus("运行");
                        }
                        clusterProgress = clusterProgress / jcount;
                        get.setProcessStatus(clusterProgress);
                        id2JobClassAdsMap.get(idstr).get(0).setJobStatus(get.getJobStatus());
                        id2JobClassAdsMap.get(idstr).get(0).setProcessStatus(get.getProcessStatus());
                        //displayedClassAds.add(get);
                        showClassAdStatusMap.get(get.getJobStatus()).add(idstr);//此处保障所有id是一份
                        //  System.out.println("get.status:" + get.getJobStatus());
                    }
                    //处理在表中显示某种状态的任务
                    if (statusListView != null) {
                        if (!statusListView.getSelectionModel().isEmpty()) {
                            String currentStatus = ((ObservableList<String>) statusListView.getSelectionModel().getSelectedItems()).toString();
                            //currentStatus.toString()
                            currentStatus = currentStatus.substring(currentStatus.indexOf("[") + 1, currentStatus.indexOf("]"));
                            // System.out.println("currentStatus:" + currentStatus);
                            ObservableList<DisplayedClassAdStub> getjobslist = FXCollections.observableArrayList();
                            if (currentStatus.equals("全部")) {
                                for (String cid : id2JobClassAdsMap.keySet()) {
                                    getjobslist.add(id2JobClassAdsMap.get(cid).get(0));
                                }

                            } else {
                                if (showClassAdStatusMap.get(currentStatus) != null) {
                                    for (String s : showClassAdStatusMap.get(currentStatus)) {

                                        getjobslist.add(id2JobClassAdsMap.get(s).get(0));

                                    }
                                }
                            }
                            displayedClusters = getjobslist;
                            if (table != null) {

                                table.getItems().clear();

                                table.getItems().addAll(displayedClusters);

                            }
                        }
                    }

                    int idleCount = 0;
                    int runningCount = 0;
                    int removedCount = 0;
                    int completeCount = 0;
                    int heldCount = 0;
                    int errorCount = 0;

                    idleCount = showClassAdStatusMap.get("等待").size();
                    runningCount = showClassAdStatusMap.get("运行").size();
                    removedCount = showClassAdStatusMap.get("移除").size();
                    completeCount = showClassAdStatusMap.get("完成").size();
                    heldCount = showClassAdStatusMap.get("挂起").size();/**/

                    errorCount = showClassAdStatusMap.get("异常").size();
                    int totalCount = idleCount + runningCount + removedCount + completeCount + heldCount + errorCount;

                    countListView.getItems().clear();
                    ObservableList<String> countlist = FXCollections.observableArrayList();//tmpobslist此处只是做中转变量
                    // countlist.addAll(total_label,completed_label, running_label, idle_label,held_label,error_label ,removed_label);
                    countlist.addAll("(" + totalCount + ")", "(" + completeCount + ")", "(" + runningCount + ")", "(" + idleCount + ")", "(" + heldCount + ")", "(" + errorCount + ")", "(" + removedCount + ")");
                    countListView.setItems(countlist);

                    //显示sampleTab中的信息
                    if (toShowOrNot == 1 && (table.getItems() != null)) {
                        int boxno = oneSelectedBoxNo;
                        // find clusterid
                        int clustertoshow = 0;
                        ObservableList<SlotClassAdStub> tmpslotAds = FXCollections.observableArrayList();//存放要放入表中的list

                        if (id2JobClassAdsMap.containsKey("" + clusterToShow)) {

                            clustertoshow = Integer.parseInt(id2JobClassAdsMap.get("" + clusterToShow).get(0).getClusterId());
                            // System.out.println("clustertoshow:" + clustertoshow+"\nid2JobClassAdsMap："+id2JobClassAdsMap.toString()+"\nlist:");
                            for (DisplayedClassAdStub disad : id2JobClassAdsMap.get("" + clusterToShow)) {
                                //System.out.println(disad.toString());
                                // System.out.println(disad.getClusterId()+"s:"+s);
                                String constraint = "ClusterId==" + clusterToShow;

                                try {
                                    classAdArray = schedd.getJobAds(constraint);
                                } catch (RemoteException ex) {
                                    Logger.getLogger(UpdateJobClassAdsWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                tmpslotAds.clear();
                                for (ClassAdStructAttr[] x : classAdArray) {
                                    ad = new ClassAd(x);
                                    SlotClassAdStub slotClassAdStub = new SlotClassAdStub(ad.get("ProcId"));//设置了样本号
                                    String cpuid = "";
                                    if (ad.get("LastRemoteHost") != null) {//对于不同执行状态的进一步判断
                                        cpuid = ad.get("LastRemoteHost").substring(ad.get("LastRemoteHost").indexOf("@") + 1);
                                    }
                                    // System.out.println("ad.get(\"ProcId\")" + ad.get("ProcId"));
                                    slotClassAdStub.setSlotId(ad.get("LastRemoteHost"));
                                    slotClassAdStub.setCpuId(cpuid);
                                    String[] ars = ad.get("Arguments").split(" ");

                                    if (ars.length > 2) {
                                        slotClassAdStub.setEachRun(ars[2]);//此处需要截取字符串
                                    }
                                    String str = ad.get("QDate");
                                    // SimpleDateFormat sdf0 = new SimpleDateFormat("HH:mm:ss");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    long m = Long.parseLong(str) * 1000;
                                    String QDate = sdf.format(new Date(m));
                                    //System.out.print("QDate:" + QDate + "\n");//提交
                                    slotClassAdStub.setSampleSubmittedTime(QDate);
                                    Long runtime = (Long.parseLong(ad.get("CommittedTime")) - Long.parseLong(ad.get("CommittedSuspensionTime")));
                                    Long hh = runtime / (60 * 60);
                                    String hhs = "" + hh;
                                    if (hh < 10) {
                                        hhs = "0" + hh;
                                    }
                                    Long mm = (runtime % (60 * 60)) / 60;
                                    String mms = "" + mm;
                                    if (mm < 10) {
                                        mms = "0" + mm;
                                    }
                                    Long ss = runtime % 60;
                                    String sss = "" + ss;
                                    if (ss < 10) {
                                        sss = "0" + ss;
                                    }
                                    String t = "" + hhs + ":" + mms + ":" + sss;
                                    slotClassAdStub.setSampleRunTime(t);

                                    status = Integer.valueOf(ad.get("JobStatus"));
                                    showJobStatus = statusName[status];
                                    slotClassAdStub.setSampleJobStatus(showJobStatus);
                                    Double progress = Double.valueOf(runtime) / 20;
                                    if (!showJobStatus.equalsIgnoreCase("完成")) {
                                        if (showJobStatus.equalsIgnoreCase("等待")) {
                                            slotClassAdStub.setSampleProcessStatus(-0.1);
                                        } else if (progress < 0.9) {
                                            slotClassAdStub.setSampleProcessStatus(progress);
                                        }
                                    } else {
                                        slotClassAdStub.setSampleProcessStatus(1.0);
                                    }
                                    tmpslotAds.add(slotClassAdStub);

                                }

                                // slotClassAds
                            }

                            // }
                            // }
                            if (sampleTab != null) {

                                sampleTab.getItems().clear();
                                sampleTab.getItems().addAll(tmpslotAds);
                                // System.out.println("table update");
                            }
                        }

                    } else {
                        sampleTab.getItems().clear();
                    }

                    // System.out.println("\nnameStu:" + Thread.currentThread().getName() + "i:" + i);
                    System.out.println("i:" + i);

                }
            }
            );

            try {
                sleep(TimeUnit.SECONDS.toMillis(3));
            } catch (InterruptedException ex) {
                Logger.getLogger(UpdateJobClassAdsWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
