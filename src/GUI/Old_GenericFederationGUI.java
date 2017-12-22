package GUI;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author abbas
 */
import CommonFnc.GUIFunctions;
import Data.HLAData.ObjClassandAttributes;
import Data.HLAData.PubSubAttribute;
import javax.swing.JFrame;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import CommonFnc.JsonParser;
import Data.Agent.Federation;
import Interfaces.DataControllerInterface;
import Interfaces.HLACommToDataInterface;
import Interfaces.HLAControlInterface;
import Interfaces.HLASendReceiveInterface;
import Interfaces.VisualInterface;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Old_GenericFederationGUI extends JFrame implements Observer {

    private VisualInterface visController;
    private HLASendReceiveInterface hlacommController;
    private HLACommToDataInterface hladataController;
    private DataControllerInterface dataController;
    private HLAControlInterface hlasmngController;
    HashMap<String, Object> hmap = new HashMap<>();
    Random randomGenerator = new Random();
    mxGraph graph = new mxGraph();
    mxGraph graph_s = new mxGraph();
    JTextArea filenm = new JTextArea();

    JTextField federationTf;
    JTextField federateTf;
    JTextField publishMessage;
    JTextField attrValTf;
    JTabbedPane tabPane;
    DefaultListModel attrmodel;
    private JComboBox<Object> objectClassesBox;
    private JList<String> attributesList;

    private JButton connect;
    private JButton runBtn;
    private JButton updateAttrBtn;
    private JButton sendInBtn;
    private JTextField statusTexf;
    private JButton sendNewGraphbtn, sendOldGraphbtn;

    private mxGraphComponent graphComponent;
    private mxGraphComponent statusGraph;

    /**
     *
     */

    public Old_GenericFederationGUI() {
        super("Federation Dependency Graph");
        CreateGUI();

    }

    private void UpdateStatus() {
    //statecheckb.setSelected(visController.getAgentPerformanceBol());
        //statusTexf.setText("Status is:   "+String.valueOf(visController.getAgentPerformance()));
    }

    public Old_GenericFederationGUI(DataControllerInterface dataController_a, VisualInterface datacontroller_a, HLASendReceiveInterface hlain, HLACommToDataInterface hladata, HLAControlInterface hlasmngController_a) {

        super("Federation Dependency Graph");
        visController = datacontroller_a;
        hlacommController = hlain;
        hladataController = hladata;
        dataController = dataController_a;
        hlasmngController = hlasmngController_a;
        mxStylesheet stylesheet = graph.getStylesheet();
        GUIFunctions.makeANDStyle(stylesheet);
        GUIFunctions.makeNodeStyle(stylesheet);
        GUIFunctions.makeORStyle(stylesheet);
        GUIFunctions.makeEdgeStyle(stylesheet);

        graph_s.setStylesheet(stylesheet);

        CreateGUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setVisible(true);

    }

    private JPanel CreateGUI() {

        tabPane = new JTabbedPane();
        attrmodel = new DefaultListModel();
        attributesList = new JList<>(attrmodel);
        objectClassesBox = new JComboBox<>();
        JTabbedPane configPnl = new JTabbedPane();
        JTabbedPane connPnl = new JTabbedPane();
        JTabbedPane drawPane = new JTabbedPane();
        Container cp = getContentPane();
        filenm.setSize(400, 10);

        filenm.setText("*****************Placeholder**************************");
        filenm.setLineWrap(true);
        filenm.setEditable(false);
        JButton open = new JButton("Open");
        sendNewGraphbtn = new JButton("Distribute New Graph");
        sendNewGraphbtn.setEnabled(false);
        sendNewGraphbtn.addActionListener(new SendMessages());

        sendOldGraphbtn = new JButton("Distribute Current Graph");
        sendOldGraphbtn.setEnabled(false);
        sendOldGraphbtn.addActionListener(new SendCurrentGraph());

        JPanel drawP = new JPanel();
        open.addActionListener(new OpenL());
        drawP.add(open);
        drawP.add(filenm);

        drawP.add(sendNewGraphbtn);
        drawP.add(sendOldGraphbtn);

        graphComponent = new mxGraphComponent(graph);
        statusGraph = new mxGraphComponent(graph_s);
        drawP.add(graphComponent);

        drawPane.add(drawP);

        connPnl.add(FedeartionConnectPnl());

        configPnl.add(PublishMessagesPnl());

        JTabbedPane tabbedPane = new JTabbedPane() {
            public Dimension getPreferredSize() {
                return new Dimension(800, 400);
            }
        ;
        };
        
        tabbedPane.add("Connection Configuration", connPnl);
        tabbedPane.add("Send Interactions/Messages", configPnl);
        tabbedPane.add("Dependency Graph", drawPane);
        tabbedPane.add("Status Panel", OnOffPanel());

        this.add(tabbedPane);
        this.pack();
        this.setVisible(true);
        //cp.add(tabbedPane);
        return drawP;

    }

    private JComboBox GetObjClassesBox() {

        ArrayList<ObjClassandAttributes> ObjectClasses = hladataController.getObjectClasses();
        if (ObjectClasses != null) {

            for (ObjClassandAttributes ObjectClasse : ObjectClasses) {
                objectClassesBox.addItem(ObjectClasse);

            }
        }
        objectClassesBox.addActionListener(new SelectItemL());
        return objectClassesBox;
    }

    private JList GetAttributesList(ObjClassandAttributes objectClass) {

        PopulateAttributesList(objectClass);

        return attributesList;
    }

    private void PopulateAttributesList(ObjClassandAttributes objectClass) {
        if (attributesList == null) {
            return;
        }
        attrmodel.removeAllElements();
        if (objectClass != null) {
            int i = 0;

            for (Entry<String, PubSubAttribute> entry : objectClass.psattributes.entrySet()) {
                String key = entry.getKey();
                PubSubAttribute value = entry.getValue();
                attrmodel.add(i, value);
            }

        }
    }

    private JPanel OnOffPanel() {
        StatusPanel statusPnl;
        JPanel emptyPnl;
        statusPnl = new StatusPanel(visController, dataController);
        JPanel pnl1 = new JPanel();
        JPanel pnl2 = new JPanel();

        pnl1.setLayout(new BoxLayout(pnl1, BoxLayout.Y_AXIS));
        emptyPnl = new JPanel();
        emptyPnl.setSize(200, 600);
        emptyPnl.setVisible(true);
        pnl1.add(statusPnl);
        pnl1.add(emptyPnl);

        pnl2.setLayout(new BoxLayout(pnl2, BoxLayout.Y_AXIS));
        pnl2.add(statusGraph);
        emptyPnl = new JPanel();
        emptyPnl.setSize(200, 600);
        emptyPnl.setVisible(true);
        pnl2.add(emptyPnl);

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));

        pnl.add(pnl1);
        pnl.add(pnl2);

        dataController.AddObserver(statusPnl);
        return pnl;
    }

    private String browseFile() {
        String fileAbsoPath = "nothing-selected";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileAbsoPath = selectedFile.getAbsolutePath();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
        return fileAbsoPath;
    }

    private JPanel FedeartionConnectPnl() {

        Container cp = getContentPane();

        connect = new JButton("Connect");
        runBtn = new JButton("Ready to Run!");
        runBtn.addActionListener(new RunL());
        runBtn.setEnabled(false);
        connect.addActionListener(new ConnectL());
        federationTf = new JTextField("federation");
        federateTf = new JTextField("Enter Federate Name");
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(5, 5));
        // open.addActionListener(new OpenL());
        p.add(federationTf);
        p.add(federateTf);
        p.add(connect);
        p.add(runBtn);

        return p;
    }

    private JPanel PublishMessagesPnl() {

        Container cp = getContentPane();

        sendInBtn = new JButton("Send");
        attrValTf = new JTextField("enter attribute value");

        sendInBtn.addActionListener(new SendInteraction());
        sendInBtn.setEnabled(false);
        publishMessage = new JTextField("Write Message");

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(10, 4));
        // open.addActionListener(new OpenL());
        p.add(publishMessage);

        p.add(sendInBtn);
        p.add(GetObjClassesBox());
        p.add(new JScrollPane(GetAttributesList(hladataController.getObjectClasse(0))));

        p.add(attrValTf);
        updateAttrBtn = new JButton("Update Attribute Value");

        updateAttrBtn.addActionListener(new UpdateAttribute());
        updateAttrBtn.setEnabled(false);
        p.add(updateAttrBtn);

        //p.add(GetPublishList());
        return p;
    }

    class SteelCheckL implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            System.out.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "SELECTED" : "DESELECTED");
        }

    }

    class OpenL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = browseFile();
            filenm.setText(fileName);
            Federation fed;
            try {
                fed = new JsonParser().DecodeDepJsonAbsolutePath(fileName);
                dataController.setDependenciesGraph(fed, true);
            } catch (IOException ex) {
                Logger.getLogger(Old_GenericFederationGUI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    class SelectItemL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObjClassandAttributes item = (ObjClassandAttributes) objectClassesBox.getSelectedItem();

            PopulateAttributesList(item);

        }
    }

    class ConnectL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            hlasmngController.CreateFederationAndAddFederate(federationTf.getText(), federateTf.getText());

            ((JButton) e.getSource()).setEnabled(false);
            runBtn.setEnabled(true);

        }
    }

    class RunL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            hlasmngController.RunFederate();
            runBtn.setEnabled(false);
            EnablingHLARelatedFunctions();

        }
    }

    private void EnablingHLARelatedFunctions() {
        sendNewGraphbtn.setEnabled(true);
        sendOldGraphbtn.setEnabled(true);
        updateAttrBtn.setEnabled(true);
        sendInBtn.setEnabled(true);
    }

    class SendInteraction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            hlacommController.SendInteractions();

        }
    }

    class UpdateAttribute implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            ObjClassandAttributes item = (ObjClassandAttributes) objectClassesBox.getSelectedItem();
            PubSubAttribute attribute = (PubSubAttribute) attrmodel.get(attributesList.getSelectedIndex());

            hlacommController.UpdateAttribute(item, attribute, attrValTf.getText());

        }
    }

    class ChangeState implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            System.out.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "SELECTED" : "DESELECTED");
            int value = 1;
            if (!(e.getStateChange() == ItemEvent.SELECTED)) {
                value = 0;
            }

            dataController.UpdateFederateState(0, value);
          //  visController. UpdateAttribute("HLAobjectRoot.FederationJson.FederateState", "State",valuestr);        

        }
    }

    class SendMessages implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

