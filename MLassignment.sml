Control.Print.printDepth := 100;
Control.Print.printLength := 100;

(* Problem 1 *)
fun foo f g x = [g [f x]]

(* Problem 2 *)
fun bar x = (fn [] => []
            | [y] => [x * y]
            | (y::ys) => bar x [y] @ bar x ys)

(* Problem 3 *)
fun part x [] = ([],[])
|   part x (l::ls) = let val (less,greater) = part x ls
                        in
                        if (l < x) then (([l] @ less), greater) else (less, ([l] @ greater))
                        end

 (* Problem 4 *)
fun partSort [] = []
 |  partSort [l] = [l]
 |  partSort (x::xs) = let val (first, second) = part x xs 
                        in 
                        (partSort first) @ [x] @ (partSort second) 
                        end

(* Problem 5 *)
fun pSort (op <) [] = []
|   pSort (op <) [l] = [l]
|   pSort (op <) (l::ls) = let fun helper x [] = ([],[])
                                |  helper x (l::ls) = let val (less, greater) = helper x ls
                                                        in
                                                        if (l < x) then (([l] @ less), greater) else (less, ([l] @ greater))
                                                        end
                            in
                            let val (first, second) = helper l ls
                            in
                            (pSort (op <) first) @ [l] @ (pSort (op <) second)
                            end
                            end

(* Problem 6 *)
exception reduce_error
fun reduce f [] = raise reduce_error
|   reduce f [l] = l
|   reduce f (x::xs) =  f x (reduce f xs)

(* Problem 7 *)
datatype 'a tree = empty | leaf of 'a | node of 'a tree list

(* Problem 8 *)
fun fringe (leaf x) = [x]
|   fringe (node []) = []
|   fringe (node L) = List.concat (map fringe L)

(* Problem 9 *)
fun sortTree (op <) (leaf []) = leaf []
|   sortTree (op <) (leaf L) = leaf (pSort (op <) L)
|   sortTree (op <) (node []) = node []
|   sortTree (op <) (node L) = node (map (sortTree (op <)) L)

(* Problem 10 *)
fun powerSet [] = [[]]
|   powerSet (x::xs) = let val subset = powerSet xs
                        in
                        (map (fn y => x::y) subset) @ subset
                        end