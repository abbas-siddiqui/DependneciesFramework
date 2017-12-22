/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.HLAData;

import Data.Layer.FederateDataLayer;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.RTIinternalError;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class HLAData {

    public HLAData() {
        setDefaults();
    }
    public RTIambassador rtiamb;

    private boolean isConnected = false;
    public URL[] fomModules = null;
    private URL[] fed_fomModules = null;
    // caches of handle types - set once we join a federation
    public ArrayList<ObjClassandAttributes> ObjectClasses;
    public ArrayList<InteractionClassPubSub> interactionClasses;

    public HLAData(RTIambassador rtiamb, ArrayList<ObjClassandAttributes> ObjectClasses, URL[] fomModules_a, URL[] fed_fomModules_a) {
        setDefaults();
        this.rtiamb = rtiamb;

        this.ObjectClasses = ObjectClasses;
        fed_fomModules = fed_fomModules_a;
        fomModules = fomModules_a;
    }

    private void setDefaults() {
        try {
            rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();

            ObjectClasses = new ArrayList<>();
            interactionClasses = new ArrayList<>();
            fomModules = new URL[]{};
            fed_fomModules = new URL[]{};

        } catch (RTIinternalError ex) {
            Logger.getLogger(FederateDataLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RTIambassador getRtiamb() {
        return rtiamb;
    }

    public void setRtiamb(RTIambassador rtiamb) {
        this.rtiamb = rtiamb;
    }

    public boolean isIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public URL[] getFomModules() {
        return fomModules;
    }

    public void setFomModules(URL[] fomModules) {
        this.fomModules = fomModules;
    }

    public URL[] getFed_fomModules() {
        return fed_fomModules;
    }

    public void setFed_fomModules(URL[] fed_fomModules) {
        this.fed_fomModules = fed_fomModules;
    }

    public ArrayList<ObjClassandAttributes> getObjectClasses() {
        return ObjectClasses;
    }

    public void setObjectClasses(ArrayList<ObjClassandAttributes> ObjectClasses) {
        this.ObjectClasses = ObjectClasses;
    }

    public ArrayList<InteractionClassPubSub> getInteractionClasses() {
        return interactionClasses;
    }

    public void setInteractionClasses(ArrayList<InteractionClassPubSub> interactionClasses) {
        this.interactionClasses = interactionClasses;
    }

    public void addInteractionClassInfo(String interactionStr, byte[] hndl_value, boolean isPublish, boolean isSubscribe) {

        InteractionClassPubSub inClass = new InteractionClassPubSub(interactionStr, hndl_value, isPublish, isSubscribe);

        interactionClasses.add(inClass);

    }

    public void SetFederationFomModules(URL[] fomModules_a) {
        fomModules = fomModules_a;
    }

    public void SetFederateFomModules(URL[] fed_fomModules_a) {
        fed_fomModules = fed_fomModules_a;
    }

    public URL[] getFederateFomModules() {
        return fed_fomModules;
    }

    public void AddClassandAttributes(RTIambassador rtiamb, String objClass, ArrayList<PubSubAttribute> attributes) {
        ObjClassandAttributes objclattr;
        objclattr = new ObjClassandAttributes();
        objclattr.AddobjClassString(objClass);
        objclattr.addAttributes(attributes);
        ObjectClasses.add(objclattr);

    }

    public ObjClassandAttributes getClassandAttributes(String objClass) {
        for (ObjClassandAttributes cla : ObjectClasses) {
            if (cla.toString().equals(objClass)) {
                return cla;
            }

        }
        return null;
    }

    public void PopulateRTIRelatedData() {
        for (ObjClassandAttributes ObjectClass : ObjectClasses) {
            ObjectClass.PopulateRTIRelatedData(rtiamb);
        }

    }

}
