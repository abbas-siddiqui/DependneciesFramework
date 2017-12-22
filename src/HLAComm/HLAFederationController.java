package HLAComm;

import Data.HLAData.InteractionClassPubSub;
import Data.HLAData.ObjClassandAttributes;
import Data.HLAData.PubSubAttribute;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateNameAlreadyInUse;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.InTimeAdvancingState;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidLookahead;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RequestForTimeConstrainedPending;
import hla.rti1516e.exceptions.RequestForTimeRegulationPending;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
import hla.rti1516e.exceptions.TimeConstrainedAlreadyEnabled;
import hla.rti1516e.exceptions.TimeRegulationAlreadyEnabled;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class HLAFederationController {

    public static final String READY_TO_RUN = "ReadyToRun";

    private void DefaultInit() {
    }

  
    public void RunIt(RTIambassador rtiamb, DependencyAmbassador fedamb, String federationName, ArrayList<ObjClassandAttributes> objclattr, ArrayList<InteractionClassPubSub> interHndls) {

        try {
          
            publishAndSubscribe(rtiamb, objclattr, interHndls);
            log("Published and Subscribed");
            
            

            ObjectClassHandle objHnd = objclattr.get(0).getObjectClassHndl();
            RegisterObject(rtiamb, objHnd);


        } catch (RTIinternalError ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void CreateFederation(String federationName, URL[] fomModules, RTIambassador rtiamb, DependencyAmbassador fedamb) {
        log("Creating RTIambassador");

        try {
            rtiamb.connect(fedamb, CallbackModel.HLA_IMMEDIATE);
        } catch (RTIinternalError | ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }

        log("Creating Federation...");

        try {

            rtiamb.createFederationExecution(federationName, fomModules);
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError ex) {
            log("All other exception during federation creation: " + ex.getMessage());
        }

    }

//    private void AddDefaultFederate(String federationName, DependencyAmbassador fedamb, RTIambassador rtiamb) throws NotConnected, FederateNotExecutionMember {
//
//        try {
//
//          
//
//            AddFederate(joinModules, federationName, "ExampleFederate", "ExampleFederateType", fedamb, rtiamb);
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

//    public void AddFedearte(String federationName, String federateName, DependencyAmbassador fedamb, RTIambassador rtiamb) {
//        try {
//            URL[] joinModules = new URL[]{
//                (new File("src/foms/FederateFOM.xml")).toURI().toURL()
//            };
//            AddFederate(joinModules, federationName, federateName, "DependencyFederateType", fedamb, rtiamb);
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void AddFederate(URL[] joinModules, String federationName, String federateName, String federateType, DependencyAmbassador fedamb, RTIambassador rtiamb) {
        try {
            HLAfloat64TimeFactory timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();
            try {
                rtiamb.joinFederationExecution(federateName, // name for the federate
                        federateType, // federate type
                        federationName, // name of federation
                        joinModules);           // modules we want to add
            } catch (CouldNotCreateLogicalTimeFactory | FederateNameAlreadyInUse ex) {
                Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
            }

            log("Joined Federation as " + federateName);

            // cache the time factory for easy access
            timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();
        } catch (FederationExecutionDoesNotExist | InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | SaveInProgress | RestoreInProgress | FederateAlreadyExecutionMember | NotConnected | CallNotAllowedFromWithinCallback | RTIinternalError | FederateNotExecutionMember ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void AnnounceSyncPoint(RTIambassador rtiamb, DependencyAmbassador fedamb) {
        try {
            rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        } catch (SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (fedamb.isAnnounced == false) {
            try {
                rtiamb.evokeMultipleCallbacks(0.1, 0.2);
            } catch (CallNotAllowedFromWithinCallback | RTIinternalError ex) {
                Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void WaitForSynchronization(RTIambassador rtiamb, DependencyAmbassador fedamb) {
        try {
            ///////////////////////////////////////////////////////
            // 6. achieve the point and wait for synchronization //
            ///////////////////////////////////////////////////////
            // tell the RTI we are ready to move past the sync point and then wait
            // until the federation has synchronized on
            rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        } catch (SynchronizationPointLabelNotAnnounced | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (fedamb.isReadyToRun == false) {
            try {
                rtiamb.evokeMultipleCallbacks(0.1, 0.2);
            } catch (CallNotAllowedFromWithinCallback | RTIinternalError ex) {
                Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void publishAndSubscribe(RTIambassador rtiamb, ArrayList<ObjClassandAttributes> objclattr, ArrayList<InteractionClassPubSub> interClass) {

        try {
            // publish all attributes of Food.Drink.Soda //
            // get all the handle information for the attributes of Food.Drink.Soda
            // package the information into a handle set
            for (ObjClassandAttributes objclattr1 : objclattr) {
                AttributeHandleSet attributes = rtiamb.getAttributeHandleSetFactory().create();
               // ObjClassandAttributes classattr = objclattr1;
               objclattr1.generateObjectClassHandle(rtiamb);
                ObjectClassHandle objClassHandl = objclattr1.getObjectClassHndl();
                
                 
             for (Map.Entry<String, PubSubAttribute> entry : objclattr1.psattributes.entrySet()) {
                String key = entry.getKey();
                PubSubAttribute attr = entry.getValue();
                objclattr1. generateAttributesHandle(rtiamb);
                    AttributeHandle attribute = attr.getAttrHndl();
                   
                    attributes.add(attribute);

                    if (attr.isIsPublish()) 
                        rtiamb.publishObjectClassAttributes(objClassHandl, attributes);
                    
                    if (attr.isIsSubscribe()) 
                        rtiamb.subscribeObjectClassAttributes(objClassHandl, attributes);
                    

                }
             objclattr1.generateObjectInstanceHandle(rtiamb);
            }
        } catch (FederateNotExecutionMember | NotConnected | RTIinternalError | SaveInProgress | RestoreInProgress | AttributeNotDefined | ObjectClassNotDefined ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        try {
        ///*****************Interaction Classes***********************
            //String iname = "HLAinteractionRoot.CustomerTransactions.FoodServed.DrinkServed";
            for (InteractionClassPubSub interClas : interClass) {

                InteractionClassHandle classhndl = rtiamb.getInteractionClassHandle(interClas.interactionStr);
                if (interClas.isIsPublish()) {
                    rtiamb.publishInteractionClass(classhndl);
                }
                if (interClas.isIsSubscribe()) {
                    rtiamb.subscribeInteractionClass(classhndl);
                }
            }
      
        } catch (FederateServiceInvocationsAreBeingReportedViaMOM | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError | NameNotFound ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method will send out an interaction of the type
     * FoodServed.DrinkServed. Any federates which are subscribed to it will
     * receive a notification the next time they tick(). This particular
     * interaction has no parameters, so you pass an empty map, but the process
     * of encoding them is the same as for attributes.
     */
    public void sendInteraction(RTIambassador rtiamb, DependencyAmbassador fedamb, ArrayList<InteractionClassPubSub> interactions) throws RTIexception {
        //////////////////////////
        // send the interaction //
        //////////////////////////
        for (InteractionClassPubSub interaction : interactions) {
            ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(1);

            InteractionClassHandle interHndl = rtiamb.getInteractionClassHandle(interaction.interactionStr);
            //parameters.put( rtiamb.getParameterHandle(interHndl,"1"),
            //			         parameters.get("1") );
            rtiamb.sendInteraction(interHndl, parameters, generateTag());

        }
    }

 
    protected ObjectInstanceHandle RegisterObject(RTIambassador rtiamb, ObjectClassHandle objClassHndl) throws RTIinternalError {

        ObjectInstanceHandle objectHandle = null;
        try {
            EncoderFactory encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
            objectHandle = rtiamb.registerObjectInstance(objClassHndl);
            log("Registered Object, handle=" + objectHandle);

        } catch (ObjectClassNotPublished | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return objectHandle;
    }

    public void updateAttributeValue(RTIambassador rtiamb, DependencyAmbassador fedamb, ObjClassandAttributes classnattribute, PubSubAttribute attribute) throws RTIexception {
     

        AttributeHandleValueMap attributes = rtiamb.getAttributeHandleValueMapFactory().create(1);

        AttributeHandle hndl = attribute.getAttrHndl();

        // HLAinteger16BE cupsValue = encoderFactory.createHLAinteger16BE((short) fedamb.federateTime);
        attributes.put(hndl, attribute.GetValue());

        rtiamb.updateAttributeValues(classnattribute.getObjectInstanceHandle(), attributes, generateTag());
    }
 public void updateAttributeValue(RTIambassador rtiamb, DependencyAmbassador fedamb, ObjClassandAttributes classnattribute,String attributestr, byte[] val) throws RTIexception {
     

        AttributeHandleValueMap attributes = rtiamb.getAttributeHandleValueMapFactory().create(1);

        AttributeHandle hndl = classnattribute.getAtrributeHandle(attributestr);


        attributes.put(hndl, val);
    
        rtiamb.updateAttributeValues(classnattribute.getObjectInstanceHandle(), attributes, generateTag());
    }
  

    private void enableTimePolicy(RTIambassador rtiamb, DependencyAmbassador fedamb) throws FederateNotExecutionMember, NotConnected {
        HLAfloat64TimeFactory timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();
        try {
            HLAfloat64Interval lookahead = timeFactory.makeInterval(fedamb.federateLookahead);

            rtiamb.enableTimeRegulation(lookahead);  // enable time regulation //

            // tick until we get the callback
            while (fedamb.isRegulating == false) {
                rtiamb.evokeMultipleCallbacks(0.1, 0.2);
            }

            rtiamb.enableTimeConstrained();   // enable time constrained //

            // tick until we get the callback
            while (fedamb.isConstrained == false) {
                rtiamb.evokeMultipleCallbacks(0.1, 0.2);
            }

            log("Time Policy Enabled");
        } catch (InvalidLookahead | InTimeAdvancingState | RequestForTimeRegulationPending | TimeRegulationAlreadyEnabled | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError | CallNotAllowedFromWithinCallback | RequestForTimeConstrainedPending | TimeConstrainedAlreadyEnabled ex) {
            Logger.getLogger(HLAFederationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void advanceTime(RTIambassador rtiamb, DependencyAmbassador fedamb, double timestep) throws RTIexception {
        // request the advance
        HLAfloat64TimeFactory timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();
        fedamb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime(fedamb.federateTime + timestep);
        rtiamb.timeAdvanceRequest(time);

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while (fedamb.isAdvancing) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }


    private void log(String message) {
        System.out.println("ExampleFederate   : " + message);
    }

    // 11. delete the object we created //
    private void deleteObject(RTIambassador rtiamb, ObjectInstanceHandle handle) throws RTIexception {
        rtiamb.deleteObjectInstance(handle, generateTag());
    }

    private short getTimeAsShort(DependencyAmbassador fedamb) {
        return (short) fedamb.federateTime;
        // return 0; //rewrite
    }

    private byte[] generateTag() {
        return ("(timestamp) " + System.currentTimeMillis()).getBytes();
    }

}
