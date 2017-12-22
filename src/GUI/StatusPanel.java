/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import CommonFnc.StaticFunctions;
import Interfaces.DataControllerInterface;
import Interfaces.VisualInterface;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 *
 * @author abbas
 */
class StatusPanel extends JPanel implements Observer {

    private static final int PREF_W =200;
    private static final int PREF_H = 200;
    private static final Color SELECTED_COLOR = Color.RED;
    private static final Stroke SELECTED_STROKE = new BasicStroke(8f);
    private List<CustomControl> controls = new ArrayList<>();

    VisualInterface visController;
    DataControllerInterface dataController;
    private CustomControl statusCnt;

    public StatusPanel(VisualInterface contr, DataControllerInterface datacontroller_a) {

        this.visController = contr;
        this.dataController= datacontroller_a;

        Shape shape;

        shape = new RoundRectangle2D.Double(0, 0, 80, 80, 10, 10);

        statusCnt = new CustomControl(shape);
        statusCnt.setState(visController.getAgentPerformanceBol(0));
        controls.add(statusCnt);

        addMouseListener(new MyMouseListener());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (CustomControl cnt : controls) {

            g2.setColor(cnt.getColor());
            g2.fill(cnt.getShape());
        }
    }

    @Override
    public Dimension getPreferredSize() {
         if (isPreferredSizeSet()) {
        return super.getPreferredSize();
       }
         return new Dimension(PREF_W, PREF_H);
    }

    @Override
    public void update(Observable o, Object arg) {
        if ("Performance Changed".equals((String) arg)) {
            statusCnt.setState(visController.getAgentPerformanceBol(0));
            repaint();
        }
        if ("Dependencies Updated".equals((String) arg)) {
             statusCnt.setState(visController.getAgentPerformanceBol(0));
            repaint();
           // FederationInfo fed = visController.getFederationGraph();

        }
    }

    private class MyMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            for (int i = controls.size() - 1; i >= 0; i--) {
                if (controls.get(i).getShape().contains(e.getPoint())) {
                    if (statusCnt == controls.get(i)) {
                        if(dataController!=null)
                        dataController.UpdateFederateState(0,StaticFunctions.ConvertBooleantoInt(!controls.get(i).getState()));

                    }
                    repaint();
                    return;
                }
            }
        }
    }
}
