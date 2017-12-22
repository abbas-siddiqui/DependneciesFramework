/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Data.HLAData.ObjClassandAttributes;
import Data.HLAData.PubSubAttribute;

/**
 *
 * @author abbas
 */
public interface HLASendReceiveInterface {
     
    public void UpdateAttribute(ObjClassandAttributes classAttr, PubSubAttribute attribute, String value);
    public void UpdateAttribute(String objClassName, String attributeName, String value); 
  
    public void SendInteractions() ;
       // Only used for Centeral Federates to Distribute the graph
    public void SendDependencyGraph( String type) ;
     public void PublishFederateDependencies(boolean isRandom);
}
