header {
package net.jhorstmann.i18n.tools;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import java.io.StringReader;

import net.jhorstmann.i18n.tools.*;
import net.jhorstmann.i18n.tools.expr.*;
}

class PluralsParser extends Parser;

options {
    defaultErrorHandler=false;
}

{
    public static Expression parseExpression(String str) throws RecognitionException, TokenStreamException {
        PluralsLexer lexer = new PluralsLexer(new StringReader(str));
        PluralsParser parser = new PluralsParser(lexer);
        return parser.expression();
    }

    public static PluralForms parsePluralForms(String str) throws RecognitionException, TokenStreamException {
        PluralsLexer lexer = new PluralsLexer(new StringReader(str));
        PluralsParser parser = new PluralsParser(lexer);
        return parser.plurals();
    }
}

plurals returns [PluralForms pf = null] { int i; Expression e = null; }
    : i=nplurals SEMI e=plural (SEMI)* EOF { pf = new PluralForms(i, e); }
    ;

nplurals returns [int n = 0]
    : NPLURALS ASSIGN i:INT { n = Integer.parseInt(i.getText()); }
    ;

plural returns [Expression e = null]
    :  PLURAL ASSIGN e=root_expr
    ;

expression returns [Expression r = null]
    : r=root_expr EOF
    ;

root_expr returns [Expression r = null]
    : r=ternary_expr
    ;

ternary_expr returns [Expression r = null] { Expression e1 = null, e2 = null; }
    : r=or_expr  (options {greedy=true;} : QUEST e1=ternary_expr COLON e2=ternary_expr { r = new TernaryExpression(r, e1, e2); } )*
    ;

or_expr returns [Expression r = null] { Expression e = null; }
    : r=and_expr ( OR e=and_expr { r = new OrExpression(r, e); } )*
    ;

and_expr returns [Expression r = null] { Expression e = null; }
    : r=eq_expr  ( AND e=eq_expr { r = new AndExpression(r, e); } )*
    ;

eq_expr returns [Expression r = null] { Expression e = null; }
    : r=cmp_expr ( EQ e=cmp_expr { r = new CmpEqExpression(r, e); }
                 | NE e=cmp_expr { r = new CmpNeExpression(r, e); } )*
    ;

cmp_expr returns [Expression r = null] { Expression e = null; }
    : r=add_expr ( LT e=add_expr { r = new CmpLtExpression(r, e); }
                 | LE e=add_expr { r = new CmpLeExpression(r, e); }
                 | GT e=add_expr { r = new CmpGtExpression(r, e); }
                 | GE e=add_expr { r = new CmpGeExpression(r, e); } )*
    ;

add_expr returns [Expression r = null] { Expression e = null; }
    : r=mul_expr ( ADD e=mul_expr { r = new AddExpression(r, e); }
                 | SUB e=mul_expr { r = new SubExpression(r, e); } )*
    ;

mul_expr returns [Expression r = null] { Expression e = null; }
    : r=neg_expr ( MUL e=neg_expr { r = new MulExpression(r, e); }
                 | DIV e=neg_expr { r = new DivExpression(r, e); }
                 | MOD e=neg_expr { r = new ModExpression(r, e); } )*
    ;

neg_expr returns [Expression r = null] { Expression e = null; }
    : NOT e=prim_expr             { r = new NotExpression(e); }
    | r=prim_expr
    ;

prim_expr returns [Expression r = null] { Expression e = null; }
    : i:INT                    { r = new ConstantExpression(Integer.parseInt(i.getText())); }
    | NUM                      { r = new VariableExpression(); }
    | OPEN e=root_expr CLOSE   { r = e; }
    ;

class PluralsLexer extends Lexer;

options {
    k=2;
    defaultErrorHandler=false;
}

WS      : (' ' | '\t')+ { $setType(Token.SKIP); };
NPLURALS: "nplurals";
PLURAL  : "plural";
NUM     : 'n';
INT     : ('0'..'9')+;
SEMI    : ';';
OPEN    : '(';
CLOSE   : ')';
ASSIGN  : '=';
QUEST   : '?';
COLON   : ':';
OR      : '|' '|';
AND     : '&' '&';
EQ      : '=' '=';
NE      : '!' '=';
GE      : '>' '=';
GT      : '>';
LE      : '<' '=';
LT      : '<';
ADD     : '+';
SUB     : '-';
NOT     : '!';
MUL     : '*';
DIV     : '/';
MOD     : '%';
