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
import Interfaces.FederationDataInterface;
import Interfaces.HLACommToDataInterface;
import Interfaces.HLAControlInterface;
import Interfaces.HLASendReceiveInterface;
import Interfaces.VisualInterface;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class CentralFederateGUI extends JFrame implements Observer {

    private VisualInterface visController;
    private HLASendReceiveInterface hlacommController;
    private HLACommToDataInterface hladataController;
    private FederationDataInterface cfController;

    private HLAControlInterface hlasmngController;
  
    Random randomGenerator = new Random();
    mxGraph graph = new mxGraph();
   
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

    public CentralFederateGUI() {
        super("Central Federate");
        
        CreateGUI();

    }

    public CentralFederateGUI(FederationDataInterface cfController_a, VisualInterface datacontroller_a, HLASendReceiveInterface hlain, HLACommToDataInterface hladata, HLAControlInterface hlasmngController_a) {

        super("Central Federate");
        visController = datacontroller_a;
        hlacommController = hlain;
        hladataController = hladata;
        cfController = cfController_a;

        hlasmngController = hlasmngController_a;
        mxStylesheet stylesheet = graph.getStylesheet();
        GUIFunctions.makeANDStyle(stylesheet);
        GUIFunctions.makeNodeStyle(stylesheet);
        GUIFunctions.makeORStyle(stylesheet);
        GUIFunctions.makeEdgeStyle(stylesheet);

        

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
        //statusGraph = new mxGraphComponent(graph_s);
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
       // tabbedPane.add("Status Panel", OnOffPanel());

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

    @Override
    public void update(Observable o, Object arg) {
         Federation fed = visController.getFederationGraph();
             GUIFunctions.UpdateFederationStatusGraph(graph,fed, 400);
       System.out.println("In update Method of:   "+ CentralFederateGUI.class);
    }

 

    class OpenL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String fileName = browseFile();
            filenm.setText(fileName);
            Federation fed;
            try {
                fed = new JsonParser().DecodeDepJsonAbsolutePath(fileName);
                cfController.setDependenciesGraph(fed, true);
            } catch (IOException ex) {
                Logger.getLogger(CentralFederateGUI.class.getName()).log(Level.SEVERE, null, ex);
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

    

    class SendMessages implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

           

                hlacommController.SendDependencyGraph( "NewGraph");

          

        }
    }

    class SendCurrentGraph implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

           

                hlacommController.SendDependencyGraph( "CurrentGraph");

        }
    }

    
}
