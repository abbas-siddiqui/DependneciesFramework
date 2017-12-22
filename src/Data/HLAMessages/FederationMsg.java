/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.HLAMessages;

import Data.Agent.Federation;
import Interfaces.Iclearable;

/**
 *
 * @author abbas
 */
public class FederationMsg implements Cloneable , Iclearable{
    FederationControlMsg controlData;
    Federation federation;

    public FederationMsg() {
        setDefaults();
    }
    private void setDefaults()
    {
    controlData = new FederationControlMsg();
    federation = new Federation();
    }

    @Override
    public void Clear() {
        setDefaults();
    }
    
    
}
