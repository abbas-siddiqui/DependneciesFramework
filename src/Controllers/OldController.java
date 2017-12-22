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
import Data.Layer.FederateDataLayer;
import Data.HLAMessages.FederationGenericMsg;
import HLAComm.DependencyAmbassador;
import HLAComm.HLAFederationController;
import Interfaces.DataControllerInterface;
import Interfaces.HLACommToDataInterface;
import Interfaces.HLAControlInterface;
import Interfaces.HLASendReceiveInterface;
import Interfaces.VisualInterface;
import com.google.gson.reflect.TypeToken;
import hla.rti1516e.exceptions.RTIexception;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class OldController extends Observable implements Observer, DataControllerInterface, HLACommToDataInterface, VisualInterface, HLASendReceiveInterface, HLAControlInterface {

    private FederateDataLayer datal;
    private HLAFederationController federate;
    private DependencyAmbassador fedamb;

    public OldController(FederateDataLayer hladata, HLAFederationController federate_a, DependencyAmbassador fedamb_a) {
        datal = hladata;
        federate = federate_a;
        fedamb = fedamb_a;

    }

    public int getAgentPerformance() {
        return datal.getFederatePerformance();
    }

    /**
     *
     * @return
     */
    public boolean getAgentPerformanceBol() {
        return StaticFunctions.ConvertInttoBoolean(datal.getFederatePerformance());
    }

    @Override
    public boolean getAgentPerformanceBol(int index) {
        return StaticFunctions.ConvertInttoBoolean(datal.getFederatePerformance());
    }

    public int getAgentPerformance(int index) {
        return datal.getFederatePerformance();
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
    public void setDependenciesGraph(Federation depGraphobj_a, boolean isAll) {

        String dependnecyGraphStr = datal.getDependnecyGraphStr();
        Federation dependencyGraph = datal.getFederationGraph();
        JsonParser jsonp = new JsonParser();
        dependencyGraph = depGraphobj_a;
        if (!isAll) {

            dependencyGraph.DeleteAllOtherFederaes(datal.getFederateName());
            ExtractDependentFederatesofFederate(dependencyGraph.getFederates().get(0)/* All other federates are deleted from the graph*/, datal.getFederate());
            ExtractDependenciesofFederate(dependencyGraph.getFederates().get(0)/* All other federates are deleted from the graph*/, datal.getFederate());
        }
        dependnecyGraphStr = jsonp.EncodeFederationToJson(dependencyGraph);
        datal.setDependnecyGraphStr(dependnecyGraphStr);
        datal.setFederationGraph(dependencyGraph);
        setChanged();
        notifyObservers("Dependencies Updated");
    }

    public void setDependenciesString(String dependencyGraph_a) {

        String dependneciesString = datal.getDependnecyGraphStr();

        Federation dependencyGraph = datal.getFederationGraph();

        if (dependencyGraph_a == null /*|| dependencyGraph_a.equals(dependnecyGraphStr)*/) {
            return;
        }
        JsonParser jsonp = new JsonParser();
        try {
            //ResetFederate(true);
            dependneciesString = dependencyGraph_a;
            dependencyGraph = jsonp.DecodeJsonString(dependencyGraph_a);//dependencyGraph = ;           

            dependencyGraph.DeleteAllOtherFederaes(datal.getFederateName());
            dependneciesString = jsonp.EncodeFederationToJson(dependencyGraph);

            ExtractDependentFederatesofFederate(dependencyGraph.getFederates().get(0)/* All other federates are deleted from the graph*/, datal.getFederate());
            ExtractDependenciesofFederate(dependencyGraph.getFederates().get(0)/* All other federates are deleted from the graph*/, datal.getFederate());
            dependencyGraph.getFederates().set(0, datal.getFederate());
            datal.setFederatePerformance(1);

            datal.setDependnecyGraphStr(dependneciesString);
            datal.setFederationGraph(dependencyGraph);
            setChanged();
            notifyObservers("Dependencies Updated");
        } catch (IOException ex) {
            Logger.getLogger(FederateDataLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //    public void setDependenciesString(String newFederation, boolean isAllfederates) {
//        datal.setDependenciesString(newFederation, isAllfederates);
//    }
    @Override
    public void ForwardReceivedMessage(String type, Object message) {
        switch (type) {
            case StaticVariables.DEPENDECY_GRAPH_ATTR:
                FederationGenericMsg<Federation> dependencyGraph;
                dependencyGraph = JsonParser.<Federation>DecodeGenericMsg((String) message, new TypeToken<FederationGenericMsg<Federation>>() {
                }.getType());
                if (dependencyGraph.getType().equalsIgnoreCase("NewGraph")) {

                    // try {
                    System.out.println("Class is ************" + dependencyGraph.getData().getClass());
                    //  Federation fed = JsonParser.DecodeJsonString(dependencyGraph.getData());
                    //  setDependenciesString(dependencyGraph.getData());
                    setDependenciesGraph(dependencyGraph.getData(), false);
                    //  setDependenciesString(dependencyGraph.getData());
                    //setDependenciesString(JsonParser.EncodeFederationToJson((Federation)dependencyGraph.getData()));
//            } catch (IOException ex) {
//                Logger.getLogger(FederateDataController.class.getName()).log(Level.SEVERE, null, ex);
//            }
                }
                break;

            case StaticVariables.FEDERATE_STATE_ATTR:
                CalculateState((String) message);
                break;

        }
    }

    private void ExtractDependentFederatesOfFederation() { // dependent federaets are now part of json string..so change the method

        DependentFederate depfed;
        if (!(datal.getFederationGraph().getFederates() == null)) {
            return; //Error Message or do something else?????????
        }
        for (Federate federatea : datal.getFederationGraph().getFederates()) {
            //ExtractDependentFederatesofFederate(federatea);
        }

    }

    private void ExtractDependentFederatesofFederate(Federate from_federate, Federate to_federate) { // dependent federaets are now part of json string..so change the method

        DependentFederate depfed = new DependentFederate();
        if (from_federate.getDependentFederates().isEmpty()) {  // If there are no dependent federates in the JSON then extract from Dependencies ---
            ExtractDependentFederatesFromDependencies(from_federate, to_federate);

        } else {
            for (DependentFederate depfederate : from_federate.getDependentFederates()) {
                depfed.MakeDeepCopy(depfederate);
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

        // There is need to search the federate first then it will be sent for th extracting dependencies and depdendent Federates
        for (Federate federatea : from_federates) {
            //ExtractDependenciesofFederate(federatea); 
        }
    }

    private void ExtractDependenciesofFederate(Federate from_federate, Federate to_federate) {

        FederateDependency dep;
        for (FederateDependency dependency : from_federate.getDependencies()) {
            dep = new FederateDependency();
            dep.setType(dependency.getType());
            for (String fedName : dependency.getDependentFederatesNames()) {
                dep.AddDependentFederateInfo(fedName);
            }
            to_federate.getDependencies().add(dep);
        }
    }

    @Override
    public void setFederationName(String federationName) {
        datal.SetFederationName(federationName);
    }

    public void setFederateName(String federateName) {
        datal.SetFederateName(federateName);
    }

    @Override
    public String getFederateName() {
        return datal.getFederateName();
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
        federate.AddFederate(datal.getFederateFomModules(), datal.getFederationName(), datal.getFederateName(), "type", fedamb, datal.getRtiamb());
        federate.AnnounceSyncPoint(datal.getRtiamb(), fedamb);
    }

    @Override
    public void RunFederate() {
        federate.RunIt(datal.getRtiamb(), fedamb, datal.getFederationName(), datal.getObjectClasses(), datal.getInteractionClasses());
        datal.setIsConnected(true);
    }

    @Override
    public void SendInteractions() {
        try {
            if (!datal.isIsConnected()) {
                return;
            }
            federate.sendInteraction(datal.getRtiamb(), fedamb, datal.getInteractionClasses());
        } catch (RTIexception ex) {
            Logger.getLogger(OldController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Only used for Centeral Federates to Distribute the graph
    @Override
    public void SendDependencyGraph( String type) {
        if (!datal.isIsConnected()) {
            return;
        }

      
        FederationGenericMsg<Federation> dependencyGraph = new FederationGenericMsg();
        dependencyGraph.setType(type);
        dependencyGraph.setData(datal.getFederationGraph());

        String depMsg = JsonParser.EncodeGenericMsg(dependencyGraph);
        ObjClassandAttributes objclass = datal.getClassandAttributes("HLAobjectRoot.Federation.DependenciesUpdate");
        PubSubAttribute attribute = objclass.getAttribute("PartialDependency");
        attribute.SetValue(depMsg);

//        FederationGenericMsg dependencyGraph = new FederationGenericMsg();
//        dependencyGraph.setType(type);
//        dependencyGraph.setData(graph);
//        
//        
//        
//        String depMsg=  JsonParser.EncodeGenericMsg(dependencyGraph);
//        ObjClassandAttributes objclass = datal.getClassandAttributes("HLAobjectRoot.Federation.DependenciesUpdate");
//        PubSubAttribute attribute = objclass.getAttribute("PartialDependency");
//        attribute.SetValue(depMsg);
        try {
            federate.updateAttributeValue(datal.getRtiamb(), fedamb, objclass, attribute);
        } catch (RTIexception ex) {
            Logger.getLogger(OldController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void UpdateFederateState(int index, int state) {

        datal.setFederatePerformance(state);
    }

    public String getFederateStateJsonString() {

        FederationGenericMsg<FederateState> stateMsg = new FederationGenericMsg();
        FederateState state = datal.getFederate().getFederateState();
        stateMsg.setFederateName(datal.getFederateName());
        stateMsg.setData(state);
        //federateState.setFederateName(federate.getFederateName());
        //federateState.setState(federate.getFederateState());
        String stateMsgstr = JsonParser.EncodeGenericMsg(stateMsg);
        return stateMsgstr;
    }

    @Override
    public void PublishState() {
          //if(!datal.isIsConnected()) return;

        // ToDo: Check first if state is same as previous state ---
        String agentStateJson = getFederateStateJsonString();
        ObjClassandAttributes objclass = datal.getClassandAttributes(StaticVariables.FEDERATE_STATE_CLASS);//(ObjClassandAttributes) objectClassesBox.getSelectedItem();
        PubSubAttribute attribute = objclass.getAttribute(StaticVariables.FEDERATE_STATE_ATTR);//(PubSubAttribute) attrmodel.get(attributesList.getSelectedIndex());
        attribute.SetValue(agentStateJson);
        try {
            federate.updateAttributeValue(datal.getRtiamb(), fedamb, objclass, attribute);
        } catch (RTIexception ex) {
            Logger.getLogger(OldController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /// Once Json Message is received from other Depedendent Federates
    @Override
    public void CalculateState(String stateJson) {

        FederationGenericMsg<FederateState> newFederateStateMsg;
        newFederateStateMsg = JsonParser.<FederateState>DecodeGenericMsg(stateJson, new TypeToken<FederationGenericMsg<FederateState>>() {
        }.getType());

        // Search for the this dependent federate
        DependentFederate depfed = datal.getDependentFederate(newFederateStateMsg.getFederateName());
        FederateState newfederateState = newFederateStateMsg.getData();
        if (depfed == null) {
            return; ///if depedent federate is not found -- meaning this federate is not depedent of this federate
        }
        if (depfed.getState().equals(newfederateState)) {
            return; // neglect as nothing has changed
        } else {
            // depfed = new DependentFederate(); //Make default seetings then the new State of the dependent DependentFederate
            depfed.setState((FederateState) newfederateState.clone());
        }
        // Now execute the effected dependencies  
        for (FederateDependency dep : datal.getDependendencies()) {
            if (dep.getType().equalsIgnoreCase("AND")) {
                if (dep.IsDependentOnIt(depfed.getFederateName())) // only if changed federate is part of this dependency 
                {
                    datal.setFederatePerformance(StaticFunctions.ConvertBooleantoInt(StaticFunctions.ANDDepEval(datal.getFederate(), dep)));
                }
            }

            if (dep.getType().equalsIgnoreCase("OR")) // only if changed federate is part of this dependency
            {
                if (dep.IsDependentOnIt(depfed.getFederateName())) // only if changed federate is part of this dependency 
                {
                    datal.setFederatePerformance(StaticFunctions.ConvertBooleantoInt(StaticFunctions.ORDepEval(datal.getFederate(), dep)));
                }
            }

        }

    }

    public Federation getFederationGraph() {
        return datal.getFederationGraph();
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
            Logger.getLogger(OldController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(OldController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void PublishFederateDependencies(boolean isRandom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
