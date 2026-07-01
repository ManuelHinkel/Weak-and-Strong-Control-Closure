package de.ControlClosure.Utils;

import de.ControlClosure.DSCC.Bernstein.Separator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SetUtils {
    public static <T> T getFirst(Set<T> s) {
        return s.iterator().next(); 
    }

    public static <T> boolean isSubset(Set<T> set, Set<T> superset) {
        for(T e: set) {
            if(!superset.contains(e)) {
                return false;
            }
        }
        return true;
    }

    public static <V> V pickRandom(Set<V> vertices) {
        Random r = new Random();
        int index = r.nextInt(vertices.size());

        int i = 0;
        for(V v: vertices) {
            if (i == index) return v;
            i++;
        }
        return null;
    }


    public static <T> Separator<T> intersection(Separator<T> a, Set<T> b) {
        Set<T> intersection = new HashSet<>(a.S());
        intersection.retainAll(b);
        return new Separator<>(intersection);
    }

}
