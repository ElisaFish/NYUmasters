abstract class Tree[+T]

case class Node[+T](v:T, l:Tree[T], r:Tree[T]) extends Tree[T]

case class Leaf[+T](v:T) extends Tree[T]

trait Addable[T] {
    def +(x: T): T
}

class A(x:Int) extends Addable[A] {
    val value: Int = x
    def +(a:A): A = new A(this.value + a.value)
    override def toString(): String = "A(" + value + ")"
}

class B(x:Int) extends A(x) {
    override def toString(): String = "B(" + value + ")"
}

class C(x:Int) extends B(x) {
    override def toString(): String = "C(" + value + ")"
}

object Part2 {
    def inOrder[T](t:Tree[T]): List[T] = {
        t match {
            case Node(v,l,r) => var myList: List[T] = inOrder(l) ++ (v::inOrder(r))
                                myList
            case Leaf(v) => var myList: List[T] = List[T](v)
                            myList
        }
    }

    def treeSum[T <: Addable[T]](t:Tree[T]): T = {
        t match {
            case Node(v,l,r) => val sum:T = v + treeSum(l) + treeSum(r)
                                sum
            case Leaf(v) => val sum:T = v
                            sum
        }
    }

    def treeMap[T,V](f:T=>V, t:Tree[T]): Tree[V] = {
        t match {
            case Node(v, l, r) =>   var u:Tree[V] = new Node[V](f(v), treeMap(f, l), treeMap(f, r))
                                    u
            case Leaf(v) => var u:Tree[V] = new Leaf[V](f(v))
                            u
        }
    }

    def BTreeMap(f:B=>B, t:Tree[B]): Tree[B] = treeMap(f, t)

    def test():Unit = {
        def faa(a:A):A = new A(a.value+10)
        def fab(a:A):B = new B(a.value+20)
        def fba(b:B):A = new A(b.value+30)
        def fbb(b:B):B = new B(b.value+40)
        def fbc(b:B):C = new C(b.value+50)
        def fcb(c:C):B = new B(c.value+60)
        def fcc(c:C):C = new C(c.value+70)
        def fac(a:A):C = new C(a.value+80)
        def fca(c:C):A = new A(c.value+90)

        val myBTree: Tree[B] = Node(new B(4),Node(new B(2),Leaf(new B(1)),Leaf(new B(3))), 
                        Node(new B(6), Leaf(new B(5)), Leaf(new B(7))))

        val myATree: Tree[A] = myBTree

        println("inOrder = " + inOrder(myATree))
        println("Sum = " + treeSum(myATree))

        //println(BTreeMap(faa,myBTree)) // faa returns an A. BTreeMap() takes a function of type B=>.B Functions are covariant on the output type, and A is not a subtype of B.
        println(BTreeMap(fab,myBTree))
        //println(BTreeMap(fba,myBTree)) // fba returns an A. BTreeMap() takes a function of type B=>B. Functions are covariant on the output type, and A is not a subtype of B.
        println(BTreeMap(fbb,myBTree))
        println(BTreeMap(fbc,myBTree))
        //println(BTreeMap(fcb,myBTree)) // fcb is type C=>B. BTreeMap() takes a function of type B=>B. Functions are contravariant on the input type, and B is not a subtype of C.
        //println(BTreeMap(fcc,myBTree)) // fcc is type C=>C. BTreeMap() takes a function of type B=>B. Functions are contravariant on the input type, and B is not a subtype of C.
        println(BTreeMap(fac,myBTree))
        //println(BTreeMap(fca,myBTree)) // fca is type C=>A. BTreeMap() takes a function of type B=>B. Functions are contravariant on the input type, and B is not a subtype of C.

        println(treeMap(faa,myATree))
        println(treeMap(fab,myATree))
        //println(treeMap(fba,myATree)) // fba is type B=>A. myATree is type A, which is not a subtype of input type B.
        //println(treeMap(fbb,myATree)) // fbb is type B=>B. myATree is type A, which is not a subtype of input type B.
        //println(treeMap(fbc,myATree)) // fbc is type B=>C. myATree is type A, which is not a subtype of input type B.
        //println(treeMap(fcb,myATree)) // fcb is type C=>B. myATree is type A, which is not a subtype of input type C.
        //println(treeMap(fcc,myATree)) // fcc is type C=>C. myATree is type A, which is not a subtype of input type C.
        println(treeMap(fac,myATree))
        //println(treeMap(fca,myATree)) // fca is type C=>A. myATree is type A, which is not a subtype of input type C.
    }

    def main(args: Array[String]) = {
        this.test()
    }
}