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

package it.col;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.col.bean.ItemBean;
import it.col.bean.PersonBean;
import it.col.exception.AttributoNonValorizzatoException;
import it.col.exception.CommandException;
import it.col.util.Constants;
import it.col.util.Utils;


/**
 * <p><code>Data</code> &egrave; la servlet della web-application col
 * che pu&ograve; essere utilizzata in pi&uacute; contesti:<ol>
 * <li>su una richiesta sincrona: per produrre output con contentType differenti da
 * 'text/html'</li>
 * <li>su una richiesta asincrona: per ottenere tuple da mostrare asincronamente
 * nelle pagine</li></ol></p>
 * <p><ul><li>Nel primo caso, questa servlet fa a meno, legittimamente, 
 * del design (View), in quanto l'output prodotto consiste in pure tuple 
 * prive di presentazione (potenzialmente: fileset CSV, formati XML, dati 
 * con o senza metadati, serialization formats, JSON, <em>and so on</em>), 
 * oppure in output gi&agrave; autoformattati (p.es. testo formattato 
 * Rich Text Format).</li>
 * <li>Nel secondo caso, la servlet specifica una pagina di output, contenente
 * la formattazione, che per&ograve; verr&agrave; invocata asincronamente.</li>
 * </ul></p>
 * <p>Questa servlet estrae l'azione dall'URL, ne verifica la
 * correttezza, quindi in base al valore del parametro <code>'entToken'</code> ricevuto
 * (qui chiamato 'qToken' per motivi storici, ma non importa)
 * richiama le varie Command che devono eseguire i comandi specifici.
 * Infine, recupera l'output dai metodi delle Command stesse richiamati
 * e li restituisce a sua volta al cliente sotto forma non necessariamente di outputstream
 * 'text/html' (come nel funzionamento standard delle applicazioni web),
 * quanto sotto forma di file nel formato richiesto (.csv, .xml, ecc.),
 * oppure passando il nome di una risorsa da richiamare in modo asincrono
 * per mostrare le tuple estratte, e passate in Request.<br />
 * In caso di richieste di output non di tipo text/html,
 * elabora anche un nome univoco per ogni file generato, basandosi sul
 * timestamp dell'estrazione/richiesta.
 * </p>
 * <p>
 * La classe che realizza l'azione deve implementare l'interfaccia
 * Command e, dopo aver eseguito le azioni necessarie, restituire
 * un set di risultati che dovr&agrave; essere utilizzato per
 * visualizzare i dati all'interno dei files serviti, ai quali
 * sarà fatto un forward.
 * </p>
 * L'azione presente nell'URL deve avere il seguente formato:
 * <pre>&lt;entToken&gt;=&lt;nome&gt;</pre>
 * dove 'nome' è il valore del parametro 'entToken' che identifica
 * l'azione da compiere al fine di generare i record.<br />
 * Oltre al parametro <code>'entToken'</code> possono essere presenti anche
 * eventuali altri parametri, ma essi non hanno interesse nel contesto
 * della presente classe, venendo incapsulati nella HttpServletRequest
 * e quindi inoltrati alla classe Command che deve fare il lavoro di
 * estrazione. Normalmente, tali altri parametri possono essere presenti
 * sotto forma di parametri sulla querystring, ma anche direttamente
 * settati nella request; ci&ograve; non interessa alcunch&eacute; ai fini
 * del funzionamento della presente classe.
 * </p>
 * <p>
 * Altre modalit&agrave; di generazione di output differenti da 'text/html'
 * (chiamate a pagine .jsp che incorporano la logica di preparazione del CSV,
 * chiamate a pagina .jsp che si occupano di presentare il metadato...)
 * sono deprecate da tempo e vanno assolutamente evitate 
 * in favore dell'uso di questa servlet.
 * </p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class Data extends HttpServlet implements Constants {

    /**
     * La serializzazione necessita della dichiarazione
     * di una costante di tipo long identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -7053908837630394953L;
    /**
     * Nome di questa classe
     * (viene utilizzato per contestualizzare i messaggi di errore)
     */
    private static final String FOR_NAME = "\n" + Logger.getLogger(Data.class.getName()) + COLON + BLANK_SPACE;
    /**
     * Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.
     */
    private static Logger log = Logger.getLogger(Data.class.getName());
    /**
     * Serve per inizializzare i rendirizzamenti con il servletToken
     */
    private ServletContext servletContext;
    /**
     * Parametro della query string identificante una Command.
     */
    private String qToken;
    /**
     * Parametro della query string per richiedere un certo formato di output.
     */
    private String format;
    /**
     * Pagina a cui la classe inoltra per mostrare le fasi nel contesto dei processi
     */
    private static final String nomeFileProcessoAjax = "/jsp/prProcessoAjax.jsp";
    /**
     * Pagina a cui la classe inoltra per mostrare le fasi nel contesto dei processi
     */
    private static final String nomeFileLog = "/jsp/diff.jsp";
    /**
     * Codifica esadecimale di un'immagine di corredo
     */
    private static final String CAUTION_EXCLAMATION_MARK_SIGN_TRIANGLE = 
        "0100090000034a04000000002104000000000400000003010800050000000b0200000000050000\n" + 
        "000c0220002000030000001e000400000007010400040000000701040021040000410b2000cc00\n" + 
        "200020000000000020002000000000002800000020000000200000000100080000000000000000\n" + 
        "000000000000000000000000000000000000000000ffffff00fcfbfb00eae9e900e7e6e5007c77\n" + 
        "7200403a3300f6f6f6006e696300201810002e261f002d261e00b1aeac00261e17003f393200c8\n" + 
        "c6c400c5c3c100b6b3b000efefee0058524c0099959100989490005b554f00f0f0ef00a4a19e00\n" + 
        "211911004e484200e8e7e7004d464000221a1200a9a6a3004a433d00aaa7a4004b443e0094908d\n" + 
        "005b56500057514b00dddcda00271f1700bab7b500f2f1f100f3f2f200b3b0ad00fefefe008682\n" + 
        "7e006b666100f7f7f7006a656000f5f5f500645f5900d0cecc00352e2600e5e4e30038312a00c0\n" + 
        "bebc002b241c00d1cfcd0075706b0078736f00fbfafa00726d680079747000fbfbfb00c3c0be00\n" + 
        "37302800d3d2d000cecccb00332c250089858000fdfdfd00807c770069635e00d9d8d7003c352e\n" + 
        "00b7b5b20059534d008d898500a6a39f004d474100e3e2e100463f3800e7e7e6004c453f009c99\n" + 
        "950095918e006c676200f4f3f300ecebea00524c4500dedddb00413a3400b9b7b400231b130069\n" + 
        "645f00eeedec00eeeeed005d57520087837f00362f2700c4c2c000b0adab0048423b0049433c00\n" + 
        "28201800d2d0ce0077736e00edeceb00f8f8f8007a7571002f272000c7c5c300fafaf9006d6862\n" + 
        "0088848000b6b4b10076716c00716c6700e4e3e20030282100d5d4d200cbc9c70029211900adaa\n" + 
        "a700e6e5e400231c14002a221a009a96920096928f0047413a0000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000000000000000000000000000\n" + 
        "000000000000000000000000000000000000000000000000000000010101010101010101010101\n" + 
        "010101010101010101010101010101010101010101012b5e343434343434343434343434343434\n" + 
        "3434343434343434345f2b010101567f8035353535353535353535353535353535353535353535\n" + 
        "35356614560101225c7d353535353535353535353535353535353535353535353535265c7e0157\n" + 
        "21797a04343434343434343434347b7b343434343434343434347b537c5257750e767701010101\n" + 
        "0101010101012b5f122b0101010101010101010178370e756f7009712b0101010101010101012a\n" + 
        "661f72010101010101010101027309746f01630a626801010101010101015e210909215e010101\n" + 
        "0101010101106d0a6e01010239096902010101010101016a1f09091f6a010101010101016b2d09\n" + 
        "6c0201010138620b6301010101010101016465662a010101010101010127676268010101012b2c\n" + 
        "095d07010101010101012b5e5f2b01010101010101286009612b0101010101595a265b01010101\n" + 
        "0101010156560101010101010101205c5a590101010101010154091617010101010101292d5556\n" + 
        "01010101010157580914010101010101010151521d1e0101010101013435353401010101010153\n" + 
        "091c510101010101010101014d1d4e1b01010101013435353401010101014f501d200101010101\n" + 
        "0101010101174b091401010101013435353401010101014c09231701010101010101010101012a\n" + 
        "0d06250101010134353534010101014849264a0101010101010101010101010731094401010101\n" + 
        "343535340101014546094707010101010101010101010101013f0b404101010134353534010101\n" + 
        "42430a1001010101010101010101010101010239093a0201013435353401013b3c093d3e010101\n" + 
        "01010101010101010101010132330b100101343535340101363733380101010101010101010101\n" + 
        "01010101012b2c092d2e01292f2d29013031092c2b010101010101010101010101010101010125\n" + 
        "0626270101282901012a0d06250101010101010101010101010101010101010122092317010101\n" + 
        "01122409140101010101010101010101010101010101010101041f1d2001010101181921040101\n" + 
        "0101010101010101010101010101010101010118191a1b01011b1c1d1e01010101010101010101\n" + 
        "010101010101010101010101121309140101150916170101010101010101010101010101010101\n" + 
        "0101010101010c0d0e0f100e0d1101010101010101010101010101010101010101010101010107\n" + 
        "08090a0b0908070101010101010101010101010101010101010101010101010104050606050401\n" + 
        "010101010101010101010101010101010101010101010101010102030302010101010101010101\n" + 
        "010101010101010101010101010101010101010101010101010101010101010101010101010400\n" + 
        "00002701ffff030000000000\n";


    /**
     * Inizializza (staticamente) le variabili globali e i parametri di inizializzazione.
     *
     * @param config la configurazione usata dal servlet container per passare informazioni alla servlet <strong>durante l'inizializzazione</strong>
     * @throws ServletException una eccezione che puo' essere sollevata quando la servlet incontra difficolta'
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        /*
         *  Inizializzazione da superclasse
         */
        super.init(config);
        /*
         *  Inizializzazione del servletToken
         */
        servletContext = getServletContext();
    }


    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("javadoc")
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
                    throws ServletException, IOException {
        // La pagina della servlet e' sganciata dal template, anzi ne costituisce un frammento
        String fileJsp = null;
        // Recupera valore di ent (servito da un ConfigManager esterno alla Data)
        qToken = req.getParameter(ConfigManager.getEntToken());
        // Recupera il formato dell'output, se specificato
        format = req.getParameter(ConfigManager.getOutToken());
        // Recupera o inizializza parametro per identificare la pagina
        String part = req.getParameter("p");
        // Dictonary contenente i soli valori di entToken abilitati a generare CSV
        LinkedList<String> csvCommands = new LinkedList<>();
        // Struttura da restituire in Request
        AbstractList<?> lista = null;
        // Mappa da restituire in Request
        AbstractMap<?,?> mappa = null;
        // Message
        log.info("===> Log su servlet Data. <===");
        // Decodifica la richiesta
        try {
            // Lista delle Command abilitate a servire un output csv
            csvCommands.add(COMMAND_PROCESS);   //Estrazione processi   ("data?q=pr")
            csvCommands.add(COMMAND_STRUCTURES);//Estrazione strutture  ("data?q=st")
            csvCommands.add(COMMAND_AUDIT);     //Estrazione interviste ("data?q=in")
            csvCommands.add(COMMAND_RISK);      //Estrazione rischi     ("data?q=ri")
            csvCommands.add(COMMAND_REPORT);    //Estrazione rischi     ("data?q=mu")
            // Verifica se deve servire un output su file
            if (format != null && !format.isEmpty()) {
                // Output è Comma Separated Values
                if (format.equalsIgnoreCase(CSV)) {

                        return;
                    }
                }
                // Output è Rich Text Format
                else if (format.equalsIgnoreCase(RTF)) {
                    // Controlla che la command su cui si invoca la funzione sia quella abilitata
                    if (qToken.equalsIgnoreCase(COMMAND_REPORT)) {
 
                        return;
                    }
                }
                // Output è HyperText Markup Language
                else if (format.equalsIgnoreCase(HTML)) {
                    // Al momento l'unica Command abilitata a gestire output html
                    if (qToken.equalsIgnoreCase(COMMAND_REPORT)) {
                        // Genera il file RTF
                        makeHTML(req, res, part);
                        // Non ha finito: deve invocare la pagina dinamica 
                        // (che a sua volta fornirà l'output per l'html statico scaricato)
                    }
                }
                // Output è in finestra di popup
                else {
                    // Ultimo valore ammesso: pop
                    if (!format.equalsIgnoreCase("pop")) { 
                        // Valore del parametro 'out' non ammesso
                        String msg = FOR_NAME + "Valore del parametro \'out\' (" + format + ") non consentito. Impossibile visualizzare i risultati.\n";
                        log.severe(msg);
                        throw new ServletException(msg);
                    }
                }

        } catch (Exception ce) {
            throw new ServletException(FOR_NAME + "Problema nel recupero dei dati richiesti.\n" + ce.getMessage(), ce);
        }
        // Forworda la richiesta, esito finale di tutto
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(fileJsp);
        dispatcher.forward(req, res);
    }

    /* **************************************************************** *
     *       Metodi per generare tuple prive di presentazione (CSV)     *
     * **************************************************************** */
    

    
    /* **************************************************************** *
     *  Metodi per generare tuple con qualche presentazione (RTF, HTML) *
     * **************************************************************** */
    

    
    /* **************************************************************** *
     *      Metodi utilizzati per servire richieste asincrone (XHR)     *
     * **************************************************************** */
    

    /* **************************************************************** *
     *   Metodi per la preparazione e la generazione dei files di dati  *
     * **************************************************************** */

    /**
     * <p>Gestisce la generazione dell&apos;output in formato di un log formattato 
     * che sar&agrave; recepito come tale dal browser e trattato di conseguenza.</p>
     * <p>Usa altri metodi, interni, per ottenere il nome del file, che dev&apos;essere un
     * nome univoco; a differenza dei metodi che preparano un file che dovr&agrave;
     * poi essere prodotto tramite lo standard output, questo metodo non ha 
     * bisogno di invocare il metodo di stampa, perch&eacute; l'output viene
     * preso in carico, a valle, da una pagina dinamica jsp attraverso 
     * la quale si genera a sua volta il contenuto di un file html corrispondente
     * all'output della jsp; l'output della pagina dinamica viene trasferito
     * in un file html grazie alla preparazione delle intestazioni della
     * HttpServletResponse effettuata al presente metodo, che evita 
     * quindi di scomodare il PrintWriter ottenendo per&ograve; 
     * un flusso &egrave; analogo.</p>
     *
     * @param req HttpServletRequest 
     * @param res HttpServletResponse per impostarvi i valori che la predispongono a servire html anziche' jsp
     * @param key String associata a un'etichetta che verra' usata come nome del file html
     * @throws ServletException eventuale eccezione da propagare
     * @throws IOException  eventuale eccezione da propagare
     */
    private static void makeHTML(HttpServletRequest req, HttpServletResponse res, String key)
                         throws ServletException, IOException {
        // Genera un nome univoco per il file che verrà servito
        String fileName = makeFilename(key);
        // Configura il response per il browser
        res.setContentType("text/html");
        // Configura il characterEncoding (v. commento)
        res.setCharacterEncoding("ISO-8859-1");
        // Configura l'header
        res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + HTML);
        // Configura il basehref per i link assoluti
        String baseHref = ConfigManager.getBaseHref(req);
        // Setta nella request il valore del <base href... />
        req.setAttribute("baseHref", baseHref);
        // Setta nella request gli estremi temporali
        req.setAttribute("now", Utils.format(Utils.convert(Utils.getCurrentDate())) );
    }


    /**
     * <p>Genera un nome univoco a partire da un prefisso dato come parametro.</p>
     *
     * @param label il prefisso che costituira' una parte del nome del file generato
     * @return <code>String</code> - il nome univoco generato
     */
    private static String makeFilename(String label) {
        // Crea un nome univoco per il file che andrà ad essere generato
        Calendar now = Calendar.getInstance();
        String fileName = ConfigManager.getLabels().get(label) + UNDERSCORE +
                          new Integer(now.get(Calendar.YEAR)).toString() + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.MONTH) + 1)) + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.DAY_OF_MONTH))) + UNDERSCORE +
                          String.format("%02d", new Integer(now.get(Calendar.HOUR_OF_DAY))) +
                          String.format("%02d", new Integer(now.get(Calendar.MINUTE))) +
                          String.format("%02d", new Integer(now.get(Calendar.SECOND)));
        return fileName;
    }

    
    /**
     * <p>Calcola il colore di highlight in Rich Text Format.</p>
     * <p>Purtroppo, questo formato, sviluppato da Microsoft, non 
     * implementa in modo efficace tutti gli attributi di formattazione
     * ed &egrave; molto dipendente dall'editor utilizzato per la visualizzazione
     * e dal sistema operativo che lo ospita, per cui le codifiche degli highlight,
     * che, almeno sulla carta, dovevano essere ben definite 
     * (<a href="https://www.biblioscape.com/rtf15_spec.htm#Heading45">v. p.es.</a>), 
     * non si comportano come atteso (oppure, l'implementazione richiederebbe 
     * maggiore approfondimento).<br>
     * In ogni caso, questo formato, che pure sembrava un ottimo compromesso
     * fra human readibility, formattazione ricca (appunto) e ampia fruibilit&egrave; 
     * e portabilit&agrave;, si rivela in realt&agrave; rigido, tutt'altro che "ricco",
     * ormai obsoleto e poco profittevole in termini di investimento di tempo di sviluppo.<br>
     * Preferibile, pertanto, puntare oggi direttamente su un formato estremamente
     * preciso, come PDF, oppure direttamente su librerie per la generazione in formati
     * pi&uacute; attuali, proprietari (e.g. .docx) o aperti (e.g. ODF).</p> 
     * 
     * @param riskLevel livello di rischio da evidenziare 
     * @return valore di highlight da stampare nel documento
     */
    public static String getHighlight(String riskLevel) {
        if (riskLevel.equals(LIVELLI_RISCHIO[0])) {
            return "\\highlight4";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[1])) {
            return "\\highlight0";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[2])) {
            return "\\highlight1";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[3])) {
            return "\\highlight2";
        } else if (riskLevel.equals(LIVELLI_RISCHIO[4])) {
            return "\\highlight13";
        } else {
            return "\\highlight0";
        }
    }
    
    
    /**
     * <p>Genera il nodo JSON</p>
     *
     * @param tipo          valore che serve a differenziare tra tipi diversi di nodi per poter applicare formattazioni o attributi diversi
     * @param codice        codice del nodo corrente
     * @param codicePadre   codice del nodo padre del nodo corrente
     * @param nome          etichetta del nodo
     * @param descr         descrizione del nodo
     * @param lbl1          label aggiuntiva
     * @param txt1          testo relativo alla label
     * @param lbl2          label aggiuntiva
     * @param txt2          testo relativo alla label
     * @param bgColor       parametro opzionale specificante il colore dei box/nodi in formato esadecimale
     * @param icona         parametro opzionale specificante il nome del file da mostrare come stereotipo
     * @param livello       livello gerarchico del nodo
     * @return <code>String</code> - il nodo in formato String
     */
    public static String getStructureJsonNode(String tipo,
                                              String codice,
                                              String codicePadre,
                                              String nome,
                                              String descr,
                                              String lbl1,
                                              String txt1,
                                              String lbl2,
                                              String txt2,
                                              String bgColor,
                                              String icona,
                                              int livello) {
        /* ------------------------ *
         *   Controlli sull'input   *
         * ------------------------ */
        String codiceGest = (codicePadre == null ? "null" : "\"" + codicePadre + "\"");
        String nodeImage = (icona == null ? "logo2.gif" : icona + livello + ".png");
        String height =  (descr.length() > 100) ? String.valueOf(descr.length()) : String.valueOf(146);
        Color backgroundColor = null;
        if (bgColor != null && !bgColor.equals(VOID_STRING)) {
            backgroundColor = Color.decode(bgColor);
        } else {
            backgroundColor = new Color(51,182,208);
        }
        /* ------------------------ */
        // Generazione nodo
        return "{\"nodeId\":\"" + codice + "\"," +
                "  \"parentNodeId\":" + codiceGest + "," +
                "  \"width\":342," +
                "  \"height\":" + height +"," +
                "  \"borderWidth\":1," +
                "  \"borderRadius\":5," +
                "  \"borderColor\":{\"red\":15,\"green\":140,\"blue\":121,\"alpha\":1}," +
                "  \"backgroundColor\":{\"red\":" + backgroundColor.getRed() + ",\"green\":" + backgroundColor.getGreen() + ",\"blue\":" + backgroundColor.getBlue() + ",\"alpha\":1}," +
                "  \"nodeImage\":{\"url\":\"web/img/" + nodeImage + "\",\"width\":50,\"height\":50,\"centerTopDistance\":0,\"centerLeftDistance\":0,\"cornerShape\":\"CIRCLE\",\"shadow\":false,\"borderWidth\":0,\"borderColor\":{\"red\":19,\"green\":123,\"blue\":128,\"alpha\":1}}," +
                "  \"nodeIcon\":{\"icon\":\"\",\"size\":30}," +
                "  \"template\":\"<div>\\n <div style=\\\"margin-left:15px;\\n margin-right:15px;\\n text-align: center;\\n margin-top:10px;\\n font-size:20px;\\n font-weight:bold;\\n \\\">" + nome + "</div>\\n <div style=\\\"margin-left:80px;\\n margin-right:15px;\\n margin-top:3px;\\n font-size:16px;\\n \\\">" + descr + "</div>\\n\\n <div style=\\\"margin-left:270px;\\n  margin-top:15px;\\n  font-size:13px;\\n  position:absolute;\\n  bottom:5px;\\n \\\">\\n<div>" + lbl1 + " " + txt1 +"</div>\\n<div style=\\\"margin-top:5px\\\">" + lbl2 + " " + txt2 + "</div>\\n</div>     </div>\"," +
                "  \"connectorLineColor\":{\"red\":220,\"green\":189,\"blue\":207,\"alpha\":1}," +
                "  \"connectorLineWidth\":5," +
              //"  \"dashArray\":\"\"," +
                "  \"expanded\":false }";
    }
    
    
    /**
     * <p>Genera la descrizione del nodo JSON</p>
     *
     * @param list          struttura vettoriale contenente informazioni
     * @param livello       livello gerarchico del nodo
     * @return <code>String</code> - il nodo in formato String
     * @throws AttributoNonValorizzatoException eccezione che viene propagata se si tenta di accedere a un dato obbligatorio non valorizzato del bean
     */
    public static String makeDescrJsonNode(ArrayList<?> list,
                                           int livello) 
                                    throws AttributoNonValorizzatoException {
        StringBuffer descr = new StringBuffer();
        descr.append("<ul>");
        for (int i = 0; i < list.size(); i++) {
            PersonBean p = (PersonBean) list.get(i);
            descr.append("<li>");
            descr.append(p.getNome());
            descr.append(BLANK_SPACE);
            descr.append(p.getCognome());
            descr.append(BLANK_SPACE + DASH + BLANK_SPACE);
            descr.append(p.getNote());
            descr.append("</li>");
        }
        descr.append("</ul>");
        // Generazione descr
        return descr.toString();
    }

}
