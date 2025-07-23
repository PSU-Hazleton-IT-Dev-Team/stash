package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;



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
    private JTextArea chatArea;
    private JTextField userPromptArea;
    private JButton goButton;
    private JTextField ServiceTagFeildADV;
    private JRadioButton includeST;
    private JRadioButton excludeST;
    private JTextField VendorFieldADV;
    private JRadioButton excludeV;
    private JRadioButton includeV;
    private JRadioButton includeM;
    private JTextField ModelFeildADV;
    private JRadioButton excludeM;
    private JRadioButton includeIT;
    private JTextField ItemTypeFeildADV;
    private JRadioButton excludeIT;
    private JRadioButton includeD;
    private JTextField DepartmentFeildADV;
    private JRadioButton excludeD;
    private JRadioButton includeO;
    private JRadioButton excludeO;
    private JTextField OwnerFeildADV;
    private JRadioButton beforeW;
    private JTextField WarrantyFeildADV;
    private JRadioButton afterW;
    private JRadioButton includeW;
    private JRadioButton excludeW;
    private JButton thisYearButton;
    private JButton nextYearButton;
    private JButton alreadyExpiredButton;
    private JTextField CommentsFeildADV;
    private JRadioButton includeC;
    private JRadioButton excludeC;
    private JButton emptyCommentButton;
    private JButton filterButton;
    private JButton cButton;
    private JButton exportCurrentTableAsButton;
    private JButton runAsReportButton;
    private JTextArea sqlInjection;
    private JPanel FilterPannel;
    private JButton injectSQLButton;
    private JTextArea printSQL;
    private JButton printToSQLOUTTxtButton;
    private JButton checkoutModeButton;

    static final Dotenv dotenv =Dotenv.load();


    /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public maingui(JFrame frame, String username, String password,String database,String unit,int limit) {
        super(frame);

        String[] columns = {"Name", "Asset Tag","Comments","Vendor","Model","Department","Item Type","Warranty Expires","Owner"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        //Set Chatarea For AI to not editable
        chatArea.setEditable(false);
        entryTable.setModel(tableModel);
        entryTable.setAutoCreateRowSorter(true);


        try {
            String user = username;
            String pass = password;
            String queryUrl = "";
            if(database.equals("Production"))
            {
                queryUrl = "https://pennstate.service-now.com/api/now/table/alm_asset?sysparm_limit="+limit+"&sysparm_display_value=true";
            }
            else if(database.equals("Development"))
            {
                queryUrl = "https://psudev.service-now.com/api/now/table/alm_asset?sysparm_limit="+limit+"&sysparm_display_value=true";
            }
            else if (database.equals("Accept"))
            {
                queryUrl = "https://psuaccept.service-now.com/api/now/table/alm_asset?sysparm_limit="+limit+"&sysparm_display_value=true";
            }
            if(unit!="All Units")
            {
                queryUrl+=("&sysparm_query=asset_tagSTARTSWITH"  + unit);
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
                commentsSEARCH.setText("");
            }
        });


        //Button to filter Results in the pane
        filterResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fetchAndDisplayFilteredAssets((DefaultTableModel) entryTable.getModel(),username,password,database,unit,limit);

            }

        });






        //Brings up Settings menu
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame settingsframe= new JFrame();
                settingsgui gui = new settingsgui(settingsframe, frame,username,password,database,unit);
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
                    fetchAndDisplayFilteredAssets((DefaultTableModel) entryTable.getModel(), username, password, database,unit,limit)
            );
        }



        entryTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && row != -1) {

                    System.out.println(entryTable.getValueAt(entryTable.getSelectedRow(), 0).toString());

                    if(entryTable.getValueAt(entryTable.getSelectedRow(), 0).toString()!=null)
                    {
                        JFrame detailsframe = new JFrame();
                        Details gui = new Details(detailsframe,username,password,database,unit,entryTable.getValueAt(entryTable.getSelectedRow(), 0).toString());
                        gui.setup_frame(1, gui.getPanel(),frame);

                    }

                }
            }
        });


        reauthenticateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame loginframe= new JFrame();
                Login gui = new Login(loginframe,limit);
                gui.setup_frame(1, gui.getPanel(),frame);
                frame.dispose();
            }
        });


        //AI Integration

        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });


        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String runner=buildServiceNowQueryURL(false,database);
                DisplayFilteredAssets((DefaultTableModel) entryTable.getModel(), username, password, runner,unit);
            }
        });


        runAsReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String runner=buildServiceNowQueryURL(true,database);
                DisplayFilteredAssets((DefaultTableModel) entryTable.getModel(), username, password, runner,unit);

            }
        });
    } // end of maingui


    public String buildServiceNowQueryURL(boolean runAsReport,String database) {
        StringBuilder query = new StringBuilder();
        boolean ignoreWarrantyField = false;

        addMultiValueFilter(query, "Asset_Tag", ServiceTagFeildADV.getText(), includeC.isSelected(), excludeC.isSelected());
        addMultiValueFilter(query, "vendor", VendorFieldADV.getText(), includeV.isSelected(), excludeV.isSelected());
        addMultiValueFilter(query, "Model", ModelFeildADV.getText(), includeM.isSelected(), excludeM.isSelected());
        addMultiValueFilter(query, "Item_Type", ItemTypeFeildADV.getText(), includeIT.isSelected(), excludeIT.isSelected());
        addMultiValueFilter(query, "Department", DepartmentFeildADV.getText(), includeD.isSelected(), excludeD.isSelected());
        addMultiValueFilter(query, "Owner", OwnerFeildADV.getText(), includeO.isSelected(), excludeO.isSelected());
        addMultiValueFilter(query, "comments", CommentsFeildADV.getText(), includeC.isSelected(), excludeC.isSelected());

        if (emptyCommentButton.isSelected()) {
            query.append("^javascript:comments==null || comments==''");
        }

        if (alreadyExpiredButton.getModel().isArmed()) {
            query.append("^javascript:gs.dateDiff(gs.nowDateTime(),Warranty_Expiration)<0");
            WarrantyFeildADV.setText("Already Expired");
            ignoreWarrantyField = true;
        } else if (thisYearButton.getModel().isArmed()) {
            query.append("^javascript:gs.datePart(Warranty_Expiration,'yyyy')==gs.datePart(gs.nowDateTime(),'yyyy')");
            WarrantyFeildADV.setText("Expires This Year");
            ignoreWarrantyField = true;
        } else if (nextYearButton.getModel().isArmed()) {
            query.append("^javascript:gs.datePart(Warranty_Expiration,'yyyy')==(gs.datePart(gs.nowDateTime(),'yyyy')+1)");
            WarrantyFeildADV.setText("Expires Next Year");
            ignoreWarrantyField = true;
        }

        String warrantyDate = WarrantyFeildADV.getText().trim();
        if (!warrantyDate.isEmpty() && !ignoreWarrantyField) {
            if (beforeW.isSelected()) {
                query.append("^Warranty_Expiration%3C").append(warrantyDate);
            } else if (afterW.isSelected()) {
                query.append("^Warranty_Expiration%3E").append(warrantyDate);
            }
        }

        String finalQuery = query.toString();
        if (finalQuery.startsWith("^")) finalQuery = finalQuery.substring(1);

        int limit = runAsReport ? 100000 : 100;
        String baseURL;

        if (database.equals("Production")) {
            baseURL = "https://pennstate.service-now.com";
        } else if (database.equals("Development")) {
            baseURL = "https://psudev.service-now.com";
        } else if (database.equals("Accept")) {
            baseURL = "https://psuaccept.service-now.com";
        } else {
            baseURL = "https://pennstate.service-now.com";
        }

        return baseURL + "/api/now/table/alm_asset?sysparm_display_value=true"
                + "&sysparm_limit=" + limit
                + "&sysparm_query=" + finalQuery;
    }


    private void addMultiValueFilter(StringBuilder query, String field, String input, boolean include, boolean exclude) {
        if (input == null || input.trim().isEmpty()) return;

        String[] values = input.trim().split("\\s+");
        if (values.length == 0) return;

        if (include) {
            // Single value
            if (values.length == 1) {
                query.append("^").append(field).append("LIKE").append(values[0]);
            }
            // Multiple values with OR logic
            else {
                query.append("^("); // start group
                for (int i = 0; i < values.length; i++) {
                    query.append(field).append("LIKE").append(values[i]);
                    if (i < values.length - 1) {
                        query.append("^OR");
                    }
                }
                query.append(")"); // end group
            }
        } else if (exclude) {
            // Exact exclusion for each value using !=
            for (String val : values) {
                query.append("^").append(field).append("!=").append(val);
            }
        }
    }

    private void sendMessage() {
        String userText = userPromptArea.getText().trim();
        if (userText.isEmpty()) return;

        chatArea.append("\nYou: " + userText + "\n");


        // AI API call
        new Thread(() -> {
            String aiResponse = callAI(userText);
            handleActions(aiResponse);
            SwingUtilities.invokeLater(() -> {
                chatArea.append("\nAI: " + aiResponse + "\n");
            });
        }).start();

        userPromptArea.setText("");
    }




    private static final OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey(dotenv.get("AIAPI_KEY"))
            .baseUrl("https://api.aimlapi.com/v1")
            .build();

    private String callAI(String prompt) {
        try {

            String context =
                    "You are an Inventory AI Agent that helps users search and filter inventory data. " +
                            "You communicate using special <ACTION> tags and operate through a Java Swing GUI that connects to a ServiceNow backend. " +
                            "The <ACTION> tags are not visible to the user. Only include them if they are needed to trigger behavior.\n\n" +

                            "Each <ACTION> represents a command. If the action is a filter, it updates a corresponding JTextField. " +
                            "When the <ACTION>data_return</ACTION> command is used, the agent presses a JButton in the UI by calling .doClick().\n\n" +

                            "Supported filter fields and their matching JTextFields (replace 'value' with the actual input):\n" +
                            "  filter_Asset_Tag::value → ServiceTagSEARCH\n" +
                            "  filter_vendor::value → VendorSEARCH\n" +
                            "  filter_Model::value → ModelSEARCH\n" +
                            "  filter_Item_Type::value → TypeSEARCH\n" +
                            "  filter_Department::value → DepartmentSEARCH\n" +
                            "  filter_Owner::value → OwnerSEARCH\n" +
                            "  filter_Warranty_Expiration::value → WarrantySEARCH (must match MM/DD/YYYY exactly)\n" +
                            "  filter_comments::value → commentsSEARCH (host name)\n\n" +

                            "Supported actions:\n" +
                            "  <ACTION>data_return</ACTION> — triggers filterResultsButton.doClick()\n" +
                            "  <ACTION>settings</ACTION> — triggers settingsButton.doClick()\n" +
                            "  <ACTION>reauthenticate</ACTION> — triggers reauthenticateButton.doClick()\n\n" +

                            "Rules:\n" +
                            "- Only exact string matches are supported\n" +
                            "- Warranty_Expiration must be an exact MM/DD/YYYY date (no 'soon' or 'before')\n" +
                            "- If multiple filters are used, apply them first, then use <ACTION>data_return</ACTION> to run the filter\n\n";



            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model("openai/gpt-4.1-nano-2025-04-14")
                    .temperature(0.7)
                    .topP(0.7)
                    .frequencyPenalty(1.0)
                    .maxTokens(512)
                    .addUserMessage(context+prompt)  // adds the user message cleanly
                    .build();
            //.topK(50)

            ChatCompletion completion = client
                    .chat()
                    .completions()
                    .create(params);

            return completion.choices()
                    .getFirst()
                    .message()
                    .content()
                    .orElse("[No content]");

        } catch (Exception e) {
            e.printStackTrace();
            return "[Error contacting AI: " + e.getMessage() + "]";
        }
    }




    public void handleActions(String input) {
        Matcher matcher = Pattern.compile("<ACTION>(.*?)</ACTION>").matcher(input);
        boolean shouldClick = false;

        while (matcher.find()) {
            String content = matcher.group(1).trim();

            if (content.startsWith("filter_") && content.contains("::")) {
                String[] parts = content.split("::", 2);
                String field = parts[0].substring(7).trim(); // remove "filter_"
                String value = parts[1].trim();

                System.out.printf("Setting filter field: %s = %s%n", field, value);

                if (field.equals("Asset_Tag")) {
                    ServiceTagSEARCH.setText(value);
                } else if (field.equals("vendor")) {
                    VendorSEARCH.setText(value);
                } else if (field.equals("Model")) {
                    ModelSEARCH.setText(value);
                } else if (field.equals("Item_Type")) {
                    TypeSEARCH.setText(value);
                } else if (field.equals("Department")) {
                    DepartmentSEARCH.setText(value);
                } else if (field.equals("Owner")) {
                    OwnerSEARCH.setText(value);
                } else if (field.equals("Warranty_Expiration")) {
                    WarrantySEARCH.setText(value);
                } else if (field.equals("comments")) {
                    commentsSEARCH.setText(value);
                } else {
                    System.out.println("Unknown filter field: " + field);
                }

            } else if (content.equals("settings")) {
                System.out.println("Opening settings...");
                settingsButton.doClick();

            } else if (content.equals("reauthenticate")) {
                System.out.println("Reauthenticating user...");
                reauthenticateButton.doClick();

            } else if (content.equals("data_return")) {
                shouldClick = true;

            } else {
                System.out.println("Unknown or unsupported action: " + content);
            }
        }

        if (shouldClick) {
            System.out.println("Executing filter...");
            filterResultsButton.doClick();
        }
    }



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

    private void fetchAndDisplayFilteredAssets(DefaultTableModel model, String user, String pass,String database,String unit,int limit) {
        model.setRowCount(0); // Clear existing table data

        // Build query string
        StringBuilder query = new StringBuilder("https://pennstate.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit="+limit+"&sysparm_query=");

        if(database.equals("Production"))
        {
            query = new StringBuilder("https://pennstate.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit="+limit+"&sysparm_query=");

        }
        else if(database.equals("Development"))
        {
            query = new StringBuilder("https://psudev.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit="+limit+"&sysparm_query=");
        }
        else if (database.equals("Accept"))
        {
            query = new StringBuilder("https://psuaccept.service-now.com/api/now/table/alm_asset?sysparm_display_value=true&sysparm_limit="+limit+"&sysparm_query=");
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


        if(unit!="All Units")
        {
            conditions.add("asset_tagSTARTSWITH" + unit);
        }

        query.append(String.join("^", conditions));
        String finalQuery = query.toString(); // Final query for use inside the worker
        System.out.println(finalQuery);
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

    private void DisplayFilteredAssets(DefaultTableModel model, String user, String pass,String url,String unit) {
        model.setRowCount(0); // Clear existing table data


        StringBuilder query = new StringBuilder(url);

        LinkedList<String> conditions = new LinkedList<>();

        if(unit!="All Units")
        {
            conditions.add("asset_tagSTARTSWITH" + unit);
        }

        query.append(String.join("^", conditions));
        String finalQuery = query.toString(); // Final query for use inside the worker
        System.out.println(finalQuery);
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