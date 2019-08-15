package gui;

import byteremote.common.socket.ProtocolType;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import main.ApplicationCore;
import utils.log.Log;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/**
 * This is a frame with which the client interacts
 */
public class RemoteFrame extends JFrame
{
    /* Log purpose */
    private static final String TAG              = "Control";
    private static final String WAITING_CLIENT   = "Waiting for client...";
    private static final String CLIENT_CONNECTED = "Client connected";

    private static final Dimension PREFERRED_SIZE = new Dimension(450, 300);

    private JTextField passwordTextField;
    private JPanel     connectionPanel;
    private JTextField ipTextField;
    private JButton    controlButton;
    private JLabel     serverStatusLabel;
    private JLabel     protocolLabel;

    public RemoteFrame(String name)
    {
        super(name);

        this.initComponents();
    }

    /**
     * Init the components. Add elements to the frame and set the listeners
     */
    private void initComponents()
    {
        this.setContentPane(this.connectionPanel);
        this.connectionPanel.setPreferredSize(RemoteFrame.PREFERRED_SIZE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);

        showMachineIPAddress();
        generatePassword();

        controlButton.addActionListener(e -> {
            try
            {
                this.btnPressed();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Callback for a button
     */
    private void btnPressed()
    {
        if (! ApplicationCore.getInstance().isServerStarted())
        {
            Log.d(TAG, "btnPressed: start server");
            startBtnPressed();
        }
        else
        {
            Log.d(TAG, "btnPressed: stop server");
            stopBtnPressed();
        }
    }

    /**
     * Callback for the start button
     */
    private void startBtnPressed()
    {
        Object[] options = {"TCP",
                            "UDP"};
        int optionChoose = - 1;

        optionChoose = JOptionPane.showOptionDialog(this,
                "Which protocol should be used for connection?",
                "Start remote server",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,    /* do not use a custom Icon */
                options,       /* the titles of buttons */
                options[0]);   /* default button title */

        if (0 == optionChoose)
        {
            ApplicationCore.getInstance().startServer(ProtocolType.TCP);
        }
        else if (1 == optionChoose)
        {
            ApplicationCore.getInstance().startServer(ProtocolType.UDP);
        }
    }

    /**
     * Callback for the stop button
     */
    private void stopBtnPressed()
    {
        ApplicationCore.getInstance().stopServer();
    }

    /**
     * Get and show the machine host address
     */
    private void showMachineIPAddress()
    {
        try (final DatagramSocket socket = new DatagramSocket())
        {
            socket.connect(InetAddress.getByName("8.8.8.8"), 53);
            ipTextField.setText(socket.getLocalAddress().getHostAddress());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Generate a 4 characters password
     */
    private void generatePassword()
    {
        Random random = new Random();

        passwordTextField.setText("" + (1000 + random.nextInt(8999)));
    }

    /**
     * Callback for server started event
     *
     * @param type Type of the protocol used
     */
    public void serverStarted(ProtocolType type)
    {
        if (ProtocolType.TCP == type)
        {
            protocolLabel.setText("TCP");

        }
        else
        {
            protocolLabel.setText("UDP");
        }

        protocolLabel.setVisible(true);
        serverStatusLabel.setText(WAITING_CLIENT);
        serverStatusLabel.setVisible(true);
        controlButton.setText("Stop");
    }

    /**
     * Callback for server closed event
     */
    public void serverClosed()
    {
        protocolLabel.setVisible(false);
        serverStatusLabel.setVisible(false);
        controlButton.setText("Start");
    }

    /**
     * Callback for the client connected event
     */
    public void clientConnected()
    {
        serverStatusLabel.setText(CLIENT_CONNECTED);
    }

    /**
     * Callback for the client disconnected event
     */
    public void clientDisconnected()
    {
        serverStatusLabel.setText(WAITING_CLIENT);
    }

    /**
     * Get the generated password and convert it to int
     *
     * @return the converted password
     */
    public int getPassword()
    {
        try
        {
            return Integer.parseInt(passwordTextField.getText());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return - 1;
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /*
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        connectionPanel = new JPanel();
        connectionPanel.setLayout(new GridLayoutManager(9, 7, new Insets(0, 0, 0, 0), - 1, - 1));
        passwordTextField = new JTextField();
        passwordTextField.setEditable(false);
        connectionPanel.add(passwordTextField, new GridConstraints(5, 3, 1, 2, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, - 1), null, 0, false));
        ipTextField = new JTextField();
        ipTextField.setEditable(false);
        ipTextField.setEnabled(true);
        connectionPanel.add(ipTextField, new GridConstraints(3, 3, 1, 2, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, - 1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        connectionPanel.add(spacer1, new GridConstraints(2, 3, 1, 2, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        connectionPanel.add(spacer2, new GridConstraints(6, 3, 1, 2, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        connectionPanel.add(spacer3, new GridConstraints(4, 3, 1, 2, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        connectionPanel.add(spacer4, new GridConstraints(8, 3, 1, 2, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        connectionPanel.add(spacer5, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("IP");
        connectionPanel.add(label1, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_EAST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                new Dimension(53, 16), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("PW");
        connectionPanel.add(label2, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_EAST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                new Dimension(53, 16), null, 0, false));
        final Spacer spacer6 = new Spacer();
        connectionPanel.add(spacer6, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), - 1, - 1));
        connectionPanel.add(panel1, new GridConstraints(7, 3, 1, 2, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH, 1,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final Spacer spacer7 = new Spacer();
        panel1.add(spacer7, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel1.add(spacer8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel1.add(spacer9, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        controlButton = new JButton();
        controlButton.setText("Start");
        panel1.add(controlButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), - 1, - 1));
        connectionPanel.add(panel2, new GridConstraints(1, 0, 1, 7, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        serverStatusLabel = new JLabel();
        serverStatusLabel.setText("");
        serverStatusLabel.setVerifyInputWhenFocusTarget(true);
        serverStatusLabel.setVisible(false);
        panel2.add(serverStatusLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        protocolLabel = new JLabel();
        Font protocolLabelFont = this.$$$getFont$$$("Segoe UI", Font.BOLD, 16, protocolLabel.getFont());
        if (protocolLabelFont != null)
            protocolLabel.setFont(protocolLabelFont);
        protocolLabel.setText("");
        protocolLabel.setVisible(false);
        panel2.add(protocolLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        connectionPanel.add(spacer10, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        connectionPanel.add(spacer11, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer12 = new Spacer();
        connectionPanel.add(spacer12, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /* @noinspection ALL */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont)
    {
        if (currentFont == null)
            return null;
        String resultName;
        if (fontName == null) {resultName = currentFont.getName();}
        else
        {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {resultName = fontName;}
            else {resultName = currentFont.getName();}
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size :
                                                                                 currentFont.getSize());
    }

    /* @noinspection ALL */
    public JComponent $$$getRootComponent$$$() { return connectionPanel; }

}
