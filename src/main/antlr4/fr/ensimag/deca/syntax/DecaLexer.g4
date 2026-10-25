lexer grammar DecaLexer;

options {
	language = Java;
	// Tell ANTLR to make the generated lexer class extend the the named class, which is where any
	// supporting code and variables will be placed.
	superClass = AbstractDecaLexer;
}

@members {
}

// Mots réservés
INSTANCEOF: 'instanceof';
READFLOAT: 'readFloat';
PROTECTED: 'protected';
PRINTLNX: 'printlnx';
EXTENDS: 'extends';
PRINTLN: 'println';
READINT: 'readInt';
PRINTX: 'printx';
RETURN: 'return';
WHILE: 'while';
PRINT: 'print';
FALSE: 'false';
CLASS: 'class';
ELSE: 'else';
NULL: 'null';
THIS: 'this';
TRUE: 'true';
ASM: 'asm';
NEW: 'new';
IF: 'if';

// Booléens
EQEQ: '==';
AND: '&&';
OR: '||';

NEQ: '!=';
LEQ: '<=';
GEQ: '>=';

EQUALS: '=';
GT: '>';
LT: '<';

// Symboles spéciaux
OPARENT: '(';
CPARENT: ')';
OBRACE: '{';
CBRACE: '}';
COMMA: ',';
SEMI: ';';

// MINUSONE: '--'; ADDONE: '++';
PERCENT: '%';
EXCLAM: '!';
MINUS: '-';
TIMES: '*';
SLASH: '/';
PLUS: '+';
DOT: '.';

// Identificateurs
fragment LETTER: 'a' .. 'z' | 'A' .. 'Z';
fragment DIGIT: '0' .. '9';
IDENT: (LETTER | '$' | '_') (LETTER | DIGIT | '$' | '_')*;

// Littéraux entiers
fragment POSITIVE_DIGIT: '1' .. '9';
INT: '0' | (POSITIVE_DIGIT DIGIT*) { syntax().checkInt(this); };

// Littéraux flottants
fragment NUM: DIGIT+;
fragment SIGN: ('+' | '-')?;
fragment EXP: ('E' | 'e') SIGN NUM;
fragment DEC: NUM '.' NUM;
fragment FLOATDEC: (DEC | DEC EXP) ('F' | 'f')?;
fragment DIGITHEX: '0' ..'9' | 'A' .. 'F' | 'a' .. 'f';
fragment NUMHEX: DIGITHEX+;
fragment FLOATHEX: ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM (
		'F'
		| 'f'
	)?;
// On sépare les deux règles pour tester plus facilemement l'underflow (true pour l'hexadecimal)
FLOAT:
	FLOATDEC { syntax().checkFloat(this, false); }
	| FLOATHEX { syntax().checkFloat(this, true); };

// Chaînes de caractères
fragment STRING_CAR: ~('"' | '\\' | '\n');
STRING: '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING:
	'"' (STRING_CAR | '\n' | '\\"' | '\\\\')* '"';

// Inclusion de fichiers
fragment FILENAME: (LETTER | DIGIT | '.' | '-' | '_')+;
INCLUDE:
	'#include' (' ')* '"' FILENAME '"' { doInclude(getText()); };

// Blancs
SPACES: (' ' | '\r' | '\n' | '\t') { skip(); };

// Commentaires
COMMENT: '//' ~('\r' | '\n')* { skip();};
MULTI_LINE_COMMENT: '/*' .*? '*/' { skip(); };