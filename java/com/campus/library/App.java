package com.campus.library;

import com.campus.library.ui.MainFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}
/*
做一些细微的更改
接下来一次更改


sdfsaf 

sdfsafasf


sdfsd
*/