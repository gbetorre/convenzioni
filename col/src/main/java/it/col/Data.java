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
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.ParameterParser;

import it.col.bean.PersonBean;
import it.col.exception.AttributoNonValorizzatoException;
import it.col.util.Constants;
import it.col.util.MailManager;
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
        // Parser of parameters
        ParameterParser parser = new ParameterParser(req);
        // Recupera valore di ent (servito da un ConfigManager esterno alla Data)
        String qToken = parser.getStringParameter(ConfigManager.getEntToken(), VOID_STRING);
        // Recupera il formato dell'output, se specificato
        String format = parser.getStringParameter(ConfigManager.getOutToken(), VOID_STRING);
        // Recupera o inizializza parametri per identificare la pagina
        String op = parser.getStringParameter(OPERATION, VOID_STRING);
        String obj = parser.getStringParameter(OBJECT, VOID_STRING);
        String data = parser.getStringParameter(DB_CONSTRUCT, VOID_STRING);
        String from = parser.getStringParameter("start", VOID_STRING);
        String to = parser.getStringParameter("end", VOID_STRING);
        // Dictonary contenente i soli valori di entToken abilitati a generare CSV
        //LinkedList<String> csvCommands = new LinkedList<>();
        // Struttura da restituire in Request
        //AbstractList<?> lista = null;
        // Mappa da restituire in Request
        //AbstractMap<?,?> mappa = null;
        // Message
        log.info("===> Log su servlet Data. <===");
        //if (op.equalsIgnoreCase(SEND)) {
            
        //}
        try {
            MailManager.sendEmail();
            fileJsp = "scElenco.jsp";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("===> Email inviata. <===");
        // Forworda la richiesta, esito finale di tutto
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(fileJsp);
        dispatcher.forward(req, res);
    }

    
    private static String getMessage(HttpServletRequest req) {
     // Parser of parameters
        ParameterParser parser = new ParameterParser(req);

        // Recupera o inizializza parametri per identificare la pagina
        String op = parser.getStringParameter(OPERATION, VOID_STRING);
        String obj = parser.getStringParameter(OBJECT, VOID_STRING);
        String data = parser.getStringParameter(DB_CONSTRUCT, VOID_STRING);
        String from = parser.getStringParameter("start", VOID_STRING);
        String to = parser.getStringParameter("end", VOID_STRING);
        return VOID_STRING;
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
        String fileName = UNDERSCORE +
                          new Integer(now.get(Calendar.YEAR)).toString() + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.MONTH) + 1)) + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.DAY_OF_MONTH))) + UNDERSCORE +
                          String.format("%02d", new Integer(now.get(Calendar.HOUR_OF_DAY))) +
                          String.format("%02d", new Integer(now.get(Calendar.MINUTE))) +
                          String.format("%02d", new Integer(now.get(Calendar.SECOND)));
        return fileName;
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
