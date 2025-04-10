package org.example;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.ArrayList;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;

public class gui {

    protected JFrame frame;
    protected static FlatAllIJThemes.FlatIJLookAndFeelInfo[] themes;

    //Getting the db connected
    public gui(JFrame frame)
    {
        this.frame = frame;
        this.themes = FlatAllIJThemes.INFOS;
        System.out.println(themes[1].getClassName());
    }

    public static void clear(Container[] c, String[] input)
    {
        if(c instanceof JTextField[])
        {
            for(Container field : c) ((JTextField) field).setText("");
        }
        else if (c instanceof JComboBox<?>[]) {
            for(Container combobox : c) ((JComboBox<?>) combobox).removeAllItems();
        }
    }

    public void setup_frame(int close_op, JPanel panel)
    {
        this.frame.setDefaultCloseOperation(close_op);
        this.frame.setContentPane(panel);
        this.frame.pack();
        this.frame.setLocationRelativeTo(this.frame);
        this.frame.setResizable(true);
        this.frame.setVisible(true);
    }

    public void setup_frame(int close_op, JPanel panel, JFrame referenceFrame)
    {
        this.frame.setDefaultCloseOperation(close_op);
        this.frame.setContentPane(panel);
        this.frame.pack();
        this.frame.setLocationRelativeTo(referenceFrame);
        this.frame.setResizable(true);
        this.frame.setVisible(true);
    }

    public static FlatAllIJThemes.FlatIJLookAndFeelInfo[] getThemes() { return themes; }

}
