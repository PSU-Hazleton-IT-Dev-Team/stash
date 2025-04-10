package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class importgui extends gui {
    private JFileChooser fileChooser;
    private JButton addButton;
    private JTextArea formattedDescription;
    private JButton exitbutton;
    private JPanel importPanel;
    private JSpinner nameaccepted;
    private JSpinner manufactureraccepted;
    private JSpinner modelaccepted;
    private JSpinner servicetagaccepted;
    private JSpinner locationaccepted;
    private JSpinner ipaccepted;
    private JSpinner macaccepted;
    private JSpinner sheetSpinner;
    private JButton switchToLoanImportsButton;
    private JLabel l1;
    private JLabel l2;
    private JLabel l3;
    private JLabel l4;
    private JLabel l5;
    private JLabel l6;
    private JLabel l7;
    private JRadioButton splitLastNameFromRadioButton;
    private JComboBox ImporttypeCombo;


    public importgui(JFrame frame) {
        super(frame);
        Database db = new Database("No","No","no","no",frame);
        ImporttypeCombo.addItem("Items");
        ImporttypeCombo.addItem("Loans");
        ImporttypeCombo.addItem("Software");

        /*
        This massive function is the add button and will get everything from the spinner values
        to add to array lists
         */
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Variables for the Formatted Description
                String rawformatteddescription;

                //Excel File Master
                File excelFile = fileChooser.getSelectedFile();

                //Getting the selected file path
                String filePath = excelFile.getAbsolutePath();

                //Printing file path for debug
                System.out.println(filePath);

                //The Array Lists that contain the data gathered
                String[] data = new String[8];

                //Variables for spinner values
                ArrayList<Integer> spinners = new ArrayList<Integer>();
                Collections.addAll(spinners, (Integer) nameaccepted.getValue(),
                        (Integer) manufactureraccepted.getValue(), (Integer) modelaccepted.getValue(),
                        (Integer) servicetagaccepted.getValue(), (Integer) locationaccepted.getValue(),
                        (Integer) ipaccepted.getValue(), (Integer) macaccepted.getValue());

                Cell cell;
                Integer sheetspinner = (Integer) sheetSpinner.getValue();

                //Now Getting each Array list filled
                try {
                    //Obtaining the file streams
                    FileInputStream inputStream = new FileInputStream(filePath);
                    Workbook workbook = new XSSFWorkbook(inputStream);

                    //Selecting the sheet that is selected by the user
                    Sheet sheet = workbook.getSheetAt(sheetspinner);

                    //Temporary String for splitting Last Name
                    String tempstring;
                    int spaceindex;

                    //Item names columns
                    for (Row row : sheet) {
                        for(int i = 0; i < 7; i++)
                        {
                            if((cell = row.getCell(spinners.get(i))) != null)
                            {
                                if(i==1 && splitLastNameFromRadioButton.isSelected() && l1.getText().equals("PSU ID"))//If we are in the second entry of the Spinners (Or First Name) AND the user wants to split last names AND we are importing loans
                                {
                                    //Taking out the First name as an Additional Data
                                    tempstring = getCellContents(cell);
                                    spaceindex = tempstring.indexOf(' ');

                                    if(spaceindex>1) data[i] = tempstring.substring(0,spaceindex);

                                }
                                else if(i==2 && splitLastNameFromRadioButton.isSelected() && l1.getText().equals("PSU ID"))//If we are in the Third entry of the Spinners (Or Last Name) AND the user wants to split last names AND we are importing loans
                                {
                                    //Taking out the Last name as an Additional Data
                                    tempstring = getCellContents(cell);
                                    spaceindex = tempstring.indexOf(' ');
                                    if(spaceindex>1) data[i] = tempstring.substring(spaceindex+1);

                                }
                                else
                                {
                                    data[i] = getCellContents(cell);
                                }
                            }
                            else data[i] = "N/A";
                        }

                        //getting the text
                        rawformatteddescription = formattedDescription.getText();

                        //Getting the formatted text
                        data[7] = replaceQuotedNumbers(rawformatteddescription,row);
                        if(!data[0].equals("N/A"))
                        {
                            if(l1.getText().equals("PSU ID"))
                            {
                                db.executeQuery("INSERT INTO `loans` (`psuid`, `fname`, `lname`, `name`, " +
                                        "`loanoutdate`, `loanexpectdate`, `loanreturndate`, `description`) VALUES ('" + data[0] + "', '" + data[1] +
                                        "', '" + data[2] + "', '" + data[3] + "', '" + data[4] + "', '" + data[5] + "', '"
                                        + data[6] + "', '" + data[7] + "');");
                            }
                            else
                            {
                            db.executeQuery("INSERT INTO `items` (`name`, `manufactuer`, `model`, `servicetag`, " +
                                    "`location`, `ip`, `mac`, `description`) VALUES ('" + data[0] + "', '" + data[1] +
                                    "', '" + data[2] + "', '" + data[3] + "', '" + data[4] + "', '" + data[5] + "', '"
                                    + data[6] + "', '" + data[7] + "');");
                            }

                        }
                        System.out.println(data[0]);
                    }

                    //Closing the file Stream and the workbook once all data is gathered
                    inputStream.close();
                    workbook.close();
                } catch (Exception error) {
                    System.out.println(error.getMessage());
                }

                //sending a notification that the button worked
                JOptionPane.showMessageDialog(frame, "Inserted items: Refresh inventory page", "Insert Notification", JOptionPane.OK_CANCEL_OPTION);


            }
        });
        //exit button that just disposes the frame
        exitbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });

        ImporttypeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ImporttypeCombo.getSelectedItem().toString().equals("Loans"))
                {
                    l1.setText("PSU ID");
                    l2.setText("First Name");
                    l3.setText("Last Name");
                    l4.setText("Item Name");
                    l5.setText("Loan Out Date");
                    l6.setText("Loan Expect Return Date");
                    l7.setText("Loan Returned Date");
                    splitLastNameFromRadioButton.setVisible(true);
                }
                else if(ImporttypeCombo.getSelectedItem().toString().equals("Items"))
                {
                    l1.setText("Item Name");
                    l2.setText("Manufacturer");
                    l3.setText("Model");
                    l4.setText("Service Tag");
                    l5.setText("Location");
                    l6.setText("IP");
                    l7.setText("MAC");
                    splitLastNameFromRadioButton.setVisible(false);
                }
                else
                {
                    l1.setText("PSU ID");
                    l2.setText("First Name");
                    l3.setText("Last Name");
                    l4.setText("Item Name");
                    l2.setText("Manufacturer");
                    l6.setText("Software");
                    l7.setText("URL");
                    splitLastNameFromRadioButton.setVisible(true);
                }
            }
        });
    }


    public JPanel getPanel()
    {
        return this.importPanel;
    }


    //Regex Code segment
    public String replaceQuotedNumbers(String input, Row row)
    {
        // Define the pattern to match single-digit numbers in quotes
        Pattern pattern = Pattern.compile("'(\\d+\\.?\\d*)'|\"(\\d+\\.?\\d*)\"");
        Matcher matcher = pattern.matcher(input);

        // StringBuffer to store the result
        StringBuffer result = new StringBuffer();

        // Find and replace each match
        while (matcher.find()) 
        {
            String number = matcher.group(1);

            //Parsing the number from the matcher group
            int colnum;


            if (matcher.group(1) != null)
            {
                colnum = Integer.parseInt(matcher.group(1));
                matcher.appendReplacement(result, getDatafromRow(colnum,row));
            }
            else if (matcher.group(2) != null)
            {
                // Otherwise, it's a double-quoted number
                colnum = Integer.parseInt(matcher.group(2));
                matcher.appendReplacement(result, getDatafromRow(colnum, row));
            }
        }

        // Append the remaining part of the string
        matcher.appendTail(result);

        return result.toString();
    }

    public String getDatafromRow(int colnum, Row row)
    {
        //Excel File Master
        File excelFile = null;

        excelFile = fileChooser.getSelectedFile();

        //Getting the selected file path
        String filePath = excelFile.getAbsolutePath();

        //Printing file path for debug
        System.out.println(filePath);


        //Now Getting value of the row
        
        String out = null;
        
        try
        {
            //Obtaining the file streams
            FileInputStream inputStream = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(inputStream);

            //Selecting the sheet that is selected by the user
            Sheet sheet = workbook.getSheetAt((Integer) sheetSpinner.getValue());


            //Item names columns

                // Get cell from column names
                Cell cell = row.getCell(colnum);

                if (cell != null) return getCellContents(cell);

                    //Skipping over this entry if it is in fact null
        }
        catch(Exception z)
        {
            System.out.println(z.getMessage()); 
        }
        return "N/A";
    }

    public String getCellContents(Cell cell)
    {
        switch (cell.getCellType())
        {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Format the date as desired
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "N/A";
        }
    }
}

