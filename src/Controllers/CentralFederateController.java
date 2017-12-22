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
import Data.Agent.Federation;
import Data.HLAMessages.FederationGenericMsg;
import Data.Layer.FederationDataLayer;
import HLAComm.DependencyAmbassador;
import HLAComm.HLAFederationController;
import Interfaces.FederationDataInterface;
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
public class CentralFederateController extends Observable implements Observer,  HLACommToDataInterface, VisualInterface, HLASendReceiveInterface, HLAControlInterface, FederationDataInterface {

    private FederationDataLayer datal;
    private HLAFederationController federate;
    private DependencyAmbassador fedamb;

    public CentralFederateController(FederationDataLayer hladata, HLAFederationController federate_a, DependencyAmbassador fedamb_a) {
        datal = hladata;
        federate = federate_a;
        fedamb = fedamb_a;

    }

    public int getAgentPerformance() {
        return datal.getFederatePerformance(StaticVariables.DEFAULT_FEDERATE); // 1st Federate is own federate
    }

    /**
     *
     * @return
     */
    public boolean getAgentPerformanceBol() {
        return StaticFunctions.ConvertInttoBoolean(datal.getFederatePerformance(StaticVariables.DEFAULT_FEDERATE));
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

    private void AddFederateDependnecies(Federate fedt, String Type) {

        // Just replace or add the federate --- 
        Federate incomingFederate = fedt;
        Federation currentFederation = datal.getFederation();  //
        Federate existingFed = currentFederation.SearchFederate(fedt.getFederateName());

        if (existingFed == null) {
            currentFederation.AddFederate(fedt);

        } else {
            
            currentFederation.DeleteFederate(fedt.getFederateName());
            currentFederation.AddFederate(fedt);
            //existingFed = (Federate) fedt.clone();

        }
        
        setChanged();
        
        notifyObservers("Dependencies Updated");

    }

    @Override
    public void ForwardReceivedMessage(String type, Object message) {
        FederationGenericMsg<Federation> dependencyGraph;
        switch (type) {

            case StaticVariables.FEDERATE_DEPENDENCY_ATTR:

                FederationGenericMsg<Federate> fedDep = JsonParser.<Federate>DecodeGenericMsg((String) message, new TypeToken<FederationGenericMsg<Federate>>() {
                }.getType());

                System.out.println("Class is ************" + fedDep.getData().getClass());

                AddFederateDependnecies(fedDep.getData(), fedDep.getType());
                // datal.setFederatePerformance(StaticVariables.StaticVariables.DEFAULT_FEDERATE, 1);

               // Federation currentFederation = datal.getFederation();  //
               // System.out.println("");
                break;

        }
    }

   
    public void setFederationName(String federationName) {
        datal.setFederationName(federationName);
    }

    public void setFederateName(String federateName) {
        datal.setFederateName(StaticVariables.DEFAULT_FEDERATE, federateName);
    }

    @Override
    public String getFederateName() {
        return datal.getFederateName(StaticVariables.DEFAULT_FEDERATE);
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
        federate.AddFederate(datal.getFederateFomModules(), datal.getFederationName(), datal.getFederateName(StaticVariables.DEFAULT_FEDERATE), "type", fedamb, datal.getRtiamb());
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
            Logger.getLogger(CentralFederateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Only used for Centeral Federates to Distribute the graph
    @Override
    public void SendDependencyGraph(String type) {
        if (!datal.isIsConnected()) {
            return;
        }

        //Federation fed = null;
   //     try {
       //     fed = JsonParser.DecodeJsonString(datal.get);
     //   } catch (IOException ex) {
      //      Logger.getLogger(CentralFederateController.class.getName()).log(Level.SEVERE, null, ex);
     //   }
        FederationGenericMsg<Federation> dependencyGraph = new FederationGenericMsg();
        dependencyGraph.setType(type);
        dependencyGraph.setData(datal.getFederation());

        String depMsg = JsonParser.EncodeGenericMsg(dependencyGraph);
        ObjClassandAttributes objclass = datal.getClassandAttributes(StaticVariables.DEPENDENCY_GRAPH_CLASS);
        PubSubAttribute attribute = objclass.getAttribute(StaticVariables.DEPENDECY_GRAPH_ATTR);
        attribute.SetValue(depMsg);

        try {
            federate.updateAttributeValue(datal.getRtiamb(), fedamb, objclass, attribute);
        } catch (RTIexception ex) {
            Logger.getLogger(CentralFederateController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(CentralFederateController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(CentralFederateController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void PublishFederateDependencies(boolean isRandom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDependenciesGraph(Federation incomingFederation, boolean isIncFederates) {

        Federate incomingFederate=null;
        Federation currentFederation = datal.getFederation();  // 
        Federate ownFederate= (Federate)datal.getFederate(StaticVariables.DEFAULT_FEDERATE).clone(); //Clone Own Federate from the Federation
        Federation newFederation = new Federation();
        newFederation.setName(currentFederation.getName());
       
        newFederation.AddFederate(StaticVariables.DEFAULT_FEDERATE, ownFederate);// Add own Federate at StaticVariables.DEFAULT_FEDERATE Location
        if(incomingFederation.SearchFederate(ownFederate.getFederateName())!=null)
        incomingFederate = (Federate)incomingFederation.SearchFederate(ownFederate.getFederateName()).clone(); // Does new Federation have my Federate???
       
        
       
        if ((!isIncFederates) && (incomingFederate !=null)) { // if not all federates should be included in the federation then delete others --- and newFederation should include my own federate
            incomingFederation.DeleteAllOtherFederaes(ownFederate.getFederateName());// delete all other federates except own
            ExtractDependentFederatesofFederate(incomingFederate, newFederation.getFederate(StaticVariables.DEFAULT_FEDERATE)); // Extract Dependent Federates from the incoming federation
            newFederation.getDependendencies(StaticVariables.DEFAULT_FEDERATE).clear(); // Delete previous dependnecies // its a clone it wouldnt effect the currefent federation yet -- if it is being accessed
            CopyDependencies(incomingFederate.getDependencies(),newFederation.getDependendencies(StaticVariables.DEFAULT_FEDERATE));
        }
        if (isIncFederates) 
        {
            
            Federate newFed;
            // Search own Federate First otherwise add it ---
            if(incomingFederate!=null){newFederation.AddFederate(StaticVariables.DEFAULT_FEDERATE, incomingFederate,true); incomingFederation.DeleteFederate(ownFederate.getFederateName()); /* delete thefederates as it is already part of new Federation*/}
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
    
      private void ExtractDependentFederatesofFederate(Federate from_federate, Federate to_federate) { // dependent federaets are now part of json string..so change the method
        DependentFederate depfed = new DependentFederate();
        if (from_federate.getDependentFederates().isEmpty()) {  // If there are no dependent federates in the JSON then extract from Dependencies ---
            ExtractDependentFederatesFromDependencies(from_federate, to_federate);

        } else {
            for (DependentFederate depfederate : from_federate.getDependentFederates()) {
                depfed.MakeDeepCopy(depfederate);// Redo!!//*********************************************************************************************
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


}
