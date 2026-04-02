package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public final class AppDialogs {

    private static final Font TITLE_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 16);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BG = new Color(28, 31, 38);
    private static final Color FG = new Color(238, 241, 245);
    private static final Color BTN_BG = new Color(64, 122, 214);
    private static final Color BTN_FG = Color.WHITE;
    private static boolean initialized;

    private AppDialogs() {}

    public static void showMessageDialog(Component parent, String message) {
        showMessageDialog(parent, message, "Notice", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showMessageDialog(Component parent, String message, String title, int messageType) {
        applyThemeOnce();
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION);
        pane.setBorder(new EmptyBorder(14, 18, 10, 18));
        JDialog dialog = pane.createDialog(parent, title == null ? "Notice" : title);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setVisible(true);
        dialog.dispose();
    }

    public static int showConfirmDialog(Component parent, String message) {
        return showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public static int showConfirmDialog(Component parent, String message, String title, int optionType, int messageType) {
        applyThemeOnce();
        JOptionPane pane = new JOptionPane(message, messageType, optionType);
        pane.setBorder(new EmptyBorder(14, 18, 10, 18));
        JDialog dialog = pane.createDialog(parent, title == null ? "Confirm" : title);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setVisible(true);
        Object value = pane.getValue();
        dialog.dispose();
        return mapConfirmValue(value);
    }

    public static String showInputDialog(Component parent, String message, String title) {
        applyThemeOnce();
        JTextField input = new JTextField(20);
        input.setFont(BODY_FONT);

        Object[] content = new Object[] { message, input };
        JOptionPane pane = new JOptionPane(content, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        pane.setBorder(new EmptyBorder(14, 18, 10, 18));

        JDialog dialog = pane.createDialog(parent, title == null ? "Input" : title);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setVisible(true);

        Object value = pane.getValue();
        dialog.dispose();

        if (!(value instanceof Integer i) || i != JOptionPane.OK_OPTION) {
            return null;
        }
        return input.getText();
    }

    private static int mapConfirmValue(Object value) {
        if (value instanceof Integer i) return i;
        return JOptionPane.CLOSED_OPTION;
    }

    private static void applyThemeOnce() {
        if (initialized) return;
        initialized = true;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }

        UIManager.put("OptionPane.background", BG);
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.messageForeground", FG);
        UIManager.put("OptionPane.messageFont", BODY_FONT);
        UIManager.put("OptionPane.buttonFont", BODY_FONT);
        UIManager.put("OptionPane.border", BorderFactory.createLineBorder(new Color(84, 91, 104), 1));

        UIManager.put("Button.background", BTN_BG);
        UIManager.put("Button.foreground", BTN_FG);
        UIManager.put("Button.select", BTN_BG.darker());
        UIManager.put("Button.focus", BTN_BG);
        UIManager.put("Button.font", BODY_FONT);
        UIManager.put("Button.margin", new java.awt.Insets(7, 16, 7, 16));

        UIManager.put("Label.foreground", FG);
        UIManager.put("Label.font", BODY_FONT);
        UIManager.put("OptionPane.messageLabel", new JLabel() {
            {
                setForeground(FG);
                setFont(TITLE_FONT);
            }
        });
    }
}
