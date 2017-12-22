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
public class DependentFederate implements Cloneable, Iclearable { // The need of this Class is that it doesnt not have further dependent Federates -- other there would be endless recurssion.

    private String name;
    private FederateState state;

    public FederateState getState() {
        return state;
    }

    public void setState(FederateState state) {
        this.state = state;
    }

    public DependentFederate() {
        setDefaults();
    }

    public DependentFederate(String name, int performance) {
        setDefaults();
        this.name = name;
        state.setPerformance(performance);
    }

    private void setDefaults() {
        state = new FederateState();
        name = "depfed";
    }

    public int getPerformance() {
        return state.getPerformance();
    }

    public void setPerformance(int per) {
        state.setPerformance(per);
    }

    public String getFederateName() {
        return name;
    }

    public void setFederateName(String name) {
        this.name = name;
    }

    public void MakeDeepCopy(DependentFederate fed) {
        // DependentFederate depfed = new DependentFederate();
        this.setFederateName(fed.getFederateName());
        this.setPerformance(fed.getPerformance());
    }

    @Override
    public Object clone() {
        DependentFederate fed = new DependentFederate();
        fed.setFederateName(this.getFederateName());
        fed.setPerformance(this.getPerformance());
        fed.setState((FederateState) this.getState().clone());
        return fed;
    }

    @Override
    public void Clear() {
        name = "depfed";
        state.Clear();
    }
}
