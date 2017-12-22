/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Agent;

import Interfaces.Iclearable;

/**
 *
 * @author abbas
 */
public class FederateState implements Cloneable, Iclearable{

   
    private int performance;
  

   
    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public FederateState() {
        setDefaults();
    }
    
    private void setDefaults()
    {
    performance =1;    
    }

    @Override
    public void Clear() {
        setDefaults();
    }
    
     @Override
    public Object clone(){
       FederateState fedState= new FederateState();
       fedState.performance = this.performance;
       return fedState;
    }
}
