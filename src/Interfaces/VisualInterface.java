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
public interface VisualInterface {

    public String getFederateName();   
    public Federation getFederationGraph();    
    public void AddObserver(Observer obser);    
     public boolean getAgentPerformanceBol(int index) ;
    public boolean getAgentPerformanceBol(String federateName);
    
    
}
