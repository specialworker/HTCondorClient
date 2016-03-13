package condorclient.utilities;

import birdbath.ClassAd;
import birdbath.Collector;
import birdbath.Schedd;
import condor.ClassAdStructAttr;
import condorclient.model.JobResourceInfoItemStub;
import condorclient.model.ObservableJobResourceInfoItem;
import condorclient.model.ObservableResourceInfoItem;
import condorclient.model.ResourceInfoItemStub;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.xml.rpc.ServiceException;

public class UpdateChartWorkerThread extends Thread {

    final NumberAxis cpu_xAxis = new NumberAxis();
    final NumberAxis cpu_yAxis = new NumberAxis();
    final NumberAxis memo_xAxis = new NumberAxis();
    final NumberAxis memo_yAxis = new NumberAxis();
    final NumberAxis disc_xAxis = new NumberAxis();
    final NumberAxis disc_yAxis = new NumberAxis();
    final LineChart<Number, Number> cpuChart
            = new LineChart<Number, Number>(cpu_xAxis, cpu_yAxis);
    final LineChart<Number, Number> memoChart
            = new LineChart<Number, Number>(memo_xAxis, memo_yAxis);
    final LineChart<Number, Number> discChart
            = new LineChart<Number, Number>(disc_xAxis, disc_yAxis);
    LineChart.Series<Number, Number> cpuSeries = new LineChart.Series<Number, Number>();
    LineChart.Series<Number, Number> memoSeries = new LineChart.Series<Number, Number>();
    LineChart.Series<Number, Number> discSeries = new LineChart.Series<Number, Number>();

    private int cpuIndex = 10;
    private int memoIndex = 10;
    private int discIndex = 10;
    private GridPane pane;
    private ScrollPane sp = new ScrollPane();
    ProgressBar pb1 = new ProgressBar(0);
    // ProgressBar pb2 = new ProgressBar(0);
    // ProgressBar pb3 = new ProgressBar(0);
    //ProgressBar pb4 = new ProgressBar(0);
    VBox vb = new VBox();
    // private ObservableList<ClassAd> slotsList = FXCollections.observableArrayList();
    private Map<String, ObservableList<ClassAd>> node2slotMap = new TreeMap<>();
    URL collector_url = null;

    // System.out.print(Thread.currentThread().getName() + "\n");
    Collector collector = null;
    ClassAd ad = null;

    URL scheddURL = null;
    Schedd schedd = null;
    int i = 0;
    int toShowOrNot = 0;
    String nodeToShow = "";
    String machineToShow = "";
    Map<String, ObservableList<String>> map = new TreeMap<>();
    ClassAdStructAttr[][] classAdArray = null;
    private ObservableList<ResourceInfoItemStub> resourcesClassAds = FXCollections.observableArrayList();
    private ObservableList<JobResourceInfoItemStub> jobResourcesClassAds = FXCollections.observableArrayList();
    public TableView<ObservableJobResourceInfoItem> job_resourcesTab;
    private TableView<ObservableResourceInfoItem> resourcesTab;
    private final ListChangeListener<ObservableResourceInfoItem> resourcesTabSelectionChanged
            = new ListChangeListener<ObservableResourceInfoItem>() {

                @Override
                public void onChanged(ListChangeListener.Change<? extends ObservableResourceInfoItem> c) {

                    // System.out.println("tableSelectionChanged\n");
                    updateWhichIPtoShow();

                }
            };

