package de.ControlClosure.DSCC.Bernstein;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Separator<T> implements Iterable<T> {
    protected Set<T> S;

    public Separator() {
        S = new HashSet<>();
    }

    public Separator(Set<T> S) {
        this.S = S;
    }

    @Override
    public Iterator<T> iterator() {
        return S.iterator();
    }

    public void add(T s) {
        S.add(s);
    }

    public void addAll(Set<T> Sp) {
        S.addAll(Sp);
    }

    public boolean contains(T s) {
        return S.contains(s);
    }

    public Set<T> S() {
        return S;
    }

    public int size() {
        return S.size();
    }

}
