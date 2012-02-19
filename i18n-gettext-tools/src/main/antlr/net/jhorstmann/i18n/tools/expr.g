header {
package net.jhorstmann.i18n.tools;

import net.jhorstmann.i18n.tools.*;
import net.jhorstmann.i18n.tools.expr.*;
}

class ExprParser extends Parser;

options {
    defaultErrorHandler=false;
}

expression returns [Expression r = null]
    : r=or_expr EOF
    ;

or_expr returns [Expression r = null] { Expression e = null; }
    : r=and_expr  ( OR e=and_expr   { r = new OrExpression(r, e); })*
    ;

and_expr returns [Expression r = null] { Expression e = null; }
    : r=neg_expr  ( AND e=neg_expr { r = new AndExpression(r, e); })*
    ;

neg_expr returns [Expression r = null] { Expression e = null; }
    : NOT e=prim_expr          { r = new NotExpression(e); }
    | r=prim_expr
    ;

prim_expr returns [Expression r = null] { Expression e = null; }
    : b:BOOL                   { r = new ConstantExpression(Integer.parseInt(b.getText())); }
    | OPEN e=or_expr CLOSE     { r = e; }
    ;

class ExprLexer extends Lexer;

options {
    k=2;
    defaultErrorHandler=false;
}

WS      : (' ' | '\t')+ { $setType(Token.SKIP); };
BOOL    : '0' | '1';
NOT     : '!';
OPEN    : '(';
CLOSE   : ')';
OR      : '|' '|';
AND     : '&' '&';