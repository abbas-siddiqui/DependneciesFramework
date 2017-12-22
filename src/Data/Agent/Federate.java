/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.Agent;

import Interfaces.Iclearable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author abbas
 */
public class Federate implements Cloneable, Iclearable {

    private String name;
    // Not in JSON Message Included --- Populated within the program
    private Set<DependentFederate> dependentFederates;
    private FederateState federateState; //Not receiving in Dependencies Graph
    private ArrayList<FederateDependency> dependencies;

    public Federate(String name) {
        this.name = name;
        setDefaults();
    }

    public FederateState getFederateState() {
        
        return federateState;
    }

    public boolean setFederateState(FederateState federateState) {
        this.federateState = federateState;
        return true;
    }

    public void setFederateName(String name) {
        this.name = name;
    }

    public String getFederateName() {
        return name;
    }

    public int getPerformance() {
        return federateState.getPerformance();
    }

    public void setPerformance(int per) {
        federateState.setPerformance(per);
    }

    public Set<DependentFederate> getDependentFederates() {
        return dependentFederates;
    }

    public void setDependentFederates(Set<DependentFederate> dependentFederates) {
        this.dependentFederates = dependentFederates;
    }

    public ArrayList<FederateDependency> getDependencies() {
        return dependencies;
    }
    
    public void ClearDependencies()
    {
    this.dependencies.clear();
    }

    public void setDependencies(ArrayList<FederateDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public void AddDependentFederate(DependentFederate depFed) {
        dependentFederates.add(depFed);
    }

    public DependentFederate GetDependentFederate(String name) {

        DependentFederate fed = null;
        for (DependentFederate federatea : dependentFederates) {
            if (federatea.getFederateName().equalsIgnoreCase(name)) {
                fed = federatea;
                break;
            }

        }
        return fed;
    }

    public Federate() {
        setDefaults();
    }

    private void setDefaults() {

        name ="federate";
        federateState = new FederateState();
        dependentFederates = new HashSet<>();
        dependencies = new ArrayList<>();

    }

    @Override
    public void Clear() {
        federateState = new FederateState();
        dependentFederates = new HashSet<>();
        dependencies = new ArrayList<>();

        for (DependentFederate depfed : dependentFederates) {
            depfed.Clear();
        }
        for (FederateDependency dependency : dependencies) {
            dependency.Clear();
        }
        federateState.Clear();

    }

    public void CopyState(FederateState newState) {
        federateState.setPerformance(newState.getPerformance());

    }

    @Override
    public Object clone() {
        Federate fedt = new Federate();
        fedt.name = this.name;

        DependentFederate depFedt = new DependentFederate();
        for (DependentFederate depfed : dependentFederates) {
            depFedt = (DependentFederate) depfed.clone();
            fedt.dependentFederates.add(depFedt);
        }
        FederateDependency dependency_c = new FederateDependency();
        for (FederateDependency dependency : dependencies) {
            dependency_c = (FederateDependency) dependency.clone();
            fedt.dependencies.add(dependency_c);
        }

        fedt.federateState = (FederateState) this.federateState.clone();

        return fedt;

    }
}
