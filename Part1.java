import java.util.*;
import java.lang.Math;

import javax.lang.model.util.ElementScanner6;

class SortedList<T extends Comparable<T> > extends ArrayList<T> implements /*List<T>,*/ Comparable<SortedList<T>> {
    @Override
    public boolean add(T t) {
        int length = this.size();
        for (int i = 0; i < length; i++)
            if (this.get(i).compareTo(t) > 0) {
                this.add(i,t);
                return true;
            }

        this.add(length,t);

        return true;
    }

    public int compareTo(SortedList<T> L2) {
        int k1 = this.size();
        int k2 = L2.size();
        int loop_end = Math.min(k1,k2);
        
        for (int i = 0; i < loop_end; i++) {
            if (! this.get(i).equals(L2.get(i))) {
                return this.get(i).compareTo(L2.get(i));
            }
        }

        if (k1 < k2) {
            return -1;
        }
        else if (k1 > k2) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public String toString() {
        String return_string = "[[";
        for (T t : this) {
            return_string += t.toString() + " ";
        }
        if (return_string.charAt(return_string.length() - 1) == ' ')
            return_string = return_string.substring(0, return_string.length() - 1);
        return_string += "]]";
        return return_string;
    }
}

class A implements Comparable<A> {
    Integer value;

    A(Integer x) {
        this.value = x;
    }

    public int compareTo(A a2) {
        return this.value.compareTo(a2.value);
    }
    
    public String toString() {
        return "A" + "<" + Integer.toString(this.value) + ">";
    }
}

class B extends A implements Comparable<A> {
    Integer first;
    Integer second;

    B(Integer x, Integer y) {
        super(x+y);
        this.first = x;
        this.second = y;
    }
    
    public String toString() {
        return "B" + "<" + Integer.toString(first) + "," + Integer.toString(second) + ">";
        //return "(" + Integer.toString(first) + "," + Integer.toString(second) + ")";
    }
}

class Part1 {
    static <T extends Comparable<T> > void addToSortedList(SortedList<? super T> L, T z) {
        boolean added = L.add(z);
    }

    //@Test
    static void test() {
        SortedList<A> c1 = new SortedList<A>();
        SortedList<A> c2 = new SortedList<A>();
        for(Integer i = 35; i >= 0; i-=5) {
            addToSortedList(c1, new A(i));
            addToSortedList(c2, new B(i+2,i+3));
        }
        
        System.out.print("c1: ");
        System.out.println(c1);
        
        System.out.print("c2: ");
        System.out.println(c2);
    
        switch (c1.compareTo(c2)) {
        case -1: 
            System.out.println("c1 < c2");
            break;
        case 0:
            System.out.println("c1 = c2");
            break;
        case 1:
            System.out.println("c1 > c2");
            break;
        default:
            System.out.println("Uh Oh");
            break;
        }
    
    }

    public static void main(String args[]) {
        test();
        
        /*
        SortedList<Integer> a = new SortedList<Integer>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        a.add(5);
        a.add(8);
        SortedList<Integer> b = new SortedList<Integer>();
        b.add(1);
        b.add(2);
        b.add(3);
        b.add(4);
        b.add(6);
        b.add(7);
        SortedList<Integer> c = new SortedList<Integer>();
        c.add(2);
        c.add(4);
        c.add(6);
        c.add(8);
        SortedList<Integer> d = new SortedList<Integer>();
        d.add(2);
        d.add(4);
        d.add(6);
        d.add(8);
        d.add(10);
        System.out.println(a.compareTo(b)); // -1
        System.out.println(c.compareTo(d)); // -1

        A a1 = new A(6);
        B b1 = new B(2,4);
        B b2 = new B(5,8);
        System.out.println(a1.compareTo(b1));  //returns 0, since 6 = (2+4)
        System.out.println(a1.compareTo(b2));  //returns -1, since 6 < (5+8)
        System.out.println(b1.compareTo(a1));  //returns 0, since (2+4) = 6
        System.out.println(b2.compareTo(a1));  //returns 1, since (5+8) > (6)
        System.out.println(b1.compareTo(b2));  //returns -1, since (2+4) < (5+8)
        */
    }
}