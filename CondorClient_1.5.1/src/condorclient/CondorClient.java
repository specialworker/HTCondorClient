/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient;

import birdbath.Schedd;
import condorclient.controller.ModifyDialogController;
import condorclient.model.DisplayedClassAd;
import condorclient.model.DisplayedClassAdStub;
import condorclient.model.JobResourceInfoItemStub;
import condorclient.model.ResourceInfoItemStub;
import condorclient.model.SlotClassAdStub;
import condorclient.utilities.TransferFileThread;
import condorclient.utilities.UpdateChartWorkerThread;
import condorclient.utilities.UpdateJobClassAdsWorkerThread;
import condorclient.utilities.XMLHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author lianxiang
 */
public class CondorClient extends Application {

    private Stage stage;
    private final double MINIMUM_WINDOW_WIDTH = 1000.0;
    private final double MINIMUM_WINDOW_HEIGHT = 600.0;
    private ObservableList<String> showDisplayedClassAds = FXCollections.observableArrayList();

    private ObservableList<DisplayedClassAdStub> displayedClassAds = FXCollections.observableArrayList();//用于填充相应的表格
    private ObservableList<ResourceInfoItemStub> resourcesClassAds = FXCollections.observableArrayList();
    private ObservableList<JobResourceInfoItemStub> jobResourcesClassAds = FXCollections.observableArrayList();
    private ObservableList<SlotClassAdStub> slotClassAds = FXCollections.observableArrayList();
    private int showSamplesofClusterId = 0;//表示要显示样本信息的cluster的Id

    final Map<String, DisplayedClassAd> displayedClassAdsMap = new TreeMap<>();
    private Map<String, ObservableList<DisplayedClassAdStub>> id2JobClassAdsMap = new TreeMap<>();//存放每个clusterId到任意的一个job的映射

    final ObservableMap<String, ObservableList<String>> showClassAdStatusMap;

    {//静态程序块？
        final Map<String, ObservableList<String>> map = new TreeMap<>();
        showClassAdStatusMap = FXCollections.observableMap(map);
        for (String s : newList("Idle", "Running", "Removed", "Completed", "Held")) {///CompletedJobs?
            showClassAdStatusMap.put(s, FXCollections.<String>observableArrayList());
        }
        // showClassAdStatusMap.addListener(showClassAdStatusMapChangeListener);
    }

    MainFXMLController mainpage;
    CreateJobDialogController create;
    ModifyDialogController modify;

    URL scheddURL = null;
    Schedd schedd = null;

