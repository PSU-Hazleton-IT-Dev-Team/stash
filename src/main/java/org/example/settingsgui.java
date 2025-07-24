package org.example;

import com.formdev.flatlaf.intellijthemes.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class settingsgui extends gui {
    private JButton saveandexitButton;
    private JPanel settingsPanel;
    private JComboBox ThemeBox;
    private JLabel Theme;
    private JButton exitButton;
    private JTextField ListLimitFeild;
    private JComboBox agentSelection;


    public settingsgui(JFrame frame, JFrame parentframe,String username, String password, String database,String unit) {
        super(frame);
        String filePath = "settings.txt";
        int savedIndex = 0;
        int savedLimit=0;
        //Setting up Theme ComboBox
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo theme : this.getThemes()) {
            System.out.println("Theme ID: " + theme.getName());
            ThemeBox.addItem(theme.getName());

            //Setting Up agents Box
            agentSelection.addItem("Chat GPT 4.1 nano");
            agentSelection.addItem("Chat GPT 4.1 mini");
            agentSelection.addItem("Gemma 3 27B");
            agentSelection.addItem("llama 4 Scout");



            try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
            {
                String line = reader.readLine();
                String line2 = reader.readLine();
                if (line != null)
                {
                    savedIndex = Integer.parseInt(line.trim());
                }
                if (line2 != null)
                {
                    savedLimit = Integer.parseInt(line2.trim());
                }
                ListLimitFeild.setText(String.valueOf(savedLimit));
            }
            catch (FileNotFoundException e)
            {
                System.err.println("Settings file not found: " + e.getMessage());
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid number format in settings file: " + e.getMessage());
            }
            catch (IOException e)
            {
                System.err.println("Error reading from the file: " + e.getMessage());
            }


            if (savedIndex >= 0 && savedIndex < ThemeBox.getItemCount())
            {
                ThemeBox.setSelectedIndex(savedIndex);
            } else
            {
                ThemeBox.setSelectedIndex(0); // Set to default if invalid
            }

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
                        writer.newLine();
                        writer.write(ListLimitFeild.getText());

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
                    maingui gui = new maingui(update,username,password,database,unit, Integer.parseInt(ListLimitFeild.getText()));
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