//            try {
//                Reader reader = new InputStreamReader(new FileInputStream(filenm.getText()), "UTF-8");
//                int intValueOfChar;
//                String targetString = "";
//                while ((intValueOfChar = reader.read()) != -1) {
//                    targetString += (char) intValueOfChar;
//                }
//                reader.close();
//
//                hlacommController.SendDependencyGraph(targetString, "NewGraph");
//
//            } catch (IOException ex) {
//                Logger.getLogger(Old_GenericFederationGUI.class.getName()).log(Level.SEVERE, null, ex);
//            }

        }
    }

    class SendCurrentGraph implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
//
//            try {
//                Reader reader = new InputStreamReader(new FileInputStream(filenm.getText()), "UTF-8");
//                int intValueOfChar;
//                String targetString = "";
//                while ((intValueOfChar = reader.read()) != -1) {
//                    targetString += (char) intValueOfChar;
//                }
//                reader.close();
//
//                hlacommController.SendDependencyGraph(targetString, "CurrentGraph");
//
//            } catch (IOException ex) {
//                Logger.getLogger(Old_GenericFederationGUI.class.getName()).log(Level.SEVERE, null, ex);
//            }

        }
    }

    @Override
    public void update(Observable obs, Object obj) {
        if (obs == visController) {

            if ("Dependencies Updated".equals((String) obj)) {
                Federation fed = visController.getFederationGraph();
                GUIFunctions.UpdateFederationStatusGraph(graph, fed, 1.3);
                GUIFunctions.UpdateFederationStatusGraph(graph_s, fed, 0.8);
            }

            if ("Performance Changed".equals((String) obj)) {
                UpdateStatus();
            }

            //System.out.println("Change has been notified" + hl.getJsonString());
        }
    }
}