    {//静态程序块？可以用于全局声明URL
        XMLHandler handler = new XMLHandler();
        String scheddStr = handler.getURL("schedd");
        try {
            scheddURL = new URL(scheddStr);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        public static void main(String[] args) {
    
        Application.launch(CondorClient.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            stage = primaryStage;
            stage.setMinHeight(600);
            stage.setMinWidth(1100);

            stage.setTitle("并行仿真任务监测");
            stage.setOnCloseRequest(new javafx.event.EventHandler<WindowEvent>() {//当关闭main窗体时，所有窗体都被关闭，即退出系统。

                @Override
                public void handle(WindowEvent event) {
                    System.out.println("mainevent" + event.getEventType());
                    System.exit(0);
                }
            }); /**/

            gotoMain();
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private <T> List<T> newList(T... items) {
        return Arrays.asList(items);
    }



    private void gotoMain() {
        try {

            mainpage = (MainFXMLController) replaceSceneContent("MainFXML.fxml");
            mainpage.setApp(this);
            mainpage.initialize2();
         
        } catch (Exception ex) {
            Logger.getLogger(CondorClient.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Node replaceSceneContent(String fxml) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        InputStream in = CondorClient.class
                .getResourceAsStream(fxml);//getResourceAsStream
        loader.setBuilderFactory(
                new JavaFXBuilderFactory());
        loader.setLocation(CondorClient.class
                .getResource(fxml));
        AnchorPane page;

        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        }

        // Store the stage width and height in case the user has resized the window
        double stageWidth = stage.getWidth();

        if (!Double.isNaN(stageWidth)) {
            stageWidth -= (stage.getWidth() - stage.getScene().getWidth());
        }

        double stageHeight = stage.getHeight();

        if (!Double.isNaN(stageHeight)) {
            stageHeight -= (stage.getHeight() - stage.getScene().getHeight());
        }

        Scene scene = new Scene(page);

        if (!Double.isNaN(stageWidth)) {
            page.setPrefWidth(stageWidth);
        }

        if (!Double.isNaN(stageHeight)) {
            page.setPrefHeight(stageHeight);
        }

        stage.setScene(scene);

        stage.sizeToScene();

        return (Node) loader.getController();
    }

    public void gotoModifyJob(String clusterId) {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = CondorClient.class
                .getResourceAsStream("ModifyDialog.fxml");//getResourceAsStream
        loader.setBuilderFactory(
                new JavaFXBuilderFactory());
        loader.setLocation(CondorClient.class
                .getResource("ModifyDialog.fxml"));
        GridPane page = null;

        try {
            page = (GridPane) loader.load(in);
        } catch (IOException ex) {
            Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Stage modifyStage = new Stage();
        //Initialize the Stage with type of modal
        modifyStage.initModality(Modality.APPLICATION_MODAL);
        //Set the owner of the Stage 
        modifyStage.initOwner(stage);
        modifyStage.setTitle("任务修改");
        Scene scene = new Scene(page, 491, 131, Color.LIGHTGREEN);
        modifyStage.setScene(scene);

        modify = loader.getController();
        modify.init(modifyStage);
        modify.clusterId = clusterId;
        modifyStage.show();
        modifyStage.setOnHidden(new javafx.event.EventHandler<WindowEvent>() {//新建任务对话框关闭后更新Box

            @Override
            public void handle(WindowEvent event) {
                if (modify.exeModify == 1) {// 区别与直接关闭窗口而没有创建任务的情况
                    modify.exeModify = 0;
                  

                }

            }
        });
    }

    public void gotoCreate() {

        FXMLLoader loader = new FXMLLoader();
        InputStream in = CondorClient.class
                .getResourceAsStream("CreateJobDialog.fxml");//getResourceAsStream
        loader.setBuilderFactory(
                new JavaFXBuilderFactory());
        loader.setLocation(CondorClient.class
                .getResource("CreateJobDialog.fxml"));
        AnchorPane page = null;

        try {
            page = (AnchorPane) loader.load(in);
        } catch (IOException ex) {
            Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Stage createDialogStage = new Stage();
        //Initialize the Stage with type of modal
        createDialogStage.initModality(Modality.APPLICATION_MODAL);
        //Set the owner of the Stage 
        createDialogStage.initOwner(stage);
        createDialogStage.setTitle("新建任务");
        Scene scene = new Scene(page, 600, 500, Color.LIGHTGREEN);
        createDialogStage.setScene(scene);
        createDialogStage.show();
        create = loader.getController();
        create.init(createDialogStage);
 
        createDialogStage.setOnHidden(new javafx.event.EventHandler<WindowEvent>() {//新建任务对话框关闭后更新Box

            @Override
            public void handle(WindowEvent event) {
              
                if (create.exeCreate == 1) {// 区别与直接关闭窗口而没有创建任务的情况
                    create.exeCreate = 0;
                    
                }
                System.out.println("event" + event.getEventType());

            }
        });

    }

    void gotoConnect() {

        UpdateChartWorkerThread updateCpuInfoThread = new UpdateChartWorkerThread(mainpage.usageChartGrid, mainpage.resourcesTab, mainpage.job_resourcesTab);

updateCpuInfoThread.start();
        UpdateJobClassAdsWorkerThread updateJobClassAdsWorkerThread = new UpdateJobClassAdsWorkerThread(mainpage.table, mainpage.sampleTab, mainpage.statusListView, mainpage.countListView, mainpage.jobManagerBar);
  updateJobClassAdsWorkerThread.start();
        TransferFileThread transferFileThread=new TransferFileThread();
  // transferFileThread.start();

    }




}