    {
        try {
            //静态程序块？可以用于全局声明URL
            XMLHandler handler = new XMLHandler();
            String scheddStr = handler.getURL("schedd");

            scheddURL = new URL(scheddStr);
        } catch (MalformedURLException ex) {
            Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public UpdateChartWorkerThread(GridPane pane, TableView<ObservableResourceInfoItem> resourcesTab, TableView<ObservableJobResourceInfoItem> job_resourcesTab) {
        setDaemon(true);
        setName("Thread haha");
        this.pane = pane;
        this.resourcesTab = resourcesTab;
        this.job_resourcesTab = job_resourcesTab;
        this.resourcesTab.setId("resourcesTable");
        this.job_resourcesTab.setId("job_resourcesTable");
        //cpu_xAxis.setLabel("Number of Month");
        // System.out.println("====0");
        cpu_xAxis.setLowerBound(0);
        cpu_xAxis.setUpperBound(7);
        cpu_yAxis.setLowerBound(0);
        cpu_yAxis.setUpperBound(1);
        cpu_yAxis.setAutoRanging(false);
        cpu_yAxis.setTickLabelsVisible(false);
        cpu_xAxis.setForceZeroInRange(false);
        cpu_xAxis.setAutoRanging(false);
        // cpu_xAxis.setBorder(Border.EMPTY);
        cpu_xAxis.setVisible(false);
        //cpu_xAxis.setTickLabelsVisible(false);
        cpu_xAxis.setTickLabelsVisible(false);
        cpuChart.setTitle("CPU总体使用率");
        cpuChart.applyCss();
        cpuChart.animatedProperty();
        cpuChart.setAnimated(true);
        cpuChart.setVerticalGridLinesVisible(false);
        //cpuChart.setMaxSize(300, 200);

        cpuChart.setCreateSymbols(false);
        //populating the cpuSeries with data
        for (int i = 0; i < 8; i++) {
            cpuSeries.getData().add(new LineChart.Data<Number, Number>(i, 0));
        }
        cpuChart.getData().add(cpuSeries);
        //pane.getChildren().add(cpuChart);
        //  System.out.println("====" + cpuSeries.toString() + cpuSeries.getData().toString());

        //memo
        memo_xAxis.setLowerBound(0);
        memo_xAxis.setUpperBound(7);
        memo_yAxis.setLowerBound(0);
        memo_yAxis.setUpperBound(1);
        memo_yAxis.setAutoRanging(false);
        memo_xAxis.setForceZeroInRange(false);
        memo_xAxis.setAutoRanging(false);
        // memo_xAxis.setBorder(Border.EMPTY);
        memo_xAxis.setVisible(false);
        memo_xAxis.setTickLabelsVisible(false);
        memo_yAxis.setTickLabelsVisible(false);
        memoChart.setTitle("内存使用率");
        memoChart.applyCss();
        memoChart.animatedProperty();
        memoChart.setAnimated(true);
        memoChart.setVerticalGridLinesVisible(false);
        //memoChart.setMaxSize(300, 200);

        memo_yAxis.setAnimated(true);
        //  memo_yAxis.setLabel(" %");
        memoChart.setCreateSymbols(false);
        //populating the memoSeries with data
        for (int i = 0; i < 8; i++) {
            memoSeries.getData().add(new LineChart.Data<Number, Number>(i, 0));
        }
        memoChart.getData().add(memoSeries);
        //pane.getChildren().add(memoChart);
        // System.out.println("====" + memoSeries.toString() + memoSeries.getData().toString());
        //disc

        disc_xAxis.setLowerBound(0);
        disc_xAxis.setUpperBound(7);
        disc_yAxis.setLowerBound(0);
        disc_yAxis.setUpperBound(1);
        disc_yAxis.setAutoRanging(false);
        disc_xAxis.setForceZeroInRange(false);
        disc_xAxis.setAutoRanging(false);
        // disc_xAxis.setBorder(Border.EMPTY);
        disc_xAxis.setVisible(false);
        disc_xAxis.setTickLabelsVisible(false);
        disc_yAxis.setTickLabelsVisible(false);
        discChart.setTitle("磁盘使用率");
        discChart.applyCss();
        discChart.animatedProperty();
        discChart.setAnimated(true);
        discChart.setVerticalGridLinesVisible(false);
        //discChart.setMaxSize(300, 200);

        disc_yAxis.setAnimated(true);
        // disc_yAxis.setLabel(" %");
        discChart.setCreateSymbols(false);
        //populating the discSeries with data

        try {
            XMLHandler handler = new XMLHandler();
            String collectorStr = handler.getURL("collector");
            collector_url = new URL(collectorStr);
        } catch (MalformedURLException e3) {

            e3.printStackTrace();
        }

        for (int i = 0; i < 8; i++) {
            discSeries.getData().add(new LineChart.Data<Number, Number>(i, 0));
        }
        discChart.getData().add(discSeries);
        //pane.getChildren().add(discChart);
        //  System.out.println("====" + discSeries.toString() + discSeries.getData().toString());
        //System.out.println("====1");
        vb.setSpacing(5);
        vb.setAlignment(Pos.CENTER);
        sp.setContent(vb);
        sp.setId("pbscroll");
        sp.setPrefHeight(100);
       // sp.setContent(new Button("hello"));
        //sp.setContent(new Label("good"));

        //System.out.println("====" );
        // sp.s
        // (AnchorPane)(sp.getContent());
        cpuChart.setId("cpuchart");
        memoChart.setId("memochart");
        discChart.setId("discchart");
        vb.setId("vbpane");
        vb.setFillWidth(true);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        // vb.setMinHeight(sp.getHeight());
        pane.add(cpuChart, 0, 0);
        pane.add(sp, 1, 0);
        pane.add(memoChart, 0, 1);
        pane.add(discChart, 1, 1);

        final ObservableList<ObservableResourceInfoItem> tableSelection = resourcesTab.getSelectionModel().getSelectedItems();

        tableSelection.addListener(resourcesTabSelectionChanged);

    }

    public void updateWhichIPtoShow() {

        final boolean nothingSelected = resourcesTab.getSelectionModel().getSelectedItems().isEmpty();
        if (nothingSelected) {
            //toShowOrNot = 0;
        } else {
            toShowOrNot = 1;

            final List<?> selectedNodeList = new ArrayList<>(resourcesTab.getSelectionModel().getSelectedItems());
            for (Object o : selectedNodeList) {
                if (o instanceof ObservableResourceInfoItem) {
                    nodeToShow = ((ObservableResourceInfoItem) o).getIp();
                    machineToShow = ((ObservableResourceInfoItem) o).getMachineId();
                    System.out.println("machineToShow" + machineToShow);
                }
            }
            ClassAdStructAttr[][] startdAdsArray = null;

            try {
                collector = new Collector(collector_url);
            } catch (ServiceException ex) {
                Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {

                startdAdsArray = collector.queryStartdAds("");//owner==\"lianxiang\"
            } catch (RemoteException ex) {
                Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            String myAddress = null;
            String slotName = null;
            node2slotMap.clear();
            //map.clear();

            String ip = "";
            for (ClassAdStructAttr[] x : startdAdsArray) {//填好node2slotMap
                ad = new ClassAd(x);
                myAddress = ad.get("MyAddress");//MyAddress=<10.4.12.217:8951>;
                ip = myAddress.substring(myAddress.indexOf("<") + 1, myAddress.indexOf(":"));
                ObservableList<ClassAd> slotlist;
                slotlist = FXCollections.<ClassAd>observableArrayList();
                if (ip.equalsIgnoreCase(nodeToShow)) {//只把指定节点对应的slot信息存入
                    node2slotMap.putIfAbsent(ip, slotlist);
                    node2slotMap.get(ip).add(ad);
                }

            }   //System.out.print("1111-----\n");
            vb.getChildren().clear();
            //  System.out.println("size:" + node2slotMap.get(nodeToShow).size() + "nodeToShow:" + nodeToShow);
            for (int i = 0; i < node2slotMap.get(nodeToShow).size(); i++) {
                HBox hb = new HBox();
                Label label = new Label();
                label.setPrefWidth(80);
                // label.setText("核号:" + i);
                ProgressBar bar = new ProgressBar();
                bar.setPrefSize(100, 10);
                hb.getChildren().addAll(label, bar, new Label());
                vb.getChildren().add(hb);

            }
            // System.out.println(vb.toString());
            //System.out.print("ffff-----\n");
            //sp.setContent(vb);
            // System.out.print("vb-----\n");
            // table.getSelectionModel().clearSelection();
        }

    }

    @Override
    public void run() {

        while (!this.isInterrupted()) {

            // UI updaten
            Platform.runLater(new Runnable() {

                @Override
                public void run() {//每个3秒执行的过程

                    System.out.println("chat--i:" + i);
                    ClassAdStructAttr[][] startdAdsArray = null;
                    ///task
                    //更新第一个TAB
                    i++;

                    Collector c = null;

                    try {
                        c = new Collector(collector_url);
                    } catch (ServiceException ex) {
                        Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        //System.out.print(Thread.currentThread().getName() + "hhh\n");

                        startdAdsArray = c.queryStartdAds("");//owner==\"lianxiang\"
                    } catch (RemoteException ex) {
                        Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String ip = null;
                    String slotName = null;
                    resourcesClassAds.clear();
                    map.clear();
                    if (startdAdsArray != null) {
                        for (ClassAdStructAttr[] x : startdAdsArray) {

                            ad = new ClassAd(x);
                            ip = ad.get("MyAddress");//MyAddress=<10.4.12.217:8951>;
                            String ipStr = ip.substring(ip.indexOf("<") + 1, ip.indexOf(":"));
                            // System.out.println("===" + ad.toString());
                            ObservableList<String> slotlist;

                            slotlist = FXCollections.<String>observableArrayList();

                            if (!map.containsKey(ipStr)) {

                                map.put(ipStr, slotlist);
                                ResourceInfoItemStub resourceInfoTableClassAd = new ResourceInfoItemStub(null);
                                resourceInfoTableClassAd.setMachineId(ad.get("Machine"));
                                // System.out.println(ipStr);
                                resourceInfoTableClassAd.setIp(ipStr);
                                resourceInfoTableClassAd.setCpu(ad.get("MonitorSelfCPUUsage"));//cpu利用率

                                // long percent = Long.getLong(ad.get("MonitorSelfResidentSetSize"))*Long.getLong(ad.get("TotalCpus"))/Long.getLong(ad.get("TotalMemory"));
                                // System.out.print("hahappkkklll"+percent);
                                resourceInfoTableClassAd.setMem(ad.get("MonitorSelfResidentSetSize"));
                                // long disk=Long.getLong(ad.get("Disk"));//condor可用的磁盘空间，应该再得到在其上工作的所有任务的diskusage,相除
                                // System.out.print("hahapphhhlll"+disk);
                                resourceInfoTableClassAd.setDisk(ad.get("Disk"));//要改
                                resourceInfoTableClassAd.setSlotNum(ad.get("TotalCpus"));

                                resourceInfoTableClassAd.setConnectInfo("connected");//查的的机器都是连接上的
                                resourcesClassAds.add(resourceInfoTableClassAd); //jobResourcesClassAds

                            }

                            map.get(ipStr).add(slotName);
                            slotName = ad.get("Name");//未用上

                        }

                        resourcesTab.getItems().clear();
                        resourcesTab.getItems().addAll(resourcesClassAds);
//更新第三个TAB
                        XMLHandler handler = new XMLHandler();

                        Map<String, String> nimap = handler.iteratorNI();
                        jobResourcesClassAds.clear();
                        try {
                            schedd = new Schedd(scheddURL);//用于查询job ClassAddress
                        } catch (ServiceException ex) {
                            Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        for (String s : nimap.keySet()) {
                            String req = "ClusterId==" + s;
                            // System.out.println(req);
                            try {
                                classAdArray = schedd.getJobAds(req);
                            } catch (RemoteException ex) {
                                Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            long longsize = 0;
                            String hosts = "";
                            String name = nimap.get(s);

                            JobResourceInfoItemStub jobResourceInfoTableClassAd = new JobResourceInfoItemStub(null);
                            for (ClassAdStructAttr[] x : classAdArray) {//clusterId一样的jobs
                                ad = new ClassAd(x);
                                // 
                                if (ad.get("RemoteHost") != null) {
                                    hosts = hosts + ad.get("RemoteHost");
                                } else if (ad.get("LastRemoteHost") != null) {
                                    hosts = hosts + ad.get("LastRemoteHost");
                                }
                                String size = ad.get("ImageSize");
                                // Long Long.parseLong(ad.get("RemoteUserCpu"))+Long.parseLong(ad.get("RemoteSysCpu"));cpu时间
                                longsize = longsize + Long.parseLong(size);
                                //System.out.print(ad.toString());
                            }
                            jobResourceInfoTableClassAd.setJobCpu(hosts);
                            jobResourceInfoTableClassAd.setJobMem("" + longsize);
                            jobResourceInfoTableClassAd.setJobName(name);

                            jobResourcesClassAds.add(jobResourceInfoTableClassAd);

                        }
                        job_resourcesTab.getItems().clear();
                        job_resourcesTab.getItems().addAll(jobResourcesClassAds);

                        //  System.out.println("\nnameStu:" + Thread.currentThread().getName());
                        ///end
                        //更新第2个TAB toShowOrNot = 0;
                        if (toShowOrNot == 1) {

                            Collector collector = null;

                            try {
                                collector = new Collector(collector_url);
                            } catch (ServiceException ex) {
                                Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            try {
                                //System.out.print(Thread.currentThread().getName() + "hhh\n");

                                startdAdsArray = collector.queryStartdAds("Machine==\"" + machineToShow + "\"");//owner==\"lianxiang\"Machine=arch.node1.com;
                            } catch (RemoteException ex) {
                                Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            String myAddress = null;

                            node2slotMap.clear();
                            //map.clear();
                            ClassAd ad = null;
                            /* for (ClassAdStructAttr[] x : startdAdsArray) {//填好node2slotMap
                             ad = new ClassAd(x);
                             System.out.println("size:" + startdAdsArray.length + ad.get("Machine"));
                             myAddress = ad.get("MyAddress");//MyAddress=<10.4.12.217:8951>;
                             // String ip2 = myAddress.substring(myAddress.indexOf("<") + 1, myAddress.indexOf(":"));
                             ObservableList<ClassAd> slotlist;
                             slotlist = FXCollections.<ClassAd>observableArrayList();
                             // if (ip2.equalsIgnoreCase(nodeToShow)) {
                             // node2slotMap.putIfAbsent(ip2, slotlist);
                             node2slotMap.putIfAbsent(ip2, slotlist);
                             // node2slotMap.get(ip2).add(ad);
                             node2slotMap.get(ip2).add(ad);
                             //}

                             }*/
                            //ssss
                            float nodeCpuUsageSum = 0;
                            float nodeCpuUsageAva = 0;
                            float nodeMemoUsageSum = 0;

                            float nodeMemoUsageAva = 0;
                            Long nodeDiscUsageSum;
                            float nodeDiscUsageAva = 0;
                            int i = 0;
                            Long l = null;
                            for (ClassAdStructAttr[] x : startdAdsArray) {
                                ad = new ClassAd(x);
                                System.out.println("size:" + startdAdsArray.length + ad.get("Machine"));
                                if (ad.get("MonitorSelfCPUUsage") != null) {
                                    nodeCpuUsageSum = nodeCpuUsageSum + Float.parseFloat(ad.get("MonitorSelfCPUUsage"));
                                    HBox vbtmp = (HBox) (vb.getChildren().get(i));
                                    ProgressBar pbtmp = (ProgressBar) vbtmp.getChildren().get(1);
                                    //  pbtmp.setTooltip(Tooltip.);
                                    Label label = (Label) vbtmp.getChildren().get(0);

                                    Float f = Float.parseFloat(ad.get("MonitorSelfCPUUsage")) * 100;
                                    int fi = f.intValue();
                                    if (fi <= 1) {
                                        fi = 1;
                                    }
                                    label.setText("核号:" + i + "  " + fi + "%");//

                                    //pbtmp.setUserData("20%");
                                    if (f <= 10) {
                                        pbtmp.setProgress(0.1);
                                    } else {
                                        pbtmp.setProgress(Float.parseFloat(ad.get("MonitorSelfCPUUsage")));
                                    }
                                    // System.out.println("MonitorSelfCPUUsage:" + a.get("MonitorSelfCPUUsage"));
                                }
                                if (ad.get("MonitorSelfResidentSetSize") != null && ad.get("TotalMemory") != null) {//TotalMemory :KB,MonitorSelfResidentSetSize:MB
                                    nodeMemoUsageSum = nodeMemoUsageSum + Float.parseFloat(ad.get("MonitorSelfResidentSetSize")) / 1024 / Float.parseFloat(ad.get("TotalMemory"));
                                    String ss = ad.get("MonitorSelfResidentSetSize");
                                    l = Long.parseLong(ad.get("TotalMemory")) / 1024;//转为GB
                                    //System.out.println("MonitorSelfResidentSetSize" + ss + "\nFloat.parseFloat(ad.get(\"TotalMemory\"))" + Float.parseFloat(ad.get("TotalMemory")) + "\nnodeMemoUsageSum:" + nodeMemoUsageSum + "\nl:" + l);

                                }
                                if (ad.get("TotalDisk") != null) {
                                    nodeDiscUsageSum = (Long.parseLong(ad.get("TotalDisk")) / 1024);//转换为MB
                                    disc_xAxis.setLabel("全部：" + nodeDiscUsageSum + "MB\n");
                                }

                                i++;

                            }
                            //ssss
                            //   System.out.println("node2slotMap:" + node2slotMap.toString());

                            // vb.getChildren().add(pb4);
                         /*   for (ClassAd a : node2slotMap.get(nodeToShow)) {
                             if (a.get("MonitorSelfCPUUsage") != null) {
                             nodeCpuUsageSum = nodeCpuUsageSum + Float.parseFloat(a.get("MonitorSelfCPUUsage"));
                             HBox vbtmp = (HBox) (vb.getChildren().get(i));
                             ProgressBar pbtmp = (ProgressBar) vbtmp.getChildren().get(1);
                             //  pbtmp.setTooltip(Tooltip.);
                             Label label = (Label) vbtmp.getChildren().get(0);

                             Float f = Float.parseFloat(a.get("MonitorSelfCPUUsage")) * 100;
                             int fi = f.intValue();
                             if (fi <= 1) {
                             fi = 1;
                             }
                             label.setText("核号:" + i + "  " + fi + "%");//

                             //pbtmp.setUserData("20%");
                             if (f <= 10) {
                             pbtmp.setProgress(0.1);
                             } else {
                             pbtmp.setProgress(Float.parseFloat(a.get("MonitorSelfCPUUsage")));
                             }
                             // System.out.println("MonitorSelfCPUUsage:" + a.get("MonitorSelfCPUUsage"));
                             }
                             if (a.get("MonitorSelfResidentSetSize") != null && a.get("TotalMemory") != null) {//TotalMemory :KB,MonitorSelfResidentSetSize:MB
                             nodeMemoUsageSum = nodeMemoUsageSum + Float.parseFloat(a.get("MonitorSelfResidentSetSize")) / 1024 / Float.parseFloat(a.get("TotalMemory"));
                             String ss = a.get("MonitorSelfResidentSetSize");
                             l = Long.parseLong(a.get("TotalMemory")) / 1024;//转为GB
                             //System.out.println("MonitorSelfResidentSetSize" + ss + "\nFloat.parseFloat(a.get(\"TotalMemory\"))" + Float.parseFloat(a.get("TotalMemory")) + "\nnodeMemoUsageSum:" + nodeMemoUsageSum + "\nl:" + l);

                             }
                             if (a.get("TotalDisk") != null) {
                             nodeDiscUsageSum = (Long.parseLong(a.get("TotalDisk")) / 1024);//转换为MB
                             disc_xAxis.setLabel("全部：" + nodeDiscUsageSum + "MB\n");
                             }

                             i++;

                             }*/
                            //查找job的classAd计算使用的disc
                            if (i > 0) {
                                nodeCpuUsageAva = nodeCpuUsageSum / i;
                            } else {
                                nodeCpuUsageAva = nodeCpuUsageSum;
                            }

                            nodeMemoUsageAva = nodeMemoUsageSum;// / 4;
                            nodeDiscUsageAva = (float) 1.0;// nodeDiscUsageSum;
                            Float cpupercent = nodeCpuUsageAva * 100;

                            Float memopercent = nodeMemoUsageAva * 100;
                            Float discpercent = nodeDiscUsageAva * 100;
                            if (cpupercent.intValue() <= 1) {
                                cpu_yAxis.setLabel("当前1" + "%");
                            }

                            cpu_yAxis.setLabel("当前" + cpupercent.intValue() + "%");

                            cpuSeries.getData()
                                    .add(new LineChart.Data<Number, Number>(cpuIndex, nodeCpuUsageAva));
                            NumberAxis cpu_xAxis = (NumberAxis) cpuChart.getXAxis();
                            // System.out.println(cpu_xAxis.getLowerBound() + "    " + cpu_xAxis.getUpperBound() + "index:" + cpuIndex);
                            if (cpuIndex < cpu_xAxis.getUpperBound()) {
                                cpuIndex += 1;
                            } else {
                                cpu_xAxis.setLowerBound(cpu_xAxis.getLowerBound() + 1);
                                cpu_xAxis.setUpperBound(cpu_xAxis.getUpperBound() + 1);
                                //lineChart.getXAxis().
                                cpuSeries.getData().remove(0);

                            }
                            //memo
                            memo_xAxis.setLabel("全部：" + l + "GB");
                            memo_yAxis.setLabel("任务占用：" + memopercent.intValue() + "%");
                            memoSeries.getData()
                                    .add(new LineChart.Data<Number, Number>(memoIndex, nodeMemoUsageAva));
                            NumberAxis memo_xAxis = (NumberAxis) memoChart.getXAxis();
                            //   System.out.println(memo_xAxis.getLowerBound() + "    " + memo_xAxis.getUpperBound() + "index:" + memoIndex);
                            if (memoIndex < memo_xAxis.getUpperBound()) {
                                memoIndex += 1;
                            } else {
                                memo_xAxis.setLowerBound(memo_xAxis.getLowerBound() + 1);
                                memo_xAxis.setUpperBound(memo_xAxis.getUpperBound() + 1);
                                //lineChart.getXAxis().
                                memoSeries.getData().remove(0);

                            }
                            //disc
                            discSeries.getData()
                                    .add(new LineChart.Data<Number, Number>(discIndex, 0.3));
                            NumberAxis disc_xAxis = (NumberAxis) discChart.getXAxis();
                            // System.out.println(disc_xAxis.getLowerBound() + "    " + disc_xAxis.getUpperBound() + "index:" + discIndex);
                            if (discIndex < disc_xAxis.getUpperBound()) {
                                discIndex += 1;
                            } else {
                                disc_xAxis.setLowerBound(disc_xAxis.getLowerBound() + 1);
                                disc_xAxis.setUpperBound(disc_xAxis.getUpperBound() + 1);
                                //lineChart.getXAxis().
                                discSeries.getData().remove(0);

                            }
                        }
                    }//end if
                }
            }
            );

            // Thread schlafen
            try {
                // fuer 3 Sekunden
                sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException ex) {
                Logger.getLogger(UpdateChartWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
