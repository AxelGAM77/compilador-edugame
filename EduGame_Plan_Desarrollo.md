# EduGame — Plan de Desarrollo del Compilador
> Lenguaje DSL educativo en español para videojuegos 2D  
> Implementación: Java · Desde cero · Analizador Léxico + Sintáctico

---

## Índice

1. [Visión general del proyecto](#1-visión-general)
2. [Regla fundamental — Instrucciones vs Bloques](#2-instrucciones-vs-bloques)
3. [Arquitectura de módulos](#3-arquitectura)
4. [Fase 1 — Analizador Léxico](#4-fase-1-analizador-léxico)
5. [Fase 2 — Analizador Sintáctico](#5-fase-2-analizador-sintáctico)
6. [Estructura de paquetes Java](#6-estructura-de-paquetes)
7. [Especificación de tokens y matrices](#7-tokens-y-matrices)
8. [Algoritmos de búsqueda propios](#8-algoritmos-de-búsqueda)
9. [Manejo de errores](#9-manejo-de-errores)
10. [Orden de implementación recomendado](#10-orden-de-implementación)
11. [Contratos de interfaz entre módulos](#11-contratos)

---

## 2. Regla Fundamental — Instrucciones vs Bloques

Esta es la regla más importante del lenguaje EduGame y afecta tanto al
léxico como al parser. Toda palabra reservada pertenece a exactamente
una de dos categorías:

---

### Categoría A — DELIMITADORES DE BLOQUE

Son palabras que **abren o cierran un contenedor lógico**.
**No terminan con punto y coma.**
Funcionan igual que `{` y `}` en lenguajes como Java o C.

#### Palabras que ABREN bloque

| Palabra reservada | Qué bloque abre |
|---|---|
| `JUEGO nombre 800x600` | Bloque raíz del programa completo |
| `ESCENA nombre` | Bloque de una escena |
| `INICIAR` | Bloque de configuración inicial (corre una vez) |
| `BUCLE` | Bloque del game loop (corre cada frame) |
| `DIBUJAR` | Bloque de renderizado (corre cada frame) |
| `SI condicion` | Bloque condicional |
| `SINO` | Bloque alternativo del SI |
| `REPETIR n VECES` | Bloque de ciclo con contador |
| `MIENTRAS condicion` | Bloque de ciclo con condición |
| `AL_PRESIONAR TECLA x` | Bloque de evento de teclado |
| `AL_SOLTAR TECLA x` | Bloque de evento al soltar tecla |
| `AL_CLICK` | Bloque de evento de mouse/touch |
| `AL_TOCAR entidad1 entidad2` | Bloque de evento de colisión |

#### Palabra que CIERRA bloque

| Palabra reservada | Qué cierra |
|---|---|
| `FIN` | Cierra cualquier bloque abierto (el más reciente) |

> **Nota para el parser:** `FIN` nunca lleva `;`. El parser lleva
> una pila interna para saber qué bloque está cerrando cada `FIN`.

---

### Categoría B — INSTRUCCIONES

Son palabras que expresan **una acción o declaración atómica**.
**Siempre terminan con punto y coma (`;`).**
El léxico produce el token `PUNTO_COMA` y el parser lo exige
al final de cada una de estas construcciones.

| Palabra reservada | Ejemplo completo |
|---|---|
| `PERSONAJE` | `PERSONAJE jugador IMAGEN "heroe.png" EN 100 300;` |
| `OBJETO` | `OBJETO moneda IMAGEN "moneda.png" EN 400 200;` |
| `FONDO` | `FONDO IMAGEN "fondo.png";` |
| `SONIDO` | `SONIDO recoger ARCHIVO "coin.wav";` |
| `MOVER` | `MOVER jugador DERECHA 5;` |
| `MOSTRAR` | `MOSTRAR "¡Ganaste!" EN 200 300;` |
| `OCULTAR` | `OCULTAR moneda;` |
| `REPRODUCIR` | `REPRODUCIR recoger;` |
| `DETENER` | `DETENER musica;` |
| `GUARDAR` | `GUARDAR puntos = puntos + 1;` |

---

### Ejemplo visual de la regla

```
JUEGO MiJuego 800x600               ← abre bloque JUEGO        (sin ;)

  PERSONAJE jugador IMAGEN "heroe.png" EN 100 300;  ← instrucción (con ;)
  OBJETO moneda IMAGEN "moneda.png" EN 400 200;     ← instrucción (con ;)
  SONIDO recoger ARCHIVO "coin.wav";                ← instrucción (con ;)

  BUCLE                             ← abre bloque BUCLE        (sin ;)

    AL_PRESIONAR TECLA DERECHA      ← abre bloque AL_PRESIONAR (sin ;)
      MOVER jugador DERECHA 5;      ← instrucción              (con ;)
    FIN                             ← cierra AL_PRESIONAR      (sin ;)

    AL_TOCAR jugador moneda         ← abre bloque AL_TOCAR     (sin ;)

      SI puntos > 10                ← abre bloque SI           (sin ;)
        MOSTRAR "¡Nivel 2!";        ← instrucción              (con ;)
      SINO                          ← cierra SI, abre SINO     (sin ;)
        REPRODUCIR recoger;         ← instrucción              (con ;)
        OCULTAR moneda;             ← instrucción              (con ;)
      FIN                           ← cierra SINO              (sin ;)

    FIN                             ← cierra AL_TOCAR          (sin ;)

  FIN                               ← cierra BUCLE             (sin ;)

FIN                                 ← cierra JUEGO             (sin ;)
```

---

### Tabla de referencia rápida

```
¿Cómo saber si una línea lleva ; ?

  → ¿La palabra abre o cierra un contenedor con FIN?
      SÍ → Es un BLOQUE → NO lleva ;
      NO → Es una INSTRUCCIÓN → SÍ lleva ;
```

---

## 1. Visión General

**EduGame** es un compilador para un lenguaje de dominio específico (DSL):

- Sintaxis completamente en español
- Orientado a videojuegos 2D educativos
- Público objetivo: estudiantes de 10 a 13 años
- Todo programa EduGame requiere una ventana gráfica (parte del dominio)
- Construido desde cero en Java sin librerías de análisis de terceros
- Las instrucciones atómicas terminan en `;`
- Los delimitadores de bloque (`JUEGO`, `ESCENA`, `BUCLE`, `FIN`, etc.) no llevan `;`

### Flujo general del compilador

```
Código fuente (.eg)
        │
        ▼
┌─────────────────┐
│ Analizador      │  Lee caracteres → produce Lista<Token>
│ Léxico          │  Reconoce ; como PUNTO_COMA
└────────┬────────┘
         │ Lista<Token>
         ▼
┌─────────────────┐
│ Analizador      │  Instrucciones → exige PUNTO_COMA al final
│ Sintáctico      │  Bloques → no exige PUNTO_COMA
└────────┬────────┘
         │ AST
         ▼
     [Fases futuras: semántico, generador de código]
```

---

## 3. Arquitectura

### Módulos principales

```
edugame/
├── lexer/          ← Analizador léxico completo
├── parser/         ← Analizador sintáctico completo
├── token/          ← Definición de Token y TipoToken
├── ast/            ← Nodos del árbol sintáctico
├── error/          ← Manejo y reporte de errores
└── main/           ← Punto de entrada, orquestador
```

### Responsabilidades por módulo

| Módulo | Responsabilidad | Produce |
|--------|----------------|---------|
| `lexer` | Leer carácter a carácter, clasificar tokens, reconocer `;` | `List<Token>` |
| `token` | Definir tipos de token incluyendo `PUNTO_COMA` | Enum `TipoToken`, clase `Token` |
| `parser` | Consumir tokens, exigir `;` en instrucciones, no en bloques | `NodoAST` raíz |
| `ast` | Representar cada construcción como nodo con hijos | Clases de nodos |
| `error` | Centralizar errores con línea y descripción | `ErrorEduGame` |
| `main` | Leer `.eg` → léxico → parser → reportar errores | Salida en consola |

---

## 4. Fase 1 — Analizador Léxico

### 4.1 Responsabilidad

Lee el código fuente carácter a carácter y produce una lista de objetos
`Token`. Reconoce el `;` como un token propio (`PUNTO_COMA`). No valida
si el `;` está en el lugar correcto — eso es responsabilidad del parser.

### 4.2 Clase Token

```java
// paquete: edugame.token

public class Token {
    private TipoToken tipo;
    private String    lexema;
    private int       linea;
    private int       columna;

    public Token(TipoToken tipo, String lexema, int linea, int columna) {
        this.tipo    = tipo;
        this.lexema  = lexema;
        this.linea   = linea;
        this.columna = columna;
    }

    // getters estándar...

    @Override
    public String toString() {
        return String.format("[%s | \"%s\" | L%d:C%d]", tipo, lexema, linea, columna);
    }
}
```

### 4.3 Enum TipoToken

```java
// paquete: edugame.token

public enum TipoToken {

    // ── DELIMITADORES DE BLOQUE (nunca llevan ;) ─────────────
    // Abren bloque:
    JUEGO, ESCENA, INICIAR, BUCLE, DIBUJAR,
    SI, SINO, REPETIR, MIENTRAS,
    AL_PRESIONAR, AL_SOLTAR, AL_CLICK, AL_TOCAR,
    // Cierra bloque:
    FIN,

    // ── INSTRUCCIONES (siempre terminan en ;) ────────────────
    PERSONAJE, OBJETO, FONDO, SONIDO,
    MOVER, MOSTRAR, OCULTAR,
    REPRODUCIR, DETENER,
    GUARDAR,

    // ── MODIFICADORES DE INSTRUCCIÓN ────────────────────────
    // Complementan instrucciones pero no son la instrucción
    // en sí ni abren bloque.
    IMAGEN, EN, ARCHIVO,
    DERECHA, IZQUIERDA, ARRIBA, ABAJO,
    TECLA, VECES,

    // ── OPERADORES LÓGICOS COMO PALABRAS ────────────────────
    Y, O, NO,

    // ── TIPOS DE DATO ────────────────────────────────────────
    NUMERO_KW, TEXTO_KW, VERDADERO, FALSO,

    // ── LITERALES ────────────────────────────────────────────
    NUMERO_ENTERO,      // 42  800  300
    NUMERO_DECIMAL,     // 3.14  0.5
    CADENA,             // "heroe.png"

    // ── IDENTIFICADORES ──────────────────────────────────────
    IDENTIFICADOR,      // jugador  moneda  miEscena

    // ── OPERADORES ───────────────────────────────────────────
    OP_SUMA,            // +
    OP_RESTA,           // -
    OP_MULT,            // *
    OP_DIV,             // /
    OP_MAYOR,           // >
    OP_MENOR,           // <
    OP_IGUAL,           // =
    OP_MAYOR_IGUAL,     // >=
    OP_MENOR_IGUAL,     // <=

    // ── SÍMBOLOS ESTRUCTURALES ───────────────────────────────
    PUNTO_COMA,         // ;  ← termina toda instrucción
    PARENTESIS_ABR,     // (
    PARENTESIS_CIE,     // )
    COMA,               // ,
    DOS_PUNTOS,         // :

    // ── ESPECIALES ───────────────────────────────────────────
    FIN_ARCHIVO,
    DESCONOCIDO
}
```

> **Convención:** los comentarios de sección dentro del enum son
> intencionales. Permiten identificar visualmente si un tipo es
> bloque o instrucción sin consultar documentación adicional.

### 4.4 ClasificadorToken — EnumSets de categoría

Esta clase centraliza la lógica de clasificación. El parser y el léxico
la consultan en lugar de repetir listas hardcodeadas.

```java
// paquete: edugame.token

import java.util.EnumSet;
import java.util.Set;

public class ClasificadorToken {

    /**
     * Tokens que ABREN un bloque. No llevan ; nunca.
     */
    public static final Set<TipoToken> ABREN_BLOQUE = EnumSet.of(
        TipoToken.JUEGO,
        TipoToken.ESCENA,
        TipoToken.INICIAR,
        TipoToken.BUCLE,
        TipoToken.DIBUJAR,
        TipoToken.SI,
        TipoToken.SINO,
        TipoToken.REPETIR,
        TipoToken.MIENTRAS,
        TipoToken.AL_PRESIONAR,
        TipoToken.AL_SOLTAR,
        TipoToken.AL_CLICK,
        TipoToken.AL_TOCAR
    );

    /**
     * Token que CIERRA un bloque. No lleva ; nunca.
     */
    public static final Set<TipoToken> CIERRAN_BLOQUE = EnumSet.of(
        TipoToken.FIN
    );

    /**
     * Tokens que inician una INSTRUCCIÓN.
     * El parser debe exigir PUNTO_COMA al final de cada una.
     */
    public static final Set<TipoToken> INICIAN_INSTRUCCION = EnumSet.of(
        TipoToken.PERSONAJE,
        TipoToken.OBJETO,
        TipoToken.FONDO,
        TipoToken.SONIDO,
        TipoToken.MOVER,
        TipoToken.MOSTRAR,
        TipoToken.OCULTAR,
        TipoToken.REPRODUCIR,
        TipoToken.DETENER,
        TipoToken.GUARDAR
    );

    // Consultas O(1) gracias a EnumSet (implementación por bitmask)

    public static boolean abreBloque(TipoToken t)         { return ABREN_BLOQUE.contains(t); }
    public static boolean cierraBloque(TipoToken t)       { return CIERRAN_BLOQUE.contains(t); }
    public static boolean iniciainstruccion(TipoToken t)  { return INICIAN_INSTRUCCION.contains(t); }
    public static boolean esDelimitador(TipoToken t)      { return abreBloque(t) || cierraBloque(t); }
}
```

> **Por qué EnumSet:** usa internamente un bitmask sobre el ordinal
> del enum. `contains()` es O(1) puro, más eficiente que `HashSet`
> para tipos enum.

### 4.5 Matrices de palabras reservadas

```java
// paquete: edugame.lexer

public class MatrizPalabrasReservadas {

    /**
     * CRÍTICO: ambos arrays deben mantenerse en el mismo orden
     * alfabético por LEXEMAS. Al agregar una palabra nueva,
     * insertarla en la posición correcta en AMBOS arrays.
     */

    public static final String[] LEXEMAS = {
        "ABAJO",        "AL_CLICK",     "AL_PRESIONAR", "AL_SOLTAR",
        "AL_TOCAR",     "ARCHIVO",      "ARRIBA",       "BUCLE",
        "DERECHA",      "DETENER",      "DIBUJAR",      "EN",
        "ESCENA",       "FALSO",        "FIN",          "FONDO",
        "GUARDAR",      "IMAGEN",       "INICIAR",      "IZQUIERDA",
        "JUEGO",        "MIENTRAS",     "MOSTRAR",      "MOVER",
        "NO",           "NUMERO",       "O",            "OBJETO",
        "OCULTAR",      "PERSONAJE",    "REPETIR",      "REPRODUCIR",
        "SI",           "SINO",         "SONIDO",       "TECLA",
        "TEXTO",        "VERDADERO",    "VECES",        "Y"
    };

    public static final TipoToken[] TIPOS = {
        TipoToken.ABAJO,        TipoToken.AL_CLICK,     TipoToken.AL_PRESIONAR, TipoToken.AL_SOLTAR,
        TipoToken.AL_TOCAR,     TipoToken.ARCHIVO,      TipoToken.ARRIBA,       TipoToken.BUCLE,
        TipoToken.DERECHA,      TipoToken.DETENER,      TipoToken.DIBUJAR,      TipoToken.EN,
        TipoToken.ESCENA,       TipoToken.FALSO,        TipoToken.FIN,          TipoToken.FONDO,
        TipoToken.GUARDAR,      TipoToken.IMAGEN,       TipoToken.INICIAR,      TipoToken.IZQUIERDA,
        TipoToken.JUEGO,        TipoToken.MIENTRAS,     TipoToken.MOSTRAR,      TipoToken.MOVER,
        TipoToken.NO,           TipoToken.NUMERO_KW,    TipoToken.O,            TipoToken.OBJETO,
        TipoToken.OCULTAR,      TipoToken.PERSONAJE,    TipoToken.REPETIR,      TipoToken.REPRODUCIR,
        TipoToken.SI,           TipoToken.SINO,         TipoToken.SONIDO,       TipoToken.TECLA,
        TipoToken.TEXTO_KW,     TipoToken.VERDADERO,    TipoToken.VECES,        TipoToken.Y
    };

    public static final int LONGITUD = LEXEMAS.length;
}
```

### 4.6 Clase Lexer — estructura general

```java
// paquete: edugame.lexer

public class Lexer {

    private final String fuente;
    private int          pos;
    private int          linea;
    private int          columna;
    private final int    longitud;

    public Lexer(String fuente) {
        this.fuente   = fuente;
        this.longitud = fuente.length();
        this.pos      = 0;
        this.linea    = 1;
        this.columna  = 1;
    }

    public List<Token> tokenizar() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = siguienteToken();
            if (t != null) tokens.add(t);
        } while (t == null || t.getTipo() != TipoToken.FIN_ARCHIVO);
        return tokens;
    }

    private Token siguienteToken() { /* ver sección 4.7 */ }

    private char actual()    { return pos < longitud ? fuente.charAt(pos) : '\0'; }
    private char siguiente() { return (pos+1) < longitud ? fuente.charAt(pos+1) : '\0'; }

    private char consumir() {
        char c = actual();
        pos++;
        if (c == '\n') { linea++; columna = 1; }
        else           { columna++; }
        return c;
    }
}
```

### 4.7 Algoritmo principal de tokenización

```
ALGORITMO siguienteToken():

  1. Ignorar espacios, tabs y saltos de línea → avanzar cursor
  2. Si fin de archivo → retornar Token(FIN_ARCHIVO, "EOF", linea, col)
  3. Guardar linea_inicio y col_inicio actuales
  4. Leer carácter actual c

  5. SWITCH sobre c:

     CASO c == '/' y siguiente == '/':
       → leerHastaFinDeLinea() → retornar null (comentario ignorado)

     CASO c == '"':
       → leerCadena() → retornar Token(CADENA, lexema, ...)

     CASO c == ';':
       → consumir → retornar Token(PUNTO_COMA, ";", linea, col)

     CASO esDigito(c):
       → leerNumero() → retornar Token(ENTERO o DECIMAL, lexema, ...)

     CASO esLetra(c) o c == '_':
       → leerPalabraOIdentificador()    ← acumula en mayúsculas
       → BuscadorLexico.buscarPalabraReservada(lexema)
       → si encontrado: retornar Token(tipoReservada, lexema, ...)
       → si no:         retornar Token(IDENTIFICADOR, lexema, ...)

     CASO c == '>':
       → si siguiente == '=': consumir dos → Token(OP_MAYOR_IGUAL)
       → si no: consumir uno  → Token(OP_MAYOR)

     CASO c == '<':
       → si siguiente == '=': consumir dos → Token(OP_MENOR_IGUAL)
       → si no: consumir uno  → Token(OP_MENOR)

     CASO c en { '+', '-', '*', '/', '=', '(', ')', ',', ':' }:
       → consumir → Token(tipo correspondiente, lexema, ...)

     DEFAULT:
       → consumir → Token(DESCONOCIDO, ...) → registrar ErrorLexico
```

### 4.8 Métodos auxiliares del Lexer

```
leerCadena():
  consumir '"' inicial
  acumular caracteres hasta '"' de cierre o fin de línea
  si no se encontró '"' de cierre → ErrorLexico "cadena no cerrada"
  retornar lexema SIN las comillas externas

leerNumero():
  acumular dígitos
  si siguiente es '.' y el carácter después es dígito:
    consumir '.' y seguir acumulando → tipo DECIMAL
  si no: tipo ENTERO

leerPalabraOIdentificador():
  acumular letras, dígitos, '_', tildes (á é í ó ú ü ñ Á É Í Ó Ú Ü Ñ)
  retornar lexema en MAYÚSCULAS (el lenguaje es case-insensitive
  para palabras reservadas)

esLetra(c):
  retornar true si c es [a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]

esDigito(c):
  retornar true si c es [0-9]
```

---

## 5. Fase 2 — Analizador Sintáctico

### 5.1 Responsabilidad

El parser aplica la siguiente política sin excepción:

- **Al parsear una instrucción:** luego de consumir todos sus tokens,
  llama a `terminarInstruccion()` que exige `PUNTO_COMA`.
  Si no lo encuentra → `ErrorSintactico`.
- **Al parsear un bloque:** consume la palabra de apertura y sus
  parámetros. **Nunca llama a `terminarInstruccion()`** en los
  delimitadores de bloque.

### 5.2 Clase Parser — navegación

```java
// paquete: edugame.parser

public class Parser {

    private final List<Token> tokens;
    private int               cursor;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.cursor = 0;
    }

    private Token actual()         { return tokens.get(cursor); }
    private TipoToken tipoActual() { return actual().getTipo(); }

    /**
     * Consume el token actual si es del tipo esperado.
     * Si no coincide → ErrorSintactico.
     */
    private Token consumir(TipoToken esperado) {
        Token t = actual();
        if (t.getTipo() == esperado) { cursor++; return t; }
        throw new ErrorSintactico(
            "Se esperaba '" + esperado + "' pero se encontró '"
            + t.getTipo() + "' (\"" + t.getLexema() + "\")",
            t.getLinea(), t.getColumna()
        );
    }

    private boolean verificar(TipoToken tipo) { return tipoActual() == tipo; }

    /**
     * Exige PUNTO_COMA al final de una instrucción.
     * Llamado SOLO en instrucciones. NUNCA en bloques.
     */
    private void terminarInstruccion() {
        consumir(TipoToken.PUNTO_COMA);
    }
}
```

### 5.3 Patrón de parseo por categoría

#### Parseo de un BLOQUE (sin ;)

```java
// Ejemplo: parsearBucle()
private NodoBucle parsearBucle() {
    consumir(TipoToken.BUCLE);           // consume "BUCLE"  ← sin ;
    List<NodoSentencia> cuerpo = parsearCuerpo();
    consumir(TipoToken.FIN);             // consume "FIN"    ← sin ;
    return new NodoBucle(cuerpo);
}

// Ejemplo: parsearSi()
private NodoSi parsearSi() {
    consumir(TipoToken.SI);              // ← sin ;
    NodoCondicion cond = parsearCondicion();
    List<NodoSentencia> cuerpoSi = parsearCuerpo();
    List<NodoSentencia> cuerpoSino = null;
    if (verificar(TipoToken.SINO)) {
        consumir(TipoToken.SINO);        // ← sin ;
        cuerpoSino = parsearCuerpo();
    }
    consumir(TipoToken.FIN);             // ← sin ;
    return new NodoSi(cond, cuerpoSi, cuerpoSino);
}
```

#### Parseo de una INSTRUCCIÓN (con ;)

```java
// Ejemplo: parsearMover()
private NodoMover parsearMover() {
    consumir(TipoToken.MOVER);
    Token entidad   = consumir(TipoToken.IDENTIFICADOR);
    Token direccion = consumirDireccion();
    Token vel       = consumir(TipoToken.NUMERO_ENTERO);
    terminarInstruccion();               // exige ";"  ← OBLIGATORIO
    return new NodoMover(entidad.getLexema(),
                         direccion.getLexema(),
                         Integer.parseInt(vel.getLexema()));
}

// Ejemplo: parsearPersonaje()
private NodoDeclaracionPersonaje parsearPersonaje() {
    consumir(TipoToken.PERSONAJE);
    Token nombre = consumir(TipoToken.IDENTIFICADOR);
    consumir(TipoToken.IMAGEN);
    Token imagen = consumir(TipoToken.CADENA);
    consumir(TipoToken.EN);
    Token x = consumir(TipoToken.NUMERO_ENTERO);
    Token y = consumir(TipoToken.NUMERO_ENTERO);
    terminarInstruccion();               // exige ";"  ← OBLIGATORIO
    return new NodoDeclaracionPersonaje(nombre.getLexema(),
                                        imagen.getLexema(),
                                        Integer.parseInt(x.getLexema()),
                                        Integer.parseInt(y.getLexema()));
}

// Ejemplo: parsearGuardar()
private NodoGuardar parsearGuardar() {
    consumir(TipoToken.GUARDAR);
    Token nombre = consumir(TipoToken.IDENTIFICADOR);
    consumir(TipoToken.OP_IGUAL);
    NodoExpresion expr = parsearExpresion();
    terminarInstruccion();               // exige ";"  ← OBLIGATORIO
    return new NodoGuardar(nombre.getLexema(), expr);
}
```

### 5.4 Dispatcher de sentencias

```java
private NodoSentencia parsearSentencia() {
    TipoToken tipo = tipoActual();

    // ── Instrucciones (terminarInstruccion() llamado internamente) ──
    if (tipo == TipoToken.MOVER)        return parsearMover();
    if (tipo == TipoToken.MOSTRAR)      return parsearMostrar();
    if (tipo == TipoToken.OCULTAR)      return parsearOcultar();
    if (tipo == TipoToken.REPRODUCIR)   return parsearReproducir();
    if (tipo == TipoToken.DETENER)      return parsearDetener();
    if (tipo == TipoToken.GUARDAR)      return parsearGuardar();

    // ── Bloques (sin ; en apertura ni en cierre) ─────────────────
    if (tipo == TipoToken.SI)           return parsearSi();
    if (tipo == TipoToken.REPETIR)      return parsearRepetir();
    if (tipo == TipoToken.MIENTRAS)     return parsearMientras();
    if (tipo == TipoToken.AL_PRESIONAR) return parsearAlPresionar();
    if (tipo == TipoToken.AL_SOLTAR)    return parsearAlSoltar();
    if (tipo == TipoToken.AL_CLICK)     return parsearAlClick();
    if (tipo == TipoToken.AL_TOCAR)     return parsearAlTocar();

    // ── Token inesperado ─────────────────────────────────────────
    Token t = actual();
    throw new ErrorSintactico(
        "Se esperaba una instrucción o bloque pero se encontró '"
        + t.getLexema() + "'",
        t.getLinea(), t.getColumna()
    );
}
```

### 5.5 Gramática BNF actualizada

```bnf
<programa>        ::= JUEGO IDENTIFICADOR <dimensiones> <cuerpo_juego> FIN

<dimensiones>     ::= NUMERO_ENTERO 'x' NUMERO_ENTERO

<cuerpo_juego>    ::= <instruccion_decl>* <bloque_iniciar>? <escena>*

<instruccion_decl>::= <decl_personaje> | <decl_objeto>
                    | <decl_sonido>    | <decl_fondo>

// ── Declaraciones: todas terminan en ; ──────────────────────────
<decl_personaje>  ::= PERSONAJE IDENTIFICADOR IMAGEN CADENA
                      EN NUMERO_ENTERO NUMERO_ENTERO ';'

<decl_objeto>     ::= OBJETO IDENTIFICADOR IMAGEN CADENA
                      EN NUMERO_ENTERO NUMERO_ENTERO ';'

<decl_sonido>     ::= SONIDO IDENTIFICADOR ARCHIVO CADENA ';'

<decl_fondo>      ::= FONDO IMAGEN CADENA ';'

// ── Bloques: sin ; en ningún delimitador ────────────────────────
<bloque_iniciar>  ::= INICIAR <sentencia>* FIN

<escena>          ::= ESCENA IDENTIFICADOR
                        <instruccion_decl>*
                        <bloque_bucle>?
                        <bloque_dibujar>?
                      FIN

<bloque_bucle>    ::= BUCLE   <sentencia>* FIN
<bloque_dibujar>  ::= DIBUJAR <sentencia>* FIN

<sentencia>       ::= <sent_instruccion> | <sent_bloque>

// ── Instrucciones internas: todas terminan en ; ─────────────────
<sent_instruccion>::= <sent_mover>     | <sent_mostrar>
                    | <sent_ocultar>   | <sent_reproducir>
                    | <sent_detener>   | <sent_guardar>

<sent_mover>      ::= MOVER IDENTIFICADOR <direccion> NUMERO_ENTERO ';'
<sent_mostrar>    ::= MOSTRAR (CADENA | IDENTIFICADOR)
                      (EN NUMERO_ENTERO NUMERO_ENTERO)? ';'
<sent_ocultar>    ::= OCULTAR IDENTIFICADOR ';'
<sent_reproducir> ::= REPRODUCIR IDENTIFICADOR ';'
<sent_detener>    ::= DETENER IDENTIFICADOR ';'
<sent_guardar>    ::= GUARDAR IDENTIFICADOR OP_IGUAL <expresion> ';'

// ── Bloques internos: sin ; en ningún delimitador ───────────────
<sent_bloque>     ::= <sent_si>          | <sent_repetir>
                    | <sent_mientras>
                    | <sent_al_presionar> | <sent_al_soltar>
                    | <sent_al_click>     | <sent_al_tocar>

<sent_si>         ::= SI <condicion> <sentencia>* (SINO <sentencia>*)? FIN
<sent_repetir>    ::= REPETIR NUMERO_ENTERO VECES <sentencia>* FIN
<sent_mientras>   ::= MIENTRAS <condicion> <sentencia>* FIN
<sent_al_presionar>::= AL_PRESIONAR TECLA <nombre_tecla> <sentencia>* FIN
<sent_al_soltar>  ::= AL_SOLTAR   TECLA <nombre_tecla> <sentencia>* FIN
<sent_al_click>   ::= AL_CLICK <sentencia>* FIN
<sent_al_tocar>   ::= AL_TOCAR IDENTIFICADOR IDENTIFICADOR <sentencia>* FIN

<direccion>       ::= DERECHA | IZQUIERDA | ARRIBA | ABAJO
<nombre_tecla>    ::= DERECHA | IZQUIERDA | ARRIBA | ABAJO | IDENTIFICADOR

<condicion>       ::= <expresion> <op_rel> <expresion>
                    | <condicion> Y <condicion>
                    | <condicion> O <condicion>
                    | NO <condicion>

<op_rel>          ::= OP_MAYOR | OP_MENOR | OP_IGUAL
                    | OP_MAYOR_IGUAL | OP_MENOR_IGUAL

<expresion>       ::= <termino> ((OP_SUMA | OP_RESTA) <termino>)*
<termino>         ::= <factor> ((OP_MULT | OP_DIV) <factor>)*
<factor>          ::= NUMERO_ENTERO | NUMERO_DECIMAL | CADENA
                    | IDENTIFICADOR | '(' <expresion> ')'
```

---

## 6. Estructura de Paquetes Java

```
src/
└── edugame/
    ├── main/
    │   └── Main.java
    │
    ├── token/
    │   ├── TipoToken.java              ← enum con grupos comentados
    │   ├── Token.java
    │   └── ClasificadorToken.java      ← EnumSets + métodos O(1)
    │
    ├── lexer/
    │   ├── Lexer.java                  ← produce PUNTO_COMA para ;
    │   ├── MatrizPalabrasReservadas.java
    │   └── BuscadorLexico.java         ← búsqueda binaria
    │
    ├── parser/
    │   ├── Parser.java                 ← terminarInstruccion() para ;
    │   └── BuscadorTokens.java
    │
    ├── ast/
    │   ├── NodoAST.java
    │   ├── NodoPrograma.java
    │   ├── NodoEscena.java
    │   ├── NodoBucle.java
    │   ├── NodoDibujar.java
    │   ├── NodoBloqueIniciar.java
    │   ├── NodoDeclaracionPersonaje.java
    │   ├── NodoDeclaracionObjeto.java
    │   ├── NodoDeclaracionSonido.java
    │   ├── NodoMover.java
    │   ├── NodoMostrar.java
    │   ├── NodoOcultar.java
    │   ├── NodoReproducir.java
    │   ├── NodoGuardar.java
    │   ├── NodoSi.java
    │   ├── NodoRepetir.java
    │   ├── NodoMientras.java
    │   ├── NodoAlPresionar.java
    │   ├── NodoAlTocar.java
    │   ├── NodoExpresion.java
    │   └── NodoCondicion.java
    │
    └── error/
        ├── ErrorEduGame.java
        ├── ErrorLexico.java
        └── ErrorSintactico.java
```

---

## 7. Especificación de Tokens y Matrices

### 7.1 Reglas de clasificación

| Tipo | Regla de reconocimiento | Lleva `;` |
|------|------------------------|-----------|
| Delimitador de bloque | En `ClasificadorToken.ABREN_BLOQUE` o `CIERRAN_BLOQUE` | **Nunca** |
| Instrucción | En `ClasificadorToken.INICIAN_INSTRUCCION` | **Siempre** |
| `IDENTIFICADOR` | Letra/`_` inicial, no en matriz | Según contexto |
| `NUMERO_ENTERO` | Solo dígitos `[0-9]+` | Según contexto |
| `NUMERO_DECIMAL` | `[0-9]+\.[0-9]+` | Según contexto |
| `CADENA` | Entre `"..."` en una línea | Según contexto |
| `PUNTO_COMA` | Carácter `;` | Es el terminador |
| `DESCONOCIDO` | Carácter no reconocido | — error léxico |

### 7.2 Prioridad de reconocimiento en el léxico

```
1. Espacios / saltos → ignorar
2. Comentario (//)   → ignorar
3. Cadena ("")       → CADENA
4. Punto y coma (;)  → PUNTO_COMA     ← alta prioridad, carácter único
5. Número            → ENTERO o DECIMAL
6. Letra o _         → buscar en matriz → RESERVADA o IDENTIFICADOR
7. Operador doble (>= <=) antes que simple (> <)
8. Operador simple   → OPERADOR
9. Símbolo           → SÍMBOLO
10. Cualquier otro   → DESCONOCIDO + error léxico
```

---

## 8. Algoritmos de Búsqueda Propios

### 8.1 Búsqueda Binaria — palabras reservadas O(log n)

```java
// paquete: edugame.lexer

public class BuscadorLexico {

    /**
     * Búsqueda binaria sobre MatrizPalabrasReservadas.LEXEMAS.
     * Requiere array ordenado alfabéticamente.
     * @param lexema  ya normalizado a MAYÚSCULAS
     * @return TipoToken si es reservada, null si es IDENTIFICADOR
     */
    public static TipoToken buscarPalabraReservada(String lexema) {
        int izq = 0;
        int der = MatrizPalabrasReservadas.LONGITUD - 1;

        while (izq <= der) {
            int mid = izq + (der - izq) / 2;
            int cmp = lexema.compareTo(MatrizPalabrasReservadas.LEXEMAS[mid]);

            if (cmp == 0)     return MatrizPalabrasReservadas.TIPOS[mid];
            else if (cmp < 0) der = mid - 1;
            else              izq = mid + 1;
        }
        return null;
    }
}
```

### 8.2 Búsqueda Lineal con Early Exit — lista de tokens O(1)–O(n)

```java
// paquete: edugame.parser

public class BuscadorTokens {

    public static int buscarPrimero(List<Token> tokens,
                                     int desde, int hasta,
                                     TipoToken buscado) {
        for (int i = desde; i < hasta && i < tokens.size(); i++) {
            if (tokens.get(i).getTipo() == buscado) return i;
        }
        return -1;
    }

    public static int contarTipo(List<Token> tokens,
                                  int desde, int hasta,
                                  TipoToken tipo) {
        int cuenta = 0;
        for (int i = desde; i < hasta && i < tokens.size(); i++) {
            if (tokens.get(i).getTipo() == tipo) cuenta++;
        }
        return cuenta;
    }

    public static boolean existe(List<Token> tokens,
                                  int desde, int hasta,
                                  TipoToken tipo) {
        return buscarPrimero(tokens, desde, hasta, tipo) != -1;
    }
}
```

### 8.3 Tabla de eficiencia

| Operación | Estructura | Algoritmo | Complejidad |
|-----------|-----------|-----------|-------------|
| Clasificar palabra reservada | Array ordenado | Búsqueda binaria | O(log n) |
| Clasificar si es bloque/instrucción | EnumSet | Bitmask | O(1) |
| Localizar siguiente token tipo X | List<Token> | Lineal early exit | O(1)–O(n) |
| Contar tokens tipo X en rango | List<Token> | Lineal completo | O(n) |

---

## 9. Manejo de Errores

### 9.1 Jerarquía

```java
public class ErrorEduGame extends RuntimeException {
    private final int linea, columna;
    public ErrorEduGame(String msg, int linea, int columna) {
        super(msg); this.linea = linea; this.columna = columna;
    }
    @Override public String toString() {
        return String.format("Error L%d:C%d → %s", linea, columna, getMessage());
    }
}
public class ErrorLexico     extends ErrorEduGame { /* prefijo [LÉXICO]     */ }
public class ErrorSintactico extends ErrorEduGame { /* prefijo [SINTÁCTICO] */ }
```

### 9.2 Errores léxicos

| Situación | Mensaje |
|-----------|---------|
| Carácter no reconocido | `Carácter no reconocido: '@'` |
| Cadena sin cerrar | `Cadena de texto no cerrada` |
| Número mal formado `3.` | `Número decimal incompleto: '3.'` |

### 9.3 Errores sintácticos relacionados con `;`

| Situación | Mensaje |
|-----------|---------|
| Instrucción sin `;` | `Se esperaba ';' al final de MOVER en L8` |
| `;` después de `FIN` | `FIN es delimitador de bloque y no lleva ';'` |
| `;` después de `BUCLE` | `BUCLE es delimitador de bloque y no lleva ';'` |
| Bloque sin `FIN` | `Se esperaba FIN para cerrar el bloque BUCLE abierto en L5` |

---

## 10. Orden de Implementación Recomendado

```
Paso 1 ── token/TipoToken.java
           Enum completo con grupos comentados.

Paso 2 ── token/Token.java
           Clase con tipo, lexema, linea, columna.

Paso 3 ── token/ClasificadorToken.java
           Definir los tres EnumSets y métodos O(1).

Paso 4 ── error/ (las tres clases)

Paso 5 ── lexer/MatrizPalabrasReservadas.java
           Verificar orden alfabético antes de continuar.

Paso 6 ── lexer/BuscadorLexico.java
           Probar: primera palabra, última, mitad, ausente.

Paso 7 ── lexer/Lexer.java
           Implementar tokenizar() con soporte a PUNTO_COMA.
           Verificar que:
             MOVER jugador DERECHA 5;
             → [..., MOVER, IDENTIFICADOR, DERECHA, NUMERO_ENTERO, PUNTO_COMA]
             BUCLE
             → [BUCLE]  (sin PUNTO_COMA al final)

Paso 8 ── ast/ (todos los nodos como estructuras de datos)

Paso 9 ── parser/BuscadorTokens.java

Paso 10 ── parser/Parser.java
            Implementar en este orden:
              parsear()
              parsearCuerpo()
              parsearDeclaracion()     ← terminarInstruccion() al final
              parsearEscena()
              parsearBucle()           ← sin terminarInstruccion()
              parsearSentencia()       ← dispatcher
              parsearMover() y demás instrucciones   (con ;)
              parsearSi() y demás bloques            (sin ;)
              parsearExpresion()
              parsearCondicion()

Paso 11 ── main/Main.java
            Leer .eg con UTF-8 → Lexer → Parser → reporte de errores.
```

---

## 11. Contratos de Interfaz Entre Módulos

```
CONTRATO: Lexer.tokenizar()
  POST:
    - Lista nunca vacía; último token siempre FIN_ARCHIVO.
    - El carácter ';' produce exactamente un token PUNTO_COMA.
    - Comentarios y espacios NO están en la lista.
    - Tokens DESCONOCIDO registrados como ErrorLexico.

CONTRATO: Parser.parsear()
  INVARIANTE:
    - terminarInstruccion() se llama si y solo si
      ClasificadorToken.iniciainstruccion(tipo) == true.
    - Ningún método de bloque llama a terminarInstruccion().
  POST:
    - Sin errores → NodoPrograma completo.
    - Con error   → ErrorSintactico con línea y columna exactas.

CONTRATO: Main.main()
  1. Leer .eg como String con charset UTF-8.
  2. Lexer → capturar ErrorLexico → si hay errores: imprimir y terminar.
  3. Parser → capturar ErrorSintactico → si hay errores: imprimir y terminar.
  4. Imprimir "Análisis completado sin errores."
```

---

*EduGame — Compilador DSL educativo en español*
*Fases: Analizador Léxico + Analizador Sintáctico · Java desde cero*
