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
import Data.Agent.Federation;
import Interfaces.HLAControlInterface;
import Interfaces.VisualInterface;
import javax.swing.JFrame;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class StateVisualizer extends JFrame implements Observer {

    private VisualInterface visualController;
    private HLAControlInterface hladataController;
    HashMap<String, Object> hmap = new HashMap<>();
    Random randomGenerator = new Random();
    mxGraph graph = new mxGraph();
    JTabbedPane tabPane;
    private mxGraphComponent graphComponent;

    /**
     *
     */
    public StateVisualizer() {
        super("Federation Dependency Graph");

        CreateGUI();

    }

    public StateVisualizer(HLAControlInterface hladataController_a, VisualInterface datacontroller_a) {

        super("Federation Visualizer");
        visualController = datacontroller_a;
        hladataController = hladataController_a;
        int rn = (int) Math.floor(Math.random() * 101);
        String federateName = "Visfed" + rn;
        hladataController.CreateFederationAndAddFederate("federation", federateName);
        hladataController.RunFederate();
       
        InitGraph();
        CreateGUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setVisible(true);

    }

    private void InitGraph()
    {
        graph = new mxGraph();
     mxStylesheet stylesheet = graph.getStylesheet();
        GUIFunctions.makeANDStyle(stylesheet);
        GUIFunctions.makeNodeStyle(stylesheet);
        GUIFunctions.makeORStyle(stylesheet);
        GUIFunctions.makeNodeOFFStyle(stylesheet);
        GUIFunctions.makeNodeONStyle(stylesheet);
        
         

    }
    
    
    private JPanel CreateGUI() {

        tabPane = new JTabbedPane();

        JTabbedPane drawPane = new JTabbedPane();

        JPanel drawP = new JPanel();

        graphComponent = new mxGraphComponent(graph);

        drawP.add(graphComponent);

        drawPane.add(drawP);

        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 400);
            }
        ;
        };
        
     
        tabbedPane.add("Dependency Graph", drawPane);
        tabbedPane.add("Status Panel", OnOffPanel());

        this.add(tabbedPane);
        this.pack();
        this.setVisible(true);

        return drawP;

    }

    private JPanel OnOffPanel() {
        StatusPanel statusPnl;

        JPanel pnl = new JPanel();

        return pnl;
    }

    public void update(Observable obs, Object obj) {
        if (obs == visualController) {

            if ("Dependencies Updated".equals((String) obj)) {
                Federation fed = visualController.getFederationGraph();
                GUIFunctions.UpdateFederationStatusGraph(graph, fed, 400);

            }

            if ("Federation Changed".equals((String) obj)) {
                Federation fed = visualController.getFederationGraph();
                GUIFunctions.UpdateFederationStatusGraph(graph, fed, 400);

            }

            if ("Performance Changed".equals((String) obj)) {
                UpdateStatus();
            }

        }
    }

    private void UpdateStatus() {
        Federation fed = visualController.getFederationGraph();
        if(graph== null)InitGraph();
        GUIFunctions.UpdateFederationStatusGraph(graph, fed, 400);
    }

}
