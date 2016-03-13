/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient;

import birdbath.ClassAd;
import birdbath.Collector;
import birdbath.Schedd;
import birdbath.Transaction;
import condor.ClassAdStructAttr;
import condor.UniverseType;
import condorclient.model.ResourceInfoItemStub;
import condorclient.utilities.BaseTool;
import condorclient.utilities.XMLHandler;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.rpc.ServiceException;
import org.apache.commons.io.FileUtils;

/**
 * FXML Controller class
 *
 * @author lianxiang
 */
public class CreateJobDialogController extends AnchorPane {

    @FXML
    private ToggleGroup dispatchGroup;
    @FXML
    private TextField jobNameText;
    @FXML
    private TextField infoFileText;
    @FXML
    private TextField expFileText;
    @FXML
    private TextField sampleNumText;
    @FXML
    private TextField cpuNumText;
    @FXML
    private TextField availCpuNumText;
    @FXML
    private TextField nodeNumText;
    @FXML
    private TextField availNodeNumText;
    @FXML
    private Button runJobButton;
    @FXML
    private Button saveJobButton;
    @FXML
    private AnchorPane availMachines;
    @FXML
    private RadioButton byslot;
    @FXML
    private RadioButton bynode;
    @FXML
    private RadioButton byappoint;
    @FXML
    private VBox machineBox;
    private int allocMthod = 0;
    private int nrun = 0;

    private final Desktop desktop = Desktop.getDesktop();
    private Stage stage;
    public int exeCreate = 0;
    private int showBoxNum = 0;
    private int slotNum = 0;
    private int nodeNum = 0;
    private int appointslotNum = 0;
    private int appointnodeNum = 0;
    private int usenodenum = 0;//实际使用的node数
    private int useslotnum = 0;//实际使用的slot数
    private int useappointnodenum = 0;//实际指定的slot数
    private int[] selectedMachine;//存放选中的机器的编号，此处编号是指Box编号
    private Map<String, ObservableList<String>> map = new TreeMap<>();//存放每个ip对应的slot
    private Map<String, ObservableList<String>> apponitmap = new TreeMap<>();
    private String expFileInitialDir = System.getProperty("user.home");
    private String infoFileInitialDir = System.getProperty("user.home");
    private ObservableList<ResourceInfoItemStub> resourcesClassAds = FXCollections.observableArrayList();
    @FXML
    private RadioButton bycondor;
    @FXML
    private Button cancelCreateButton;
    @FXML
    private TextField resultFileText;
    @FXML
    private Label snumError;
    @FXML
    private Label nnumError;
    @FXML
    private Label jobNameError;
    private Boolean nameOk = false;
    private Boolean infoOk = false;
    private Boolean expOk = false;
    private Boolean resultOk = false;
    private Boolean allocOk = false;
    @FXML
    private Label otherError;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void infoFileChooserFired(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导入信息流文件");
        fileChooser.setInitialDirectory(
                new File(infoFileInitialDir)
        );
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IXL", "*.ixl"));

        File file = fileChooser.showOpenDialog(this.stage);

        if (file != null) {
            infoFileText.setText(file.getAbsolutePath());
            infoFileInitialDir = file.getParent();

            System.out.println("infoFileInitialDir:" + infoFileInitialDir);
            // System.out.println(file.getName());

            // openFile(file);
        }
        infoOk = true;
        enableButton();
    }

    @FXML
    private void resultDirChooserFired(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择任务结果目录");
        String cwd = System.getProperty("user.home");
        dirChooser.setInitialDirectory(
                new File(cwd)
        );

        File chosenDir = dirChooser.showDialog(this.stage);
        // File chosenDir = chooser.showDialog(primaryStage);
        if (chosenDir != null) {
            resultFileText.setText(chosenDir.getAbsolutePath());

            System.out.println(chosenDir.getAbsolutePath());
        } else {
            System.out.print("no directory chosen");
        }
        resultOk = true;
        enableButton();

    }

