/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient;

import birdbath.ClassAd;
import birdbath.Schedd;
import birdbath.Transaction;
import condor.ClassAdAttrType;
import condor.ClassAdStructAttr;
import condor.UniverseType;
import condorclient.model.DisplayedClassAdStub;
import condorclient.model.ObservableDisplayedClassAd;
import condorclient.model.ObservableJobResourceInfoItem;
import condorclient.model.ObservableResourceInfoItem;
import condorclient.model.ObservableSlotClassAd;
import condorclient.utilities.XMLHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import javax.xml.rpc.ServiceException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author lianxiang
 */
public class MainFXMLController extends AnchorPane {

    private CondorClient application;

    public TableColumn<ObservableDisplayedClassAd, String> colClusterName; // Value injected by FXMLLoader
//    @FXML //  fx:id="table"
    // public TableView<ObservableDisplayedClassAd> table; // Value injected by FXMLLoader
    @FXML //  fx:id="create_button"
    private Button create_button; // Value injected by FXMLLoader
    @FXML //  fx:id="modify_button"
    private Button modify_button; // Value injected by FXMLLoader
    @FXML //  fx:id="remove_button"
    private Button remove_button; // Value injected by FXMLLoader
    @FXML //  fx:id="run_button"
    private Button run_button; // Value injected by FXMLLoader
    @FXML //  fx:id="stop_button"
    private Button stop_button; // Value injected by FXMLLoader
    @FXML //  fx:id="pause_button"
    private Button pause_button; // Value injected by FXMLLoader
    @FXML //  fx:id="goon_button"
    private Button goon_button; // Value injected by FXMLLoader
    // @FXML //  fx:id="download_button"
    //  private Button download_button; // Value injected by FXMLLoader
    @FXML //  fx:id="check_button"
    private Button check_button; // Value injected by FXMLLoader
    @FXML //  fx:id="connect_button"
    private Button connect_button; // Value injected by FXMLLoader
    private VBox boxContainer; // Value injected by FXMLLoader
    @FXML
    public GridPane usageChartGrid;
    private int[] isSelected = new int[1000];
    private int modifyNo = 0;
    public TableView table = new TableView<DisplayedClassAdStub>();
    public TableView sampleTab = new TableView<DisplayedClassAdStub>();

    private String displayedClassAdId; // the id of the bug displayed in the details section.
    private String displayedClassAdsStatusName; // the name of the project of the bug displayed in the detailed section.
    private String errorMessage;

    final ObservableList<ObservableDisplayedClassAd> tableContent = FXCollections.observableArrayList();
    final ObservableList<ObservableResourceInfoItem> resourcesContent = FXCollections.observableArrayList();
    final ObservableList<ObservableJobResourceInfoItem> jobContent = FXCollections.observableArrayList();
    final ObservableList<ObservableSlotClassAd> sampleContent = FXCollections.observableArrayList();
    //private final int MaxBoxNum = 31;
    private int showBoxNum = 0;
    public static final String ColClusterIdMapKey = "clusterId";
    public static final String ColClusterNameMapKey = "clusterName";
    public static final String ColSubmittedTimeMapKey = "submittedTime";
    public static final String ColRunTimeMapKey = "runTime";
    public static final String ColJobStatusMapKey = "jobStatus";
    public static final String ColInfoFileNameMapKey = "infoFileName";
    public static final String ColExpFileNameMapKey = "expFileName";
    public static final String ColProcessStatusMapKey = "processStatus";
    public static final String ColTotalSampleNumMapKey = "totalSampleNum";

    public static final String ColConnectInfoMapKey = "connectInfo";
    public static final String ColIpMapKey = "ip";
    public static final String ColMachineIdMapKey = "machineId";
    public static final String ColMemMapKey = "mem";
    public static final String ColDiskMapKey = "disk";
    public static final String ColSlotNumMapKey = "slotNum";
    public static final String ColCpuMapKey = "cpu";

    public static final String ColJobNameMapKey = "jobName";
    public static final String ColJobCpuMapKey = "jobCpu";
    public static final String ColJobMemMapKey = "jobMem";
    public int showSampleTab = 0;
    public int oneSelectedBoxNo = 0;
    public static final String ColSampleIdMapKey = "sampleId";
    public static final String ColEachRunMapKey = "eachRun";
    public static final String ColSampleSubmittedTimeMapKey = "sampleSubmittedTime";
    public static final String ColSampleRunTimeMapKey = "sampleRunTime";
    public static final String ColSampleJobStatusMapKey = "sampleJobStatus";
    public static final String ColSampleProcessStatusMapKey = "sampleProcessStatus";
    public static final String ColCpuIdMapKey = "cpuId";
    public static final String ColSlotIdMapKey = "slotId";
    private final int MAXFILENUM = 10;//定义最大文件传输数量为10

