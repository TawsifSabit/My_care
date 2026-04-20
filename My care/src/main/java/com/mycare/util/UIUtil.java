package com.mycare.util;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UIUtil {
    private static boolean initialized = false;
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PANEL_BORDER = new Color(220, 225, 230);
    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static void initModernTheme() {
        if (initialized) {
            return;
        }
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 999);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("TextComponent.borderWidth", 1);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.arc", 10);
            UIManager.put("Table.showGrid", true);
            UIManager.put("Table.gridColor", new Color(230, 230, 230));
            UIManager.put("Table.selectionBackground", new Color(194, 217, 255));
            UIManager.put("Table.selectionForeground", Color.BLACK);
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Button.font", DEFAULT_FONT);
            UIManager.put("Label.font", DEFAULT_FONT);
            UIManager.put("TextField.font", DEFAULT_FONT);
            UIManager.put("TextArea.font", DEFAULT_FONT);
            UIManager.put("ComboBox.font", DEFAULT_FONT);
            initialized = true;
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }

    public static void styleFrame(JFrame frame) {
        styleWindow(frame);
    }

    public static void styleWindow(Window window) {
        if (window instanceof JFrame) {
            ((JFrame) window).getContentPane().setBackground(BACKGROUND_COLOR);
        } else if (window instanceof JDialog) {
            ((JDialog) window).getContentPane().setBackground(BACKGROUND_COLOR);
        }
        window.setBackground(BACKGROUND_COLOR);
    }

    public static void styleCardPanel(JPanel panel) {
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_BORDER),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, new Color(25, 118, 210));
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, new Color(76, 175, 80));
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, new Color(211, 47, 47));
    }

    public static JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        styleButton(button, background, Color.WHITE);
        return button;
    }

    public static void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(Color.WHITE);
    }

    public static void styleTextArea(JTextArea area) {
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PANEL_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        area.setBackground(Color.WHITE);
    }

    public static void styleTable(JTable table) {
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(194, 217, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(230, 235, 240));
        table.getTableHeader().setForeground(new Color(55, 65, 75));
        table.getTableHeader().setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
    }
}
