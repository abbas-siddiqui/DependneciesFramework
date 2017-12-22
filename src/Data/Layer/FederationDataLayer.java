/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Layer;

import Data.Agent.DependentFederate;
import Data.Agent.Federate;
import Data.Agent.FederateDependency;
import Data.Agent.FederateState;
import Data.HLAData.PubSubAttribute;
import Data.HLAData.ObjClassandAttributes;
import Data.Agent.Federation;
import Data.HLAData.HLAData;
import Data.HLAData.InteractionClassPubSub;
import hla.rti1516e.RTIambassador;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author abbas
 */
public class FederationDataLayer extends Observable {

    private Federation federation;
    private HLAData hladata;
    private String depdenyGraphMsg;

    public String getDepdenyGraph() {
        return depdenyGraphMsg;
    }

    public void setDepdenyGraph(String depdenyGraphMsg) {
        this.depdenyGraphMsg = depdenyGraphMsg;
    }

    public FederationDataLayer(RTIambassador rtiamb, ArrayList<ObjClassandAttributes> objectClasses, URL[] fomModules_a, URL[] fed_fomModules_a) {
        setDefaults();
        hladata = new HLAData(rtiamb, objectClasses, fomModules_a, fed_fomModules_a);
    }

    public FederationDataLayer() {
        setDefaults();
    }

    private void setDefaults() {
        
        ResetFederate(false);
        hladata = new HLAData();
        federation = new Federation();
        Federate federate = new Federate();
        federation.getFederates().add(federate);
        depdenyGraphMsg = "";
        
      
        
    }

    private void ResetFederate(boolean isWithName) {

//     if(isWithName) federate = new Federate(federate.getFederateName());
//     else federate = new Federate();
//    dependnecyGraphStr="";
//    dependencyGraph= new Federation();
    }

    public Federate getFedeate(String name) {
        return federation.getFederate(name);
    }

    public Federate getFederate(int index) { /// If it is only single federate --- this method retrieves first federate
        return federation.getFederate(index);
    }

   
    public void setFederate(int index, Federate federate) {
        federation.setFederate(index, federate);
    }

    public String getDependnecyGraphJson() {
        return depdenyGraphMsg;
    }

    public void setFederateName(int index, String fedName) {
        federation.SetFederateName(index, fedName);
    }

    public String getFederateName(int index) {
        return federation.getFederateName(index);
    }

    public void setFederationName(String fednName) {
        federation.setName(fednName);
    }

    public Federation getFederation() {
        return federation;
    }

    public void setFederatePerformance(int index, int per) {
        if (federation.setFederatePerformance(index, per)) {
            setChanged();
            notifyObservers("Performance Changed");
        }
    }

    public void setFederateState(int index, FederateState state) {
        if (federation.setFederateSate(index, state)) {
            setChanged();
            notifyObservers("Performance Changed");
        }
    }

    public int getFederatePerformance(int index) {
        return federation.getFederatePerformance(index);
    }

    public String getFederationName() {
        return federation.getName();
    }

    public DependentFederate getDependentFederate(int index, String fed) {
        return federation.getDependentFederate(index, fed);
    }
   public Set<DependentFederate> getDependentFederates(int index) {
        return federation.getDependentFederates(index);
    }
    public ArrayList<FederateDependency> getDependendencies(int index) {
        return federation.getDependendencies(index);
    }

    public void setNewFederation(Federation newfederation) {
        federation.Clear(); //Clear to avoid memory leakage
        federation = newfederation;
       setChanged();
        notifyObservers("Federation Changed"); // Federation Graph has been updated

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
