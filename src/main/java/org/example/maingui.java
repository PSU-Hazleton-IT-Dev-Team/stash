package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class maingui extends gui {
    private JButton loansButton;
    private JButton inventoryButton;
    private JButton deletedButton;
    private JTextField nameORpsuid;
    private JTextField servicetagORname;
    private JTextField modelORlname;
    private JTextField manufactuerORfname;
    private JButton addButton;
    private JTextField ipORloanexpectdate;
    private JTextField macORloanreturndate;
    private JTextArea descriptionArea;
    private JButton sendToDeletedButton;
    private JButton updateButton;
    private JTextField locationORloanoutdate;
    private JTextField ServiceTagSEARCH;
    private JTextField WarrantySEARCH;
    private JTextField VendorSEARCH;
    private JTextField OwnerSEARCH;
    private JTextField ModelSEARCH;
    private JTextField TypeSEARCH;
    private JButton filterResultsButton;
    private JTextField DepartmentSEARCH;
    private JPanel mainPanel;
    private JButton clearAdditions;
    private JButton clearFilters;
    private JLabel superdec;
    private JLabel desc2;
    private JLabel desc3;
    private JLabel desc4;
    private JLabel desc5;
    private JLabel desc6;
    private JLabel desc7;
    private JLabel desc9;
    private JLabel desc10;
    private JLabel desc11;
    private JLabel desc12;
    private JLabel desc13;
    private JLabel desc14;
    private JLabel desc15;
    private JTable entryTable;
    private JTextField commentsSEARCH;
    private JLabel desc16;
    private JButton importButton;
    private JButton settingsButton;
    private JTabbedPane RightTabs;
    private JProgressBar progressBar;
    private JButton reauthenticateButton;
    private JTextArea sqlInjection;
    private JPanel FilterPannel;
    private JButton injectSQLButton;
    private JTextArea printSQL;
    private JButton printToSQLOUTTxtButton;
    private JButton checkoutModeButton;


    /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public maingui(JFrame frame, String username, String password,String database) {
        super(frame);

        String[] columns = {"Name", "Asset Tag","Comments","Vendor","Model","Department","Item Type","Warranty Expires","Owner"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        entryTable.setModel(tableModel);


        try {
            String user = username;
            String pass = password;
            String queryUrl = "";
            if(database.equals("Production"))
            {
                queryUrl = "https://pennstate.service-now.com/api/now/table/alm_asset?sysparm_limit=100&sysparm_display_value=true";
            }
            else if(database.equals("Development"))
            {
                queryUrl = "https://psudev.service-now.com/api/now/table/alm_asset?sysparm_limit=100&sysparm_display_value=true";
            }
            else if (database.equals("Accept"))
            {
                queryUrl = "https://psuaccept.service-now.com/api/now/table/alm_asset?sysparm_limit=100&sysparm_display_value=true";
            }

            System.out.println(queryUrl);
            HttpURLConnection conn = (HttpURLConnection) new URL(queryUrl).openConnection();
            conn.setRequestMethod("GET");

            // Encode credentials
            String basicAuth = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setRequestProperty("Accept", "application/xml");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream input = conn.getInputStream();

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(input);
                doc.getDocumentElement().normalize();

                NodeList resultNodes = doc.getElementsByTagName("result");
                for (int i = 0; i < resultNodes.getLength(); i++) {
                    Node resultNode = resultNodes.item(i);
                    if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) resultNode;

                        String ServiceTag = getTagValue("serial_number", el);

                        String assetTag = getTagValue("asset_tag", el);
                        String Comments =  getTagValue("comments", el);
                        String Warranty = getTagValue("warranty_expiration", el);
                        String Vendor = getNestedTagValue("vendor", "display_value", el);
                        String Model = getNestedTagValue("model", "display_value", el);
                        String Department = getNestedTagValue("department", "display_value", el);
                        String ItemType = getNestedTagValue("model_category", "display_value", el);
                        String Owner = getNestedTagValue("assigned_to", "display_value", el);


                        tableModel.addRow(new Object[]{assetTag, ServiceTag,Comments,Vendor,Model,Department,ItemType,Warranty,Owner});
                    }
                }
            } else {
                System.out.println(responseCode);
                JOptionPane.showMessageDialog(frame, "HTTP Error " + responseCode + ": " + conn.getResponseMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Failed to fetch or parse XML:\n" + e.getMessage());
        }






    //Clear filter listener
        clearFilters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServiceTagSEARCH.setText("");
                VendorSEARCH.setText("");
                ModelSEARCH.setText("");
                TypeSEARCH.setText("");
                DepartmentSEARCH.setText("");
                OwnerSEARCH.setText("");
                WarrantySEARCH.setText("");
            }
        });


        //Button to filter Results in the pane
        filterResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fetchAndDisplayFilteredAssets((DefaultTableModel) entryTable.getModel(),username,password,database);

            }

        });




        //Brings up Import Menu
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame importframe= new JFrame();
                importgui gui = new importgui(importframe);
                gui.setup_frame(1, gui.getPanel(),frame);
                importframe.setResizable(false);
            }
        });

        //Brings up Settings menu
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame settingsframe= new JFrame();
                settingsgui gui = new settingsgui(settingsframe, frame,username,password,database);
                gui.setup_frame(1, gui.getPanel(),frame);
            }
        });

        JTextField[] searchFields = {
                ServiceTagSEARCH, VendorSEARCH, ModelSEARCH,
                TypeSEARCH, DepartmentSEARCH, OwnerSEARCH,
                WarrantySEARCH, commentsSEARCH
        };

        for (JTextField field : searchFields) {
            field.addActionListener(e ->
                    fetchAndDisplayFilteredAssets((DefaultTableModel) entryTable.getModel(), username, password, database)
            );
        }



        reauthenticateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginframe= new JFrame();
                Login gui = new Login(loginframe);
                gui.setup_frame(1, gui.getPanel(),frame);
                frame.dispose();
            }
        });
    } // end of maingui

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getTextContent() != null) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }

    //Returning Panel to Main
    public JPanel getPanel()
    {
        return this.mainPanel;
    }//end of function "getPanel"
    public void setFrame(JFrame frame)
    {
        this.frame = frame;
    }


    private static String getNestedTagValue(String parentTag, String childTag, Element element) {
        NodeList parentNodes = element.getElementsByTagName(parentTag);
        if (parentNodes.getLength() > 0) {
            Element parent = (Element) parentNodes.item(0);
            NodeList childNodes = parent.getElementsByTagName(childTag);
            if (childNodes.getLength() > 0 && childNodes.item(0).getTextContent() != null) {
                return childNodes.item(0).getTextContent().trim();
            }
        }
        return "";
    }

    private void fetchAndDisplayFilteredAssets(DefaultTableModel model, String user, String pass,String database) {
        model.setRowCount(0); // Clear existing table data

        // Build query string
        StringBuilder query = new StringBuilder("https://pennstate.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit=100&sysparm_query=");

        if(database.equals("Production"))
        {
            query = new StringBuilder("https://pennstate.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit=100&sysparm_query=");

        }
        else if(database.equals("Development"))
        {
            query = new StringBuilder("https://psudev.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit=100&sysparm_query=");
        }
        else if (database.equals("Accept"))
        {
            query = new StringBuilder("https://psuaccept.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit=100&sysparm_query=");
        }
        LinkedList<String> conditions = new LinkedList<>();

        if (!ServiceTagSEARCH.getText().isEmpty())
            conditions.add("serial_numberLIKE" + ServiceTagSEARCH.getText());
        if (!ModelSEARCH.getText().isEmpty())
            conditions.add("model.nameLIKE" + ModelSEARCH.getText());
        if (!VendorSEARCH.getText().isEmpty())
            conditions.add("vendor.nameLIKE" + VendorSEARCH.getText());
        if (!DepartmentSEARCH.getText().isEmpty())
            conditions.add("department.nameLIKE" + DepartmentSEARCH.getText());
        if (!OwnerSEARCH.getText().isEmpty())
            conditions.add("assigned_to.nameLIKE" + OwnerSEARCH.getText());
        if (!WarrantySEARCH.getText().isEmpty())
            conditions.add("warranty_expirationLIKE" + WarrantySEARCH.getText());
        if (!TypeSEARCH.getText().isEmpty())
            conditions.add("model_category.nameLIKE" + TypeSEARCH.getText());
        if (!commentsSEARCH.getText().isEmpty())
            conditions.add("commentsLIKE" + commentsSEARCH.getText());

        query.append(String.join("^", conditions));
        String finalQuery = query.toString(); // Final query for use inside the worker

        // SwingWorker to handle background loading
        SwingWorker<Void, Integer> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                publish(5); // Start progress

                HttpURLConnection conn = (HttpURLConnection) new URL(finalQuery).openConnection();
                conn.setRequestMethod("GET");

                String basicAuth = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
                conn.setRequestProperty("Authorization", "Basic " + basicAuth);
                conn.setRequestProperty("Accept", "application/xml");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    publish(25); // After successful connection

                    InputStream input = conn.getInputStream();

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(input);
                    doc.getDocumentElement().normalize();

                    NodeList resultNodes = doc.getElementsByTagName("result");
                    int total = resultNodes.getLength();

                    for (int i = 0; i < total; i++)
                    {
                        Node resultNode = resultNodes.item(i);
                        if (resultNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element el = (Element) resultNode;

                            String ServiceTag = getTagValue("serial_number", el);
                            String assetTag = getTagValue("asset_tag", el);
                            String Comments = getTagValue("comments", el);
                            String Warranty = getTagValue("warranty_expiration", el);
                            String Vendor = getNestedTagValue("vendor", "display_value", el);
                            String Model = getNestedTagValue("model", "display_value", el);
                            String Department = getNestedTagValue("department", "display_value", el);
                            String ItemType = getNestedTagValue("model_category", "display_value", el);
                            String Owner = getNestedTagValue("assigned_to", "display_value", el);

                            final Object[] row = new Object[]{assetTag, ServiceTag, Comments, Vendor, Model, Department, ItemType, Warranty, Owner};

                            SwingUtilities.invokeLater(() -> model.addRow(row));
                        }


                        int progress = 15 + (int)(((double)i / total) * 90);
                        publish(progress);
                    }
                    publish(0);
                }

            else {
            System.out.println(responseCode);
            JOptionPane.showMessageDialog(frame, "HTTP Error " + responseCode + ": " + conn.getResponseMessage());
        }



                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latestProgress = chunks.get(chunks.size() - 1);
                progressBar.setValue(latestProgress);
            }

            @Override
            protected void done() {
               progressBar.setValue(100);
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {}
                    SwingUtilities.invokeLater(() -> progressBar.setValue(0));
                }).start();

            }
        };

        worker.execute();
    }



}// end of class maingui.java

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////