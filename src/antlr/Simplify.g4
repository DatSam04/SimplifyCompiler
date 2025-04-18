grammar Simplify;

tokens{
    Keyword1, Keyword2, Exist, OtherKeyword, Number, String, ID, ASSIGN, NumOp, CompOp, BoolOp
}

options {
    language = Java;
}

/*  Instruction and Command to generate listener, visitor, lexer, and parser file
    Navigate to folder src\antlr
    Run in command line: java -jar "..\antlrJar\antlr-4.13.2-complete.jar" Simplify.g4 -visitor -o "..\generated"
    add package generated at the top of these file(BaseListener, Listener, Basevisitor, Visitor, Lexer,Parser)
*/

/* Instruction for testing grammar rule with testRig
  1. java -jar lib/antlr-4.13.2-complete.jar -o src/antlr -package antlr src/antlr/Simplify.g4 -> Generate .java file
  2. javac -cp "lib/antlr-4.13.2-complete.jar" -d src/antlr/production src/antlr/*.java -> Generate .class file
  3. java -cp "lib/antlr-4.13.2-complete.jar;src/antlr/production" org.antlr.v4.gui.TestRig antlr.Simplify program -gui inputTest.txt
*/


//Lexer rules (Token definition)
//Reserved keywords for different feature (loops, conditionals, function, class, print, and read input)
Keyword1: 'if' | 'else' | 'or' | 'f_loop' | 'w_loop' | 'func' | 'class' | 'result' | 'readInput';
Keyword2: 'num' | 'dec' | 'str' | 'bool' | 'arr' | 'dict'; //Reserved keywords for data type
Exist: 'exist'; //exist for conditional rule
OtherKeyword: 'to' | 'return'; //To used in loops
ID: [a-zA-Z_][a-zA-Z_0-9]*; //Variables name (identifier)
Number: '-'?[0-9]+ | '-'?[0-9]+ '.' [0-9]+ ; //Numeric value contains both real number and decimal point
String: '"' .*? '"'; //String literal (must start and end with double quotation marks)
Ws: [ \t\n\r]+ -> skip;
ASSIGN: '=' | '-=' | '+=' | '*=' | '/=';

//Numerical operator
MUL: '*';
DIV: '/';
PLUS: '+';
MINUS: '-';

//Comparison operator
CompOp: '==' | '!=' | '>' | '>=' | '<' | '<=';

//Boolean operator
BoolOp: '&' | '|';

//Boolean literals
True: 'True';
False: 'False';

//Comments format
SingleLineComment: '#' ~[\r\n]* -> skip; //Use # to start single line comment

MultiLineComment: '#m' ( ~[\r\n] | '\r' | '\n' )*? '/m' -> skip; //Use #m to start multi line comment and end with /m

//Parser rules
program: statement+ EOF;

type: 'num' | 'dec' | 'str' | 'bool' | 'void'; // Data types

comments: SingleLineComment | MultiLineComment;

statement: assignment
        | arr_function
        | classInit
        | comments
        | conditional
        | declaration
        | dict_function
        | expression
        | function
        | loop
        | initialization
        | result
        | str_function
        ;

//Declare a variable with specific data types
declaration
    : type ID ';'                   //declare a variable
    | 'arr' '[' type ']' ID ';'     //declare an array
    | 'dict' ID ';'                 //declare a dictionary
    ;

//Initialize a variable with specific data types and assign values to it
initialization
    : type ID ASSIGN expression ';'                 //Initialize a variable
    | 'arr' '[' type ']' ID ASSIGN elementList ';'  //Initialize an array
    | 'dict' ID ASSIGN itemList                     //Initialize a dictionary
    ;

elementList: '[' (expression (',' expression)*)? ']';

//format for item(key & value) inside dictionary
itemList: '{' (keyValuePair (',' keyValuePair)*)? '}';

keyValuePair: String ':' expression;

//Assign or modify value of existing variable
assignment
    : ID ASSIGN expression ';'                       //assign new value to a variable
    | ID '[' Number ']' ASSIGN expression ';'        //modify value of an element in an array
    | ID '[' (String | ID) ']' ASSIGN expression ';' //Modify value of a key in a dictionary
    ;

expression
    : expression (MUL | DIV) expression        #numOpExpr
    | expression PLUS expression               #compOpExpr
    | expression MINUS expression              #compOpExpr
    | expression CompOp expression             #compOpExpr
    | expression BoolOp expression             #boolOpExpr
    | True                                     #trueExpr
    | False                                    #falseExpr
    | ID                                       #idExpr
    | Number                                   #numberExpr
    | String                                   #stringExpr
    | '[]'                                     #emptyArrExpr
    | '{}'                                     #emptyDictExpr
    | arrAccessExpr                            #arrayAccessExpr
    | dictAccessExpr                           #dictionaryAccessExpr
    | funcExpr                                 #functionExpr
    | readInputExpr                            #readExpr
    ;

//Access format for array and dictionary
arrAccessExpr: (ID | ID '.' dictMethod '(' ')') '[' (Number | ID) ']' ';'?;
//(String | ID) ensure it will accept both a simple str and a str variable
dictAccessExpr: ID '[' (String | ID) ']' ';'?;

//built-in method for array, dictionary, and string
arr_function: ID '.' arrMethod '(' expression? ')' ';'?;

dict_function: ID '.' dictMethod '(' expression? ')' ';'?;

str_function: ID '.' strMethod '(' expression? ')' ';'?;

//add: add an element to the end of the array
//rm: remove a specific element in the array
arrMethod: 'add' | 'rm' | 'size';

//addItem: add an item to the end of the dictionary
//rmItem: remove a specific item in the dictionary
dictMethod: 'addItem' | 'rmItem' | 'key' | 'value' | 'size';

//String method
strMethod: 'length';

//conditional statement
//If conditional statement must have else statement
conditional: ifBlock block (orBlock block)* 'else' block;
ifBlock: 'if' '(' expression ')' Exist;
orBlock: 'or' '(' expression ')' Exist;

loop
    : 'f_loop' '(' 'num' ID ASSIGN Number 'to' (Number | str_function) ')' Exist block  #fLoop
    | 'w_loop' '(' ID 'to' (Number | str_function) ')' Exist block                      #whLoop
    ;

//format for conditional statement and loop
block: '{' statement* '}';

//expression for calling function that can pass 0 or more values to the function arguments
funcExpr: ID '(' (expression (',' expression)*)? ')' ';'?;

//Format for defining a function with 0 or more arguments
function: 'func' ('dict'? type | 'arr' '[' type ']') ID '(' (argumentList)? ')' functionBlock;

argumentList: argument (',' argument)*;

argument: 'arr' '[' type ']' ID | 'dict' ID | type ID;

//A function must have return statement
functionBlock: '{' (statement)* returnStatement '}';

returnStatement: 'return' expression? ';';

//Rule for defining class
classInit: 'class' ID '{' classProperty?'}';

classProperty: (initialization | declaration)* function*;

result: 'result' '(' expression ')' ';';

readInputExpr: 'readInput' '(' ')' ';'?;