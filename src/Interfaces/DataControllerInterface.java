/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Data.Agent.Federation;
import java.util.Observer;

/**
 *
 * @author abbas
 */
public interface DataControllerInterface {
    public void setFederationName(String federationName) ; 
    public void setDependenciesGraph(Federation depGraphobj_a, boolean isAll);
    public void AddObserver(Observer obser);    
    void PublishState() ;
    /// Once Json Message is received from other Depedendent Federates
    public void CalculateState(String stateJson);
    public Federation getFederationGraph(); 
    public void UpdateFederateState(int index, int state);
    
}
