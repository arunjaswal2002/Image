import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class ImageOperation {
    private static JProgressBar progressBar;
    private static boolean darkMode = false;
    private static JFrame f;

    public static void operate(int key, File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[fis.available()];
            fis.read(data);

            SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    int i = 0;
                    for (byte b : data) {
                        data[i] = (byte) (b ^ key);
                        i++;
                        publish((int) ((i / (double) data.length) * 100));
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.close();
                    return null;
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    int progress = chunks.get(chunks.size() - 1);
                    progressBar.setValue(progress);
                }

                @Override
                protected void done() {
                    progressBar.setValue(100);
                    JOptionPane.showMessageDialog(null, "Done! Check your selected image path!");
                }
            };

            worker.execute();

            fis.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error occurred! Please try again.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        f = new JFrame();
        f.setTitle("Image Encryption/Decryption Tool");
        f.setSize(500, 250);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font font = new Font("Roboto", Font.ITALIC, 18);

        JButton button1 = new JButton("Enter Key To Encrypt/Decrypt Image");
        button1.setFont(font);
        button1.setBackground(new Color(70, 130, 180));

        JTextField textField = new JTextField(15);
        textField.setFont(font);

        JButton button = new JButton("Select Image");
        button.setFont(font);
        button.setBackground(new Color(50, 205, 50));

        Label l1, l2;
        l1 = new Label("To Decrypt image, give the same key as given during Encryption!");
        l2 = new Label("And select the same image!");

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JButton toggleDarkModeButton = new JButton("Toggle Dark Mode");
        toggleDarkModeButton.addActionListener(e -> toggleDarkMode());

        button.addActionListener(e -> {
            String text = textField.getText();
            int temp = Integer.parseInt(text);
            operate(temp, showFileChooser());
        });

        // Use GridBagLayout for better control over component placement
        f.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        f.getContentPane().setBackground(darkMode ? Color.DARK_GRAY : new Color(255, 250, 240));
        f.add(button1, gbc);

        gbc.gridy = 1;
        f.add(textField, gbc);

        gbc.gridy = 2;
        f.add(button, gbc);

        gbc.gridy = 3;
        f.add(l1, gbc);

        gbc.gridy = 4;
        f.add(l2, gbc);

        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        f.add(progressBar, gbc);

        gbc.gridy = 6;
        f.add(toggleDarkModeButton, gbc);

        f.setVisible(true);
    }

    private static File showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        return fileChooser.getSelectedFile();
    }

    private static void toggleDarkMode() {
        darkMode = !darkMode;
        updateDarkMode();
    }

    private static void updateDarkMode() {
        Color backgroundColor = darkMode ? Color.DARK_GRAY : new Color(255, 250, 240);
        f.getContentPane().setBackground(backgroundColor);
    }
}
