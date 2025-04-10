package  org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends gui {
    private JPasswordField passwordField1;
    private JPanel panel1;
    private JTextField textField1;
    private JButton loginButton;
    private JLabel pleaseWaitLabel;

    public Login(JFrame frame) {
        super(frame);
        pleaseWaitLabel.setVisible(false);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pleaseWaitLabel.setVisible(true);
                String pass = new String(passwordField1.getPassword()); // Get password correctly
                String name = textField1.getText(); // Get username or name


                JFrame mainframe = new JFrame("PSU Stash Database");
                maingui gui = new maingui(mainframe,name,pass);
                gui = new maingui(mainframe,name,pass);


                mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                mainframe.setContentPane(gui.getPanel());
                mainframe.pack();
                mainframe.setLocationRelativeTo(frame);
                mainframe.setResizable(true);
                mainframe.setVisible(true);
                frame.dispose();

            }
        });
    }

    public JPasswordField getPasswordField1() {
        return passwordField1;
    }

    public JTextField getTextField1() {
        return textField1;
    }

    public JPanel getPanel() {
        return panel1;
    }
}
