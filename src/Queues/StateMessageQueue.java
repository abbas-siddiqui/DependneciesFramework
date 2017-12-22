/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Queues;

import CommonFnc.StaticFunctions;
import CommonFnc.StaticVariables;
import Data.Agent.DependentFederate;
import Data.Agent.FederateDependency;
import Data.Agent.FederateState;
import Data.HLAMessages.FederationGenericMsg;
import Data.Layer.FederationDataLayer;
import com.google.common.collect.EvictingQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class StateMessageQueue implements Runnable {

    Queue<FederationGenericMsg<FederateState>> federateStateMsgs;
    private FederationDataLayer datal;
    private boolean isRunning = true;

    public StateMessageQueue(FederationDataLayer data_a) {
        datal = data_a;
        setDefaults();
    }

    private void setDefaults() {
        federateStateMsgs = EvictingQueue.create(StaticVariables.STATE_MSG_Q_SIZE);
        isRunning = true;
    }

    public void Stop() {
        isRunning = false;
    }

    public void Add(FederationGenericMsg<FederateState> newstate) {
        federateStateMsgs.add(newstate);
    }

    public FederationGenericMsg<FederateState> Poll() {
        return federateStateMsgs.poll();
    }

    public void StartThread() {
        Thread t = new Thread(this, "Admin Thread");

        // prints thread created
        System.out.println("thread  = " + t);

        // this will call run() function
        System.out.println("Calling run() function... ");
        t.start();
    }

    public void PrintQ() {

        System.out.print(federateStateMsgs.poll().getFederateName());
//        for (FederationGenericMsg<FederateState> msg: federateStateMsgs)
//        {
//    System.out.println(msg.getFederateName());
//        }
    }

    @Override
    public void run() {

        while (isRunning) {
            if (!federateStateMsgs.isEmpty()) {
                CalculateState(federateStateMsgs.poll());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(StateMessageQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void CalculateState(FederationGenericMsg<FederateState> newFederateStateMsg) {

        // Search for the this dependent federate
        DependentFederate depfed = datal.getFederate(StaticVariables.DEFAULT_FEDERATE).GetDependentFederate(newFederateStateMsg.getFederateName());

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
        for (FederateDependency dep : datal.getFederate(StaticVariables.DEFAULT_FEDERATE).getDependencies()) {
            if (dep.getType().equalsIgnoreCase("AND")) {
                if (dep.IsDependentOnIt(depfed.getFederateName())) // only if changed federate is part of this dependency 
                {
                    datal.setFederatePerformance(StaticVariables.DEFAULT_FEDERATE, StaticFunctions.ConvertBooleantoInt(StaticFunctions.ANDDepEval(datal.getFederate(StaticVariables.DEFAULT_FEDERATE), dep)));
                }
            }

            if (dep.getType().equalsIgnoreCase("OR")) // only if changed federate is part of this dependency
            {
                if (dep.IsDependentOnIt(depfed.getFederateName())) // only if changed federate is part of this dependency 
                {
                    datal.setFederatePerformance(StaticVariables.DEFAULT_FEDERATE, StaticFunctions.ConvertBooleantoInt(StaticFunctions.ORDepEval(datal.getFederate(StaticVariables.DEFAULT_FEDERATE), dep)));
                }
            }

        }

    }
//    public static void main(String[] arg)
//    {
//        StateMessageQueue q = new StateMessageQueue(null);
//        FederationGenericMsg<FederateState> msg = new FederationGenericMsg();
//        msg.setFederateName("1");
//        q.Add(msg);
//        msg = new FederationGenericMsg();
//        msg.setFederateName("2");
//        q.Add(msg);
//        msg = new FederationGenericMsg();
//        msg.setFederateName("3");
//         q.Add(msg);
//         msg = new FederationGenericMsg();
//        msg.setFederateName("4");
//         q.Add(msg);
//         msg = new FederationGenericMsg();
//        msg.setFederateName("5");
//        q.Add(msg);
//        
//        q.PrintQ();     
//        
//        
//    
//    }
}
