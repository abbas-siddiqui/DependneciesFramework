/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Data.HLAData.ObjClassandAttributes;
import java.util.ArrayList;
import java.util.Observer;

/**
 *
 * @author abbas
 */
public interface HLACommToDataInterface {

    public ObjClassandAttributes getClassandAttributes(String objClassName);

    public void ForwardReceivedMessage(String type, Object message);  

    public ArrayList<ObjClassandAttributes> getObjectClasses();

    public ObjClassandAttributes getObjectClasse(int index);

    public void AddObserver(Observer obser);

}
