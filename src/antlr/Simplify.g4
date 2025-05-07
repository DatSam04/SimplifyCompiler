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

DictMethod: 'addItem' | 'rmItem' | 'key' | 'value' | Size;

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
ASSIGN: '=' | '-=' | '+=' | '*=' | '/=';
SEMI: ';';

//Numerical operator
MUL: '*';
DIV: '/';
PLUS: '+';
MINUS: '-';

//Comparison operator
CompOp: '==' | '!=' | '>' | '>=' | '<' | '<=';

//Boolean operator
BoolOp: '&' | '|';


//Comments format
SingleLineComment: '#' ~[\r\n]* -> skip; //Use # to start single line comment

MultiLineComment: '#m' ( ~[\r\n] | '\r' | '\n' )*? '/m' -> skip; //Use #m to start multi line comment and end with /m

//Parser rules
program: statement+ EOF;

comments: SingleLineComment | MultiLineComment;

statement: funcExpr
        | assignment
        | classInit
        | comments
        | conditional
        | declaration
        | function
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
declaration: type ID (ASSIGN initializer)? SEMI;

initializer:  elementList | itemList | expression;

elementList: '[' (expression (',' expression)*)? ']';

//format for item(key & value) inside dictionary
itemList: '{' (keyValuePair (',' keyValuePair)*)? '}';

keyValuePair: String ':' expression;

//Assign or modify value of existing variable
assignment: ID ('[' expression ']')? ASSIGN expression SEMI;   //assign new value to a variable, element in an array, or to an item in a dictionary

expression
    : expression '[' expression ']'                                    #indexAccessExpr
    | expression '.' methodName '(' expression? ')'                    #methodCallExpr
    | '[]'                                                             #emptyArrExpr
    | '[' expression (',' expression)* ']'                             #arrExpr
    | '{}'                                                             #emptyDictExpr
    | '{' String ': ' expression (',' String ': ' expression)* '}'     #dictExpr
    | ID                                                               #idExpr
    | Number                                                           #numberExpr
    | Decimal                                                          #decimalExpr
    | String                                                           #stringExpr
    | funcExpr                                                         #functionExpr
    | readInputExpr                                                    #readExpr
    | expression (MUL | DIV) expression                                #numOpExpr
    | expression PLUS expression                                       #numOpExpr
    | expression MINUS expression                                      #numOpExpr
    | expression CompOp expression                                     #compOpExpr
    | expression BoolOp expression                                     #boolOpExpr
    | True                                                             #trueExpr
    | False                                                            #falseExpr
    | '(' expression ')'                                               #parenExpr
    ;

methodName: ArrMethod | DictMethod | StrMethod;

//conditional statement
//If conditional statement must have else statement
conditional: ifBlock block (orBlock block)* Cond_Else block;
ifBlock: Cond_If '(' expression ')' Exist;
orBlock: Cond_Or '(' expression ')' Exist;

loop
    : F_Loop '(' Num ID ASSIGN Number To expression ')' Exist block  #fLoop
    | W_Loop '(' ID To expression ')' Exist block                      #whLoop
    ;

//format for conditional statement and loop
block: '{' statement* '}';

//expression for calling function that can pass 0 or more values to the function arguments
funcExpr: ID '(' (expression (',' expression)*)? ')' SEMI?;

//Format for defining a function with 0 or more arguments
function: Func type ID '(' (argumentList)? ')' '{' (statement)* returnStatement '}';

argumentList: argument (',' argument)*;

argument: type ID;

returnStatement: Return expression? SEMI;

//Rule for defining class
classInit: Class ID '{' classProperty '}';

classProperty: declaration* function*;

result: Output '(' expression ')' SEMI;

readInputExpr: Input '(' ')' SEMI?;