/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Layer;

import Data.HLAData.HLAData;
import Data.Agent.DependentFederate;
import Data.Agent.FederateDependency;
import Data.Agent.Federate;
import Data.Agent.Federation;
import Data.HLAData.InteractionClassPubSub;
import Data.HLAData.ObjClassandAttributes;
import Data.HLAData.PubSubAttribute;
import hla.rti1516e.RTIambassador;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;

/**
 *
 * @author abbas
 */
public class FederateDataLayer extends Observable {

    private String dependnecyGraphStr;

   
   
    private String federationName = "";
    private Federate federate;
    private Federation dependencyGraph;

     public String getDependnecyGraphStr() {
        return dependnecyGraphStr;
    }
      public void setDependnecyGraphStr(String dependnecyGraphStr) {
        this.dependnecyGraphStr = dependnecyGraphStr;
    }

    public void setFederationGraph(Federation dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    private HLAData hladata;

    public FederateDataLayer(RTIambassador rtiamb,  ArrayList<ObjClassandAttributes> objectClasses, URL[] fomModules_a, URL[] fed_fomModules_a) {
        setDefaults();
        hladata = new HLAData(rtiamb, objectClasses, fomModules_a, fed_fomModules_a);
    }

    public FederateDataLayer() {
        setDefaults();
    }

    private void setDefaults() {
        hladata = new HLAData();        
        ResetFederate(false);
    }
    
    private void ResetFederate(boolean isWithName)
    {
         
     if(isWithName) federate = new Federate(federate.getFederateName());
     else federate = new Federate();
    dependnecyGraphStr="";
    dependencyGraph= new Federation();
    }

    public Federate getFederate() {
        return federate;
    }

    public void setFederate(Federate federate) {
        this.federate = federate;
    }

   

    public void SetFederateName(String fedName) {
        federate.setFederateName(fedName);
    }

    public String getFederateName() {
        return federate.getFederateName();
    }

    public void SetFederationName(String fednName) {
        federationName = fednName;
    }

  

    
    public Federation getFederationGraph() {
        return dependencyGraph;
    }

    public void setFederatePerformance(int per) {
        if (per == federate.getFederateState().getPerformance()) {
            return;  //*********** if only performance has been really changed 
        }
        federate.getFederateState().setPerformance(per);

        setChanged();
        notifyObservers("Performance Changed");
    }

    public int getFederatePerformance() {
        return federate.getFederateState().getPerformance();
    }

    public String getFederationName() {
        return federationName;
    }

    public DependentFederate getDependentFederate(String fed) {
        return federate.GetDependentFederate(fed);
    }

    public ArrayList<FederateDependency> getDependendencies() {
        return federate.getDependencies();
    }

    //************************************** HLA Methods ***********************************************
    //**************************************************************************************************
    public RTIambassador getRtiamb() {
        return hladata.getRtiamb();
    }

    public void setRtiamb(RTIambassador rtiamb) {
        hladata.setRtiamb(rtiamb);
    }

    

    public boolean isIsConnected() {
        return hladata.isIsConnected();
    }

    public void setIsConnected(boolean isConnected) {
        hladata.setIsConnected(isConnected);
    }

    public URL[] getFomModules() {
        return hladata.getFomModules();
    }

    public void setFomModules(URL[] fomModules) {
        hladata.setFomModules(fomModules);
    }

    public URL[] getFed_fomModules() {
        return hladata.getFed_fomModules();
    }

    public void setFed_fomModules(URL[] fed_fomModules) {
        hladata.setFed_fomModules(fed_fomModules);
    }

    public ArrayList<ObjClassandAttributes> getObjectClasses() {
        return hladata.getObjectClasses();
    }

    public void setObjectClasses(ArrayList<ObjClassandAttributes> ObjectClasses) {
        hladata.setObjectClasses(ObjectClasses);
    }

    public ArrayList<InteractionClassPubSub> getInteractionClasses() {
        return hladata.getInteractionClasses();
    }

    public void setInteractionClasses(ArrayList<InteractionClassPubSub> interactionClasses) {
        hladata.setInteractionClasses(interactionClasses);
    }

    public void addInteractionClassInfo(String interactionStr, byte[] hndl_value, boolean isPublish, boolean isSubscribe) {

        hladata.addInteractionClassInfo(interactionStr, hndl_value, isPublish, isSubscribe);

    }

    public void SetFederationFomModules(URL[] fomModules_a) {
        hladata.SetFederationFomModules(fomModules_a);
    }

    public void SetFederateFomModules(URL[] fed_fomModules_a) {
        hladata.SetFederateFomModules(fed_fomModules_a);
    }

    public URL[] getFederateFomModules() {
        return hladata.getFederateFomModules();
    }

    public void AddClassandAttributes(RTIambassador rtiamb, String objClass, ArrayList<PubSubAttribute> attributes) {
        hladata.AddClassandAttributes(rtiamb, objClass, attributes);

    }

    public ObjClassandAttributes getClassandAttributes(String objClass) {
        return hladata.getClassandAttributes(objClass);
    }

    public void PopulateRTIRelatedData() {
        hladata.PopulateRTIRelatedData();

    }

}
