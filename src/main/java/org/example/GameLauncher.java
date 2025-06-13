package org.example;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.xml.parsers.*;

import org.example.client.AuthClient;
import org.example.client.LoginResponse;
import org.example.client.RequestClient;
import org.example.client.SocketService;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class GameLauncher extends JFrame implements ActionListener {

    private JButton loginBtn = new JButton("Login");
    private JButton joinBtn = new JButton("Join");
    private JButton myGamesBtn = new JButton("My Games");
    private JButton playBtn = new JButton("Spielen / tmp");

    private String userName = null;
    private String userPasswd = null;
    private String hostAdress = null;

    RequestClient requestClient = null;
    LoginResponse loginResponse = null;

    GameLauncher gl = this;

    public GameLauncher(String hostAdress) {
        super("Game Launcher");
        this.hostAdress = hostAdress;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 8, 8));

        add(loginBtn);
        add(joinBtn);
        add(myGamesBtn);
        add(playBtn);

        loginBtn.addActionListener(this);
        joinBtn.addActionListener(this);
        myGamesBtn.addActionListener(this);
        playBtn.addActionListener(this);

        joinBtn.setEnabled(false);
        myGamesBtn.setEnabled(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            new LoginDialog(this).setVisible(true);

        } else if (e.getSource() == joinBtn) { //zeigt offene Spiele an
            Document doc = null;
            try {
                doc = requestClient.requestXml("/api/xml/getopengames");
                System.out.println(doc.getXmlVersion());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            new XmlListFrame(this, "test.xml", false, doc).setVisible(true);

        } else if (e.getSource() == myGamesBtn) {
            return;
            //new XmlListFrame(this, "test.xml", false, null).setVisible(true);
        } else if (e.getSource() == playBtn) {
            /*try {
                requestClient.sendJoinRequest(1);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }*/

            try {
                dispose();
                SocketService socketService = new SocketService(1);
                socketService.connectAndListen(loginResponse);
                Runtime.getRuntime().addShutdownHook(new Thread(socketService::onExit));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Dialog für Benutzername / Passwort
     */
    private class LoginDialog extends JDialog implements ActionListener {
        private JTextField userField = new JTextField(15);
        private JPasswordField passField = new JPasswordField(15);
        private JButton ok = new JButton("OK");
        private JButton cancel = new JButton("Abbrechen");

        LoginDialog(JFrame parent) {
            super(parent, "Login", true);
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 4, 4, 4);
            c.anchor = GridBagConstraints.WEST;

            c.gridx = 0;
            c.gridy = 0;
            add(new JLabel("Username:"), c);
            c.gridx = 1;
            add(userField, c);
            c.gridx = 0;
            c.gridy = 1;
            add(new JLabel("Password:"), c);
            c.gridx = 1;
            add(passField, c);

            JPanel btnPanel = new JPanel();
            btnPanel.add(ok);
            btnPanel.add(cancel);
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            c.anchor = GridBagConstraints.CENTER;
            add(btnPanel, c);

            ok.addActionListener(this);
            cancel.addActionListener(this);
            pack();
            setLocationRelativeTo(parent);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == ok) { // login performen
                userName = userField.getText().trim();
                userPasswd = String.valueOf(passField.getPassword());
                System.out.println(userPasswd + " " + userName);

                AuthClient loginService = new AuthClient();
                try {

                    loginResponse = loginService.logIntoServer(userName, userPasswd, hostAdress, 8443);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    return;
                }

                if (loginResponse.isAuthFailed()) {
                    JOptionPane.showMessageDialog(GameLauncher.this, "Login fehlgeschlagen!");
                } else {
                    dispose();
                    JOptionPane.showMessageDialog(GameLauncher.this, "Willkommen, " + userName + "!");
                    gl.setTitle("GL - " + userName);
                    joinBtn.setEnabled(true);
                    myGamesBtn.setEnabled(true);
                    requestClient = new RequestClient(loginResponse, "https://" + hostAdress + ":" + "8443");
                }
            } else {
                dispose();
            }
        }
    }

    /**
     * Generic frame that shows the contents of an XML file (<games><game name="..."/></games>)
     */
    private static class XmlListFrame extends JFrame implements ActionListener, ListSelectionListener {

        private final DefaultListModel<String> model = new DefaultListModel<>();
        private final JList<String> list = new JList<>(model);
        private final JButton accept = new JButton("Akzeptieren");
        private final JButton delete = new JButton("Löschen");
        private final JButton close = new JButton("Schließen");
        private Document d = null;

        private final String filePath;
        private final boolean allowDelete;

        XmlListFrame(JFrame parent, String filePath, boolean allowDelete, Document d) {
            super(allowDelete ? "My Games" : "Join Spieleliste");
            this.filePath = filePath;
            this.allowDelete = allowDelete;
            this.d = d;

            loadXml();

            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addListSelectionListener(this);

            JScrollPane scroll = new JScrollPane(list);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(accept);
            if (allowDelete) buttonPanel.add(delete);
            buttonPanel.add(close);

            accept.setEnabled(false);
            delete.setEnabled(false);

            accept.addActionListener(this);
            delete.addActionListener(this);
            close.addActionListener(e -> dispose());

            setLayout(new BorderLayout(8, 8));
            add(scroll, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            setSize(300, 300);
            setLocationRelativeTo(parent);
        }

        private void loadXml() {
            model.clear();
            File f = new File(filePath);
            if (!f.exists()) return;
            try {
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = db.parse(f);
                for (Node node : iterable(d.getElementsByTagName("game"))) {
                    Element el = (Element) node;
                    model.addElement("Spiel von " + el.getElementsByTagName("owner").item(0).getTextContent() + " erstellt am " + el.getElementsByTagName("lastModified").item(0).getTextContent());
                }
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                ex.printStackTrace();
            }
        }

        private void saveXml() {
            try {
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = db.newDocument();
                Element root = doc.createElement("games");
                doc.appendChild(root);
                for (int i = 0; i < model.size(); i++) {
                    Element g = doc.createElement("game");
                    g.setAttribute("name", model.get(i));
                    root.appendChild(g);
                }

                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == accept) {
                String game = list.getSelectedValue();
                //todo: hier stuff machen
                JOptionPane.showMessageDialog(this, (allowDelete ? "Spiel öffnen: " : "Beitreten zu: ") + game);
            } else if (e.getSource() == delete && allowDelete) {
                int idx = list.getSelectedIndex();
                if (idx >= 0) {
                    model.remove(idx);
                    saveXml();
                }
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            boolean sel = !list.isSelectionEmpty();
            accept.setEnabled(sel);
            if (allowDelete) delete.setEnabled(sel);
        }

        /**
         * Helper to iterate NodeList with foreach
         */
        private static Iterable<Node> iterable(final NodeList nl) {
            return () -> new java.util.Iterator<Node>() {
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < nl.getLength();
                }

                @Override
                public Node next() {
                    return nl.item(i++);
                }
            };
        }
    }

    public static void main(String[] args) {
        String hostAdress = IntInputDialog.getInputWithMessage("die ip des servers: ");
        SwingUtilities.invokeLater(() -> new GameLauncher(hostAdress).setVisible(true));
    }
}