    private ObservableList<DisplayedClassAdStub> boxToClusterIdList = FXCollections.observableArrayList();

    private ObservableList<String> displayedStatusNames;//displayedProjectNames
    private final Map<String, DisplayedClassAdStub> id2JobClassAdsMap = new TreeMap<>();
    // Thread th;
    ObservableList<String> displayedStatusView = FXCollections.observableArrayList();//projectsView
    @FXML
    public TableView<ObservableResourceInfoItem> resourcesTab;
    @FXML
    private TableColumn<ObservableResourceInfoItem, String> colMachineId;
    @FXML
    private TableColumn<ObservableResourceInfoItem, String> colIp;
    @FXML
    private TableColumn<ObservableResourceInfoItem, String> colCpu;
    @FXML
    private TableColumn<ObservableResourceInfoItem, String> colMem;
    @FXML
    private TableColumn<ObservableResourceInfoItem, String> colDisk;
    @FXML
    private TableColumn<ObservableResourceInfoItem, String> colSlotNum;
    @FXML
    private TableColumn<ObservableResourceInfoItem, String> colConnectInfo;

    @FXML
    public TableView<ObservableJobResourceInfoItem> job_resourcesTab;
    @FXML
    private TableColumn<ObservableJobResourceInfoItem, String> colJobName;
    @FXML
    private TableColumn<ObservableJobResourceInfoItem, String> colJobCpu;
    @FXML
    private TableColumn<ObservableJobResourceInfoItem, String> colJobMem;

    String[] statusName = {"", "等待", "运行", "移除", "完成", "挂起"};
    int status = 0;
    @FXML
    public ListView<String> statusListView;//
    @FXML
    public ListView<String> countListView;

    @FXML
    public ToolBar jobManagerBar;
    @FXML
    public AnchorPane tablePane;
    @FXML
    public AnchorPane sampleTablePane;
    @FXML
    public VBox sampleBox;
    private Boolean isConnected = false;
    private String condoruser;

    //defining a series
    {
        XMLHandler handler = new XMLHandler();
        String collectorStr = handler.getURL("collector");
        String scheddStr = handler.getURL("schedd");
        condoruser=handler.getUser();
    }

    public void setApp(CondorClient application) {
        connect_button.setDisable(true);
        this.application = application;

    }

    void initialize2() {
        // TODO

        errorMessage = "";

        configureTable();

        if (create_button != null) {
            create_button.setDisable(true);
        }
        initConfigureButtons();
        //connectToService();

        connect_button.setDisable(false);

        int i = 0;
        while (i < 1000) {
            isSelected[i] = 0;
            i++;
        }
        if (statusListView != null) {
            statusListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            //projectItemSelected是listener
            //   displayedStatusNames.addListener(displayedStatusNamesListener);
        }
       // condoruser=handler.

    }

    @FXML
    void modifyJobFired(ActionEvent event) {

        final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
        for (Object o : selectedNodeList) {
            if (o instanceof ObservableDisplayedClassAd) {
                modifyNo = Integer.parseInt(((ObservableDisplayedClassAd) o).getClusterId());
            }
        }

        application.gotoModifyJob("" + modifyNo);
    }

