Control.Print.printDepth := 100;
Control.Print.printLength := 1000;


print("\n***************** Solution 1 Starts ********************\n");

datatype bstree = leaf | node of int * bstree * bstree;

fun reduce(f,b,[]) = b | reduce(f,b,h::t) = f(h,reduce(f,b,t));


fun insert(key,leaf) = node(key,leaf,leaf) 
	| insert(key,node(root,left,right)) = if key < root then node(root,insert(key,left),right) 
						else if key > root then node (root,left,insert(key,right)) 
						else node(key,left,right);

fun testcase1() = reduce(insert,leaf,[50,30,20,40,60]);

testcase1();

print("***************** Solution 1 Ends ********************\n\n\n");




print("\n***************** Solution 2 Starts ********************\n");

datatype 'a ntree = leaf of 'a | node of 'a ntree list;

fun map(f, []) = []
  | map(f,h::t) = f(h) :: map(f,t);
  
fun reduce(f, b, []) = b 
  |  reduce(f, b, h::t) = f(h, reduce(f, b, t));

fun subst(leaf(x), v1, v2) =  
		    if x = v1 then leaf(v2) else leaf(x)
  | subst(node(n), v1, v2) = let 
				fun d(tree) = subst(tree, v1, v2)
  			 	in node(map(d, n))
  			     end;
 				
fun cat(leaf(value)) = value
  | cat(node(n)) = let 
            		fun d(tree, catStr) =  if catStr = "" then cat(tree) else cat(tree) ^ " " ^ catStr;
  			in reduce(d, "", n)
  		   end;

fun test_subst() = subst(node([leaf("x"),node([leaf("y"), leaf("x"), leaf("z")])]), "x", "w");

fun test_cat() = cat(node([leaf("x"),node([leaf("y"),leaf("x"),leaf("z")])]));

test_subst();

test_cat();

print("***************** Solution 2 Ends ********************\n\n\n");




print("\n***************** Solution 3 Starts ********************\n");

datatype bstree = leaf | node of int * bstree * bstree;

fun reduce(f,b,[]) = b | reduce(f,b,h::t) = f(h,reduce(f,b,t));


fun insert(key,leaf) = node(key,leaf,leaf) | insert(key,node(root,left,right)) = if key < root then node(root,insert(key,left),right) else if key > root then node (root,left,insert(key,right)) else node(key,left,right);

fun dfirst2(tree)  = let fun df(leaf, inorderStr) = inorderStr
  			| df(node(n, left, right), inorderStr) = df(left, n::df(right, inorderStr))
		     in
			df(tree,[])
		     end;			


fun testcase1() = reduce(insert,leaf,[50,30,20,40,60]);

fun test_dfirst2() = dfirst2(testcase1());

test_dfirst2();

print("***************** Solution 3 Ends ********************\n\n\n");




print("\n***************** Solution 4 Starts ********************\n");

datatype 'a inf_list = lcons of 'a * (unit -> 'a inf_list)

fun church(n) = let fun thk() = church("(f " ^ n ^ ")") in lcons("Lf.Lx.(f " ^ n ^ ")", thk) end;

fun take(0, _) = [] | take(n, lcons(h, thk)) = h :: take(n-1, thk());

take(5,church("x"));

print("***************** Solution 4 Ends ********************\n\n\n");
