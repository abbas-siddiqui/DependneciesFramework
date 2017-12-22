/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import CommonFnc.StaticFunctions;
import CommonFnc.JsonParser;
import CommonFnc.StaticVariables;
import Data.HLAData.PubSubAttribute;
import Data.HLAData.ObjClassandAttributes;
import Data.Agent.DependentFederate;
import Data.Agent.Federate;
import Data.Agent.FederateDependency;
import Data.Agent.FederateState;
import Data.Agent.Federation;
import Data.HLAMessages.FederationGenericMsg;
import Data.Layer.FederationDataLayer;
import HLAComm.DependencyAmbassador;
import HLAComm.HLAFederationController;
import Interfaces.DataControllerInterface;
import Interfaces.HLACommToDataInterface;
import Interfaces.HLAControlInterface;
import Interfaces.HLASendReceiveInterface;
import Interfaces.VisualInterface;
import Queues.StateMessageQueue;
import com.google.gson.reflect.TypeToken;
import hla.rti1516e.exceptions.RTIexception;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class FederateController extends Observable implements Observer, DataControllerInterface, HLACommToDataInterface, VisualInterface, HLASendReceiveInterface, HLAControlInterface {

    private FederationDataLayer datal;
    private HLAFederationController federate;
    private DependencyAmbassador fedamb;
    private static int  DEFAULT_FEDERATE =0;
    private StateMessageQueue stateMsgQ;

    public FederateController(FederationDataLayer hladata, HLAFederationController federate_a, DependencyAmbassador fedamb_a) {
        datal = hladata;
        federate = federate_a;
        fedamb = fedamb_a;
        stateMsgQ =new StateMessageQueue (datal);

    }

    public int getAgentPerformance() {
        return datal.getFederatePerformance(DEFAULT_FEDERATE); // 1st Federate is own federate
    }

    /**
     *
     * @return
     */
    public boolean getAgentPerformanceBol() {
        return StaticFunctions.ConvertInttoBoolean(datal.getFederatePerformance(DEFAULT_FEDERATE));
    }

    @Override
    public boolean getAgentPerformanceBol(int index) {
        return StaticFunctions.ConvertInttoBoolean(datal.getFederatePerformance(index));
    }

    public int getAgentPerformance(int index) {
        return datal.getFederatePerformance(index);
    }

    @Override
    public ArrayList<ObjClassandAttributes> getObjectClasses() {
        if (datal != null) {
            return datal.getObjectClasses();
        } else {
            return null;
        }

    }

    @Override
    public ObjClassandAttributes getClassandAttributes(String objClassName) {
        return datal.getClassandAttributes(objClassName);
    }

    @Override
    public ObjClassandAttributes getObjectClasse(int index) {
        return datal.getObjectClasses().get(index);
    }

    @Override
    public void setDependenciesGraph(Federation incomingFederation, boolean isIncAllFederates) {

        Federate incomingFederate=null;
        Federation currentFederation = datal.getFederation();  // 
        Federate ownFederate=(Federate) datal.getFederate(DEFAULT_FEDERATE).clone(); //Clone Own Federate from the Federation
        Federation newFederation = new Federation();
        newFederation.setName(currentFederation.getName());
        
        
       
       // newFederation.AddFederate(DEFAULT_FEDERATE, ownFederate);// Add own Federate at DEFAULT_FEDERATE Location
        
        
        if(incomingFederation.SearchFederate(ownFederate.getFederateName())!=null) // if incoming federation has own federate then
        incomingFederate = incomingFederation.SearchFederate(ownFederate.getFederateName()); // Does new Federation have my Federate???
       
        
       
        if ((!isIncAllFederates) && (incomingFederate !=null)) { // if not all federates should be included in the federation then delete others --- and newFederation should include my own federate
            incomingFederation.DeleteAllOtherFederaes(ownFederate.getFederateName());// delete all other federates except own
           
            ownFederate.ClearDependencies();
           // newFederation.getDependendencies(DEFAULT_FEDERATE).clear(); // Delete previous dependnecies // its a clone it wouldnt effect the currefent federation yet -- if it is being accessed
             ExtractDependentFederatesofFederate(incomingFederate, ownFederate);// newFederation.getFederate(DEFAULT_FEDERATE)); // Extract Dependent Federates from the incoming federation
            
             
             
             CopyDependencies(incomingFederate.getDependencies(),ownFederate.getDependencies());
             
             newFederation.ClearFederates();
             newFederation.AddFederate(DEFAULT_FEDERATE, ownFederate);
        }
        if (isIncAllFederates) 
        {
            
            Federate newFed;
            // Search own Federate First otherwise add it ---
            if(incomingFederate!=null){newFederation.AddFederate(DEFAULT_FEDERATE, incomingFederate,true); incomingFederation.DeleteFederate(ownFederate.getFederateName()); /* delete thefederates as it is already part of new Federation*/}
            for(Federate inFed:incomingFederation.getFederates()){ //
            // if it is not own Federate then just copy it as it is without need for specific position or change in anything.
            newFed= new Federate();
            newFed.setFederateName(inFed.getFederateName());
            ExtractDependentFederatesofFederate(inFed, newFed); // Extract Dependent Federates from the incoming federation
            CopyDependencies(inFed.getDependencies(),newFed.getDependencies());
            newFederation.AddFederate(newFed);
            }
        }
      currentFederation.Clear();
        
        datal.setNewFederation(newFederation); // 
        
    }


    @Override
    public void ForwardReceivedMessage(String type, Object message) {
        switch (type) {
            case StaticVariables.DEPENDECY_GRAPH_ATTR:
                FederationGenericMsg<Federation> dependencyGraph;
                dependencyGraph = JsonParser.<Federation>DecodeGenericMsg((String) message, new TypeToken<FederationGenericMsg<Federation>>() {
                }.getType());
                if (dependencyGraph.getType().equalsIgnoreCase("NewGraph")) {

                    
                    System.out.println("Class is ************" + dependencyGraph.getData().getClass());
                   
                    setDependenciesGraph(dependencyGraph.getData(), false);
                    datal.setFederatePerformance(DEFAULT_FEDERATE, 1);
                
                }
                break;

            case StaticVariables.FEDERATE_STATE_ATTR:
               // CalculateState((String) message);
                 FederationGenericMsg<FederateState> newFederateStateMsg;
        newFederateStateMsg = JsonParser.<FederateState>DecodeGenericMsg((String) message, new TypeToken<FederationGenericMsg<FederateState>>() {
        }.getType());
                stateMsgQ.Add(newFederateStateMsg);
                break;
                
            case StaticVariables.FEDERATE_DEPENDENCY_ATTR:
                ;
                 
                break;

        }
    }


    private void ExtractDependentFederatesofFederate(Federate from_federate, Federate to_federate) { 
        DependentFederate depfed = new DependentFederate();
        if (from_federate.getDependentFederates().isEmpty()) {  // If there are no dependent federates in the JSON then extract from Dependencies ---
            ExtractDependentFederatesFromDependencies(from_federate, to_federate);

        } else {
            for (DependentFederate depfederate : from_federate.getDependentFederates()) {
                depfed = (DependentFederate)depfederate.clone();
                to_federate.AddDependentFederate(depfed);
            }
        }

    }

    private void ExtractDependentFederatesFromDependencies(Federate from_federate, Federate to_federate) {

        DependentFederate depfed;
        for (FederateDependency dependency : from_federate.getDependencies()) {
            for (String fed : dependency.getDependentFederatesNames()) {
                depfed = new DependentFederate();
                depfed.setFederateName(fed);
                to_federate.AddDependentFederate(depfed);
            }
        }

    }

    private void ExtractDependenciesofFederation(ArrayList<Federate> from_federates, ArrayList<Federate> to_federates) {
        //ExtractDependenciesofFederate(currentFederation.getFederates().get(DEFAULT_FEDERATE)/* All other federates are deleted from the graph*/, datal.getFederate());
        // There is need to search the federate first then it will be sent for th extracting dependencies and depdendent Federates
        for (Federate federatea : from_federates) {
            //ExtractDependenciesofFederate(federatea); 
        }
    }

    private void CopyDependencies(ArrayList<FederateDependency> from_Deps, ArrayList<FederateDependency> to_Deps) {

        FederateDependency dep;
       
        if(from_Deps.isEmpty())return;
        for (FederateDependency dependency : from_Deps) {          
            dep = (FederateDependency)dependency.clone();
            to_Deps.add(dep);
        }
    }

    @Override
    public void setFederationName(String federationName) {
        datal.setFederationName(federationName);
    }

    public void setFederateName(String federateName) {
        datal.setFederateName(DEFAULT_FEDERATE,federateName);
    }

    @Override
    public String getFederateName() {
        return datal.getFederateName(DEFAULT_FEDERATE);
    }

    @Override
    public void CreateFederationAndAddFederate(String federationName, String federateName) {

        setFederationName(federationName);
        setFederateName(federateName);
        CreateFederation();
        AddFederate();
    }

    private void CreateFederation() {
        federate.CreateFederation(datal.getFederationName(), datal.getFomModules(), datal.getRtiamb(), fedamb);
    }

    private void AddFederate() {
        federate.AddFederate(datal.getFederateFomModules(), datal.getFederationName(), datal.getFederateName(DEFAULT_FEDERATE), "type", fedamb, datal.getRtiamb());
        federate.AnnounceSyncPoint(datal.getRtiamb(), fedamb);
    }

    @Override
    public void RunFederate() {
        federate.RunIt(datal.getRtiamb(), fedamb, datal.getFederationName(), datal.getObjectClasses(), datal.getInteractionClasses());
        datal.setIsConnected(true);
        stateMsgQ.StartThread();
    }

    @Override
    public void SendInteractions() {
        try {
            if (!datal.isIsConnected()) {
                return;
            }
            federate.sendInteraction(datal.getRtiamb(), fedamb, datal.getInteractionClasses());
        } catch (RTIexception ex) {
            Logger.getLogger(FederateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Only used for Centeral Federates to Distribute the graph
    @Override
    public void SendDependencyGraph( String type) {
//        if (!datal.isIsConnected()) {
//            return;
//        }
//
//        Federation fed = null;
//        try {
//            fed = JsonParser.DecodeJsonString(graph);
//        } catch (IOException ex) {
//            Logger.getLogger(FederateController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        FederationGenericMsg<Federation> dependencyGraph = new FederationGenericMsg();
//        dependencyGraph.setType(type);
//        dependencyGraph.setData(fed);
//
//        String depMsg = JsonParser.EncodeGenericMsg(dependencyGraph);
//        ObjClassandAttributes objclass = datal.getClassandAttributes("HLAobjectRoot.Federation.DependenciesUpdate");
//        PubSubAttribute attribute = objclass.getAttribute("PartialDependency");
//        attribute.SetValue(depMsg);
//
//        try {
//            federate.updateAttributeValue(datal.getRtiamb(), fedamb, objclass, attribute);
//        } catch (RTIexception ex) {
//            Logger.getLogger(FederateController.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    @Override
    public void UpdateFederateState(int index, int state) {

        datal.setFederatePerformance(index,state);
    }

    public String getFederateStateJsonString() {

        FederationGenericMsg<FederateState> stateMsg = new FederationGenericMsg();
        FederateState state = datal.getFederate(DEFAULT_FEDERATE).getFederateState();
        stateMsg.setFederateName(datal.getFederateName(DEFAULT_FEDERATE));
        stateMsg.setData(state);
               String stateMsgstr = JsonParser.EncodeGenericMsg(stateMsg);
        return stateMsgstr;
    }

    @Override
    public void PublishState() {
          if(!datal.isIsConnected()) return;

        // ToDo: Check first if state is same as previous state ---
        String agentStateJson = getFederateStateJsonString();
        ObjClassandAttributes objclass = datal.getClassandAttributes("HLAobjectRoot.Federation.FederateState");//(ObjClassandAttributes) objectClassesBox.getSelectedItem();
        PubSubAttribute attribute = objclass.getAttribute("State");//(PubSubAttribute) attrmodel.get(attributesList.getSelectedIndex());
        attribute.SetValue(agentStateJson);
        try {
            federate.updateAttributeValue(datal.getRtiamb(), fedamb, objclass, attribute);
        } catch (RTIexception ex) {
            Logger.getLogger(FederateController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /// Once Json Message is received from other Depedendent Federates
    @Override
    public void CalculateState(String stateJson) {

        FederationGenericMsg<FederateState> newFederateStateMsg;
        newFederateStateMsg = JsonParser.<FederateState>DecodeGenericMsg(stateJson, new TypeToken<FederationGenericMsg<FederateState>>() {
        }.getType());

        // Search for the this dependent federate
        DependentFederate depfed = datal.getFederate(DEFAULT_FEDERATE).GetDependentFederate(newFederateStateMsg.getFederateName());
       
        if (depfed == null) {
            return; ///if depedent federate is not found -- meaning this federate is not depedent of this federate
        }
        // if found then extract the new incoming State of this dependent Federate
        FederateState incomingFederateState = newFederateStateMsg.getData();
        if (depfed.getState().equals(incomingFederateState)) {
            return; // neglect as nothing has changed
        } else {
           
            depfed.setState(incomingFederateState);
        }
        // Now execute the effected dependencies  
        for (FederateDependency dep : datal.getFederate(DEFAULT_FEDERATE).getDependencies()) {
            if (dep.getType().equalsIgnoreCase("AND")) {
                if (dep.IsDependentOnIt(depfed.getFederateName())) // only if changed federate is part of this dependency 
                {
                    datal.setFederatePerformance(DEFAULT_FEDERATE, StaticFunctions.ConvertBooleantoInt(StaticFunctions.ANDDepEval(datal.getFederate(DEFAULT_FEDERATE), dep)));
                }
            }

            if (dep.getType().equalsIgnoreCase("OR")) // only if changed federate is part of this dependency
            {
                if (dep.IsDependentOnIt(depfed.getFederateName())) // only if changed federate is part of this dependency 
                {
                    datal.setFederatePerformance(DEFAULT_FEDERATE,StaticFunctions.ConvertBooleantoInt(StaticFunctions.ORDepEval(datal.getFederate(DEFAULT_FEDERATE), dep)));
                }
            }

        }

    }

    public Federation getFederationGraph() {
        return datal.getFederation();
    }

    public void AddObserver(Observer obser) {
        addObserver(obser);
    }

    @Override
    public void update(Observable o, Object obj) {
        if (o == datal) {
            setChanged();

            if ("Dependencies Updated".equals((String) obj)) {
                notifyObservers("Dependencies Updated");
            }

            if ("Performance Changed".equals((String) obj)) {
                PublishState();
                notifyObservers("Performance Changed");
            }
            if ("Federation Changed".equals((String) obj)) {
                notifyObservers("Dependencies Updated");
            }
           
           

        }
    }

    @Override
    public boolean getAgentPerformanceBol(String federateName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void UpdateAttribute(ObjClassandAttributes classAttr, PubSubAttribute attribute, String value) {
        try {
            attribute.SetValue(value);
            federate.updateAttributeValue(datal.getRtiamb(), fedamb, classAttr, attribute);
        } catch (RTIexception ex) {
            Logger.getLogger(FederateController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void UpdateAttribute(String objClassName, String attributeName, String value) {
        ObjClassandAttributes objclass = datal.getClassandAttributes(objClassName);//(ObjClassandAttributes) objectClassesBox.getSelectedItem();
        PubSubAttribute attribute = objclass.getAttribute(attributeName);//(PubSubAttribute) attrmodel.get(attributesList.getSelectedIndex());
        attribute.SetValue(value);
        try {
            federate.updateAttributeValue(datal.getRtiamb(), fedamb, objclass, attribute);
        } catch (RTIexception ex) {
            Logger.getLogger(FederateController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void PublishFederateDependencies(boolean isRandom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
