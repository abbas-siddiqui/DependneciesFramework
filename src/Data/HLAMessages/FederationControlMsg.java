/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.HLAMessages;

import Interfaces.Iclearable;

/**
 *
 * @author abbas
 */
public class FederationControlMsg implements Cloneable, Iclearable{
    String type;
   
      public FederationControlMsg() {
          setDefaults();
    }
    private void setDefaults()
    {
   type="Default";
    }

    @Override
    public void Clear() {
        setDefaults();
    }
}
