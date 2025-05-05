package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
    private JButton saveCommentsAndExitButton;
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

    public Details(JFrame frame, String user, String pass,String database,String unit,String Name)
    {
        super(frame);
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
                        owneroffice.setText(Owner);
                        ownercampus.setText(Owner);

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


    public JPanel getPanel() {
        return detailsPanel;
    }
}
