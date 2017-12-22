/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import CommonFnc.JsonParser;
import CommonFnc.StaticFunctions;
import CommonFnc.StaticVariables;
import Data.Agent.Federate;
import Data.Agent.FederateState;
import Data.Agent.Federation;
import Data.Layer.FederationDataLayer;
import Data.HLAData.ObjClassandAttributes;

import Data.HLAMessages.FederationGenericMsg;
import HLAComm.DependencyAmbassador;
import HLAComm.HLAFederationController;
import Interfaces.HLACommToDataInterface;
import Interfaces.HLAControlInterface;
import Interfaces.VisualInterface;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author abbas
 */
public class FederationStatesGUIController extends Observable implements Observer, HLACommToDataInterface, VisualInterface, HLAControlInterface {
    
    private FederationDataLayer datal;
    private HLAFederationController federate;
    private DependencyAmbassador fedamb;
    
    public FederationStatesGUIController(FederationDataLayer data, HLAFederationController federate_a, DependencyAmbassador fedamb_a) {
        datal = data;
        federate = federate_a;
        fedamb = fedamb_a;        
    }
    
    public void setFederationName(String federationName) {
        datal.setFederationName(federationName);
    }
    
    public void setFederateName(int index, String federateName) {
        datal.getFederation().SetFederateName(index, federateName);
    }
    
    public String getFederateName(int index) {
        return datal.getFederation().getFederateName(index);
    }
    
    @Override
    public void CreateFederationAndAddFederate(String federationName, String federateName) {
        
        setFederationName(federationName);
        setFederateName(0, federateName);
        CreateFederation();
        AddFederate();
    }
    
    public void setDependenciesString(String dependencyGraph, boolean isReset) {
        if (dependencyGraph == null /*|| dependencyGraph_a.equals(dependnecyGraphStr)*/) {
            return;
        }
        JsonParser jsonp = new JsonParser();
        
         FederationGenericMsg<Federation> dependencyGraphMsg;
         dependencyGraphMsg = JsonParser.<Federation>DecodeGenericMsg(dependencyGraph,  new TypeToken<FederationGenericMsg<Federation>>(){}.getType());
         if (isReset) {
         }
         Federation currentFederation = datal.getFederation();
         String currentFederationName = currentFederation.getName();
         String currentFederateName = datal.getFederateName(0);
         Federation federationgraph = dependencyGraphMsg.getData();
         Federation newFederation = new Federation();
         newFederation.setName(currentFederationName);
         if(federationgraph.SearchFederate(currentFederateName)== null)
         {
             Federate fed = new Federate();
             fed = (Federate)datal.getFederate(0).clone();
             newFederation.getFederates().clear();
             newFederation.getFederates().add(fed);
             
         }
         for (Federate ifed: federationgraph.getFederates())
         {
             
             Federate fed = new Federate();
             fed = (Federate)ifed.clone();
             if (ifed.getFederateName().equalsIgnoreCase(newFederation.getFederate(0).getFederateName()))
             {
                 Federate existFed = newFederation.getFederate(0);
                 existFed.setDependencies(fed.getDependencies());
                 existFed.setDependentFederates(fed.getDependentFederates());
                 existFed.setFederateState(fed.getFederateState());
             }
             else
                 newFederation.getFederates().add(fed);
         }
         datal.setNewFederation(newFederation);
        
    }

   

  
    @Override
    public Federation getFederationGraph() {
        return  datal.getFederation();
    }
    
    @Override
    public void update(Observable o, Object obj) {
        if (o == datal) {
            setChanged();
            
            if ("Dependencies Updated".equals((String) obj)) {
                setChanged();
                notifyObservers("Dependencies Updated");
            }
            
            if ("Performance Changed".equals((String) obj)) {
                setChanged();
                notifyObservers("Performance Changed");
            }
            
             if ("Federation Changed".equals((String) obj)) {
                setChanged();
                notifyObservers("Federation Changed");
            }
            
        }
    }
    
  

    @Override
    public ObjClassandAttributes getClassandAttributes(String objClassName) {
        return datal.getClassandAttributes(objClassName);
    }
    
    @Override
    public void ForwardReceivedMessage(String type, Object message) {
        switch (type) {
            case StaticVariables.DEPENDECY_GRAPH_ATTR:            
                                   
                setDependenciesString((String) message, true);
                break;
            case StaticVariables.FEDERATE_STATE_ATTR:
                FederateStaetChaged((String) message);
                break;
            
        }
    }
    
    private void FederateStaetChaged(String federateStateJson) {
        JsonParser jsonp = new JsonParser();
        
         FederationGenericMsg<FederateState> newFederateStateMsg;
         newFederateStateMsg = JsonParser.<FederateState>DecodeGenericMsg(federateStateJson,  new TypeToken<FederationGenericMsg<FederateState>>(){}.getType());
    
        Federation federation = datal.getFederation();
        
        for (Federate federate_a : federation.getFederates()) {
            if (federate_a.getFederateName().equalsIgnoreCase(newFederateStateMsg.getFederateName())) {
                
                
               federate_a.CopyState(newFederateStateMsg.getData());
               setChanged();
                notifyObservers("Performance Changed");
            }
        }        
        
      
        
    }
    
   
    @Override
    public boolean getAgentPerformanceBol(int index) {
        return StaticFunctions.ConvertInttoBoolean(datal.getFederatePerformance(index));        
    }

    @Override
    public boolean getAgentPerformanceBol(String federateName) {
        return StaticFunctions.ConvertInttoBoolean(datal.getFedeate(federateName).getPerformance());        
    }

 

   
   
    
    
    
    //********************************HLACommToDataInterface *************************
    
    private void CreateFederation() {
        federate.CreateFederation(datal.getFederationName(), datal.getFomModules(), datal.getRtiamb(), fedamb);
    }
    
    private void AddFederate() {
        federate.AddFederate(datal.getFederateFomModules(), datal.getFederationName(), datal.getFederateName(0), "type", fedamb, datal.getRtiamb());
        federate.AnnounceSyncPoint(datal.getRtiamb(), fedamb);
    }
    
    @Override
    public void RunFederate() {
        federate.RunIt(datal.getRtiamb(), fedamb, datal.getFederationName(), datal.getObjectClasses(), datal.getInteractionClasses());
        datal.setIsConnected(true);
    }

    @Override
    public ArrayList<ObjClassandAttributes> getObjectClasses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObjClassandAttributes getObjectClasse(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void AddObserver(Observer obser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFederateName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  

   
  
}
