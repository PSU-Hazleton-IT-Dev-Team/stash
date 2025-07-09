package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class Details extends gui {
    private JTextField name;
    private JTextField tag;
    private JTextField vendor;
    private JTextField model;
    private JTextField department;
    private JTextField type;
    private JTextField warranty;
    private JTextPane comments;
    private JButton exitButton;
    private JTextField createdon;
    private JTextField stock;
    private JTextField cost;
    private JTextField updatedby;
    private JTextField disposalreason;
    private JTextField createdby;
    private JTextField depricationamnt;
    private JButton iButton;
    private JPanel detailsPanel;
    private JTextField updatedon;
    private JTextField owner;
    private JTextField owneroffice;
    private JTextField ownercampus;
    private JButton saveCommentsButton;
    private JButton saveCommentsAndExitButton;

    public Details(JFrame frame, String user, String pass,String database,String unit,String Name)
    {

        super(frame);
        String assetID=null;
        try {

            String queryUrl = "";
            if(database.equals("Production"))
            {
                queryUrl = "https://pennstate.service-now.com/api/now/table/alm_asset?sysparm_limit=1&sysparm_display_value=true";
            }
            else if(database.equals("Development"))
            {
                queryUrl = "https://psudev.service-now.com/api/now/table/alm_asset?sysparm_limit=1&sysparm_display_value=true";
            }
            else if (database.equals("Accept"))
            {
                queryUrl = "https://psuaccept.service-now.com/api/now/table/alm_asset?sysparm_limit=1&sysparm_display_value=true";
            }

                queryUrl+=("&sysparm_query=asset_tag="  + Name);



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

                      assetID=getTagValue("sys_id",el);


                        String ServiceTag = getTagValue("serial_number", el);

                        String assetTag = getTagValue("asset_tag", el);
                        String Comments =  getTagValue("comments", el);
                        String Warranty = getTagValue("warranty_expiration", el);
                        String Vendor = getNestedTagValue("vendor", "display_value", el);
                        String Model = getNestedTagValue("model", "display_value", el);
                        String Department = getNestedTagValue("department", "display_value", el);
                        String ItemType = getNestedTagValue("model_category", "display_value", el);
                        String Owner = getNestedTagValue("assigned_to", "display_value", el);
                        String CreatedBy = getTagValue("sys_created_by",el);
                        String CreatedOn = getTagValue("sys_created_on",el);
                        String UpdatedBy = getTagValue("sys_updated_by",el);
                        String UpdatedOn =getTagValue("sys_updated_on",el);
                        String Cost=getTagValue("cost", el);
                        String StockRoom=getNestedTagValue("stock_room", "display_value", el);
                        String DepricationAmnt=getTagValue("depreciated_amount",el);

                        String linkedPerson = getNestedTagValue("assigned_to", "link", el);
                        String Campus = fetchLinkedField(linkedPerson,user,pass,"u_employee_campus_code");
                        String Office = fetchLinkedField(linkedPerson, user,pass,"u_office_address");


                        name.setText(assetTag);
                        tag.setText(ServiceTag);
                        vendor.setText(Vendor);
                        model.setText(Model);
                        department.setText(Department);
                        type.setText(ItemType);
                        warranty.setText(Warranty);
                        comments.setText(Comments);
                        createdby.setText(CreatedBy);
                        updatedby.setText(UpdatedBy);
                        updatedon.setText(UpdatedOn);
                        cost.setText(Cost);
                        stock.setText(StockRoom);
                        depricationamnt.setText(DepricationAmnt);
                        createdon.setText(CreatedOn);
                        owner.setText(Owner);
                        owneroffice.setText(Office);
                        ownercampus.setText(Campus);
                        //disposalreason.setText(Cost);

                        name.setEditable(false);
                        tag.setEditable(false);
                        vendor.setEditable(false);
                        model.setEditable(false);
                        department.setEditable(false);
                        type.setEditable(false);
                        warranty.setEditable(false);
                        comments.setEditable(true);
                        createdby.setEditable(false);
                        updatedby.setEditable(false);
                        updatedon.setEditable(false);
                        cost.setEditable(false);
                        stock.setEditable(false);
                        depricationamnt.setEditable(false);
                        createdon.setEditable(false);
                        owner.setEditable(false);
                        owneroffice.setEditable(false);
                        ownercampus.setEditable(false);
                        disposalreason.setEditable(false);

                        owneroffice.setCaretPosition(0);

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

        String finalAssetID = assetID;

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        saveCommentsAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (finalAssetID != null) {
                    String newComment = comments.getText();
                    updateCommentsField(finalAssetID, newComment, user, pass, database);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Asset sys_id not available.");
                }
            }
        });

        saveCommentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (finalAssetID != null) {
                    String newComment = comments.getText();
                    updateCommentsField(finalAssetID, newComment, user, pass, database);
                } else {
                    JOptionPane.showMessageDialog(frame, "Asset sys_id not available.");
                }
            }
        });
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
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getTextContent() != null) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }

    private static String fetchLinkedField(String url, String user, String pass, String tagName) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            // Basic Auth
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

                // Fetch <result><tagName>Value</tagName></result>
                NodeList resultNodes = doc.getElementsByTagName("result");
                if (resultNodes.getLength() > 0) {
                    Element resultEl = (Element) resultNodes.item(0);
                    NodeList targetNodes = resultEl.getElementsByTagName(tagName);
                    if (targetNodes.getLength() > 0 && targetNodes.item(0).getTextContent() != null) {
                        return targetNodes.item(0).getTextContent().trim();
                    }
                }
            } else {
                System.err.println("Linked field fetch failed: HTTP " + responseCode + " - " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void updateCommentsField(String sysId, String newComment, String user, String pass, String database) {
        try {
            String instanceUrl = switch (database) {
                case "Production" -> "https://pennstate.service-now.com";
                case "Development" -> "https://psudev.service-now.com";
                case "Accept" -> "https://psuaccept.service-now.com";
                default -> throw new IllegalArgumentException("Unknown instance");
            };

            URL url = new URL(instanceUrl + "/api/now/table/alm_asset/" + sysId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            String basicAuth = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);

            conn.setDoOutput(true);
            String json = "{\"comments\": \"" + newComment.replace("\"", "\\\"") + "\"}";
            conn.getOutputStream().write(json.getBytes());

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                JOptionPane.showMessageDialog(null, "Comments updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update comments. HTTP " + responseCode + ": " + conn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating comments: " + e.getMessage());
        }
    }



    public JPanel getPanel() {
        return detailsPanel;
    }
}
