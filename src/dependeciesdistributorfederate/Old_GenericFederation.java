/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dependeciesdistributorfederate;

import CommonFnc.StaticFunctions;
import CommonFnc.StaticVariables;
import Controllers.FederateController;
import GUI.FederateGUI;
import Data.Layer.FederationDataLayer;
import GUI.Old_GenericFederationGUI;
import HLAComm.DependencyAmbassador;
import HLAComm.HLAFederationController;
import java.io.IOException;

/**
 *
 * @author abbas
 */
public class Old_GenericFederation {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        FederationDataLayer datal;
        FederateController controller;
        datal = new FederationDataLayer();
        HLAFederationController fed = new HLAFederationController();
        StaticFunctions.AddFederateModules(datal, StaticVariables.MODULES_DEFAULT_LOCATION);
        StaticFunctions.AddFederationModules(datal, StaticVariables.MODULES_DEFAULT_LOCATION);
        StaticFunctions.AddGeneralClassesAndAttributes(datal);
        StaticFunctions.AddGeneralInteractionClasses(datal.getInteractionClasses());
        DependencyAmbassador fedamb = new DependencyAmbassador();
        controller = new FederateController(datal, fed, fedamb);
        fedamb.SetDataController(controller);
        Old_GenericFederationGUI graphGui = new Old_GenericFederationGUI(controller, controller, controller, controller, controller);
        datal.addObserver(controller);
        controller.addObserver(graphGui);

    }


}
