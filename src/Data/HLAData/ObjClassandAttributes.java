/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.HLAData;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class ObjClassandAttributes {

    private String classHndlString;
    private ObjectClassHandle objClassHandl;
    private ObjectInstanceHandle objeInsHndl;

    /**
     *
     */
    public HashMap<String, PubSubAttribute> psattributes;

    public ObjClassandAttributes() {
        psattributes = new HashMap<>();
        classHndlString = "";
    }

    public void AddobjClassString(String classHndlStringa) {

        classHndlString = classHndlStringa;

    }

    public void addAttributes(ArrayList<PubSubAttribute> psattributs) {

        for (PubSubAttribute psattribut : psattributs) {
            psattributes.put(psattribut.getAttributestr(), psattribut);
        }

    }

    public ObjectInstanceHandle getObjectInstanceHandle() {

        return objeInsHndl;
    }

    public void generateAttributesHandle(RTIambassador rtiamb) {
        try {

            for (Map.Entry<String, PubSubAttribute> entry : psattributes.entrySet()) {
                String key = entry.getKey();
                PubSubAttribute attr = entry.getValue();

                attr.setAttrHndl( rtiamb.getAttributeHandle(objClassHandl, attr.getAttributestr()));

            }
        } catch (NameNotFound | InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            Logger.getLogger(ObjClassandAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateObjectInstanceHandle(RTIambassador rtiamb) {
        try {
            objeInsHndl = rtiamb.registerObjectInstance(objClassHandl);

        } catch (ObjectClassNotPublished | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            Logger.getLogger(ObjClassandAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateObjectClassHandle(RTIambassador rtiamb) {
        try {
              if (objClassHandl ==null) 
            objClassHandl = rtiamb.getObjectClassHandle(classHndlString);
        } catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            Logger.getLogger(ObjClassandAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObjectClassHandle getObjectClassHndl() {
      
        return objClassHandl;
    }

    public HashMap<String, PubSubAttribute> getAttributes() {
        return psattributes;
    }

    public int getAttributesSize() {
        return psattributes.size();
    }

    public boolean isPublish(String attribute_a) {
        return psattributes.get(attribute_a).isIsPublish();
    }

    public boolean isSubscribe(String attribute_a) {
        return psattributes.get(attribute_a).isIsSubscribe();
    }

    public AttributeHandle getAtrributeHandle(String attribute_a) {
        return psattributes.get(attribute_a).getAttrHndl();

    }

    public byte[] getAtrributeValue(String attribute_a) {
        return psattributes.get(attribute_a).GetValue();

    }
    
     public PubSubAttribute getAttribute(String attribute_a) {
      return psattributes.get(attribute_a);
    }

    @Override
    public String toString() {
        return classHndlString;
    }

    public void PopulateRTIRelatedData(RTIambassador rtiamb) {
        generateObjectClassHandle(rtiamb);
        generateObjectInstanceHandle(rtiamb);
        generateAttributesHandle(rtiamb);

    }
}
