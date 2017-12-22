/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Data.Agent.Federation;

/**
 *
 * @author abbas
 */
public interface FederationDataInterface {
  public void setDependenciesGraph(Federation depGraphobj_a, boolean isAll);
     public void setFederationName(String federationName) ; 
}
