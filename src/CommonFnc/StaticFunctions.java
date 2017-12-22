/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonFnc;

import Data.Agent.DependentFederate;
import Data.Agent.FederateDependency;
import Data.Agent.Federate;
import Data.HLAData.InteractionClassPubSub;
import Data.HLAData.PubSubAttribute;
import Data.Layer.FederationDataLayer;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.exceptions.RTIinternalError;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class StaticFunctions {

    private static EncoderFactory ENCODER_FACTORY;

    public static byte[] toByteArray(int value) {
        return new byte[]{
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value};
    }

    public static EncoderFactory getEncoderFactory() {

        try {
            if (ENCODER_FACTORY == null) {
                ENCODER_FACTORY = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
            }

        } catch (RTIinternalError ex) {
            Logger.getLogger(StaticFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ENCODER_FACTORY;
    }

    public static boolean ConvertInttoBoolean(int state) {
        boolean isEnabled = false;
        if (state > 0) {
            isEnabled = true;
        } else if (state == 0) {
            isEnabled = false;
        }

        return isEnabled;
    }

    public static int ConvertBooleantoInt(boolean state) {
        int per = 0;
        if (state) {
            per = 1;
        }

        return per;
    }

    public static boolean ANDDepEval(Federate federate, FederateDependency dep) {

        int i = 0;
        DependentFederate depfed = federate.GetDependentFederate(dep.getDependentFederatesNames().get(0));
        boolean isEnabled = StaticFunctions.ConvertInttoBoolean(depfed.getPerformance());
        for (String federateName : dep.getDependentFederatesNames()) {

            depfed = federate.GetDependentFederate(federateName);
            if (i == 0) {

            } else {
                isEnabled = isEnabled && StaticFunctions.ConvertInttoBoolean(depfed.getPerformance());
            }
            i++;
        }

        return isEnabled;
    }

    public static boolean ORDepEval(Federate federate, FederateDependency dep) {

        int i = 0;
        DependentFederate depfed = federate.GetDependentFederate(dep.getDependentFederatesNames().get(0));
        boolean isEnabled = StaticFunctions.ConvertInttoBoolean(depfed.getPerformance());
        for (String federateName : dep.getDependentFederatesNames()) {

            depfed = federate.GetDependentFederate(federateName);
            if (i == 0) {

            } else {
                isEnabled = isEnabled || StaticFunctions.ConvertInttoBoolean(depfed.getPerformance());
            }
            i++;
        }

        return isEnabled;
    }

    public static String decodeHLAStringToJavaString(byte[] bytes) {
        HLAASCIIstring val_s = getEncoderFactory().createHLAASCIIstring();
        try {

            val_s.decode(bytes);

        } catch (DecoderException ex) {
            Logger.getLogger(StaticFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val_s.getValue();
    }

    public static Federate MakeDeepCopy(Federate fed) {
        Federate depfed = new Federate();
        depfed.setFederateName(fed.getFederateName());
        depfed.setPerformance(fed.getPerformance());

        return depfed;

    }

    public static void MakeDeepCopy(Federate tofederate, DependentFederate fed) {

        tofederate.setFederateName(fed.getFederateName());
        tofederate.setPerformance(fed.getPerformance());

    }

    public static String GetRandomDependencyType() {
        String type = StaticVariables.AND_DEP;
        int rn = 1 + (int) Math.floor(Math.random() * StaticVariables.TYPES_OF_DEP); // Temporary numbers will be re
        switch (rn) {
            case 3:
                type = StaticVariables.OR_DEP;
                break;
            case 1:
                type = StaticVariables.AND_DEP;
                break;
            case 2:
                type = StaticVariables.OR_DEP;
                break;

        }

        return type;
    }

    public static void AddFederateModules(FederationDataLayer datal, String location) {
        try {

            datal.SetFederateFomModules(GetFederateModules(location));

        } catch (MalformedURLException ex) {
            Logger.getLogger(StaticFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void AddFederationModules(FederationDataLayer datal, String location) {

        try {
            datal.SetFederationFomModules(GetFederationModules(location));
        } catch (MalformedURLException ex) {
            Logger.getLogger(StaticFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // seperate function so that we can read the names of the federates from different locations
    public static URL[] GetFederateModules(String location) throws MalformedURLException {
        URL[] joinModules1 = null;
        if (location == null || location.equals(StaticVariables.MODULES_DEFAULT_LOCATION)) // Default Location  
        {
            joinModules1 = new URL[]{
                (new File(StaticVariables.FEDERATE_MOD1)).toURI().toURL()
            };
        }

        return joinModules1;
    }

    // seperate function so that we can read the names of the federates from different locations
    public static URL[] GetFederationModules(String location) throws MalformedURLException {

        URL[] fomModules = new URL[]{
            (new File(StaticVariables.FEDERATION_MOD_1)).toURI().toURL(),};

        return fomModules;
    }

    public static void AddGeneralInteractionClasses(ArrayList<InteractionClassPubSub> interactionClasses) {
        InteractionClassPubSub interClass = new InteractionClassPubSub();
        interClass.interactionStr = StaticVariables.INTERACTION_CLASS1;
        interClass.setIsPublish(true);
        interClass.setIsSubscribe(true);
        interactionClasses.add(interClass);
    }

    public static void AddGeneralClassesAndAttributes(FederationDataLayer datal) {
        ArrayList<PubSubAttribute> attributes = new ArrayList<>();
        AddAttribute(attributes, StaticVariables.DEPENDECY_GRAPH_ATTR, true, true, StaticVariables.HLASTRING);
        datal.AddClassandAttributes(datal.getRtiamb(), StaticVariables.DEPENDENCY_GRAPH_CLASS, attributes);

        attributes = new ArrayList<>();
        AddAttribute(attributes, StaticVariables.FEDERATE_STATE_ATTR, true, true, StaticVariables.HLASTRING);
        AddAttribute(attributes, StaticVariables.FEDERATE_DEPENDENCY_ATTR, true, true, StaticVariables.HLASTRING);
        datal.AddClassandAttributes(datal.getRtiamb(), StaticVariables.FEDERATE_STATE_CLASS, attributes);
    }

    public static void AddAttribute(ArrayList<PubSubAttribute> attributes, String name, boolean isPublish, boolean isSubscribe, String type) {
        PubSubAttribute attr = null;
        if (type.equals("")) {
            attr = new PubSubAttribute();
        } else {
            attr = new PubSubAttribute(type);
        }

        attr.setAttributestr(name);
        attr.setIsPublish(isPublish);
        attr.setIsSubscribe(isSubscribe);

        attributes.add(attr);
    }

    public static int[] Sample(double[] pp, int q) {
        double[] cdf = pp.clone();
        for (int i = 1; i < cdf.length; i++) {
            cdf[i] += cdf[i - 1];
        }

        for (int i = 0; i < cdf.length; i++) {
            System.out.println(cdf[i]);
        }

        int[] values = new int[q];
        for (int i = 0; i < q; i++) {
            // binarySearch returns a negative number at the insertion point
            // if the exact number isn't found. We don't expect to hit an
            // exact match with Math.random(), so the Math.abs() call
            // can't be too expensive.
            values[i] = Math.abs(Arrays.binarySearch(cdf, Math.random()));
        }

        return values;
    }
}
