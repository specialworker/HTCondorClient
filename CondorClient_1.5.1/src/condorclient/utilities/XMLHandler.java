/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient.utilities;

/**
 *
 * @author lianxiang
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

public class XMLHandler {

    File file = new File("condorclient.xml");

    public XMLHandler() {
        // TODO Auto-generated constructor stub
    }

    public String createName_IdXML() {
        String strXML = null;
        Document document = DocumentHelper.createDocument();
        // document.
        Element root = document.addElement("root");

        Element info = root.addElement("info");

        Element job = info.addElement("job");
        job.addAttribute("name", "test");
        job.addAttribute("id", "0");

        StringWriter strWtr = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter xmlWriter = new XMLWriter(strWtr, format);
        try {
            xmlWriter.write(document);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        strXML = strWtr.toString();
        //--------

        //-------
        //strXML=document.asXML();
        //------
        //-------------
        File file = new File("niInfo.xml");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            XMLWriter out = new XMLWriter(new FileWriter(file));
            out.write(document);
            out.flush();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //--------------

        return strXML;
    }

    public Map<String, String> iteratorNI() {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");//此处要写到job

        Iterator iter = list.iterator();
        Element root = document.getRootElement();
        Element info = root.element("info");
        Map<String, String> nimap = new TreeMap<>();
        while (iter.hasNext()) {
            Element job = (Element) iter.next();
            String name = job.attributeValue("name");
            String id = job.attributeValue("id");
            nimap.put(id, name);

        }
        return nimap;
    }

    public void removeJob(String clusterId) {

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");//此处要写到job
        Iterator iter = list.iterator();
        Element root = document.getRootElement();
        Element info = root.element("info");
        while (iter.hasNext()) {
            Element job = (Element) iter.next();
            String id = job.attributeValue("id");
            if (id.equals(clusterId)) {

                info.remove(job);
            }

        }
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(new File(
                    "niInfo.xml")));
            writer.write(document);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //s
    public String getJobName(String clusterId) {
        String name = null;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element job = (Element) iter.next();

                String id = job.attributeValue("id");
                if (id.equals(clusterId)) {
                    name = job.attributeValue("name");
                }

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");
            name = "";
        }

        return name;
    }
    //e

    public String getURL(String who) {
        String url = null;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("condorclient.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/url");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element e = (Element) iter.next();

                if (who.equalsIgnoreCase("schedd")) {
                    url = e.attributeValue("schedd");
                } else {
                    url = e.attributeValue("collector");
                }

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");
            url = "";
        }

        return url;
    }

    public String getExpFile(String clusterId) {
        String expFile = null;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element job = (Element) iter.next();

                String id = job.attributeValue("id");
                if (id.equals(clusterId)) {
                    expFile = job.attributeValue("expFile");
                }

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");
            expFile = "";
        }

        return expFile;
    }

    public String getInfoFile(String clusterId) {

        String infoFile = null;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element job = (Element) iter.next();

                String id = job.attributeValue("id");
                if (id.equals(clusterId)) {
                    infoFile = job.attributeValue("infoFile");
                }

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");
            infoFile = "";
        }

        return infoFile;
    }

    public String getTaskDir(String clusterId) {
        String dir = null;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element job = (Element) iter.next();

                String id = job.attributeValue("id");
                if (id.equals(clusterId)) {
                    dir = job.attributeValue("dir");
                }

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");
            dir = "";
        }

        return dir;
    }

    public void modifyJob(String name, String clusterId) {

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();

        while (iter.hasNext()) {
            Element job = (Element) iter.next();

            String id = job.attributeValue("id");
            if (id.equals(clusterId)) {
                job.attribute("name").setValue(name);
            }

        }
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(new File(
                    "niInfo.xml")));
            writer.write(document);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addJob(String name, String clusterId, String resultpath, String expFile, String infoFile) {

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        Element root = document.getRootElement();
        Element info = root.element("info");
        Element job = info.addElement("job");
        job.addAttribute("name", name);
        job.addAttribute("id", clusterId);
        job.addAttribute("dir", resultpath);
        job.addAttribute("transfer", "0");
        job.addAttribute("expFile", expFile);
        job.addAttribute("infoFile", infoFile);
        //  System.out.println("id:" + job.attribute("id").getValue());

        /**
         * 将document中的内容写入文件中
         */
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(new File(
                    "niInfo.xml")));
            writer.write(document);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String createXML() {
        String strXML = null;
        Document document = DocumentHelper.createDocument();
        // document.
        Element root = document.addElement("root");

        Element job = root.addElement("Job");

        Element jobDescFile = job.addElement("item");
        jobDescFile.addAttribute("about", "descfile");
        Element file_name = jobDescFile.addElement("name");
        file_name.addText("submit.txt");
        Element filer_path = jobDescFile.addElement("path");
        filer_path.addText("D:\\HTCondor\\test\\2");

        StringWriter strWtr = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter xmlWriter = new XMLWriter(strWtr, format);
        try {
            xmlWriter.write(document);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        strXML = strWtr.toString();
        //--------

        //-------
        //strXML=document.asXML();
        //------
        //-------------
        File file = new File("condorclient.xml");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            XMLWriter out = new XMLWriter(new FileWriter(file));
            out.write(document);
            out.flush();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //--------------

        return strXML;
    }

    public void parserXML(String strXML) {
        SAXReader reader = new SAXReader();
        StringReader sr = new StringReader(strXML);
        InputSource is = new InputSource(sr);
        try {
            Document document = reader.read(is);

            Element root = document.getRootElement();

            //get element
            List<Element> jobList = root.elements("Job");
            List<Element> itemList = jobList.get(0).elements("item");
            for (int i = 0; i < itemList.size(); i++) {
                Element element = itemList.get(i);
                String about = element.attributeValue("about");
                System.out.println("about = " + about);
                //get all element
                List<Element> childList = element.elements();
                for (int j = 0; j < childList.size(); j++) {
                    Element e = childList.get(j);
                    System.out.println(e.getName() + "=" + e.getText());
                }
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //得到任务描述文件的完整路径
    public String getSendFilesDir() {

        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(file);
        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 将XML文档转换为String    
        String xmlStr = doc.asXML();
        //System.out.println("xmlStr:" + xmlStr);  
        StringReader sr = new StringReader(xmlStr);
        InputSource is = new InputSource(sr);
        String name = null;
        String path = null;
        try {
            Document document = reader.read(is);

            Element root = document.getRootElement();

            //get element
            List<Element> jobList = root.elements("Job");
            List<Element> itemList = jobList.get(0).elements("item");
            Element element = itemList.get(0);
            List<Element> childList = element.elements();
            Element eName = childList.get(0);
            Element ePath = childList.get(1);
            name = eName.getText();
            path = ePath.getText();
            System.out.println(eName.getName() + "=" + eName.getText());
            System.out.println(ePath.getName() + "=" + ePath.getText());

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // return path + name;
        return path;
    }

    public void parserXMLbyXPath(String strXML) {
        SAXReader reader = new SAXReader();
        StringReader sr = new StringReader(strXML);
        InputSource is = new InputSource(sr);
        try {
            Document document = reader.read(is);
            List list = document.selectNodes("/root/Job/item");
            for (int i = 0; i < list.size(); i++) {
                Element e = (Element) list.get(i);
                System.out.println("item=" + e.attributeValue("about"));
                List list1 = e.selectNodes("./*");
                for (int j = 0; j < list1.size(); j++) {
                    Element e1 = (Element) list1.get(j);
                    System.out.println(e1.getName() + "=" + e1.getText());
                }
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getNRun(String file) {
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(new File(file));
        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 将XML文档转换为String    
        String xmlStr = doc.asXML();
        //System.out.println("xmlStr:" + xmlStr);  
        StringReader sr = new StringReader(xmlStr);
        InputSource is = new InputSource(sr);
        String s = null;
        try {
            Document document = reader.read(is);
            Element root = document.getRootElement();
            Element ee = root.element("SimTimeDef");
            Element eee = ee.element("NRun");
            s = eee.getText();
            // System.out.println("getText" + eee.getText()+"getName"+eee.getName()+"path:"+eee.getPath());

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return s;
    }
        public String getUser() {
        //s
        String userName = null;
       
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("condorclient.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        List list = document.selectNodes("/root/user");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element e = (Element) iter.next();

                userName = e.attributeValue("name");
              

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");
            userName="condor";

        }
     
        return userName;
        //e

    }

    public String getexecutableFile() {
        //s
        String exeName = null;
        String exePath = null;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("condorclient.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        List list = document.selectNodes("/root/exe");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element e = (Element) iter.next();

                exeName = e.attributeValue("name");
                exePath = e.attributeValue("path");

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");

        }
        System.out.println("exeName + exePath:" + exeName + exePath);
        return exePath + exeName;
        //e

    }

    public void removeJobs(int[] delClusterIds, int delsum) {

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");//此处要写到job
        Iterator iter = list.iterator();
        Element root = document.getRootElement();
        Element info = root.element("info");
        try {
            while (iter.hasNext()) {
                Element job = (Element) iter.next();
                String id = job.attributeValue("id");
                for (int i = 0; i < delsum; i++) {
                    if (id.equals("" + delClusterIds[i])) {

                        info.remove(job);
                    }
                }

            }
        } catch (Exception e) {

            System.out.println("删除多个任务名失败！");
        }

        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(new File(
                    "niInfo.xml")));
            writer.write(document);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean jobNameExist(String jobname) {
        Boolean isExist = false;

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element job = (Element) iter.next();

                String name = job.attributeValue("name");
                if (name.equals(jobname)) {
                    isExist = true;
                    break;
                }

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");

        }
        return isExist;

    }

    int getTransfer(String clusterId) {
        int transfer = 0;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();
        try {
            while (iter.hasNext()) {
                Element job = (Element) iter.next();

                String id = job.attributeValue("id");
                if (id.equals(clusterId)) {
                    transfer = Integer.parseInt(job.attributeValue("transfer"));
                }

            }
        } catch (Exception ex) {
            System.out.println("NOT FOUND!");

        }

        return transfer;
    }

    void setTransfer(String clusterId) {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new File("niInfo.xml"));

        } catch (DocumentException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List list = document.selectNodes("/root/info/job");
        Iterator iter = list.iterator();

        while (iter.hasNext()) {
            Element job = (Element) iter.next();

            String id = job.attributeValue("id");
            if (id.equals(clusterId)) {
                job.attribute("transfer").setValue("1");
            }

        }
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileWriter(new File(
                    "niInfo.xml")));
            writer.write(document);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * @param args
     */

}
