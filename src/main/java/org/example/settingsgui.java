package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import io.github.cdimascio.dotenv.Dotenv;

public class settingsgui extends gui {
    private JButton saveandexitButton;
    private JPanel settingsPanel;
    private JComboBox ThemeBox;
    private JLabel Theme;
    private JTextField tempipbox;
    private JRadioButton useTemporaryConnectionRadioButton;
    private JButton exitButton;
    private JTextField usernamebox;
    private JComboBox symbolsBox;


    public settingsgui(JFrame frame, JFrame parentframe,String username, String password, String database) {
        super(frame);
        //Setting up Theme ComboBox
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo theme : this.getThemes()) {
            System.out.println("Theme ID: " + theme.getName());
            ThemeBox.addItem(theme.getName());
        }

        saveandexitButton.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e)
            {



                //Getting the Selected item for theme
                int selection = ThemeBox.getSelectedIndex();



                try
                {
                    Class<?> theme = Class.forName(gui.getThemes()[selection].getClassName());
                    UIManager.setLookAndFeel((LookAndFeel) theme.getDeclaredConstructor().newInstance());
                    // Specify the file path
                    String filePath = "settings.txt";

                    // Write to the file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))
                    {
                        // Write content to the file
                        writer.write(String.valueOf(selection));

                    }
                    catch (IOException s)
                    {
                        System.err.println("Error writing to the file: " + s.getMessage());
                    }

                    //Disposing maingui because it is wrong theme
                    parentframe.dispose();

                    //Creating the updated Jframe
                    JFrame update= new JFrame("PSU Stash Database");

                    //Reloading UI
                    maingui gui = new maingui(update,username,password,database);
                    gui.setup_frame(3, gui.getPanel(), parentframe);
                }
                catch(Exception a)
                {
                    System.out.println("error");
                }
                //Switching between the themes and reloading
                //Closing settings window
                frame.dispose();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Dispose with no action
                frame.dispose();
            }
        });
    }

    public JPanel getPanel() {
        return this.settingsPanel;
    }
}
