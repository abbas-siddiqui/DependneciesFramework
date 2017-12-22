/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HLAComm;

/**
 *
 * @author abbas
 */
/*
 *   Copyright 2012 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
import CommonFnc.StaticFunctions;
import CommonFnc.StaticVariables;
import Data.HLAData.ObjClassandAttributes;
import Interfaces.HLACommToDataInterface;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.time.HLAfloat64Time;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link ExampleFederate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback
 * information.
 */
public class DependencyAmbassador extends NullFederateAmbassador {
	//----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private HLACommToDataInterface datacontroller;

    // these variables are accessible in the package
    protected double federateTime = 0.0;
    protected double federateLookahead = 1.0;

    protected boolean isRegulating = false;
    protected boolean isConstrained = false;
    protected boolean isAdvancing = false;

    protected boolean isAnnounced = false;
    protected boolean isReadyToRun = false;

//    private Observable observable = new Observable();
//
//    public void addObserver(Observer o) {
//        observable.addObserver(o);
//    }
//
//    public void notifyObservers() {
//        observable.notifyObservers();
//    }
    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------
    public void SetDataController(HLACommToDataInterface datacontroller_a) {
        datacontroller = datacontroller_a;
        setDefaults();

    }

    public DependencyAmbassador() {
        setDefaults();
    }

    private void setDefaults() {
//        observable = new Observable();
    }
    //----------------------------------------------------------
    //                    INSTANCE METHODS
    //----------------------------------------------------------

    private void log(String message) {
        System.out.println("FederateAmbassador: " + message);
    }

    private String decodeJson(byte[] bytes) {
        HLAASCIIstring val_s = StaticFunctions.getEncoderFactory().createHLAASCIIstring();
        try {

            val_s.decode(bytes);

        } catch (DecoderException ex) {
            Logger.getLogger(DependencyAmbassador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val_s.getValue();
    }

    //////////////////////////////////////////////////////////////////////////
    ////////////////////////// RTI Callback Methods //////////////////////////
    //////////////////////////////////////////////////////////////////////////
    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject, //??????????????????????????????????
            AttributeHandleValueMap theAttributes,
            byte[] tag,
            OrderType sentOrdering,
            TransportationTypeHandle theTransport,
            LogicalTime time,
            OrderType receivedOrdering,
            FederateAmbassador.SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Reflection for object:");

        // print the handle
        builder.append(" handle=" + theObject);
        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the attribute information
        builder.append(", attributeCount=" + theAttributes.size());
        builder.append("\n");

        ObjClassandAttributes stateobjClass = datacontroller.getClassandAttributes(StaticVariables.FEDERATE_STATE_CLASS);
        ObjClassandAttributes objClassdatalayer = datacontroller.getClassandAttributes(StaticVariables.DEPENDENCY_GRAPH_CLASS);
        for (AttributeHandle attributeHandle : theAttributes.keySet()) {
          //  builder.append("\tattributeHandle=");
            // builder.append(" Well lets try that ******************************" + theAttributes.get(attributeHandle));
            if (attributeHandle.equals(objClassdatalayer.getAtrributeHandle(StaticVariables.DEPENDECY_GRAPH_ATTR))) {
                builder.append(attributeHandle);
                //  builder.append(" (Flavor)    ");
                builder.append(", attributeValue=" + attributeHandle.toString());
                String jsonString = decodeJson(theAttributes.get(attributeHandle));
                // builder.append("Does it work??" + jsonString);
                datacontroller.ForwardReceivedMessage(StaticVariables.DEPENDECY_GRAPH_ATTR, jsonString);

            }
            if (attributeHandle.equals(stateobjClass.getAtrributeHandle(StaticVariables.FEDERATE_STATE_ATTR))) {
                builder.append(attributeHandle);

                builder.append("State has been discovered");
                String jsonString = decodeJson(theAttributes.get(attributeHandle));
                builder.append(" /n  State Is" + jsonString);
                // datacontroller.CalculateState(jsonString);
                datacontroller.ForwardReceivedMessage(StaticVariables.FEDERATE_STATE_ATTR, jsonString);
            }
            if (attributeHandle.equals(stateobjClass.getAtrributeHandle(StaticVariables.FEDERATE_DEPENDENCY_ATTR))) {
                builder.append(attributeHandle);
                builder.append("New Dependency of a Single Federate");
                String jsonString = decodeJson(theAttributes.get(attributeHandle));
                builder.append(" /n  Dependnecy Is" + jsonString);
                datacontroller.ForwardReceivedMessage(StaticVariables.FEDERATE_DEPENDENCY_ATTR, jsonString);
            }
            {
                //	builder.append( attributeHandle );
                //	builder.append( " (Unknown)   " );
            }
            builder.append("\n");
        }

        log(builder.toString());
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
            ParameterHandleValueMap theParameters,
            byte[] tag,
            OrderType sentOrdering,
            TransportationTypeHandle theTransport,
            LogicalTime time,
            OrderType receivedOrdering,
            FederateAmbassador.SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {
        StringBuilder builder = new StringBuilder("Interaction Received:");

        // print the handle
        builder.append(" handle=" + interactionClass.toString());
//		if( interactionClass.equals(datal.servedHandle) )
//		{
//			builder.append( " (DrinkServed)" );
//		}

        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=" + theParameters.size());
        builder.append("\n");
        for (ParameterHandle parameter : theParameters.keySet()) {
            // print the parameter handle
            builder.append("\tparamHandle=");
            builder.append(parameter);
            // print the parameter value
            builder.append(", paramValue=");
            builder.append(theParameters.get(parameter).length);
            builder.append(" bytes");
            builder.append("\n");
        }

        log(builder.toString());
    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject,
            byte[] tag,
            OrderType sentOrdering,
            FederateAmbassador.SupplementalRemoveInfo removeInfo)
            throws FederateInternalError {
        log("Object Removed: handle=" + theObject);
    }

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    @Override
    public void timeRegulationEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isAdvancing = false;
    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject,
            ObjectClassHandle theObjectClass,
            String objectName)
            throws FederateInternalError {
        log("Discoverd Object: handle=" + theObject + ", classHandle="
                + theObjectClass + ", name=" + objectName);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
            ParameterHandleValueMap theParameters,
            byte[] tag,
            OrderType sentOrdering,
            TransportationTypeHandle theTransport,
            FederateAmbassador.SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        this.receiveInteraction(interactionClass,
                theParameters,
                tag,
                sentOrdering,
                theTransport,
                null,
                sentOrdering,
                receiveInfo);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
            AttributeHandleValueMap theAttributes,
            byte[] tag,
            OrderType sentOrder,
            TransportationTypeHandle transport,
            FederateAmbassador.SupplementalReflectInfo reflectInfo)
            throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        reflectAttributeValues(theObject,
                theAttributes,
                tag,
                sentOrder,
                transport,
                null,
                sentOrder,
                reflectInfo);
    }

    @Override
    public void synchronizationPointRegistrationFailed(String label,
            SynchronizationPointFailureReason reason) {
        log("Failed to register sync point: " + label + ", reason=" + reason);
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    @Override
    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(StaticVariables.READY_TO_RUN)) {
            this.isAnnounced = true;
        }
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(StaticVariables.READY_TO_RUN)) {
            this.isReadyToRun = true;
        }
    }
	//----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
}
