# AGENTS.md — EduGame Compiler

## Project Type
Java compiler for EduGame DSL (educational 2D game language in Spanish).

## Rule: Instructions vs Blocks (CRITICAL)
- **Instructions**: end with `;` (must call `terminarInstruccion()` in parser)
- **Blocks**: NEVER end with `;` (delimiters: `JUEGO`, `ESCENA`, `BUCLE`, `FIN`, `SI`, `SINO`, etc.)

```
MOVER jugador DERECHA 5;    ← instruction (has ;)
BUCLE                       ← block (no ;)
  AL_PRESIONAR TECLA...
  FIN
FIN                         ← block closer (no ;)
```

## Structure
```
edugame/
├── lexer/     — Lexer.java produces Token list, recognizes ';' as PUNTO_COMA
├── parser/   — Parser.java requires ';' after instructions only
├── token/    — TipoToken.java (enum), Token.java
├── ast/      — NodoAST subclasses
├── error/    — ErrorLexico, ErrorSintactico
└── main/     — Main.java entry point
```

## Key Classes
- `ClasificadorToken` — EnumSet for O(1) block/instruction classification
- `BuscadorLexico` — binary search O(log n) for reserved words
- `terminarInstruccion()` — parser method that enforces `PUNTO_COMA`

## Commands
Compile/run from `src/`:
```bash
javac edugame/**/*.java
java edugame.main.Main archivo.eg
```

## Implementation Order (if building from scratch)
1. TipoToken enum
2. Token class
3. ClasificadorToken
4. Error classes
5. MatrizPalabrasReservadas + BuscadorLexico
6. Lexer (must produce PUNTO_COMA for ';')
7. AST nodes
8. Parser (terminateInstruccion() for instructions, NOT for blocks)
9. Main

## No External Dependencies
Plain Java only. No ANTLR, JavaCC, or third-party lexer/parser tools.

## Documentation
See `EduGame_Plan_Desarrollo.md` for full architecture, BNF grammar, and implementation details.