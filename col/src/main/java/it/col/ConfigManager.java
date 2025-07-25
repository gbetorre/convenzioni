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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import it.col.bean.CodeBean;
import it.col.bean.CommandBean;
import it.col.bean.ItemBean;
import it.col.command.Command;
import it.col.db.DBWrapper;
import it.col.exception.WebStorageException;
import it.col.util.Constants;


/**
 * <p>ConfigManager &egrave; la classe che recupera i parametri 
 * di inizializzazione della web-application &nbsp;
 * <code>Rischi on Line (rol)</code> 
 * e li espone attraverso metodi accessori.</p>
 * <p>Per evitare riferimenti circolari, questa classe &egrave; autorizzata 
 * a chiedere cose ad altre classi, ma le classi interrogate non dovrebbero 
 * poi, a loro volta, chiedere a questa.<br>
 * Invece, le classi non interpellate da questa (sia Servlet sia Command) 
 * possono chiedere a questa classe tutti i valori delle variabili di classe, 
 * che sono esposte tramite appositi metodi accessori, e che rappresentano 
 * il motivo per cui questa classe stessa &egrave; stata creata.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ConfigManager extends HttpServlet {

    /**
     * La serializzazione necessita della dichiarazione
     * di una costante di tipo long identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 1L;
    /**
     * <p>Logger della classe per scrivere i messaggi di errore.</p>
     * <p>All logging goes through this logger.</p>
     * <p>To avoid the 'Read access to enclosing field Main.log
     * is emulated by a synthetic accessor method' warning,
     * the visibility is changed to 'friendly' (id est 'default',
     * id est 'visible from the same package').</p>
     * <p>In altre parole:</ul>
     * non &egrave; privata ma Default (friendly) per essere visibile
     * negli elementi ovverride implementati da questa classe.</p>
     */
    /* default */ static Logger log = Logger.getLogger(ConfigManager.class.getName());
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * <p>DataBound.</p>
     * <p>Viene definito come:<dl>
     * <dt>variabile statica</dt>
     * <dd>per poterla utilizzare nei blocchi statici
     * (p.es. blocco statico di inizializzazione)</dd>
     * <dt>variabile di classe</dt>
     * <dd>per poterla valorizzare in inizializzazione e poterla utilizzare
     * poi in tutto il codice</dd>
     * <dt>variabile inizializzata</dt>
     * <dd>per agevolare l'applicazione del <code>pattern Singleton</code>,
     * che deve essere utilizzato ad ogni possibile istanziazione,
     * evitando in tal modo la generazione di istanziazioni multiple.</dd></dl>
     * <small>NOTA: Le variabili locali, sia di metodo sia di blocco,
     * <cite id="horton">vanno sempre inizializzate, all'atto
     * della loro definizione. Tuttavia ci&ograve; non vale per
     * le variabili di classe e di istanza, che possono essere soltanto
     * dichiarate nel contesto della definizione della classe e di cui va
     * curata l'inizializzazione successiva.</cite>
     * In questo caso, per prudenza, si deroga a questa regola
     * per i motivi dianzi detti.</small>
     * <p>La variabile non &egrave; privata ma Default per gli stessi motivi
     * per i quali la visibilit&agrave; dell'istanza di Logger non &egrave;
     * privata; lasciarla privata provocherebbe un:
     * <pre>Write access to enclosing field Main.db is emulated by a synthetic accessor method</pre>
     * cio&egrave; a dire che &quot;lui&quot; farebbe un metodo
     * <code>getDb()</code> dietro le quinte per garantire l'accesso al campo
     * privato; preferibile, quindi, aprire direttamente la visibilit&agrave;
     * a livello di package, come peraltro &quot;lui&quot; (I mean: Eclipse)
     * stesso suggerisce, fornendo il seguente messaggio:<br />
     * <code>Quick fix:</code>
     * <pre>Change visiblility of 'db' to 'package'</pre></p>
     */
    static DBWrapper db = null;
    /**
     * Struttura vettoriale contenente le command predefinite sotto forma di voci di menu
     */
    private static Vector<CommandBean> classiCommand;
    /**
     * Tabella hash (dictionary) contenente le command predefinite.
     */
    private static ConcurrentHashMap<String, Command> commands;
    /**
     * Struttura vettoriale contenente le rilevazioni trovate quando il server sale.
     */
    private static ArrayList<CodeBean> surveyList;
    /**
     * Tabella hash (dictionary) contenente le rilevazioni trovate quando il server sale.
     */
    private static ConcurrentHashMap<String, CodeBean> surveys;
    /**
     * Tabella hash (dictionary) contenente il numero di quesiti trovati per ciascuna rilevazione
     */
    private static ConcurrentHashMap<String, Integer> questionAmount;
    /**
     * Tabella hash (dictionary) contenente etichette predefinite 
     * in funzione del valore del parametro 'p'; queste possono essere utilizzate
     * poi nella costruzione dei nomi dei files generati dall'applicazione
     */
    private static ConcurrentHashMap<String, String> labels;
    /** 
     * Lista delle tipologie delle misure di prevenzione/mitigazione
     * del rischio corruttivo
     */
    private static ArrayList<CodeBean> measureTypes;
    /** 
     * Lista dei caratteri delle misure di prevenzione/mitigazione
     * del rischio corruttivo
     */
    private static HashMap<String, CodeBean> measureCharacters;
    /** 
     * Lista dei tipi di indicatori di monitoraggio
     */
    private static ArrayList<CodeBean> indicatorTypes;
    /**
     * Tabella hash (dictionary) contenente i tipi di indicatori indicizzati per id
     */
    private static ConcurrentHashMap<Integer, CodeBean> indicatorTypesAsMap;
    /**
     * <p>Nome del parametro di inizializzazione, valorizzato nel
     * descrittore di deploy, che identifica il nome della web application.</p>
     * <p>Nelle ultime applicazioni che ho sviluppato valeva:<code><ul> 
     * <li>'almalaurea',</li> <li>'pm',</li> <li>'pol',</li> <li>'processi'</li> 
     * <li>'rischi'.</li></ul></code> 
     * Comunque si chiami, di esso &egrave; da ricercare dopo la root ma
     * <em>prima</em> della QueryString.</p>
     */
    private static String appName;
    /**
     * <p>Nome del parametro di inizializzazione, valorizzato nel
     * descrittore di deploy, che identifica la command cui la Servlet che risponde
     * (Main, Data, etc.) deve girare la richiesta. </p>
     * <p>Storicamente valeva <code>'ent'</code> ma &egrave; stato abbreviato.<br> 
     * Comunque si chiami, di esso &egrave;
     * da ricercare il valore nella QueryString.</p>
     */
    private static String entToken;
    /**
     * <p>Nome del parametro di inizializzazione, valorizzato nel
     * descrittore di deploy, che identifica il formato di output desiderato. </p>
     */
    private static String outToken;
    /**
     * Nome e percorso della pagina di errore cui ridirigere in caso
     * di eccezioni rilevate.
     */
    private static String errorJsp;
    /**
     * <p>Nome del parametro di inizializzazione, valorizzato nel
     * descrittore di deploy, che identifica il nome della pagina jsp
     * che rappresenta la home page del sito locale.</p>
     */
    private static String homePage;
    /**
     * <p>Nome del template in cui vengono assemblati i vari 'pezzi'
     * che compongono l'output html finale.</p>
     */
    private static String templateJsp;
    /**
     * <p>Nome del della directory generale destinata a contenere documenti, generalmente
     * organizzati a loro volta in sottodirectory, in base alla tipologia, come:<dl>
     * <dt>download</dt>
     * <dd>subdir contenente documenti da scaricare (i cui link vengono esposti nelle pagine)</dd>
     * <dt>upload</dt>
     * <dd>subdir contenente documenti caricati dall'utente</dd>
     * <dt>json</dt>
     * <dd>subdir contenente documenti generati dall'applicazione stessa</dd></dl></p>
     */
    private static String dirDocuments;
    /**
     * <p>Nome della (sotto)directory destinata a contenere i file formato json
     * generati dall'applicazione e utilizzati tipicamente da librerie lato client.</p>
     */
    private static String dirJson = "json";


    /**
     * Inizializza (staticamente) le variabili globali
     * che saranno utilizzate anche dalle altre classi:
     * <ul>
     * <li> connessione al database: <code>db</code> </li>
     * <li> tabella hash di tutte le classi richiamabili dall'applicazione </li>
     * <li> nome del parametro identificante la command corrente </li>
     * <li> pagina di errore </li>
     * etc...
     * </ul>
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
        /* ******************************************************************** *
         *    Lettura dei parametri di configurazione dell'applicazione POL     *
         * ******************************************************************** */
        /*
         * Nome della pagina di errore
         */
        errorJsp = getServletContext().getInitParameter("errorJsp");
        if (errorJsp == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'errorJsp'!\n\n");
        }
        /*
         * Nome del parametro che identifica l'applicazione web
         */
        appName = getServletContext().getInitParameter("appName");
        if (appName == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'appName'!\n\n");
        }
        /*
         * Nome del parametro che identifica la Command da interpellare
         */
        entToken = getServletContext().getInitParameter("entToken");
        if (entToken == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'entToken'!\n\n");
        }
        /*
         * Nome del parametro che identifica la Command da interpellare
         */
        outToken = getServletContext().getInitParameter("outToken");
        if (outToken == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'outToken', utilizzabile per specificare un formato di output diverso dal default.\n\n");
        }
        /*
         * Nome home page
         */
        homePage = getServletContext().getInitParameter("home");
        if (homePage == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'nomeHomePage'!\n\n");
        }
        /*
         * Nome del template da invocare per l'assemblaggio dei vari componenti dell'output
         */
        templateJsp = getServletContext().getInitParameter("templateJsp");
        if (templateJsp == null) {
            throw new ServletException(FOR_NAME + "\n\nManca il parametro di contesto 'templateJsp'!\n\n");
        }
        /*
         * Nome della directory destinata a contenere documenti (da scaricare, uploadati, generati)
         */
        dirDocuments = getServletContext().getInitParameter("urlDirectoryDocumenti");
        /*
         * Attiva la connessione al database
         */
        try {
            db = new DBWrapper();
        }
        catch (WebStorageException wse) {
            throw new ServletException(FOR_NAME + "Non e\' possibile avere una connessione al database.\n" + wse.getMessage(), wse);
        }
        /*
         * Inizializza la tabella <code>commands</code> che deve contenere
         * tutte le classi che saranno richiamabili da questa
         * servlet. Tali classi dovrebbero essere dichiarate in un file
         * di configurazione (p.es. web.xml) o nel database.
         */
        classiCommand = new Vector<>();
        try {
            classiCommand = db.lookupCommand();
        }
        catch (Exception e) {
            throw new ServletException(FOR_NAME + "Problemi nel caricare le classi Command.\n" + e.getMessage(), e);
        }
        CommandBean voceMenu = null;
        Command classCommand = null;
        commands = new ConcurrentHashMap<>();
        for (int i = 0; i < classiCommand.size(); i++) {
            voceMenu = classiCommand.get(i);
            try {
                classCommand = (Command) Class.forName("it.rol.command." + voceMenu.getNomeClasse()).newInstance();
                classCommand.init(voceMenu);
                commands.put(voceMenu.getNome(), classCommand);
            } catch (ClassNotFoundException cnfe) {
                String error = FOR_NAME +
                               "La classe collegata alla voce menu '" +
                               voceMenu.getNome() +
                               "' &egrave; '" +
                               voceMenu.getNomeClasse() +
                               " e non pu&ograve; essere caricata: " + cnfe.getMessage();
                throw new ServletException(error);
            } catch (Exception e) {
                    String error = FOR_NAME +
                                   "Problema generico nel caricare la classe " +
                                   voceMenu.getNomeClasse() +
                                   ".\n Dettaglio errore: " +
                                   e.getMessage();
                    throw new ServletException(error);
            }
        }
        /*
         * Carica una lista, che esporra' staticamente, contenente
         * tutti i tipi di misura (informazione pubblica, non storicizzata e
         * necessaria in vari punti dell'applicazione perche' le liste che
         * dipendono dai tipi sono scorse dinamicamente).
         *
        measureTypes = null;
        try {
            measureTypes = db.getMeasureTypes();
        }
        catch (WebStorageException wse) {
            throw new ServletException(FOR_NAME + "Problemi nel metodo che estrae i tipi di misura.\n" + wse.getMessage(), wse);
        }
        catch (Exception e) {
            throw new ServletException(FOR_NAME + "Problemi nel caricare i tipi di misura.\n" + e.getMessage(), e);
        }
        // Carica una lista, che esporra' staticamente, contenente tutti i tipi di convenzioni
        measureCharacters = new HashMap<>();
        try {
            ArrayList<CodeBean> characters = db.getMeasureCharacters();
            for (CodeBean character : characters) {
                measureCharacters.put(character.getInformativa(), character);
            }
        }
        catch (WebStorageException wse) {
            throw new ServletException(FOR_NAME + "Problemi nel metodo che estrae i caratteri delle misure.\n" + wse.getMessage(), wse);
        }
        catch (Exception e) {
            throw new ServletException(FOR_NAME + "Problemi nel caricare i caratteri di misura.\n" + e.getMessage(), e);
        }
        // Carica una lista, che esporra' staticamente, contenente tutte le finalità delle convenzioni
        indicatorTypes = null;
        try {
            indicatorTypes = db.getIndicatorTypes();
        }
        catch (WebStorageException wse) {
            throw new ServletException(FOR_NAME + "Problemi nel metodo che estrae i tipi di indicatori.\n" + wse.getMessage(), wse);
        }
        catch (Exception e) {
            throw new ServletException(FOR_NAME + "Problemi nel caricare i tipi di indicatori.\n" + e.getMessage(), e);
        }
        try {
            // Istanzia la mappa quando gli serve...
            indicatorTypesAsMap = new ConcurrentHashMap<>();
            // for each
            for (CodeBean type : indicatorTypes) {
                // Chiave del dizionario dei tipi indicatore
                Integer key = new Integer(type.getId());
                // Valorizza la mappa contenente i tipi indicatore
                indicatorTypesAsMap.put(key, type);                
            }
        }
        catch (NullPointerException npe) {
            throw new ServletException(FOR_NAME + "Si e\' verificato un problema di puntamento alle rilevazioni o a un altro oggetto.\n" + npe.getMessage(), npe);
        }
        catch (Exception e) {
            throw new ServletException(FOR_NAME + "Problemi nel caricare la struttura contenente le rilevazioni.\n" + e.getMessage(), e);
        }
        /*
         * Carica una struttura dati, che esporra' staticamente, contenente tutte 
         * le etichette utilizzabili nella costruzione dei nomi dei files 
         * generati dall'applicazione in funzione del valore del parametro p.
         */
        labels = null;
        // La istanzia
        labels = new ConcurrentHashMap<>();
        // La carica
        labels.put(Constants.PART_SELECT_QSS,   Constants.INTERVIEWS);
        labels.put(Constants.PART_RESUME_QST,   Constants.INTERVIEW);
        labels.put(Constants.COMMAND_STRUCTURES,Constants.STRUCTURES);
        labels.put(Constants.COMMAND_PROCESS,   Constants.PROCESS);
        labels.put(Constants.COMMAND_RISK,      Constants.RISKS);
        labels.put(Constants.COMMAND_REPORT,    Constants.REPORT_MDM);
        labels.put(Constants.PART_SELECT_STR,   Constants.LOG_IND);
        labels.put(Constants.PART_MEASURES,     Constants.REPORT_MDM_MES);
    }


    /**
     * Costruisce il percorso di base dell'applicazione che si sta navigando,
     * che precede i parametri.<br />
     * Restituisce tale percorso.<br />
     * Serve a ricostruire i percorsi dei fogli di stile, dei files inclusi, ecc.
     *
     * @param req  HttpServletRequest contenente il protocollo usato (p.es.: <code>http, https,</code> o <code>ftp</code>)
     * @return <code>String</code> - una stringa che rappresenta la root, da settare nelle jsp (p.es.: <code>&lt;base href="http://www.univr.it/"&gt;</code>)
     */
    public static String getBaseHref(HttpServletRequest req) {
        StringBuffer baseHref = new StringBuffer();
        baseHref.append(req.getScheme());
        baseHref.append("://");
        baseHref.append(req.getServerName());
        if (req.getServerPort() != 80) {
            baseHref.append(":");
            baseHref.append(req.getServerPort());
        }
        baseHref.append(req.getContextPath());
        baseHref.append('/');
        return new String(baseHref);
    }


    /* ************************************************************************ *
     *                    Getters sulle variabili di classe                     *
     * ************************************************************************ */

    /**
     * <p>Restituisce il nome del parametro identificante la Root dell'applicazione web.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>String</code> - il nome usato per l'applicazione stessa
     */
    public static String getAppName() {
        return new String(appName);
    }


    /**
     * <p>Restituisce il nome del parametro identificante il token della command.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>String</code> - il nome usato nell'applicazione per identificare il token delle command
     */
    public static String getEntToken() {
        return new String(entToken);
    }


    /**
     * <p>Restituisce il nome del parametro identificante il token del formato di output,
     * se si desidera un formato diverso da text/html, che &egrave; il formato di default.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>String</code> - il nome usato nell'applicazione per identificare il token dell'output
     */
    public static String getOutToken() {
        return new String(outToken);
    }


    /**
     * <p>Restituisce il nome del parametro identificante la pagina di errore.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>String</code> - il nome usato nell'applicazione per identificare il nome della pagina di errore
     */
    public static String getErrorJsp() {
        return new String(errorJsp);
    }


    /**
     * <p>Restituisce il nome del parametro identificante la home.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>String</code> - il nome usato nell'applicazione per identificare il nome della home
     */
    public static String getHomePage() {
        return new String(homePage);
    }


    /**
     * <p>Restituisce il nome del parametro identificante il template dell'applicazione web.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>String</code> - il nome usato nell'applicazione per identificare il nome del template
     */
    public static String getTemplate() {
        return new String(templateJsp);
    }


    /**
     * <p>Restituisce il nome del parametro identificante il nome della directory dove
     * vengono conservati i documenti.</p>
     *
     * @return <code>String</code> - il nome usato nell'applicazione per identificare il nome della directory root dove vengono salvati e inseriti i files
     */
    public static String getDirDocuments() {
        return new String(dirDocuments);
    }


    /**
     * <p>Restituisce il nome del parametro identificante il nome della directory dove
     * vengono generati i files json necessari a specifiche librerie lato client.</p>
     * <p>Metodo getter su variabili di classe concatenate.</p>
     *
     * @return <code>String</code> - il nome usato nell'applicazione per identificare il nome della directory dove vengono salvati i files json dall'applicazione stessa generati
     */
    public static String getDirJson() {
        return new String(dirDocuments + File.separator + dirJson);
    }


    /**
     * <p>Restituisce una struttura di tipo vettoriale, contenente
     *  le command predefinite incapsulate dentro oggetti di tipo voce di menu.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>Vector&lt;ItemBean&gt;</code> - le command indicizzate per proprio token
     */
    public static Vector<CommandBean> getClassiCommand() {
        return classiCommand;
    }


    /**
     * <p>Restituisce una struttura di tipo Tabella hash (dictionary),
     * contenente le command predefinite gi&agrave; istanziate.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>ConcurrentHashMap&lt;String, Command&gt;</code> - le command indicizzate per proprio token
     */
    public static ConcurrentHashMap<String, Command> getCommands() {
        return commands;
    }


    /**
     * <p>Restituisce una struttura di tipo Tabella hash (dictionary),
     * contenente tutte le rilevazioni indicizzate per codice.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>ConcurrentHashMap&lt;String, CodeBean&gt;</code> - le rilevazioni incapsulate in CodeBean e indicizzate per proprio codice
     */
    public static ConcurrentHashMap<String, CodeBean> getSurveys() {
        return surveys;
    }


    /**
     * <p>Restituisce un oggetto incapsulante i valori di una rilevazione
     * dato il suo codice passato come parametro.</p>
     * <p>Assume che la variabile privata di classe su cui viene effettuata
     * la ricerca sia stata correttamente valorizzata.</p>
     *
     * @param key il codice della rilevazione, in forma di oggetto String, che il metodo accetta come argomento
     * @return <code>CodeBean</code> - la rilevazione cercata
     */
    public static CodeBean getSurvey(String key) {
        return surveys.get(key.toUpperCase());
    }


    /**
     * <p>Restituisce una struttura di tipo vettoriale, con mantenimento dell'ordine di
     * inserimento, contenente tutte le rilevazioni indicizzate per codice.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - le rilevazioni incapsulate in CodeBean
     */
    public static ArrayList<CodeBean> getSurveyList() {
        return surveyList;
    }

    
    /**
     * <p>Restituisce una struttura di tipo Tabella hash (dictionary),
     * contenente il numero di quesiti trovato per ogni rilevazione.
     * La chiave di ogni entry &egrave; il codice della rilevazione stesso.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>ConcurrentHashMap&lt;String, Integer&gt;</code> - il numero di quesiti trovati per ciascuna rilevazione, incapsulato in oggetto Wrapper di tipo primitivo
     */
    public static ConcurrentHashMap<String, Integer> getQuestionAmount() {
        return questionAmount;
    }

    
    /**
     * <p>Restituisce una struttura di tipo Tabella hash (dictionary),
     * contenente alcune etichette che possono essere utili come prefissi
     * di file di estrazione e usi simili.
     * La chiave di ogni entry &egrave; valore del parametro p.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>ConcurrentHashMap&lt;String, String&gt;</code> - etichette associate al valore di p
     */
    public static ConcurrentHashMap<String, String> getLabels() {
        return labels;
    }
    
    
    /**
     * <p>Restituisce una struttura di tipo vettoriale,
     * contenente le tipologie di misura di prevenzione/mitigazione.
     * del rischio corruttivo.</p>
     *
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - lista tipi di misure
     */
    public static ArrayList<CodeBean> getMeasureTypes() {
        return measureTypes;
    }
    
    
    /**
     * <p>Restituisce una struttura di tipo dictionary,
     * contenente i caratteri delle misure di prevenzione/mitigazione.
     * del rischio corruttivo indicizzati per codice carattere.</p>
     *
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - lista tipi di misure
     */
    public static HashMap<String, CodeBean> getMeasureCharacters() {
        return measureCharacters;
    }
    
    
    /**
     * <p>Restituisce una struttura di tipo vettoriale,
     * contenente le tipologie degli indicatori.<br> 
     * Si &egrave; resa necessaria per discriminare tra i tipi di indicatori 
     * di monitoraggio per la verifica dell'applicazione 
     * delle misure di mitigazione del rischio corruttivo; tuttavia, 
     * le tipologie di indicatori sono descrittori generici ed applicabili, 
     * ovviamente, anche agli indicatori delle dimensioni P ed I.</p>
     *
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - lista tipi di indicatori
     */
    public static ArrayList<CodeBean> getIndicatorTypes() {
        return indicatorTypes;
    }
    

    /**
     * <p>Restituisce una struttura di tipo Tabella hash (dictionary),
     * contenente tutti i tipi indicatore indicizzati per id.</p>
     * <p>Metodo getter sulla variabile privata di classe.</p>
     *
     * @return <code>ConcurrentHashMap&lt;Integer, CodeBean&gt;</code> - i tipi indicatore indicizzati per Wrapper di id
     */
    public static ConcurrentHashMap<Integer, CodeBean> getIndicatorTypesAsMap() {
        return indicatorTypesAsMap;
    }
    
}
