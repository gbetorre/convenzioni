/*
 *   Convenzioni On Line (COL-GeCo). 
 *   Applicazione web: 
 *   - per la visualizzazione delle convenzioni attivate dall'ateneo, 
 *   - per la gestione delle convenzioni della pubblica amministrazione, 
 *   - per ottenere notifiche riguardo le scadenze ed i rinnovi
 *   - e per effettuare il monitoraggio delle attività legate alle convenzioni.
 *
 *   Agreements Mapping and Management Software (COL-GeCo).
 *   Web application: 
 *   - for viewing conventions activated by the university,
 *   - for the management of public administration conventions,
 *   - to obtain notifications regarding deadlines and renewals
 *   - and to carry out monitoring of activities related to agreements..
 *   
 *   Copyright (C) 2025 Giovanroberto Torre
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, see <https://www.gnu.org/licenses/>.
 *
 *   Giovanroberto Torre <gianroberto.torre@gmail.com>
 *   Universita' degli Studi di Verona
 *   Via Dell'Artigliere, 8
 *   37129 Verona (Italy)
 */

package it.col.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import it.col.bean.CodeBean;


/**
 * <p>Questa &egrave; l'interfaccia contenente le costanti utilizzabili da tutti i 
 * consumer java della web-application &nbsp;<code>Convenzioni on Line (col)</code>.</p>
 * <p>Definisce costanti di utilit&agrave;,
 * insiemi di valori ammessi per parametri applicativi, maschere di input, 
 * etichette etc.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public interface Constants extends Serializable {
    /* ************************************************************************ *
     *            Costanti parlanti per valori di inizializzazione              *
     *  - Se un valore vale -1 significa che è stato inizializzato da Command   *
     *  - Se un valore vale -2 significa che è stato inizializzato nel Bean     *
     *  - Se un valore vale  0 è la cardinalità di una struttura vuota          *    
     * ************************************************************************ */
    /**
     * <p>Costante parlante per i test che controllano
     * che interi abbiano un valore maggiore di zero.</p>
     * <p>Maggiormente visibile e chiara del valore che incapsula (0)
     * per questo motivo pu&ograve; essere utilizzata in inizializzazioni di
     * variabili e in test che controllano che specifici parametri
     * abbiano un valore significativo.</p>
     */
    public static final byte NOTHING = 0;
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione
     * in un Controller o da utilizzare come argomento.</p>
     * Incapsula un valore convenzionale per i test che controllano,
     * nelle Command, se &egrave; presente un valore significativo;
     * se in Request non &grave; presente tale valore, lo imposta a
     * un valore di default negativo (-1), in modo da facilitare i test a valle
     * del tentato recupero.
     * <p>Maggiormente visibile e chiara del valore che incapsula
     * <code>(-1) </code> per questo motivo pu&ograve; essere utilizzata
     * direttamente nel codice ad esempio in caso di inzializzazioni
     * di default effettuate dal
     * </code>{@link com.oreilly.servlet.ParameterParser ParameterParser}</code>.</p>
     */
    public static final int DEFAULT_ID = -1;
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione
     * in un Bean del Model o da utilizzare come argomento.</p>
     * <p>Incapsula un valore convenzionale definito nel bean padre CodeBean
     * per comodit&agrave; di accesso dal di fuori del package bean e delle
     * sottoclassi di CodeBean.</p>
     */
    public static final int BEAN_DEFAULT_ID = CodeBean.BEAN_DEFAULT_ID;
    /**
     * <p>Costante da utilizzare quando serve un valore convenzionale 
     * per inizializzazione di un attributo di un Bean del Model di tipo
     * float o da utilizzare come argomento.</p>
     * <p>Incapsula un valore convenzionale definito nel bean padre CodeBean
     * per comodit&agrave; di accesso dal di fuori del package bean e delle
     * sottoclassi di CodeBean.</p>
     */
    public static final float BEAN_DEFAULT_FLOAT = CodeBean.BEAN_DEFAULT_FLOAT;
    /**
     * <p>Valore identificante la tabella contenente una struttura o un processo 
     * di primo livello (p.es. 1 = struttura_liv1 | 1 = macroprocesso_at)</p>
     */
    public static final byte ELEMENT_LEV_1 = 1;
    /**
     * <p>Valore identificante la tabella contenente una struttura o un processo
     * di secondo livello (2 = struttura_liv2 | 2 = processo_at)</p>
     */
    public static final byte ELEMENT_LEV_2 = 2;
    /**
     * <p>Valore identificante la tabella contenente una struttura o un processo
     * di terzo livello (3 = struttura_liv3 | 3 = sottoprocesso_at)</p>
     */
    public static final byte ELEMENT_LEV_3 = 3;
    /**
     * <p>Valore identificante la tabella contenente una struttura
     * di terzo livello (4 = struttura_liv4).
     * Per quanto riguarda i processi, il quarto livello pu&ograve;
     * identificare le fasi
     * (1 = macroprocesso_at | 2 = processo_at | 3 = sottoprocesso_at)</p>
     */
    public static final byte ELEMENT_LEV_4 = 4;
    /**
     * <p>Costante parlante per flag di recupero sessione utente.</p>
     */
    public static final boolean IF_EXISTS_DONOT_CREATE_NEW = false;
    /**
     * <p>Etichetta per valore indecidibile</p>
     */
    public static final String ND = "Non determinabile";
    /* ************************************************************************ *
     * ---------------------------    COMMANDS    ----------------------------  *
     * ************************************************************************ */
    /**
     * <p>Costante per il parametro identificante la HomePageCommand.</p>
     */
    public static final String COMMAND_HOME             = "home";
    /**
     * <p>Costante per il parametro identificante la Command delle convenzioni.</p>
     */
    public static final String COMMAND_CONV             = "conv";
    /* ************************************************************************ *
     * --------------------------    QUERYSTRINGS    -------------------------- *
     * ************************************************************************ */
    /**
     * <p>Costante per il parametro identificante il tipo di output.</p>
     */
    public static final String PART_OUTPUT              = "out";
    /**
     * <p>Costante per il parametro identificante un messaggio (conferma, codice di errore, etc.).</p>
     */
    public static final String MESSAGE                  = "msg";
    /**
     * <p>Costante per il parametro identificante un referral (riferimento).
     * Una stessa funzionalit&agrave; (callee) pu&ograve; essere invocata 
     * da diversi chiamanti (caller) e non &egrave; detto che il risultato esatto
     * della callee debba essere lo stesso per tutti i caller. 
     * Questo parametro permette di identificare tra i diversi caller 
     * qual &egrave; quello che ha fatto la callee.
     * Se si dovesse mantenere traccia della coppia caller-callee potrebbero 
     * essere creati due parametri, p.es. CLR E CLE; per il momento &egrave;
     * sufficiente mantenere l'identificazione del chiamante (caller).</p>
     */
    public static final String REFERRAL                 = "ref";
    /* 
     * --------------------    OPERATIONS    -------------------- 
     */
    /**
     * <p>Costante per il parametro identificante che c'è da fare un'operazione.</p>
     */
    public static final String OPERATION                = "op";
    /**
     * <p>Costante per il parametro identificante l'oggetto dell'operazione.</p>
     */
    public static final String OBJECT                   = "obj";
    /**
     * <p>Costante per il parametro identificante la funzione di selezione di un elemento.</p>
     */
    public static final String SELECT                   = "sel";
    /**
     * <p>Costante per il parametro identificante la funzione di inserimenot di un elemento.</p>
     */
    public static final String INSERT                   = "ins";
    /**
     * <p>Costante per il parametro identificante la funzione di aggiornamento di un elemento.</p>
     */
    public static final String UPDATE                   = "upd";
    /**
     * <p>Costante per il parametro identificante la funzione di eliminazione di un elemento.</p>
     */
    public static final String DELETE                   = "del";
    /* 
     * --------------------    OTHER PARAMS    -------------------- 
     */
    /**
     * <p>Costante per il parametro identificante la pagina dell'utente.</p>
     */
    public static final String PART_USR                 = "usr";
    /**
     * <p>Costante per il parametro identificante la pagina dei credits dell'applicazione.</p>
     */
    public static final String PART_CREDITS             = "crd";
    /* ************************************************************************ *
     *   Costanti tipografiche per la generazione di output (p.es. csv) e URL   *
     * ************************************************************************ */
    /**
     * <p>Costante da utilizzare quando serve uno spazio (l'equivalente,
     * in Java, dell'html &quot;&nbsp;&quot;), generalmente usato
     * per separare pi&uacute; sottostostringhe in una stringa
     * da restituire come valore oppure messaggio.</p>
     */
    public static final char BLANK_SPACE = ' ';
    /**
     * Costante per l'uso del separatore trattino
     */
    public static final char HYPHEN = '-';
    /**
     * Costante per l'uso del separatore underscore
     */
    public static final char UNDERSCORE = '_';
    /**
     * Costante per l'uso del carattere punto e virgola 
     */
    public static final char SEMICOLON = ';';
    /**
     * Costante per l'uso del carattere due punti
     */
    public static final char COLON = ':';
    /**
     * Costante per l'uso del separatore '=' (p.es., tra parametro e valore) 
     */
    public static final char EQ = '=';
    /**
     * Costante per l'uso del separatore 'slash' (p.es. nei percorsi)
     */
    public static final char SLASH = '/';
    /**
     * Costante per l'uso del separatore '?' (question mark)
     */
    public static final char QM = '?';
    /**
     * Costante per far riferimento al carattere di virgoletta semplice, o apostrofo
     */
    public static final char APOSTROPHE = '\'';
    /**
     * Costante per far riferimento al carattere di virgoletta doppia
     */
    public static final char QUOTE = '\"';
    /**
     * Costante per far riferimento al carattere di percentuale
     */
    public static final char PER_CENT = '%';
    /**
     * Costante per far riferimento al carattere tipografico di virgoletta inglese
     */
    public static final char ENGLISH_SINGLE_QUOTE = '’';
    /**
     * Costante per il carattere tipografico di doppie virgolette inglesi aperte
     */
    public static final char ENGLISH_DOUBLE_QUOTE_OPEN = '“';
    /**
     * Costante per il carattere tipografico di doppie virgolette inglesi chiuse
     */   
    public static final char ENGLISH_DOUBLE_QUOTE_CLOSE = '”';
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione,
     * o da utilizzare come argomento, per effettuare test, etc.</p>
     * <p>Graficamente maggiormente visibile e chiara della stringa vuota
     * che contiene (doppi apici aperti chiusi) &ndash;
     * se quest'ultima &egrave; utilizzata direttamente nel codice &ndash;
     * &egrave; pi&uacute; ottimizzata rispetto al puntamento
     * a una nuova stringa attraverso
     * il richiamo del costruttore &nbsp;<code>new String("");</code><br />
     * (si veda, ad esempio:
     * <a href="http://www.precisejava.com/javaperf/j2se/StringAndStringBuffer.htm">
     * questa discussione</a>)</p>.
     */
    public static final String VOID_STRING = "";
    /**
     * <p>Costante da utilizzare quando serve un valore che, come dice il nome, 
     * funga da stringa vuota in linguaggio SQL, tipicamente da passare 
     * come parametro, specialmente nelle clausole di tipo ILIKE ().</p>
     * <p>Utile soprattutto perch&eacute; graficamente maggiormente visibile 
     * e chiara della stringa vuota SQL, che corrisponde a due apici singoli 
     * consecutivi, che per poter essere trattati da Java come stringa, 
     * devono a loro volta essere circondati da doppi apici.</p>
     */
    public static final String VOID_SQL_STRING = "''";
    /**
     * <p>Costante da utilizzare quando serve un valore per inizializzazione
     * di default di un parametro mancante sulla querystring.</p>
     * <p>Incapsula il classico trattino (-) che viene utilizzato come default
     * per i valori mancanti di parametri richiesti tipicamente dalle
     * Command.</p>
     */
    public static final String DASH = String.valueOf(HYPHEN);
    /**
     * Costante per l'uso del separatore semicolon 
     */
    public static final String SEPARATOR = String.valueOf(SEMICOLON);
    /**
     * Costante per l'uso del separatore punto
     */
    public static final String DOT = ".";
    /**
     * Costante per l'uso del separatore virgola
     */
    public static final String COMMA = ",";
    /**
     * Costante per il separatore che precede immediatamente la querystring (root question mark)
     */
    public static final String ROOT_QM = String.valueOf(SLASH) + String.valueOf(QM);
    /**
     * Costante per il separatore dei token querystring "&"
     */
    public static final String AMPERSAND = "&";
    /* ************************************************************************ *
     * Formati di files, naming di files, tipi MIME etc.
     * ************************************************************************ */
    /**
     * Costante per il tipo MIME testo semplice
     */
    public static final String MIME_TYPE_TEXT = "text/plain";   
    /**
     * Costante per il tipo MIME html
     */
    public static final String MIME_TYPE_HTML = "text/html";
    /**
     * Costante per il formato di file "Hyper Text Markup Language"
     */
    public static final String HTML = "html";
    /**
     * Costante per il formato di file "Comma Separated Values"
     */
    public static final String CSV = "csv";
    /**
     * Costante per il formato di file "Rich Text Format"
     */
    public static final String RTF = "rtf";
    /**
     * Costante per il formato di file "JavaScript Object Notation"
     */
    public static final String JSON = "json";
    /**
     * Costante per il formato di file "Java Server Pages"
     */
    public static final String JSP_EXT = "jsp";
    /* ************************************************************************ *
     * Costanti di tempo (p.es. formati di data, date significative, etc.) *
     * ************************************************************************ */
    /**
     * <p>Pattern che deve avere una data (oggetto java.util.Date o GregorianCalendar) per essere conforme al fornato SQL.</p>
     */
    public static final String DATA_SQL_PATTERN = "yyyy-MM-dd";
    /**
     * <p>Pattern che specifica il formato di una String che rappresenta una data
     * esprimendola relativamente per esteso; esempio: "Tue Jul 07 00:00:00 CEST 2020".</p>
     */
    public static final String DATA_GENERAL_PATTERN ="EEE MMM dd HH:mm:ss zzz yyyy";
    /**
     * <p>Contiene la formattazione che deve avere una data all'interno dell'applicazione.</p>
     */
    public static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat(DATA_SQL_PATTERN);
    /**
     * <p>Pattern per un oggetto Time espresso su ore:minuti:secondi.<br />
     * <strong>ATTENZIONE:</strong> nella localizzazione italiana 
     * la maschera di formattazione deve essere del tipo HH:mm e non hh:mm, 
     * altrimenti un orario come le 12:05 viene interpretato come 00:05! 
     * Capital H permette di specificare il reset delle ore 
     * su un totale di 24, non su un totale di 12.<br />
     * cfr. https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
     * <pre>
     * Letter   Date or Time Component
     *  H       Hour in day (0-23)    
     *  h       Hour in am/pm (1-12)   
     * </pre></p> 
     */
    public static final String TIME_SQL_PATTERN = "HH:mm:ss";
    /**
     * <p>Pattern per un oggetto Time espresso su 12 ore.</p>
     */
    public static final String TWELWE_HOUR_FORMAT = "hh:mm";
    /**
     * <p>Numero di anni da spostare per l'estrazione degli elementi
     * nella visualizzazione di default.
     * P.es. un valore 1 implica un'estrazione di un anno, tra la data corrente
     * e la data corrente dell'anno prossimo.</p>
     */
    public static final int YEAR_SHIFT = 1;
    /**
     * Il TIMESTAMP dei sistemi corrisponde al numero di secondi trascorsi
     * da una data convezionale conosciuta come <em>Unix Epoch</em>
     * (1° gennaio 1970, quando tutto &egrave; cominciato)<sup>1</sup>.<br />
     * <small>1: Primo kernel UNIX del 1969, sviluppato dai Bell Laboratories
     * a partire dal 1970.</small>
     */
    public static final String UNIX_EPOCH = "1970-01-01";
    /**
     * Il 7 febbraio 2106 il tempo UNIX raggiunger&agrave; la cifra esadecimale
     * FFFFFFFF16 (corrispondente a 4.294.967.295 secondi) che, per i sistemi
     * a 32 bit, &egrave; il massimo computabile. Per sistemi del genere,
     * il successivo secondo sar&agrave; interpretato come:
     * 00:00:00 1 January 1970 UTC
     * (quindi, in pratica, gli orologi dei sistemi a 32 bit verranno resettati a UNIX EPOCH).
     */
    public static final String THE_END_OF_TIME = "2106-02-07";

}
