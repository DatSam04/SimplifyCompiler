grammar Simplify;

options {
    language = Java;
}

/* Instruction for testing grammar rule with testRig
  1. java -jar lib/antlr-4.13.2-complete.jar -o src/antlr -package antlr src/antlr/Simplify.g4 -> Generate .java file
  2. javac -cp "lib/antlr-4.13.2-complete.jar" -d src/antlr/production src/antlr/*.java -> Generate .class file
  3. java -cp "lib/antlr-4.13.2-complete.jar;src/antlr/production" org.antlr.v4.gui.TestRig antlr.Simplify program -gui inputTest.txt
*/


//Lexer rules (Token definition)
//Reserved keywords for different feature (loops, conditionals, function, class)
Cond_If: 'if';
Cond_Else: 'else';
Cond_Or: 'or';
F_Loop: 'f_loop';
W_Loop: 'w_loop';
Func: 'func';
Class: 'class';

//built-in method for different types
StrMethod: 'length';

ArrMethod: 'add' | 'rm' | Size;

DictMethod: 'addItem' | 'rmItem' | 'key' | Size;

//Type
Str: 'str';
Num: 'num';
Dec: 'dec';
Bool: 'bool';
Arr: 'arr';
Dict: 'dict';
Output: 'result';               //Print to console
Input: 'readInput';             //read input from console
//Keyword for loop and conditional statement
Exist: 'exist';
To: 'to';
Return: 'return';

//Keyword for built-in method of type
Size: 'size';

True: 'True';
False: 'False';
ID: [a-zA-Z_][a-zA-Z_0-9]*;     //Variables name (identifier)
Number: '-'?[0-9]+;
Decimal: '-'?[0-9]+ '.' [0-9]+;
String: '"' .*? '"';            //String literal (must start and end with double quotation marks)
Ws: [ \t\n\r]+ -> skip;
SEMI: ';';

//Comparison operator
EQ: '==';
NE: '!=';
GT: '>';
GE: '>=';
LT: '<';
LE: '<=';

ASSIGN: '=' | '-=' | '+=' | '*=' | '/=';

//Numerical operator
MUL: '*';
DIV: '/';
PLUS: '+';
MINUS: '-';

//Boolean operator
AND: '&';
OR: '|';


//Comments format
SingleLineComment: '#' ~[\r\n]* -> skip; //Use # to start single line comment

MultiLineComment: '#m' ( ~[\r\n] | '\r' | '\n' )*? '/m' -> skip; //Use #m to start multi line comment and end with /m

//Parser rules
program: statement+ EOF;

comments: SingleLineComment | MultiLineComment;

statement: function
        | classInit
        | innerStatement
        ;

innerStatement: assignment
        | comments
        | conditional
        | declaration
        | loop
        | result
        | expression SEMI
        ;

type
    : Str                       #StrType
    | Num                       #NumType
    | Dec                       #DecType
    | Bool                      #BoolType
    | Arr '[' type ']'          #ArrType
    | Dict                      #DictType
    ;
//Declare a variable with specific data types
declaration: type ID (ASSIGN expression)? SEMI;

//Assign or modify value of existing variable
assignment: ID ('[' expression ']')? ASSIGN expression SEMI;   //assign new value to a variable, element in an array, or to an item in a dictionary

expression
    : ID '[' expression ']'                                                 #indexAccessExpr
    | ID '.' methodName '(' expression? ')'                                 #methodCallExpr
    | String ':' expression                                                 #addDictItemExpr
    | '[]'                                                                  #emptyArrExpr
    | '[' expression (',' expression)* ']'                                  #arrExpr
    | '{}'                                                                  #emptyDictExpr
    | '{' expression ':' expression (',' expression ':' expression)* '}'    #dictExpr
    | ID                                                                    #idExpr
    | Number                                                                #numberExpr
    | Decimal                                                               #decimalExpr
    | String                                                                #stringExpr
    | funcExpr                                                              #functionExpr
    | readInputExpr                                                         #readExpr
    | left=expression (MUL | DIV) right=expression                          #numOpExpr
    | left=expression PLUS right=expression                                 #numOpExpr
    | left=expression MINUS right=expression                                #numOpExpr
    | left=expression compOp right=expression                               #compOpExpr
    | left=expression boolOp right=expression                               #boolOpExpr
    | True                                                                  #trueExpr
    | False                                                                 #falseExpr
    | '(' expression ')'                                                    #parenExpr
    ;

methodName: ArrMethod | DictMethod | StrMethod;

compOp: EQ | NE | GT | GE | LT | LE;

boolOp: AND | OR;

//conditional statement
//If conditional statement must have else statement
conditional: ifBlock (orBlock)* elseBlock;
ifBlock: Cond_If '(' expression ')' Exist block;
orBlock: Cond_Or '(' expression ')' Exist block;
elseBlock: Cond_Else block;

loop: forLoop | whileLoop;

forLoop: F_Loop '(' type ID ASSIGN Number To expression ')' Exist block;
whileLoop: W_Loop '(' ID To expression ')' Exist block;

//format for conditional statement and loop
block: '{' innerStatement* '}';

//expression for calling function that can pass 0 or more values to the function arguments
funcExpr: ID '(' (expression (',' expression)*)? ')' SEMI?;

//Format for defining a function with 0 or more arguments
function: Func type ID '(' (argumentList)? ')' '{' (innerStatement)* returnStatement '}';

argumentList: argument (',' argument)*;

argument: type ID;

returnStatement: Return expression? SEMI;

//Rule for defining class
classInit: Class ID '{' classProperty '}';

classProperty: (declaration | assignment)* function*;

result: Output '(' expression ')' SEMI;

readInputExpr: Input '(' String ')' SEMI?;