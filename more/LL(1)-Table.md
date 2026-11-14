# LL(1) Parse Table — SEL Grammar
For each production rule A -> alpha, the table M[A, a] is filled using two rules:

    For every terminal a in First(alpha) \ {epsilon}, add A -> alpha to M[A, a].

    If epsilon is in First(alpha), add A -> alpha to M[A, b] for every terminal b in Follow(A).

Example (<ExprArith'>):

    Rule [11] (<ExprArith'> -> +  <ExprArith'>): Since First = {+}, we place [11] in M[<ExprArith'>, +].

    Rule [13] (<ExprArith'> -> epsilon): Since it derives epsilon, we place [13] in the columns of Follow(<ExprArith'>) = { ;, ), ==, <=, <, &&, ||, ] }.

Since no cell contains more than one rule, there are no conflicts and the grammar is LL(1).

| Token | Main | [VarName] | [Number] | ( | ) | { | } | ; | := | @ | IN | If | LOOP | + | - | * | / | == | <= | < | && | \|\| | ! | FALSE | TRUE | [ | ] | $ |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| \<S\> | [1] | | | | | | | | | | | | | | | | | | | | | | | | | | | |
| \<Code\> | | [2] | | | | | [3] | | | [2] | [2] | [2] | [2] | | | | | | | | | | | | | | | |
| \<Instruction\> | | [4] | | | | | | | | [7] | [8] | [6] | [5] | | | | | | | | | | | | | | | |
| \<Assign\> | | [9] | | | | | | | | | | | | | | | | | | | | | | | | | | |
| \<ExprArith\> | | [10] | [10] | [10] | | | | | | | | | | | [10] | | | | | | | | | | | | | |
| \<ExprArith'\> | | | | | [13] | | | [13] | | | | | | [11] | [12] | | | [13] | [13] | [13] | [13] | [13] | | | | | [13] | |
| \<Term\> | | [14] | [14] | [14] | | | | | | | | | | | [14] | | | | | | | | | | | | | |
| \<Term'\> | | | | | [17] | | | [17] | | | | | | [17] | [17] | [15] | [16] | [17] | [17] | [17] | [17] | [17] | | | | | [17] | |
| \<Factor\> | | [18] | [19] | [20] | | | | | | | | | | | [21] | | | | | | | | | | | | | |
| \<Cond\> | | [22] | [22] | [22] | | | | | | | | | | | [22] | | | | | | | | [22] | [22] | [22] | [22] | | |
| \<Cond'\> | | | | | [24] | | | | | | | | | | | | | | | | | [23] | | | | | [24] | |
| \<CondAnd\> | | [25] | [25] | [25] | | | | | | | | | | | [25] | | | | | | | | [25] | [25] | [25] | [25] | | |
| \<CondAnd'\> | | | | | [27] | | | | | | | | | | | | | | | | [26] | [27] | | | | | [27] | |
| \<CondAtom\> | | [32] | [32] | [32] | | | | | | | | | | | [32] | | | | | | | | [31] | [28] | [29] | [30] | | |
| \<Comp\> | | | | | | | | | | | | | | | | | | [33] | [34] | [35] | | | | | | | | |
| \<If\> | | | | | | | | | | | | [36] | | | | | | | | | | | | | | | | |
| \<Loop\> | | | | | | | | | | | | | [37] | | | | | | | | | | | | | | | |
