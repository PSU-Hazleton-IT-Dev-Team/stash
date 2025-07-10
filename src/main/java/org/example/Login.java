package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends gui {
    private JPasswordField passwordField;
    private JPanel loginPanel;
    private JTextField usernameFeild;
    private JButton loginButton;
    private JLabel pleaseWaitLabel;
    private JComboBox envriomentOption;
    private JComboBox unitBox;
    private JRadioButton useAPIKeyRadioButton;
    private JLabel unamelabel;
    private JLabel pwordlabel;

    public Login(JFrame frame, int limit)
    {
        super(frame);
        pleaseWaitLabel.setText(" ");

        // Add environment options
        envriomentOption.addItem("Production");
        envriomentOption.addItem("Development");
        envriomentOption.addItem("Accept");

        // Add unit options
        unitBox.addItem("All Units");
        unitBox.addItem("H5");
        unitBox.addItem("W2");
        unitBox.addItem("W3");
        unitBox.addItem("I3");
        unitBox.addItem("F1");
        unitBox.addItem("T5");



        // Shared login action
        ActionListener loginAction = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                pleaseWaitLabel.setText("Authenticating...");

                // Run the login operation in a background thread
                SwingWorker<Void, Void> worker = new SwingWorker<>()
                {
                    @Override
                    protected Void doInBackground() {
                        String pass = new String(passwordField.getPassword());
                        String name = usernameFeild.getText();
                        String database = envriomentOption.getSelectedItem().toString();
                        String unit = unitBox.getSelectedItem().toString();

                        if (useAPIKeyRadioButton.isSelected())
                        {

                        }

                        JFrame mainframe = new JFrame("Stash");
                        maingui gui = new maingui(mainframe, name, pass, database,unit,limit);

                        SwingUtilities.invokeLater(() ->
                        {
                            mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                            mainframe.setContentPane(gui.getPanel());
                            mainframe.pack();
                            mainframe.setLocationRelativeTo(frame);
                            mainframe.setResizable(true);
                            mainframe.setVisible(true);
                            frame.dispose();
                        });

                        return null;
                    }

                    @Override
                    protected void done() {
                        pleaseWaitLabel.setText("Welcome!"); // Optional
                    }
                };

                worker.execute();
            }
        };

        // Attach to login button
        loginButton.addActionListener(loginAction);

        // Trigger login on Enter key press in username or password fields
        usernameFeild.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
        useAPIKeyRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (useAPIKeyRadioButton.isSelected())
                {
                    usernameFeild.setVisible(false);
                    passwordField.setVisible(false);
                    unamelabel.setVisible(false);
                    pwordlabel.setVisible(false);
                }
                else
                {
                    usernameFeild.setVisible(true);
                    passwordField.setVisible(true);
                    unamelabel.setVisible(true);
                    pwordlabel.setVisible(true);
                }

            }
        });
    }

    public JPasswordField getPasswordField1()
    {
        return passwordField;
    }

    public JTextField getTextField1()
    {
        return usernameFeild;
    }

    public JPanel getPanel()
    {
        return loginPanel;
    }
}
