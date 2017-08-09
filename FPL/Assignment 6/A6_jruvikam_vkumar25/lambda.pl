%  Lambda Calculus Simulator and Test Cases


let(first, l(m,a(a(m, l(x,l(y,x))), dummy))).

let(mult,  l(n1,l(n2,l(f,a(n1, a(n2, f)))))).

let(list, l(c,l(n, a(a(c,tom), a(a(c, ding), a(a(c, hari), n)))))).

let(len, l(list,l(f,l(x,a(a(list,l(x,f)),x))))).


test0 :- norm(a(l(x,x),p)).

test1 :- norm(l(x,a(a(p,q),x))).

test2 :- norm(a(a(l(x,l(y,a(x,y))),m), n)).

test3 :- norm(a(a(l(x,l(y,a(x,y))), a(l(x,x),p)), l(x,a(t,x)))).

test4 :-
	let(first, F),
	let(list, L),
	norm(a(F,L)).

test5 :- let(mult, M),
	 church(2, A),
	 church(5, B),
	 norm(a(a(M,A),B)).


% define church over here
church(A, B) :- int_to_chruch(A, C),
                B = l(f, l(x, C)).

int_to_chruch(0,x).

int_to_chruch(A, B) :- C is A-1,
             	     int_to_chruch(C, E),
           	     B = a(f, E).


norm(T) :-
	 write('--> '),
	 pretty_print(T), nl,
	 reduce(T,R),
	 (T == R
	   -> true
	    ; norm(R)
	 ).


% define pretty_print over here

pretty_print(S) :- pretty_print_gen(S).

pretty_print_gen(a(X,Y)) :-
    write('('),
    pretty_print_gen(X),
    write(' '),
    pretty_print_gen(Y),
    write(')').

pretty_print_gen(l(X,Y)) :-
    write('L'),
    write(X),
    write('.'),
    pretty_print_gen(Y).

pretty_print_gen(Z) :- write(Z).


reduce(X, X) :-
	atom(X).

% remaining eight cases go here

reduce(a(l(A,B),C),D) :-
	substitute(B,A,C,D).

reduce(l(A,a(B,C)), D) :-
	(A == C
	   -> (occurs_free_in(C,B)
	      ->  reduce(a(B,C),E),
	          D = l(A,E)
	      ;	  D = B
	      )
	    ; reduce(a(B,C),E),
	      D = l(A,E)
	 ).

reduce(l(A,B),C) :-
	reduce(B,D),
	C = l(A,D).

% occurs_free_in is needed in defining eta-reduction

occurs_free_in(X, X) :-
	atom(X).
occurs_free_in(X, l(Y,T)) :-
	X \== Y,
	occurs_free_in(X, T).
occurs_free_in(X, a(T1, T2)) :-
	occurs_free_in(X, T1) ;
        occurs_free_in(X, T2).



% Substitute is needed in defining beta-reduction: T2 = T1[V <- T]

substitute(T1,V,T,T2) :-
	free(T, Free),
	subst(T1,V,T,T2,Free),
	!.

subst(V,V,T,T, _).
subst(V1,V2,_,V1,_) :-
	atom(V1),
	V1 \== V2.
subst(l(V,T1),V,_,l(V,T1),_).
subst(l(V1,T1),V,T,l(V2,T3),Free) :-
	V1 \== V,
	member(V1,Free),
	rename(V1,V2),
	subst(T1,V1,V2,T2,Free),
	subst(T2,V,T,T3,Free).
subst(l(V1,T1),V,T,l(V1,T2),Free) :-
	V1 \== V,
	\+ member(V1,Free),
	subst(T1,V,T,T2,Free).
subst(a(T1,T2),V,T,a(T3,T4),Free) :-
	subst(T1,V,T,T3,Free),
	subst(T2,V,T,T4,Free).


rename(V,R) :-
	name(V,L),
	append(L,[49],L2),
	name(R,L2).

free(V, [V]) :-
	atom(V).
free(l(V,T), L) :-
	free(T, L2),
	remove(L2,V,L).
free(a(T1,T2), L) :-
	free(T1,L1),
	free(T2,L2),
	union(L1,L2,L).

remove([],_,[]).
remove([H|T],H,T).
remove([H|T],V,[H|T2]) :-
	H \== V,
	remove(T,V,T2).












