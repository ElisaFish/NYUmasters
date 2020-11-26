;; Problem 1
;; (fromTo k n) returns list of integers from k to n, inclusive
;; Base Case: if k > n, return the empty list
;; Hypothesis: Assume (fromTo (+ k 1) n) returns the list of integers from K+1 to n, smaller than the previous problem
;; Recursive Step: (fromTo k n) = (cons k (fromTo (+ k 1) n)
(define (fromTo k n)
  (cond
    ((> k n) '())
    (else (cons k (fromTo (+ k 1) n)))
  )
)

;; Problem 2
;; (removeMults m L) returns a list of elements in L that are not multiples of m
;; Base Case: if L is empty, return the empty list
;; Assumption: assume (removeMults m (cdr L)) operates on a smaller list than L, namely all but the first element
;; Recursive Step: (removeMults m L) = (cons (if (~= 0 (modulo (car L) m)) (car L)) (removeMults m (cdr L)))
(define (removeMults m L)
  (cond
    ((null? L) '())
    ((> (modulo (car L) m) 0) (cons (car L) (removeMults m (cdr L))))
    (else (removeMults m (cdr L)))
    )
  )

;; Problem 3
;; (removeAllMults L) returns a list of the elements in L that aren't multiples of other elements in the list
;; Base Case: if L is empty, return the empty list
;; Assumption: assume removeAllMults works on cdr L, a smaller list than L
;; Recursive Step: (removeMults (car L) (cdr L)) removes from cdr L all the multiples of (car L), then cons (car L) and
(define (removeAllMults L)
  (cond
    ((null? L) '())
    (else (cons (car L) (removeAllMults (removeMults (car L) (cdr L)))))
    )
  )

;; Problem 4
;; (primes n) computes a list of all primes less than or equal to n
(define (primes n)
  (removeAllMults (fromTo 2 n))
  )

;; Problem 5
;; (maxDepth L) returns the maximum nesting depth of any element in L
;; Base Case: L has no elements
;; Assumption: (maxDepth L) works on any element of the previous list
;; Recursive Step: (cond (list? (car L)) (max (+ 1 (maxDepth (car L))) (maxDepth (cdr L)))
;;                 If (car L) is a list, take the max of 1 + recursion into (car L) and recursion into (cdr L)
(define (maxDepth L)
  (cond
    ((null? L) 0)
    ((list? (car L)) (max (+ 1 (maxDepth (car L))) (maxDepth (cdr L))))
    (not (list? (car L)) (maxDepth (cdr L)))
    ))

;; Problem 6
;; (prefix exp) transforms an arithmetic clause from infix notation to prefix notation
;; Base Case: exp is a number, or length of exp is 1
;; Assumption: (prefix exp) works on the next element of the list, cadr L
;; Recursive Step: apply prefix to any sublist, also apply prefix to (cddr exp)
(define (prefix exp)
  (cond
    ((number? exp) exp)
    ((= (length exp) 1) (if (list? (car exp)) (prefix (car exp)) (car exp)))
    ((> (length exp) 2) (cons (cadr exp)
                              (cons (if (list? (car exp)) (prefix (car exp)) (car exp))
                                    (list (prefix (cddr exp)))
                               )
                         )
    )
  )
)
  
;; Problem 7
;; (composition fns) takes a list of functions and returns a function that is the composition of functions in fns
;; Base Case: fns has length 1, return (car fns)
;; Assumption: composition works on (cdr fns), a smaller list of functions
;; Recursive Step: run first function on composition of list of remaining functions, and make it a lambda
(define (composition fns)
  (cond
    ((null? fns) )
    ((= 1 (length fns)) (car fns))
    (else (lambda (x) ((car fns) ((composition (cdr fns)) x))))))

;; Problem 8
;; (bubble-to-nth L N) takes a list L of numbers and returns a list where the largest number of the first N elements
;;                     is in the Nth place
;; Base Case: N = 1
;; Assumption: bubble-to-nth works from the first element to the Nth element, swapping the larger element to the
;;             higher spot
;; Recursive Step: (bubble-to-nth L (- N 1))
(define (bubble-to-nth L N)
  (cond
    ((= N 1) L)
    (else (cond
            ((> (car L) (cadr L))  (cons (cadr L) (bubble-to-nth (cons (car L) (cddr L)) (- N 1))))
            (else (cons (car L) (bubble-to-nth (cdr L) (- N 1))))))))

;; Problem 9
;; (b-s L N) returns L except with the first N elements sorted
;; Base Case: N = 1 return L
;; Assumption: (bubble-to-Nth) counts down from N to 1, always bubbling the highest number to the Nth spot
;; Recursive Step: (b-s (bubble-to-nth L N) (- N 1))
(define (b-s L N)
  (cond
    ((= N 1) L)
    (else (b-s (bubble-to-nth L N) (- N 1)))))

;; Problem 10
;; (bubble-sort L) calls (b-s L N) to sort list L
(define (bubble-sort L)
  (b-s L (length L)))