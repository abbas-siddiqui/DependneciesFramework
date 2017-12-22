/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Agent;

import Interfaces.Iclearable;
import java.util.ArrayList;

/**
 *
 * @author abbas
 */
public class FederateDependency implements Cloneable, Iclearable {

    private String type;
    private ArrayList<DependencyInfo> dependentFederatesInfo;

    public FederateDependency() {
        setDefaults();
    }

    private void setDefaults() {
        type = "AND";
        dependentFederatesInfo = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<DependencyInfo> getDependentFederateInfo() {
        return dependentFederatesInfo;
    }

    public void setDependentFederatesInfo(ArrayList<DependencyInfo> dependentFederatesInfo) {
        this.dependentFederatesInfo = dependentFederatesInfo;
    }

    public void AddDependentFederateInfo(String depFedName) {
        DependencyInfo depinfo = new DependencyInfo();
        depinfo.setFederateName(depFedName);
        dependentFederatesInfo.add(depinfo);
    }

    public void AddDependentFederateInfo(String depFedName, int probability) {
        DependencyInfo depinfo = new DependencyInfo();
        depinfo.setFederateName(depFedName);
        depinfo.setProbablity(probability);
        dependentFederatesInfo.add(depinfo);
    }

    private void AddDependentFederateInfo(DependencyInfo depinfo) {
        dependentFederatesInfo.add(depinfo);

    }

    public boolean IsDependentOnIt(String depfedName) {
        for (DependencyInfo depinfo : dependentFederatesInfo) {
            return depinfo.getFederateName().equalsIgnoreCase(depfedName);
        }

        return false;
    }

    public ArrayList<String> getDependentFederatesNames() {
        ArrayList<String> federateNames = new ArrayList<>();
        for (DependencyInfo depinfo : dependentFederatesInfo) {
            federateNames.add(depinfo.getFederateName());
        }

        return federateNames;

    }

    @Override
    public void Clear() {
        setDefaults();
    }

    @Override
    public Object clone() {
        FederateDependency fedDep = new FederateDependency();
        fedDep.type = this.type;
        for (DependencyInfo fedName : this.dependentFederatesInfo) {
            fedDep.AddDependentFederateInfo(fedName);
        }
        return fedDep;

    }

}

class DependencyInfo {

    
    private String federateName;
    private int probability;
    public DependencyInfo() {
        setDefaults();
    }
    public void setDefaults()
    {
    federateName="";
    probability = 100;
    }


    public String getFederateName() {
        return federateName;
    }

    public void setFederateName(String federateName) {
        this.federateName = federateName;
    }
    

    public int getProbablity() {
        return probability;
    }

    public void setProbablity(int probablity) {
        this.probability = probablity;
    }

}