    @FXML
    private void expFileChooserFired(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导入实验框架文件");

        fileChooser.setInitialDirectory(
                new File(expFileInitialDir)
        );
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXL", "*.exl"));

        File file = fileChooser.showOpenDialog(this.stage);
        String s = "";
        if (file != null) {
            expFileText.setText(file.getAbsolutePath());
            expFileInitialDir = file.getParent();
            XMLHandler handler = new XMLHandler();
            s = handler.getNRun(file.getAbsolutePath());
        }

        sampleNumText.setText(s);
        sampleNumText.setDisable(true);
        //要修改
        expOk = true;
        enableButton();
    }

    @FXML
    private void runJobFired(ActionEvent event) {

        nrun = Integer.parseInt(sampleNumText.getText().trim());
        if (nrun == 0) {
            return;
        }
        int eachrun = 0;

        // System.out.println("map:" + map.toString());
        ObservableList<String> slotname = FXCollections.observableArrayList();
        XMLHandler handler = new XMLHandler();
        if (allocMthod == 0) {//按slot分配

            if (!slotNumIsOk()) {
                return;
            }
            int i = 0;
            for (String s : map.keySet()) {
                if (i >= useslotnum) {
                    break;
                }
                i = i + map.get(s).size();
                if (i <= useslotnum) {//处理了整除余数问题

                    slotname.addAll(map.get(s));
                } else {
                    i = i - map.get(s).size();
                    for (String rest : map.get(s)) {
                        if (i >= useslotnum) {
                            break;
                        }
                        slotname.add(rest);
                        i++;
                    }
                }
            }//得到slotname
            //提交任务
            // System.out.println("allocMthod:" + allocMthod + "useslotnum:" + useslotnum + "\nslotname:" + slotname.toString());
        } else if (allocMthod == 1) {//按节点分配
            //  
            if (!nodeNumInputIsOk()) {//判断输入是否合理
                return;
            }
            int i = 0;
            for (String s : map.keySet()) {
                if (i >= usenodenum) {
                    break;
                }
                i++;
                if (slotname.size() + map.get(s).size() <= nrun) {
                    slotname.addAll(map.get(s));
                } else if (slotname.size() < nrun) {//只加入另一个ip中的某些节点
                    for (String rs : map.get(s)) {

                        slotname.add(rs);
                        if (slotname.size() == nrun) {
                            break;
                        }
                    }
                    break;
                }
            }
            //System.out.println("allocMthod:" + allocMthod + "usenodenum:" + usenodenum + "\nslotname:" + slotname.toString());
        } else if (allocMthod == 2) {//指定节点分配
            int i = 0;
            for (String s : apponitmap.keySet()) {
                // slotname.addAll(map.get(s));//apponitmap没有存放每个key对应的list
                if (i >= nrun) {
                    break;
                }
                i++;
                //s
                if (slotname.size() + map.get(s).size() <= nrun) {
                    slotname.addAll(map.get(s));
                } else if (slotname.size() < nrun) {//只加入另一个machine中的某些slot
                    for (String rs : map.get(s)) {
                        slotname.add(rs);
                        if (slotname.size() == nrun) {
                            break;
                        }
                    }
                    break;
                }
            }
            if (slotname.size() == 0) {
                //System.out.println("+++++"+otherError.getText());
                otherError.setText("选择分配节点");
                return;
            }

            //System.out.println("allocMthod:" + allocMthod + "zhi ding ge shu:" + apponitmap.keySet().size() + "\nslotname:" + slotname.toString());
        } else if (allocMthod == 3) {//自适应，只是根据现有的核数量决定提交的次数而不指定具体提交到哪个节点
            int aptNum = 0;
            if (slotNum <= nrun) {//取两者中较小的
                aptNum = slotNum;
            } else {
                aptNum = nrun;
            }
            for (int i = 0; i < aptNum; i++) {//slotNum集群中总共的核数
                slotname.add("");
            }
        }
        eachrun = nrun / slotname.size();
        int restrun = nrun - eachrun * slotname.size();//剩余的给最后一个slot
        //System.out.println("eachrun" + eachrun + "restrun" + restrun);

        URL url = null;

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
        } catch (ServiceException e2) {
        }

