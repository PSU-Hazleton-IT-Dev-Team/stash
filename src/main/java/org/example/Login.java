package  org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends gui {
    private JPasswordField passwordField;
    private JPanel loginPanel;
    private JTextField usernameFeild;
    private JButton loginButton;
    private JLabel pleaseWaitLabel;

    public Login(JFrame frame) {
        super(frame);
        pleaseWaitLabel.setText(" ");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                pleaseWaitLabel.setText("Authenticating...");

                // Run the login operation in a background thread
                SwingWorker<Void, Void> worker = new SwingWorker<>()
                {
                    @Override
                    protected Void doInBackground() throws Exception
                    {
                        String pass = new String(passwordField.getPassword());
                        String name = usernameFeild.getText();

                        JFrame mainframe = new JFrame("PSU Stash Database");
                        maingui gui = new maingui(mainframe, name, pass);

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
                    protected void done()
                    {
                        pleaseWaitLabel.setText("Welcome!"); // Clear message when done (optional)
                    }
                };

                worker.execute();
            }
        });
    }

    public JPasswordField getPasswordField1() {
        return passwordField;
    }

    public JTextField getTextField1() {
        return usernameFeild;
    }

    public JPanel getPanel() {
        return loginPanel;
    }
}