    @FXML
    void pauseButtonFired(ActionEvent event) {
        int delNo = 0;
        int pauseId = 0;
        int n = JOptionPane.showConfirmDialog(null, "确认挂起任务吗?", "确认挂起框", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {

            //得到checkbox对应的clusterId
            System.out.print(Thread.currentThread().getName() + "\n");

            URL url = null;
            XMLHandler handler = new XMLHandler();
            String scheddStr = handler.getURL("schedd");
            try {
                url = new URL(scheddStr);
            } catch (MalformedURLException e3) {
                // TODO Auto-generated catch block
                e3.printStackTrace();
            }
            Schedd schedd = null;

            try {
                schedd = new Schedd(url);
            } catch (ServiceException ex) {
                Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            //ClassAdStructAttr[]是元素类型
            int boxToClusterId;

            ClassAd ad = null;//birdbath.ClassAd;
            ClassAdStructAttr[][] classAdArray = null;

            Transaction xact = schedd.createTransaction();
            try {
                xact.begin(30);

            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            int job = 0;
            //s
            final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
            for (Object o : selectedNodeList) {
                if (o instanceof ObservableDisplayedClassAd) {
                    pauseId = Integer.parseInt(((ObservableDisplayedClassAd) o).getClusterId());
                }
            }
            //e

            String findreq = "owner==\""+condoruser+"\"&&ClusterId==" + pauseId;

            try {
                classAdArray = schedd.getJobAds(findreq);
            } catch (RemoteException ex) {
                Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            String showJobStatus = null;
            for (ClassAdStructAttr[] x : classAdArray) {
                ad = new ClassAd(x);
                job = Integer.parseInt(ad.get("ProcId"));

                status = Integer.valueOf(ad.get("JobStatus"));
                showJobStatus = statusName[status];
                try {
                    if (showJobStatus.equals("等待") || showJobStatus.equals("运行") || showJobStatus.equals("异常")) {
                        xact.holdJob(pauseId, job, "");
                    } else {//弹出框提示一下
                        if (showJobStatus.equals("完成") || showJobStatus.equals("挂起")) {
                            JOptionPane.showMessageDialog(null, "当前任务状态不支持挂起操作");
                            return;
                        }
                    }
                    // System.out.print("ts.getClusterId():" + showClusterId + "\n");
                } catch (RemoteException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                xact.commit();
            } catch (RemoteException e) {

                e.printStackTrace();
            }

        } else if (n == JOptionPane.NO_OPTION) {
            System.out.println("qu xiao");

        }
    }

    @FXML

    void deleteButtonFired(ActionEvent event) {
        int delNo = 0;

        int n = JOptionPane.showConfirmDialog(null, "确认删除任务吗?", "确认删除框", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {

            URL url = null;
            XMLHandler handler = new XMLHandler();
            String scheddStr = handler.getURL("schedd");
            try {
                url = new URL(scheddStr);
                // url = new URL("http://localhost:9628");
            } catch (MalformedURLException e3) {
                // TODO Auto-generated catch block
                e3.printStackTrace();
            }
            Schedd schedd = null;

            try {
                schedd = new Schedd(url);
            } catch (ServiceException ex) {
                Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            //ClassAdStructAttr[]是元素类型
            ClassAd ad = null;//birdbath.ClassAd;
            ClassAdStructAttr[][] classAdArray = null;

            int cluster = 0;

            int job = 0;
            Transaction xact = schedd.createTransaction();
            try {
                xact.begin(30);

            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
            String taskStatus = "";
            for (Object o : selectedNodeList) {
                if (o instanceof ObservableDisplayedClassAd) {
                    delNo = Integer.parseInt(((ObservableDisplayedClassAd) o).getClusterId());
                    taskStatus = ((ObservableDisplayedClassAd) o).getJobStatus();
                    if (taskStatus.equals("挂起")) {
                        JOptionPane.showMessageDialog(null, "删除该任务之前应先终止该任务");
                        return;
                    }
                    String findreq = "owner==\""+condoruser+"\"&&ClusterId==" + delNo;
                    try {
                        classAdArray = schedd.getJobAds(findreq);
                    } catch (RemoteException ex) {
                        Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (ClassAdStructAttr[] x : classAdArray) {
                        ad = new ClassAd(x);
                        job = Integer.parseInt(ad.get("ProcId"));
                        try {
                            xact.closeSpool(delNo, job);

                            // System.out.print("ts.getClusterId():" + showClusterId + "\n");
                        } catch (RemoteException ex) {
                            Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        xact.removeCluster(delNo, "");
                    } catch (RemoteException ex) {
                        Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            //end

            try {
                xact.commit();
            } catch (RemoteException e) {

                e.printStackTrace();
            }
            //删除任务名
//            XMLHandler handler = new XMLHandler();
            int delNo1[] = new int[1];//
            delNo1[0] = delNo;
            handler.removeJobs(delNo1, 1);

        } else if (n == JOptionPane.NO_OPTION) {
            System.out.println("qu xiao");

        }

    }

    @FXML
    void reScheduleFired(ActionEvent event) {
        //删除原有cluster，再次创建一个
        int oldId = 0;//此处只能是一次重新创建一个任务
        int delNo = 0;
        int reScheduleId = 0;

        URL url = null;

        // String scheddURLStr="http://localhost:9628";
        XMLHandler handler = new XMLHandler();
        String scheddStr = handler.getURL("schedd");
        try {
            url = new URL(scheddStr);
        } catch (MalformedURLException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        Schedd schedd = null;

        int n = JOptionPane.showConfirmDialog(null, "确认重新提交该任务吗?", "确认删除框", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {

            //得到checkbox对应的clusterId
            System.out.print(Thread.currentThread().getName() + "\n");

            try {
                schedd = new Schedd(url);
            } catch (ServiceException ex) {
                Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            //ClassAdStructAttr[]是元素类型

            ClassAd ad = null;//birdbath.ClassAd;
            ClassAdStructAttr[][] classAdArray = null;
            ClassAdStructAttr[][] classAdArray2 = null;
            String taskStatus = "";

            final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
            for (Object o : selectedNodeList) {
                if (o instanceof ObservableDisplayedClassAd) {
                    oldId = Integer.parseInt(((ObservableDisplayedClassAd) o).getClusterId());
                    taskStatus = ((ObservableDisplayedClassAd) o).getJobStatus();
                    if (taskStatus.equals("等待") || taskStatus.equals("运行") || taskStatus.equals("挂起")) {
                        JOptionPane.showMessageDialog(null, "当前任务状态不支持重新运行");
                        return;
                    }

                }
            }
            //e
            int job = 0;
            Transaction xact = schedd.createTransaction();
            try {
                xact.begin(30);

            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println("oldId:" + oldId);

            String findreq = "owner==\""+condoruser+"\"&&ClusterId==" + oldId;
            try {
                classAdArray = schedd.getJobAds(findreq);
                classAdArray2 = schedd.getJobAds(findreq);
            } catch (RemoteException ex) {
                Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //新建一个任务，并将原来任务中的jobclassAd中的部分属性取出用于新任务的创建
            int newClusterId = 0;
            try {
                newClusterId = xact.createCluster();

            } catch (RemoteException e) {
            }
            int newJobId = 0;
//            XMLHandler handler = new XMLHandler();
            String oldname = handler.getJobName("" + oldId);
            String transferInput = "";
            String jobdir = null;
            //从原来job中获取信息用于创建新的job
            int jobcount = 0;
            String timestamp = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
            System.out.println("oldname" + oldname);
            jobdir = handler.getTaskDir("" + oldId);
            String expfilename = handler.getExpFile("" + oldId);
            String infofilename = handler.getInfoFile("" + oldId);
            for (ClassAdStructAttr[] x : classAdArray) {

                ad = new ClassAd(x);
                job = Integer.parseInt(ad.get("ProcId"));
                ClassAdStructAttr[] attributes = null;// {new ClassAdStructAttr()};

                System.out.println("jobdir:===" + jobdir);

                //截取每个输入文件
               // 
                File[] files = {new File(expfilename), new File(infofilename)};// new File[2];

                System.out.println(expfilename + "===" + infofilename);

                //新建结果回收目录
                String oldiwd = jobdir.substring(0, jobdir.length() - 14);//D:\tmp\test\dirtest\20140902200811;
                String resultdirstr = oldiwd + timestamp + "\\" + job;//新的job结果目录
                // new String(handler.getTaskDir(oldname).getBytes(),"UTF-8");
                //获得定义的属性
                //attributes[0] = new ClassAdStructAttr("Iwd", ClassAdAttrType.value3, resultdirstr);
                File newjobdir = new File(resultdirstr);
                try {
                    System.out.println("resultdirstr:" + resultdirstr);
                    FileUtils.forceMkdir(newjobdir);
                } catch (IOException ex) {
                    Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
                }
                //复制输入文件到结果目录
               /* for (int j = 0; j < inputfilecount; j++) {
                 System.out.println("j:" + j);
                 try {
                 FileUtils.copyFileToDirectory(files[j], newjobdir);
                 } catch (IOException ex) {
                 Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 }*/

                try {
                    newJobId = xact.createJob(newClusterId);
                } catch (RemoteException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {

                    xact.submit(newClusterId, newJobId,
                            condoruser, UniverseType.VANILLA, ad.get("Cmd"), ad.get("Arguments"), ad.get("Requirements"), attributes, files);
                    xact.closeSpool(oldId, job);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            //s
            try {
                xact.removeCluster(oldId, "");
            } catch (RemoteException ex) {
                Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                xact.commit();
            } catch (RemoteException e) {

                e.printStackTrace();
            }
            try {
                schedd.requestReschedule();
            } catch (RemoteException ex) {
                Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("comit:" + oldname);

            String olddir = handler.getTaskDir("" + oldId);

            System.out.println("olddir:" + olddir);
            // olddir.substring(0, olddir.length() - 14);//截取后14位时间戳
            String newdir = olddir.substring(0, olddir.length() - 14) + timestamp;
            System.out.println("newdir:" + newdir);
            //  System.out.println(olddir+"\nmm"+newdir);
            handler.removeJob("" + oldId);
            handler.addJob(oldname, "" + newClusterId, newdir, expfilename, infofilename);

            // reConfigureButtons();
        } else if (n == JOptionPane.NO_OPTION) {
            System.out.println("qu xiao");

        }

        System.out.print("recreateTransaction succeed\n");

    }

    private void configureTable() {
        //selected.setCellValueFactory(new PropertyValueFactory<>("selected"))
        //s

        table.getColumns().addAll(new TableColumn("任务名"), new TableColumn("任务ID"), new TableColumn("信息流文件"), new TableColumn("实验框架文件"), new TableColumn("样本总数"), new TableColumn("创建时间"), new TableColumn("运行时间"), new TableColumn("执行进度"), new TableColumn("状态"));

        //private 
        ObservableList<DisplayedClassAdStub> data
                = FXCollections
                .observableArrayList();

        ObservableList<TableColumn> observableList = table.getColumns();
        observableList.get(0).setCellValueFactory(new PropertyValueFactory(ColClusterNameMapKey));
        observableList.get(1).setCellValueFactory(new PropertyValueFactory(ColClusterIdMapKey));
        observableList.get(2).setCellValueFactory(new PropertyValueFactory(ColInfoFileNameMapKey));
        observableList.get(3).setCellValueFactory(new PropertyValueFactory(ColExpFileNameMapKey));
        observableList.get(4).setCellValueFactory(new PropertyValueFactory(ColTotalSampleNumMapKey));
        observableList.get(5).setCellValueFactory(new PropertyValueFactory(ColSubmittedTimeMapKey));
        observableList.get(6).setCellValueFactory(new PropertyValueFactory(ColRunTimeMapKey));
        observableList.get(7).setCellValueFactory(new PropertyValueFactory(ColProcessStatusMapKey));
        observableList.get(7).setCellFactory(ProgressBarTableCell.forTableColumn());
        observableList.get(8).setCellValueFactory(new PropertyValueFactory(ColJobStatusMapKey));

        table.setItems(tableContent);
        tablePane.getChildren().add(table);
        // table.setSelectionModel(null);
//sampleTab
        sampleTab.getColumns().addAll(new TableColumn("样本组号"), new TableColumn("样本数"), new TableColumn("提交时间"), new TableColumn("运行时间"), new TableColumn("执行进度"), new TableColumn("状态"), new TableColumn("执行节点"), new TableColumn("执行位置"));
        ((TableColumn) sampleTab.getColumns().get(6)).setVisible(false);
        ((TableColumn) sampleTab.getColumns().get(2)).setPrefWidth(150);
        ((TableColumn) sampleTab.getColumns().get(5)).setPrefWidth(50);
        ((TableColumn) sampleTab.getColumns().get(7)).setPrefWidth(150);
        //private 
        ObservableList<DisplayedClassAdStub> sampleData
                = FXCollections
                .observableArrayList();

        ObservableList<TableColumn> sampleList = sampleTab.getColumns();
        sampleList.get(0).setCellValueFactory(new PropertyValueFactory(ColSampleIdMapKey));
        sampleList.get(1).setCellValueFactory(new PropertyValueFactory(ColEachRunMapKey));
        sampleList.get(2).setCellValueFactory(new PropertyValueFactory(ColSampleSubmittedTimeMapKey));
        sampleList.get(3).setCellValueFactory(new PropertyValueFactory(ColSampleRunTimeMapKey));
        sampleList.get(4).setCellValueFactory(new PropertyValueFactory(ColSampleProcessStatusMapKey));
        sampleList.get(4).setCellFactory(ProgressBarTableCell.forTableColumn());
        sampleList.get(5).setCellValueFactory(new PropertyValueFactory(ColSampleJobStatusMapKey));
        sampleList.get(6).setCellValueFactory(new PropertyValueFactory(ColCpuIdMapKey));
        sampleList.get(7).setCellValueFactory(new PropertyValueFactory(ColSlotIdMapKey));

        sampleTab.setItems(sampleContent);
        sampleBox.getChildren().add(sampleTab);

        //end sampleTab
        // ColConnectInfoMapKey  ColIpMapKey ColMachineIdMapKey ColMemMapKey ColDiskMapKey ColSlotNumMapKey
        //ColJobNameMapKey  ColJobCpuMapKey ColJobMemMapKey
        colConnectInfo.setCellValueFactory(new PropertyValueFactory<>(ColConnectInfoMapKey));
        colIp.setCellValueFactory(new PropertyValueFactory<>(ColIpMapKey));
        colMachineId.setCellValueFactory(new PropertyValueFactory<>(ColMachineIdMapKey));
        colMem.setCellValueFactory(new PropertyValueFactory<>(ColMemMapKey));
        colSlotNum.setCellValueFactory(new PropertyValueFactory<>(ColSlotNumMapKey));
        colDisk.setCellValueFactory(new PropertyValueFactory<>(ColDiskMapKey));
        colCpu.setCellValueFactory(new PropertyValueFactory<>(ColCpuMapKey));
        resourcesTab.setItems(resourcesContent);
        //resourcesTab.setSelectionModel(null);

        colJobName.setCellValueFactory(new PropertyValueFactory<>(ColJobNameMapKey));
        colJobCpu.setCellValueFactory(new PropertyValueFactory<>(ColJobCpuMapKey));
        colJobMem.setCellValueFactory(new PropertyValueFactory<>(ColJobMemMapKey));
        job_resourcesTab.setItems(jobContent);
        job_resourcesTab.setSelectionModel(null);

    }

    @FXML
    public void connectButtonFired(ActionEvent event) {

        System.out.print("hah:" + "\n");

        if (!isConnected) {
            isConnected = true;
            application.gotoConnect();
            //  connect_button.getStyleClass().add("connected");
            create_button.setDisable(false);
            connect_button.setDisable(true);

        }//启动线程实时更新table中的任务信息
        else {
            isConnected = false;
            System.exit(0);

        }
        //设置box显示情况
        //configBox();
        // 

    }

    @FXML
    public void releaseJobFired(ActionEvent event) {

        int releaseNo = 0;
        int releaseClusterId = 0;
        int n = JOptionPane.showConfirmDialog(null, "确认继续执行任务吗?", "确认删除框", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {

            //得到checkbox对应的clusterId
            System.out.print(Thread.currentThread().getName() + "\n");

            URL url = null;
            XMLHandler handler = new XMLHandler();
            String scheddStr = handler.getURL("schedd");
            try {
                url = new URL(scheddStr);
                //url = new URL("http://localhost:9628");
            } catch (MalformedURLException e3) {
                // TODO Auto-generated catch block
                e3.printStackTrace();
            }
            Schedd schedd = null;

            try {
                schedd = new Schedd(url);
            } catch (ServiceException ex) {
                Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            //ClassAdStructAttr[]是元素类型
            ClassAd ad = null;//birdbath.ClassAd;
            ClassAdStructAttr[][] classAdArray = null;

            int tmp1 = 0;

            final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
            for (Object o : selectedNodeList) {
                if (o instanceof ObservableDisplayedClassAd) {
                    releaseClusterId = Integer.parseInt(((ObservableDisplayedClassAd) o).getClusterId());
                }
            }
            //e
            int job = 0;
            Transaction xact = schedd.createTransaction();
            try {
                xact.begin(30);

            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.out.println("releaseClusterId:" + releaseClusterId);

           // String findreq = "owner==\"htcondor\"&&ClusterId==" + releaseClusterId;
             String findreq = "owner==\""+condoruser+"\"&&ClusterId==" + releaseClusterId;
            try {
                classAdArray = schedd.getJobAds(findreq);
            } catch (RemoteException ex) {
                Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            String showJobStatus = null;
            for (ClassAdStructAttr[] x : classAdArray) {
                ad = new ClassAd(x);
                job = Integer.parseInt(ad.get("ProcId"));

                status = Integer.valueOf(ad.get("JobStatus"));
                showJobStatus = statusName[status];
                try {
                    if (showJobStatus.equals("挂起")) {
                        xact.releaseJob(releaseClusterId, job, "");
                    } else {//其他状态不支持继续操作
                        if (showJobStatus.equals("完成") || showJobStatus.equals("运行") || showJobStatus.equals("等待") || showJobStatus.equals("异常") || showJobStatus.equals("移除")) {
                            JOptionPane.showMessageDialog(null, "当前任务状态不支持继续操作");
                            return;
                        }

                    }
                    System.out.print("ts.releaseClusterId():" + releaseClusterId + "job" + job + "\n");
                } catch (RemoteException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                xact.commit();
            } catch (RemoteException e) {

                e.printStackTrace();
            }
            //继续执行的任务名

        } else if (n == JOptionPane.NO_OPTION) {
            System.out.println("qu xiao");

        }
    }

    @FXML
    public void removedWithoutClosingPoolFired(ActionEvent event) {//与删除任务方法区别在于不close pool
        int delNo = 0;
        int delsum = 0;
        int n = JOptionPane.showConfirmDialog(null, "确认终止任务吗?", "确认终止框", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {

            //得到checkbox对应的clusterId
            System.out.print(Thread.currentThread().getName() + "\n");

            URL url = null;
            XMLHandler handler = new XMLHandler();
            String scheddStr = handler.getURL("schedd");
            try {

                url = new URL(scheddStr);
                //url = new URL("http://localhost:9628");
            } catch (MalformedURLException e3) {
                // TODO Auto-generated catch block
                e3.printStackTrace();
            }
            Schedd schedd = null;

            try {
                schedd = new Schedd(url);
            } catch (ServiceException ex) {
                Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            //ClassAdStructAttr[]是元素类型
            ClassAd ad = null;//birdbath.ClassAd;
            ClassAdStructAttr[][] classAdArray = null;

            int cluster = 0;

            int job = 0;
            Transaction xact = schedd.createTransaction();
            try {
                xact.begin(30);

            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
          //  System.out.println("delClusterIds:" + delClusterIds.toString());

            //s
            int removeId = 0;
            final List<?> selectedNodeList = new ArrayList<>(table.getSelectionModel().getSelectedItems());
            for (Object o : selectedNodeList) {
                if (o instanceof ObservableDisplayedClassAd) {
                    removeId = Integer.parseInt(((ObservableDisplayedClassAd) o).getClusterId());
                }
            }
            //e

            String findreq = "owner==\""+condoruser+"\"&&ClusterId==" + removeId;
            try {
                classAdArray = schedd.getJobAds(findreq);
            } catch (RemoteException ex) {
                Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (ClassAdStructAttr[] x : classAdArray) {
                ad = new ClassAd(x);
                job = Integer.parseInt(ad.get("ProcId"));
                try {
                    xact.removeJob(removeId, job, "");

                    // System.out.print("ts.getClusterId():" + showClusterId + "\n");
                } catch (RemoteException ex) {
                    Logger.getLogger(MainFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try {
                xact.commit();
            } catch (RemoteException e) {

                e.printStackTrace();
            }

        } else if (n == JOptionPane.NO_OPTION) {
            System.out.println("qu xiao");

        }

    }

    @FXML
    public void createJob(ActionEvent event) {

        if (application == null) {
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            errorMessage = null;
            errorMessage = "应用程序初始化失败";
            System.out.print(errorMessage);
        } else {
            application.gotoCreate();

        }
    }

    private void reConfigureButtons() {

        create_button.setDisable(false);
        modify_button.setDisable(true);
        remove_button.setDisable(true);
        run_button.setDisable(true);
        stop_button.setDisable(true);
        pause_button.setDisable(true);
        goon_button.setDisable(true);
        // download_button.setDisable(true);
        check_button.setDisable(true);

    }

    private void initConfigureButtons() {

        if (modify_button != null) {
            modify_button.setDisable(true);
        }
        if (remove_button != null) {
            remove_button.setDisable(true);
        }
        if (run_button != null) {
            run_button.setDisable(true);
        }
        if (stop_button != null) {
            stop_button.setDisable(true);
        }
        if (goon_button != null) {
            goon_button.setDisable(true);
        }
        if (pause_button != null) {
            pause_button.setDisable(true);
        }
        /* if (download_button != null) {
         download_button.setDisable(true);
         }*/
        if (check_button != null) {
            check_button.setDisable(true);
        }

    }

}
