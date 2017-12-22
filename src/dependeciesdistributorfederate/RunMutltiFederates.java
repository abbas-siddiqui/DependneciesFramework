/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dependeciesdistributorfederate;

import CommonFnc.StaticFunctions;
import CommonFnc.StaticVariables;
import Controllers.MultiFederatesController;
import Data.Layer.FederationDataLayer;
import GUI.MultiFederates;
import HLAComm.DependencyAmbassador;
import HLAComm.HLAFederationController;
import java.io.IOException;

/**
 *
 * @author abbas
 */
public class RunMutltiFederates {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        FederationDataLayer federationData ;
        MultiFederatesController controller=null;
        federationData = new FederationDataLayer();

        StaticFunctions.AddFederateModules(federationData, StaticVariables.MODULES_DEFAULT_LOCATION);
        StaticFunctions.AddFederationModules(federationData,StaticVariables.MODULES_DEFAULT_LOCATION);       
        HLAFederationController fed = new HLAFederationController();
        StaticFunctions.AddGeneralClassesAndAttributes(federationData);
        StaticFunctions.AddGeneralInteractionClasses(federationData.getInteractionClasses());
        DependencyAmbassador fedamb = new DependencyAmbassador();
        controller = new MultiFederatesController(federationData,fed, fedamb);
        fedamb.SetDataController(controller);
        MultiFederates graphGui = new MultiFederates(controller,controller,controller) ;
        federationData.addObserver(controller);
        controller.addObserver(graphGui);
      
    }

    
}
