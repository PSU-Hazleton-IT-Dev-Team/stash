package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main
{



    public static void main(String[] args)
    {

        //Variables



        JFrame frame = new JFrame("Login");
        Login logingui = new Login(frame);



        //Setting Look and Feel
        try
        {
            //Loading the Theme from settings
            String filePath="settings.txt";
            int storedtheme = 0;

            // Read from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
            {
                String line;
                int currentlinenumber=0;
                int linenumber=1;
                while ((line = reader.readLine()) != null)
                {
                    currentlinenumber++;
                    if (currentlinenumber == linenumber)
                    {
                        storedtheme = Integer.parseInt(line);
                    }
                }
            }
            catch (Exception e)
            {
                System.err.println("Error reading from the file: " + e.getMessage());
            }

            Class<?> theme = Class.forName(gui.getThemes()[storedtheme].getClassName());
            UIManager.setLookAndFeel((LookAndFeel) theme.getDeclaredConstructor().newInstance());

        }
        catch(Exception e)
        {
            System.out.println("UI ERROR");
        }

        //Logic I/OS
        frame = new JFrame("Login");
        Image icon = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/iconlogo.png"));
        frame.setIconImage(icon);


        logingui= new Login(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(logingui.getPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);


    }// end of function 'main'

}// end of class 'Main.java'