        int j = 0;
        //创建该任务本次执行的目录
        String timestamp = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
        String resultdirstr = resultFileText.getText().trim() + "\\" + jobNameText.getText().trim() + "\\" + timestamp;
        System.out.println(resultdirstr);
        try {
            FileUtils.forceMkdir(new File(resultdirstr));
        } catch (IOException ex) {
            Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(timestamp);
        //获得本次任务执行所需要传输的文件
        File infoFile = new File(infoFileText.getText().trim());
        File expFile = new File(expFileText.getText().trim());

        File[] transfiles = {infoFile, expFile};

        Transaction xact = schedd.createTransaction();
        try {
            xact.begin(30);

        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int clusterId = 0;
        try {
            clusterId = xact.createCluster();

        } catch (RemoteException e) {
        }
        for (String name : slotname) {//若指定3个slot，则是其中随意的三个
            j++;
            int jobId = 0;
            try {
                jobId = xact.createJob(clusterId);
            } catch (RemoteException e) {
            }
            String jobdirstr = resultdirstr + "\\" + jobId;
            File jobdir = new File(jobdirstr);
            try {
                FileUtils.forceMkdir(jobdir);//创建每个job对应的目录
            } catch (IOException ex) {
                Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //复制输入文件到每个job的目录下
          /*  for (File f : transfiles) {
             try {
             FileUtils.copyFileToDirectory(f, jobdir);
             } catch (IOException ex) {
             Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
             }

             }*/

            if (j == slotname.size()) {
                eachrun = eachrun + restrun;
            }
            String eachrunstr = "" + eachrun;

            String argumentsStr = infoFile.getName() + " " + expFile.getName() + " " + eachrunstr;// null;//".exl .ixl nrun"
            String requirementsStr = "Name==\"" + name + "\"";//Name=slot1@Lenovo-PC;String req="Name==\"slot1@Lenovo-PC\"";
            if (name.equals("")) {
                requirementsStr = null;//自适应分配方式
            }
            System.out.println("requirementsStr:" + requirementsStr + "eachrunstr:" + eachrunstr);

            ClassAdStructAttr[] attributes = null;// {new ClassAdStructAttr()};

            // ClassAdStructAttr attribute =new ClassAdStructAttr("WhenToTransferOutput",ClassAdAttrType.value3,"ON_EXIT_OR_EVICT");
            //  attributes[0] = new ClassAdStructAttr("Iwd", ClassAdAttrType.value3, jobdirstr);
            // attributes[1] = new ClassAdStructAttr("JobLeaseDuration", ClassAdAttrType.value1,"1200");
            String commandStr = handler.getexecutableFile();//可执行文件

            try {
                xact.submit(clusterId, jobId,
                        "htcondor", UniverseType.VANILLA, commandStr, argumentsStr, requirementsStr, attributes, transfiles);

            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        try {
            xact.commit();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            schedd.requestReschedule();//请求马上调度
        } catch (RemoteException ex) {
            Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String jobName = jobNameText.getText();
        handler.addJob(jobName, "" + clusterId, resultdirstr,expFileText.getText().trim(),infoFileText.getText().trim());//结果目录存入jobItem项

        exeCreate = 1;
        stage.close();

    }

    @FXML
    private void saveJobFired(ActionEvent event) {

        nrun = Integer.parseInt(sampleNumText.getText().trim());
        if (nrun == 0) {
            return;
        }
        int eachrun = 0;

        // System.out.println("map:" + map.toString());
        ObservableList<String> slotname = FXCollections.observableArrayList();
        XMLHandler handler = new XMLHandler();
        if (allocMthod == 0) {//按slot分配

            if (!slotNumIsOk()) {
                return;
            }

            int i = 0;
            for (String s : map.keySet()) {
                if (i >= useslotnum) {
                    break;
                }

                i = i + map.get(s).size();
                if (i <= useslotnum) {//处理了整除余数问题

                    slotname.addAll(map.get(s));
                } else {
                    i = i - map.get(s).size();
                    for (String rest : map.get(s)) {
                        if (i >= useslotnum) {
                            break;
                        }
                        slotname.add(rest);
                        i++;
                    }
                }
            }//得到slotname
            //提交任务
            // System.out.println("allocMthod:" + allocMthod + "useslotnum:" + useslotnum + "\nslotname:" + slotname.toString());
        } else if (allocMthod == 1) {//按节点分配
            if (!nodeNumInputIsOk()) {
                return;
            }
            int i = 0;
            for (String s : map.keySet()) {
                if (i >= usenodenum) {
                    break;
                }
                i++;
                //s
                if (slotname.size() + map.get(s).size() <= nrun) {
                    slotname.addAll(map.get(s));
                } else if (slotname.size() < nrun) {//只加入另一个ip中的某些节点
                    for (String rs : map.get(s)) {
                        slotname.add(rs);
                        if (slotname.size() == nrun) {
                            break;
                        }
                    }
                    break;
                }

            }

            //System.out.println("allocMthod:" + allocMthod + "usenodenum:" + usenodenum + "\nslotname:" + slotname.toString());
        } else if (allocMthod == 2) {//指定节点分配
            int i = 0;
            for (String s : apponitmap.keySet()) {
                // slotname.addAll(map.get(s));//apponitmap没有存放每个key对应的list  
                ///eee
                if (i >= nrun) {
                    break;
                }
                i++;
                //s
                if (slotname.size() + map.get(s).size() <= nrun) {
                    slotname.addAll(map.get(s));
                } else if (slotname.size() < nrun) {//只加入另一个machine中的某些slot
                    for (String rs : map.get(s)) {
                        slotname.add(rs);
                        if (slotname.size() == nrun) {
                            break;
                        }
                    }
                    break;
                }
                ///eee
            }
            if (slotname.size() == 0) {
                // System.out.println("+++++"+otherError.getText());
                otherError.setText("选择分配节点");
                return;
            }

            //System.out.println("allocMthod:" + allocMthod + "zhi ding ge shu:" + apponitmap.keySet().size() + "\nslotname:" + slotname.toString());
        } else if (allocMthod == 3) {//不限定
            //e
               int aptNum = 0;
            if (slotNum <= nrun) {//取两者中较小的
                aptNum = slotNum;
            } else {
                aptNum = nrun;
            }
            for (int i = 0; i < aptNum; i++) {//slotNum集群中总共的核数
                slotname.add("");
            }
            //e
        }
        eachrun = nrun / slotname.size();
        int restrun = nrun - eachrun * slotname.size();//剩余的给最后一个slot
        //System.out.println("eachrun" + eachrun + "restrun" + restrun);
        URL url = null;

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
        } catch (ServiceException e2) {
        }
        //s
        String timestamp = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
        String resultdirstr = resultFileText.getText().trim() + "\\" + jobNameText.getText().trim() + "\\" + timestamp;
        System.out.println(resultdirstr);
        try {
            FileUtils.forceMkdir(new File(resultdirstr));
        } catch (IOException ex) {
            Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(timestamp);
        //获得本次任务执行所需要传输的文件
        File infoFile = new File(infoFileText.getText().trim());
        File expFile = new File(expFileText.getText().trim());
        // File testFile = new File("D:\\HTCondor\\test\\2\\inputfile.txt");
        File[] transfiles = {infoFile, expFile};

        //e
        Transaction xact = schedd.createTransaction();
        try {

            if (xact != null) {
                xact.begin(3000);
            }
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        int clusterId = 0;
        try {
            clusterId = xact.createCluster();

        } catch (RemoteException e) {
        }
        int j = 0;
        for (String name : slotname) {//若指定3个slot，则是其中随意的三个
            j++;
            int jobId = 0;
            try {
                jobId = xact.createJob(clusterId);
            } catch (RemoteException e) {
            }
            //s
            String jobdirstr = resultdirstr + "\\" + jobId;

            File jobdir = new File(jobdirstr);
            try {
                FileUtils.forceMkdir(jobdir);//创建每个job对应的目录
            } catch (IOException ex) {
                Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //复制输入文件到每个job的目录下
           /* for (File f : transfiles) {
             try {
             FileUtils.copyFileToDirectory(f, jobdir);
             } catch (IOException ex) {
             Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
             }

             }*/
            //e
            if (j == slotname.size()) {
                eachrun = eachrun + restrun;
            }
            String eachrunstr = "" + eachrun;

            String argumentsStr = infoFile.getName() + " " + expFile.getName() + " " + eachrunstr;// "" + eachrunstr;// null;//".exl .ixl nrun"
            String requirementsStr = "Name==\"" + name + "\"";//Name=slot1@Lenovo-PC;String req="Name==\"slot1@Lenovo-PC\"";

            if (name.equals("")) {
                requirementsStr = null;//自适应分配方式
            }
            ClassAdStructAttr[] attributes = null;// {new ClassAdStructAttr()};
            // ClassAdStructAttr attribute =new ClassAdStructAttr("WhenToTransferOutput",ClassAdAttrType.value3,"ON_EXIT_OR_EVICT");
            //attributes[0] = new ClassAdStructAttr("Iwd", ClassAdAttrType.value3, jobdirstr);
            //JobLeaseDuration
//            attributes[1] = new ClassAdStructAttr("JobLeaseDuration", ClassAdAttrType.value1,"1200");
            String commandStr = handler.getexecutableFile();//可执行文件

            try {
                xact.submit(clusterId, jobId,
                        "htcondor", UniverseType.VANILLA, commandStr, argumentsStr, requirementsStr, attributes, transfiles);

            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                xact.holdJob(clusterId, jobId, "");//与新建任务的唯一区别
            } catch (RemoteException ex) {
                Logger.getLogger(CreateJobDialogController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        try {
            xact.commit();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jobName = jobNameText.getText();
        handler.addJob(jobName, "" + clusterId, resultdirstr,expFileText.getText().trim(),infoFileText.getText().trim());

        // System.out.print("createTransaction succeed haha!!\n");
        exeCreate = 1;
        stage.close();

    }

    @FXML
    private void cancelCreateFired(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void allocMethod(ActionEvent event) {
        String s = event.getTarget().toString();
        // System.out.println(s);
        String id = s.substring(s.indexOf("=") + 1, s.indexOf(","));//取得第16和17个共两个字符
        if (id.equals("byslot")) {//按核数分配
            if (byslot.isSelected()) {
                allocMthod = 0;
                nodeNumText.setText("");
                // nodeNumText.setEditable(false);
                nodeNumText.setDisable(true);
                availNodeNumText.setText("");
                //availNodeNumText.setEditable(false);
                availNodeNumText.setDisable(true);
                nnumError.setText("");
                otherError.setText("");
                cpuNumText.setDisable(false);
                availCpuNumText.setText("" + slotNum);
                availCpuNumText.setDisable(true);
                for (int i = 0; i < showBoxNum; i++) {
                    //  availMachines.getChildren().get(i).setDisable(true);
                    // ((CheckBox) availMachines.getChildren().get(i)).setSelected(false);
                    machineBox.getChildren().get(i).setDisable(true);
                    ((CheckBox) machineBox.getChildren().get(i)).setSelected(false);
                }
            }
        } else if (id.equals("bynode")) {//按节点分配
            if (bynode.isSelected()) {
                allocMthod = 1;
                // nodeNumText.setText("");
                nodeNumText.setDisable(false);
                availNodeNumText.setText("" + nodeNum);
                availNodeNumText.setDisable(true);
                cpuNumText.setText("");
                cpuNumText.setDisable(true);
                availCpuNumText.setText("");
                availCpuNumText.setDisable(true);
                snumError.setText("");
                otherError.setText("");
                for (int i = 0; i < showBoxNum; i++) {
                    // availMachines.getChildren().get(i).setDisable(true);
                    machineBox.getChildren().get(i).setDisable(true);
                    // ((CheckBox) availMachines.getChildren().get(i)).setSelected(false);
                }

            }
        } else {//指定节点分配
            if (byappoint.isSelected()) {
                appointslotNum = 0;//每次将指定节点总数清零
                allocMthod = 2;
                nodeNumText.setDisable(true);
                nodeNumText.setText("");
                availNodeNumText.setText("");
                availNodeNumText.setDisable(true);
                cpuNumText.setText("");
                cpuNumText.setDisable(true);
                availCpuNumText.setText("");
                availCpuNumText.setDisable(true);
                nnumError.setText("");
                snumError.setText("");
                // otherError.setText("");
                for (int i = 0; i < showBoxNum; i++) {
                    // availMachines.getChildren().get(i).setDisable(false);
                    machineBox.getChildren().get(i).setDisable(false);
                    //s
                    // ((CheckBox) availMachines.getChildren().get(i)).setOnAction(new EventHandler<ActionEvent>() {
                   /* ((CheckBox) machineBox.getChildren().get(i)).setOnAction(new EventHandler<ActionEvent>() {
                     @Override
                     public void handle(ActionEvent event) {
                     if (((CheckBox) event.getTarget()).isSelected()) {
                     otherError.setText("");
                     }
                     }
                     });*/
                    //e

                }

            } else //s
            if (bycondor.isSelected()) {//自适应分配

                allocMthod = 3;
                nodeNumText.setDisable(true);
                nodeNumText.setText("");
                availNodeNumText.setText("");
                availNodeNumText.setDisable(true);
                cpuNumText.setText("");
                cpuNumText.setDisable(true);
                availCpuNumText.setText("");
                availCpuNumText.setDisable(true);
                nnumError.setText("");
                snumError.setText("");
                otherError.setText("");
                //reset checkbox
                // int cbn = availMachines.getChildren().size();
                int cbn = machineBox.getChildren().size();
                for (int i = 0; i < cbn; i++) {
                    // ((CheckBox) availMachines.getChildren().get(i)).setDisable(true);
                    //((CheckBox) availMachines.getChildren().get(i)).setSelected(false);
                    ((CheckBox) machineBox.getChildren().get(i)).setDisable(true);
                    ((CheckBox) machineBox.getChildren().get(i)).setSelected(false);
                }

            }
            //e

        }
        allocOk = true;
        enableButton();

    }

    public void init(Stage createDialogStage) {
        this.stage = createDialogStage;
        runJobButton.setDisable(true);
        saveJobButton.setDisable(true);
        if (machineBox != null) {
            machineBox.setSpacing(3);
            System.out.println("yi you===================================================");
        } else {
            machineBox = new VBox();
            System.out.println("xin jian===================================================");
        }
       configAvailMachinesPane();

    }

    public void configAvailMachinesPane() {

        URL collector_url = null;
        XMLHandler handler = new XMLHandler();
        String collectorStr = handler.getURL("collector");
        try {
            collector_url = new URL(collectorStr);
        } catch (MalformedURLException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        ClassAd ad = null;//birdbath.ClassAd;

        ClassAdStructAttr[][] startdAdsArray = null;

        Collector c = null;
        try {
            c = new Collector(collector_url);
        } catch (ServiceException ex) {
            Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            startdAdsArray = c.queryStartdAds("");//owner==\"lianxiang\"
        } catch (RemoteException ex) {
            Logger.getLogger(CondorClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        String ip = null;
        String slotName = null;
        resourcesClassAds.clear();
        map.clear();

        for (ClassAdStructAttr[] x : startdAdsArray) {

            ad = new ClassAd(x);

            ip = ad.get("MyAddress");
            String ipStr = ip.substring(ip.indexOf("<") + 1, ip.indexOf(":"));
            // System.out.println(ad.toString());
            ObservableList<String> slotlist;

            slotlist = FXCollections.<String>observableArrayList();
            slotName = ad.get("Name");//未用上

            if (!map.containsKey(ipStr)) {

                map.put(ipStr, slotlist);
            }

            map.get(ipStr).add(slotName);

            //PublicClaimId
        }

        showBoxNum = map.size();
        nodeNum = showBoxNum;
        selectedMachine = new int[nodeNum];
        //  vb=new VBox();
        //  machineBox.setSpacing(3);
        int i = 0;
        for (String s : map.keySet()) {
            CheckBox n = new CheckBox();
            String boxId = "box" + i;
            n.setId(boxId);
            n.setText(s);
            slotNum = slotNum + map.get(s).size();
            n.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    String s = event.getTarget().toString();
                    // System.out.println(s);
                    String id = s.substring(s.indexOf("=box") + 4, s.indexOf(","));
                    System.out.println("boxid" + id);
                    //String ip = s.substring(s.indexOf("]'") + 2, s.indexOf("'"));
                    int intid = Integer.parseInt(id);//Integer.getInteger(id);
                    //  System.out.println(intid + "id" + id);
                    // CheckBox sbox = (CheckBox) availMachines.getChildren().get(intid);
                    CheckBox sbox = (CheckBox) machineBox.getChildren().get(intid);
                    if (sbox.isSelected()) {
                        selectedMachine[intid] = 1;
                        // System.out.println(selectedMachine[intid]);
                        apponitmap.putIfAbsent(sbox.getText().trim(), map.get(sbox.getText().trim()));//只是放了ip没有放其对应的slots
                        appointslotNum = appointslotNum + map.get(sbox.getText().trim()).size();//

                        System.out.println("machine id " + sbox.getText().trim() + "machine size" + map.get(sbox.getText().trim()).size());
                    } else {
                        selectedMachine[intid] = 0;
                        System.out.println("weixuanzhoong:" + selectedMachine[intid]);
                        if (apponitmap.containsKey(sbox.getText())) {
                            apponitmap.remove(sbox.getText());
                        }
                        appointslotNum = appointslotNum - map.get(sbox.getText().trim()).size();//
                    }
                    otherError.setText("");
                    availCpuNumText.setText("");
                    availCpuNumText.setText("" + appointslotNum);
                    availCpuNumText.setDisable(true);
                    availNodeNumText.setText("");
                    availNodeNumText.setText("" + apponitmap.size());
                    availNodeNumText.setDisable(true);
                    // System.out.println("apponitmap.size():" + apponitmap.size() + "apponitmap.values().size():" + apponitmap.values().size());

                }
            });

            //availMachines.getChildren().add(n);
            machineBox.getChildren().add(n);

            //  boxContainer.getChildren().get(showBoxNum - 1).setVisible(true);
            i++;

        }
        // availMachines.getChildren().add(vb);

    }

    @FXML
    private void checkExistFired(MouseEvent event) {
        XMLHandler handler = new XMLHandler();

        Boolean isExist = handler.jobNameExist(jobNameText.getText().trim());
        if (isExist) {
            jobNameError.setText("任务名已经存在");
            nameOk = false;
        } else if (!jobNameText.getText().trim().equals("")) {
            jobNameError.setText("");
            nameOk = true;

        }
        enableButton();
        //  System.out.println("=========================================nameOk" + nameOk);
    }

    private void enableButton() {

        if (nameOk && infoOk && expOk && resultOk && allocOk) {
            saveJobButton.setDisable(false);
            runJobButton.setDisable(false);
        }

    }

    private boolean nodeNumInputIsOk() {

        //s
        if (nodeNumText.getText().trim().equals("")) {
            nnumError.setText("不能为空");
            //isOk=false;
            return false;
        } else {
            if (!BaseTool.isInteger(nodeNumText.getText().trim())) {
                nnumError.setText("输入为数字");
                return false;
            }
            usenodenum = Integer.parseInt(nodeNumText.getText().trim());
            if (usenodenum == 0) {

                nnumError.setText("不能为0");
                return false;
            } else if (usenodenum > nodeNum) {

                nnumError.setText("超出了可用范围");
                return false;
            }
        }
        return true;
        //e
    }

    private boolean slotNumIsOk() {
        if (cpuNumText.getText().trim().equals("")) {
            snumError.setText("不能为空");
            return false;
        } else {
            if (!BaseTool.isInteger(cpuNumText.getText().trim())) {
                snumError.setText("输入为数字");
                return false;
            }
            useslotnum = Integer.parseInt(cpuNumText.getText().trim());
            if (useslotnum == 0) {

                snumError.setText("不能为0");
                return false;
            } else if (useslotnum > slotNum || useslotnum > nrun) {//useslotnum不能超出nrun的范围

                snumError.setText("超出了可用范围");
                return false;
            }
        }
        return true;
    }

}
