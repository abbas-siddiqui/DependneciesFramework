/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonFnc;

/**
 *
 * @author abbas
 */
public class StaticVariables {
    
    
    
    
    public static final String FEDERATE_MOD1 = "src/foms/FederateFOM.xml";
    
    public static final String FEDERATION_MOD_1 = "src/foms/DepDistrib.xml";
    public static final String MODULES_DEFAULT_LOCATION = "";
    
    public static final String INTERACTION_CLASS1 = "HLAinteractionRoot.MessageExchange";

    public static final int ITERATIONS = 20;
    public static final String READY_TO_RUN = "ReadyToRun";   
    public static final String FED_PREFIX = "fed";
    public static int DEFAULT_FEDERATE = 0;

    // ******************** EVENTS *********************
    public static final String DEPENDENCIES_UPDATE = "Dependencies Updated";
    public static final String PERFERFORMANCE_UPDATE = "Performance Updated";
    public static final String FEDERATION_UPDATE = "Federation Updated";

    //*************************** HLA Classes & Attributes
    public static final String FEDERATE_STATE_CLASS = "HLAobjectRoot.Federation.FederateState";
     public static final String DEPENDENCY_GRAPH_CLASS = "HLAobjectRoot.Federation.DependenciesUpdate";
    public static final String FEDERATE_STATE_ATTR = "State";   
    public static final String DEPENDECY_GRAPH_ATTR = "PartialDependency";
    
    // Received By Central Federate 
    public static final String FEDERATE_DEPENDENCY_ATTR = "UpdateDependnecy";
    
    //*************************** HLA Data Types
    
    public static final String HLASTRING = "HLAASCIIstring";
    
    //*************************** Dependnecies Types
    
    public static final int TYPES_OF_DEP = 2;
    public static final String AND_DEP = "AND";
    public static final String OR_DEP = "OR";
    
    
   //*************************** GUI Variables
    
    public static final int DEFAULT_MIN_WINDOW_WIDTH= 100;
    
   //*************************** Business Logic Variables
    public static final int STATE_MSG_Q_SIZE= 2;
    
}
