/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Agent;

import Interfaces.Iclearable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author abbas
 */
public class Federation implements Cloneable, Iclearable {

    private String name;
    private List<Federate> federates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Federate> getFederates() {
        return federates;
    }
    public void ClearFederates()
    {
    federates.clear();
    }

    public void setFederates(List<Federate> federates) {
        this.federates = federates;
    }

    public Federate getFederate(int index) {
        return federates.get(index);
    }

    public Federate getFederate(String name) {
        return SearchFederate(name);
    }

    public void setFederate(int index, Federate federate) {
        federates.set(index, federate);
    }

    public void AddFederate(int index, Federate federate, boolean isDelete) {
        if(isDelete) federates.remove(index);
        federates.add(index, federate);
    }
    public void AddFederate(int index, Federate federate) {
        federates.add(index, federate);
    }
     public void DeleteFederate(int index) {
        federates.remove(index);
    }
      public void DeleteFederate(String fedName) {
          federates.remove(SearchFederate(fedName));       
    }
    public void AddFederate( Federate federate) {
        federates.add(federate);
    }
    public void SetFederateName(int index, String fedName) {
        federates.get(index).setFederateName(fedName);
    }

    public String getFederateName(int index) {
        return federates.get(index).getFederateName();
    }

    public boolean setFederatePerformance(int index, int per) {

        if (per == federates.get(index).getFederateState().getPerformance()) {
            return false;  //*********** if only performance has been really changed 
        }
        federates.get(index).getFederateState().setPerformance(per);

        return true;
    }

    public boolean setFederateSate(int index, FederateState state) {

        if (state == federates.get(index).getFederateState()) {
            return false;  //*********** if only performance has been really changed 
        }
        federates.get(index).setFederateState(state);

        return true;
    }

    public int getFederatePerformance(int index) {
        return federates.get(index).getFederateState().getPerformance();
    }

    public int getFederatePerformance(String name) {
        return this.SearchFederate(name).getPerformance();
    }

    public Federation() {
        setDefaults();
        //Place at least one empty federate

    }

    private void setDefaults() {
        name = "federation";
        federates = new ArrayList<>();
//        Federate federate = new Federate();
//        federates.add(federate);

    }

    public DependentFederate getDependentFederate(int index, String fed) {
        return federates.get(index).GetDependentFederate(fed);
    }
    
     public Set<DependentFederate> getDependentFederates(int index) {
        return federates.get(index).getDependentFederates();
    }

    public ArrayList<FederateDependency> getDependendencies(int index) {
        return federates.get(index).getDependencies();
    }

    //public boolean SearchFederate(String federateName)
    public Federate SearchFederate(String federateName) {
        Federate fed = null;

        for (Federate federate : federates) {
            if (federateName.equals(federate.getFederateName())) {
                fed = federate;
            }
        }

        return fed;

    }

    public void DeleteAllOtherFederaes(String federateName) {
        Federate fed = SearchFederate(federateName);
        List<Federate> federatesdummy = new ArrayList<>();
        federatesdummy.add(fed);
        federates.clear();
        federates.removeAll(federates);
        federates = federatesdummy;

    }

    @Override
    public Object clone() {
        Federation fed = new Federation();

        fed.name = this.name;
        fed.federates.clear();
        Federate cloneFed = new Federate();
        for (Federate fedt : this.federates) {
            cloneFed = (Federate) fedt.clone();
            fed.federates.add(cloneFed);
        }

        return fed;
    }

    @Override
    public String toString() {
        return name + federates;
    }

    @Override
    public void Clear() {
        name = "federation";
        for (Federate fed : federates) {
            fed.Clear();
        }

    }

}
