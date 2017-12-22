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
import Data.Agent.Federation;
import Interfaces.HLAControlInterface;
import Interfaces.HLASendReceiveInterface;
import Interfaces.VisualInterface;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MultiFederates extends JFrame implements Observer {

    private VisualInterface visualController;
    private HLAControlInterface hladataController;
     private HLASendReceiveInterface hlasendreceiveController;

    Random randomGenerator = new Random();

    /**
     *
     */
    public MultiFederates() {
        super("");

        CreateGUI();

    }

    public MultiFederates(HLAControlInterface hladataController_a, VisualInterface datacontroller_a, HLASendReceiveInterface hlasendReceive_a) {

        super("");
        visualController = datacontroller_a;
        hladataController = hladataController_a;
        hlasendreceiveController= hlasendReceive_a;
       
              CreateGUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setVisible(true);

    }

    private JPanel CreateGUI() {
        JFormattedTextField deg;
        NumberFormat integerFieldFormatter = NumberFormat.getIntegerInstance();
        integerFieldFormatter.setGroupingUsed(false);
        deg = new JFormattedTextField(integerFieldFormatter);
        deg.setColumns(3); //whatever size you wish to set
        
        
         JButton runBtn = new JButton("Ready to Run!");
         JButton depBtn = new JButton("Send Own Dependencies!");
        runBtn.addActionListener(new RunL());
         depBtn.addActionListener(new SendDepL());
       



        JTabbedPane panes = new JTabbedPane();

        JPanel startP = new JPanel();

        startP.add(deg);
        startP.add(runBtn);
        startP.add(depBtn);
//        startP.add(ftf[0]);
//        startP.add(ftf[1]);

        panes.add(startP);

        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 400);
            }
        ;
        };
        
     
        tabbedPane.add("Federates Starter", panes);

        this.add(tabbedPane);
        this.pack();
        this.setVisible(true);

        return startP;

    }

    @Override
    public void update(Observable obs, Object obj) {
        if (obs == visualController) {

            if ("Dependencies Updated".equals((String) obj)) {
                Federation fed = visualController.getFederationGraph();

            }

            if ("Federation Changed".equals((String) obj)) {
                Federation fed = visualController.getFederationGraph();

            }

            if ("Performance Changed".equals((String) obj)) {

            }

        }
    }
    
    class RunL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

           ((JButton)e.getSource()).setEnabled(false);
            int rn = (int) Math.floor(Math.random() * 101);
        String federateName = "fed" + rn;
            hladataController.CreateFederationAndAddFederate("federation", federateName);
        hladataController.RunFederate();  
           
        

        }
    }
    class SendDepL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

          hlasendreceiveController.PublishFederateDependencies(true);
        

        }
    }

}
