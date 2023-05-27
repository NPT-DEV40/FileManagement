import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InformationForm extends JFrame {
    private JButton acceptButton;
    private JButton declineButton;
    private JLabel informationLabel;

    public InformationForm() {
        setTitle("Information Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        // Create the buttons
        acceptButton = new JButton("Accept");
        declineButton = new JButton("Decline");

        // Create the label
        informationLabel = new JLabel("Client requesting to create file");
        informationLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create the panel to hold the components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        // Add components to the panel
        panel.add(acceptButton);
        panel.add(declineButton);

        // Create another panel for label and button panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(informationLabel, BorderLayout.CENTER);
        mainPanel.add(panel, BorderLayout.SOUTH);

        // Add main panel to the frame
        add(mainPanel);

        // Add action listeners to the buttons
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String information = informationLabel.getText();
                JOptionPane.showMessageDialog(null, "Accepted: " + information);
            }
        });

        declineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String information = informationLabel.getText();
                JOptionPane.showMessageDialog(null, "Declined: " + information);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                InformationForm form = new InformationForm();
                form.setVisible(true);
            }
        });
    }
}
