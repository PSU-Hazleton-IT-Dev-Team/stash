package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckoutMenu extends gui {
    private JTextField itemNameField;
    private JPanel panel1;
    private JTextField psuIDField;
    private JTextField fnameField;
    private JTextField lnameField;
    private JTextField returnDateField;
    private JButton checkOutButton;
    private JButton exitButton;

    public CheckoutMenu(JFrame frame, JFrame parentframe) {
        super(frame);

        //Exit Button Action Listener
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }

        });

        //Checkout Button Action Listener
        checkOutButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {



                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();


                String itemname=itemNameField.getText();
                String psuid=psuIDField.getText();
                String fname=fnameField.getText();
                String lname=lnameField.getText();
                String currentdate=dateFormat.format(date);
                String returndate=returnDateField.getText();


                try
                {
                    //db.executeQuery("INSERT INTO `loans` (`psuid`, `fname`, `lname`, `name`, `loanoutdate`, `loanexpectdate`, `loanreturndate`, `description`) VALUES ('" + psuid + "', '" + fname + "', '" + lname + "', '" + itemname + "', '" + currentdate + "', '" + returndate + "', '" + "n/a" + "', '" + "n/a" + "');");
                    JOptionPane.showMessageDialog(frame, "Loan Made! : Refresh loans page", "Insert Notification", JOptionPane.OK_CANCEL_OPTION);
                }
                catch(Exception v)
                {
                    JOptionPane.showMessageDialog(frame, "Error: Unable to insert SQL", "Error", JOptionPane.ERROR_MESSAGE);
                }

                itemNameField.setText("");
                psuIDField.setText("");
                fnameField.setText("");
                lnameField.setText("");
                returnDateField.setText("");



            }
        });
    }


    public JPanel getPanel() {
        return this.panel1;
    }
}
