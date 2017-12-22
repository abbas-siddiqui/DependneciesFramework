/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dependeciesdistributorfederate;

import CommonFnc.StaticFunctions;
import CommonFnc.StaticVariables;
import Controllers.CentralFederateController;
import Data.Layer.FederationDataLayer;
import GUI.CentralFederateGUI;
import HLAComm.DependencyAmbassador;
import HLAComm.HLAFederationController;
import java.io.IOException;

/**
 *
 * @author abbas
 */
public class CentralFederate {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        FederationDataLayer federationData;
        CentralFederateController controller;
        federationData = new FederationDataLayer();
        StaticFunctions.AddFederateModules(federationData, StaticVariables.MODULES_DEFAULT_LOCATION);
        StaticFunctions.AddFederationModules(federationData, StaticVariables.MODULES_DEFAULT_LOCATION);       
        HLAFederationController fed = new HLAFederationController();
        StaticFunctions.AddGeneralClassesAndAttributes(federationData);
        StaticFunctions.AddGeneralInteractionClasses(federationData.getInteractionClasses());
        DependencyAmbassador fedamb = new DependencyAmbassador();
        controller = new CentralFederateController(federationData, fed, fedamb);
        fedamb.SetDataController(controller);
        CentralFederateGUI graphGui = new CentralFederateGUI(controller, controller, controller, controller, controller);
        federationData.addObserver(controller);
        controller.addObserver(graphGui);

    }

}
