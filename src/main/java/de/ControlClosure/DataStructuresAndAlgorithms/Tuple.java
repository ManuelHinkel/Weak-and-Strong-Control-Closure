package de.ControlClosure.DataStructuresAndAlgorithms;

public class Tuple<V,U> {
    public V first;
    public U second;

    public Tuple(V first, U second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString(){
        return "(" +first + ", " + second + ")";
    }
}
