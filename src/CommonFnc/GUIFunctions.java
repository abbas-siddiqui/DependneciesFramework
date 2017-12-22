/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonFnc;

import Data.Agent.FederateDependency;
import Data.Agent.Federate;
import Data.Agent.Federation;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class GUIFunctions {

    public static HashMap<String, String> colors = new HashMap<>();

    public static void AssignColors(Federation federation) {

        for (Federate federate : federation.getFederates()) {
            AssignColors(federate);

        }

    }

    public static void AssignColors(Federate federate) {
        Random randomGenerator = new Random();
        colors.put(federate.getFederateName(), String.format("#%02x%02x%02x", randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255)));
    }

//    public static void UpdateGraph(mxGraph graph, Federation federation, double scale) {
//
//        if(colors.isEmpty())AssignColors(federation);
//        
//        HashMap<String, Object> hmap = new HashMap<>();
//        Random randomGenerator = new Random();
//
//        
//        Object parent = graph.getDefaultParent();
//        graph.refresh();
//        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
//        //graph.removeCells(cells, true);
//        try {
//            graph.getModel().beginUpdate();
//            Object v;
//
//            //Draw Vertices/Federates
//            for (Federate federate : federation.getFederates()) {
//                v = graph.insertVertex(parent, null, federate.getFederateName(), 0, 0, 80, 30, "NodeStyle");
//                hmap.put(federate.getFederateName(), v);
////                for (DependentFederate depFederate: federate.getDependentFederates())
////                {
////                v = graph.insertVertex(parent, null, depFederate.getFederateName(), 0, 0, 80, 30, "NodeStyle");
////                hmap.put(federate.getFederateName(), v);
////                }
//            }
//
//            String hex;
//            int i=1;
//            //Draw connections/dependencies/edges
//            for (Federate fed : federation.getFederates()) {
//                hex = colors.get(fed.getFederateName());//String.format("#%02x%02x%02x", ChangeColor(i+10), ChangeColor(i+2), ChangeColor(i+3));
//                i++;
//                graph.getStylesheet().putCellStyle(fed.getFederateName(), createEdgeStyle(hex, hex));
//
//                for (FederateDependency dependencie : fed.getDependencies()) {
//                    Object depStyle = new Object();
//                    switch (dependencie.getType()) {
//                        case "AND": {
//                            depStyle = graph.insertVertex(parent, null, "+", 0, 0, 20, 20, "ANDStyle");
//                            break;
//                        }
//                        case "OR": {
//                            depStyle = graph.insertVertex(parent, null, "||", 0, 0, 20, 20, "ORStyle");
//                            break;
//                        }
//                    }
//
//                    graph.insertEdge(parent, null, fed.getFederateName(), hmap.get(fed.getFederateName()), depStyle, fed.getFederateName());
//                    for (String depFederate : dependencie.getDependentFederatesNames()) {
//
//                        if (!hmap.containsKey(depFederate)) {
//                            v = graph.insertVertex(parent, null, depFederate, 0, 0, 80, 30, "NodeStyle");
//                            hmap.put(depFederate, v);
//                        }
//
//                        graph.insertEdge(parent, null, fed.getFederateName() + "-" + depFederate, depStyle, hmap.get(depFederate), fed.getFederateName());
//                    }
//
//                }
//            }
//            mxIGraphLayout layout = new mxCircleLayout(graph);
//            layout.execute(parent);
//        } finally {
//            graph.getModel().endUpdate();
//        }
//        graph.getView().setScale(scale);
//
//    }
    public static void UpdateFederationStatusGraph(mxGraph graph, Federation federation, double windowWidth) {

        if (windowWidth <20) windowWidth=StaticVariables.DEFAULT_MIN_WINDOW_WIDTH;
        try {

            if (colors.isEmpty()) {
                AssignColors(federation);
            }

            HashMap<String, Object> hmap = new HashMap<>();

            Object parent = graph.getDefaultParent();
            graph.refresh();
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
            //graph.removeCells(cells, true);   
            try {
                graph.getModel().beginUpdate();
                Object v;

                //Draw Vertices/Federates
                for (Federate federate : federation.getFederates()) {

                    if (federate.getPerformance() > 0) {
                        v = graph.insertVertex(parent, null, federate.getFederateName(), 0, 0, 80, 30, "NodeONStyle"); // *** need to draw the federate before drawing edges to depedendent federates as you can create an edge if vertex is not already on graph
                    } else {
                        v = graph.insertVertex(parent, null, federate.getFederateName(), 0, 0, 80, 30, "NodeOFFStyle");
                    }
                    // v = graph.insertVertex(parent, null, federate.getFederateName(), 0, 0, 80, 30, "NodeStyle");
                    hmap.put(federate.getFederateName(), v);
                }

                String hex;
                int i = 1;
                //Draw connections/dependencies/edges
                for (Federate fed : federation.getFederates()) {

                    hex = colors.get(fed.getFederateName());//String.format("#%02x%02x%02x", ChangeColor(i+10), ChangeColor(i+20), ChangeColor(i+30));
                    if (hex == null) {
                        AssignColors(fed);
                        hex = colors.get(fed.getFederateName());
                    }
                    i = i + 20;
                    graph.getStylesheet().putCellStyle(fed.getFederateName(), createEdgeStyle(hex, hex));

                    for (FederateDependency dependencie : fed.getDependencies()) {
                        Object depStyle = new Object();
                        switch (dependencie.getType()) {
                            case "AND": {
                                depStyle = graph.insertVertex(parent, null, "+", 0, 0, 20, 20, "ANDStyle");
                                break;
                            }
                            case "OR": {
                                depStyle = graph.insertVertex(parent, null, "||", 0, 0, 20, 20, "ORStyle");
                                break;
                            }
                        }

                        graph.insertEdge(parent, null, fed.getFederateName(), hmap.get(fed.getFederateName()), depStyle, fed.getFederateName());
                        for (String depFederate : dependencie.getDependentFederatesNames()) {

                            if (!hmap.containsKey(depFederate)) {
                                v = graph.insertVertex(parent, null, depFederate, 0, 0, 80, 30, "NodeStyle");
                                hmap.put(depFederate, v);
                            }

                            graph.insertEdge(parent, null, fed.getFederateName() + "-" + depFederate, depStyle, hmap.get(depFederate), fed.getFederateName());
                        }

                    }
                }
                mxIGraphLayout layout = new mxCircleLayout(graph);
                layout.execute(parent);
            } finally {
                graph.getModel().endUpdate();
            }

            mxGraphView view = graph.getView();

            int graphWidth = (int) view.getGraphBounds().getWidth();
            view.setScale((double) windowWidth / graphWidth * view.getScale());
      //  graph.getView().setScale(scale);

        } catch (Exception e) {
            Logger.getLogger(GUIFunctions.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public static int ChangeColor(int i) {

        int colorcode;

        colorcode = i * 2 + 40;
        if (colorcode > 255) {
            colorcode = colorcode - 255;
        }
        return colorcode;
    }

    public static void makeORStyle(mxStylesheet sheet) {
        Hashtable<String, Object> orStyle = new Hashtable<>();
        orStyle.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
        orStyle.put(mxConstants.STYLE_STROKEWIDTH, 2);
        orStyle.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.CYAN));
        orStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        orStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        sheet.putCellStyle("ORStyle", orStyle);

    }

    public static void makeANDStyle(mxStylesheet sheet) {
        Hashtable<String, Object> andStyle = new Hashtable<>();
        andStyle.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.LIGHT_GRAY));
        andStyle.put(mxConstants.STYLE_STROKEWIDTH, 2);
        andStyle.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        andStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_HEXAGON);
        andStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        sheet.putCellStyle("ANDStyle", andStyle);
    }

    public static void makeNodeStyle(mxStylesheet sheet) {
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.LIGHT_GRAY));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 186, 100)));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        sheet.putCellStyle("NodeStyle", style);

    }

    public static void makeDependentNodeStyle(mxStylesheet sheet) {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        sheet.putCellStyle("NodeStyle", style);

    }

    public static void makeNodeONStyle(mxStylesheet sheet) {
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.GREEN));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        sheet.putCellStyle("NodeONStyle", style);

    }

    public static Map<String, Object> createEdgeStyle(String strokeColor, String fontColor) {

        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        style.put(mxConstants.STYLE_STROKEWIDTH, 3);
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        style.put(mxConstants.STYLE_STROKECOLOR, strokeColor);// "#6482B9");
        style.put(mxConstants.STYLE_FONTCOLOR, fontColor);// "#446299");

        return style;
    }

    public static void makeNodeOFFStyle(mxStylesheet sheet) {
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.RED));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        sheet.putCellStyle("NodeOFFStyle", style);

    }

    public static void makeEdgeStyle(mxStylesheet sheet) {
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.RED));
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.RED));
        style.put(mxConstants.STYLE_STROKEWIDTH, 3);
        //style.put(mxConstants.STYLE_DASHED, true);
        style.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_TOPTOBOTTOM);
        sheet.putCellStyle("edge", style);

    }
}
