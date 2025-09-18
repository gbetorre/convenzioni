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

package it.col.command;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.col.ConfigManager;
import it.col.Main;
import it.col.SessionManager;
import it.col.bean.CodeBean;
import it.col.bean.CommandBean;
import it.col.bean.ItemBean;
import it.col.bean.PersonBean;
import it.col.db.DBWrapper;
import it.col.db.Query;
import it.col.exception.AttributoNonValorizzatoException;
import it.col.exception.CommandException;
import it.col.exception.WebStorageException;
import it.col.util.Constants;
import it.col.util.Utils;


/** 
 * <p><code>IndicatorCommand.java</code> (ic)<br />
 * Implementa la logica per la gestione degli indicatori di monitoraggio
 * e relative misurazioni.</p>
 * 
 * <p>Created on 14:01 18/09/2024 Wed Sep 18 14:01:46 CEST 2024</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ConventionCommand extends CommandBean implements Command, Constants {

    /* ******************************************************************** *
     *  Dichiara e/o inizializza variabili di classe e variabili d'istanza  *
     * ******************************************************************** */
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = -7546430466772067442L;
    /**
     *  Nome di questa classe 
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * Pagina per mostrare la lista delle misure aventi dettagli di monitoraggio (registro delle misure monitorate)
     */
    private static final String nomeFileElenco = "/jsp/icElenco.jsp";
    /**
     * Pagina per mostrare la lista delle misure monitorate raggruppate per struttura (pagina iniziale monitoraggio)
     */
    private static final String nomeFileElencoMisure  = "/jsp/icMisure.jsp";
    /**
     * Pagina per mostrare i dettagli di una misura monitorata
     */
    private static final String nomeFileMisura = "/jsp/icMisura.jsp"; 
    /**
     * Pagina per mostrare la form di aggiunta dei dettagli di una misura monitorata
     */
    private static final String nomeFileInsertMisura = "/jsp/icMisuraForm.jsp";    
    /**
     * Pagina per mostrare la lista degli indicatori di una misura monitorata
     */
    private static final String nomeFileElencoIndicatori = "/jsp/icIndicatori.jsp";
    /**
     * Pagina per mostrare i dettagli di un indicatore di monitoraggio
     */
    private static final String nomeFileDettaglio = "/jsp/icIndicatore.jsp";
    /**
     * Pagina per mostrare la maschera di inserimento/modifica di un indicatore di monitoraggio
     */
    private static final String nomeFileInsertIndicatore = "/jsp/icIndicatoreForm.jsp";    
    /**
     * Pagina per mostrare la lista delle misurazioni (collegate agli indicatori) di una misura monitorata
     */
    private static final String nomeFileElencoMisurazioni = "/jsp/icMisurazioni.jsp";
    /**
     * Pagina per mostrare i dettagli di una misurazione
     */
    private static final String nomeFileMisurazione = "/jsp/icMisurazione.jsp";
    /**
     * Pagina per mostrare la maschera di inserimento di una misurazione
     */ 
    private static final String nomeFileInsertMisurazione = "/jsp/icMisurazioneForm.jsp";
    /**
     * Struttura a cui la command fa riferimento per richiamare le pagine
     */    
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     * Struttura a cui la command fa riferimento per generare i titoli pagina
     */    
    private static final HashMap<String, String> titleFile = new HashMap<>();
    /** 
     * Lista dei tipi di indicatore
     */
    private static ArrayList<CodeBean> types;

  
    /* ******************************************************************** *
     *                    Routine di inizializzazione                       *
     * ******************************************************************** */
    /** 
     * <p>Raccoglie i valori dell'oggetto ItemBean
     * e li passa a questa classe command.</p>
     *
     * @param voceMenu la VoceMenuBean pari alla Command presente.
     * @throws CommandException se l'attributo paginaJsp di questa command non e' stato valorizzato.
     */
    @Override
    public void init(CommandBean voice) throws CommandException {
        this.setId(voice.getId());
        this.setNome(voice.getNome());
        this.setLabel(voice.getLabel());
        this.setNomeClasse(voice.getNomeClasse());
        this.setPagina(voice.getPagina());
        this.setInformativa(voice.getInformativa());
        if (this.getPagina() == null) {
          String msg = FOR_NAME + "La command " + this.getNome() + " non ha il campo pagina. Impossibile visualizzare i risultati.\n";
          throw new CommandException(msg);
        }
        /* Carica la hashmap contenente le pagine da includere in funzione dei parametri sulla querystring
        nomeFile.put(COMMAND_INDICATOR,             nomeFileElenco);
        nomeFile.put(PART_MEASURES,                 nomeFileElencoMisure);
        nomeFile.put(PART_INDICATOR,                nomeFileElencoIndicatori);
        nomeFile.put(PART_MONITOR,                  nomeFileElencoMisurazioni);
        nomeFile.put(PART_INSERT_MONITOR_DATA,      nomeFileInsertMisura);     
        nomeFile.put(PART_INSERT_INDICATOR,         nomeFileInsertIndicatore);
        nomeFile.put(PART_INSERT_MEASUREMENT,       nomeFileInsertMisurazione);
        nomeFile.put(PART_SELECT_MEASUREMENT,       nomeFileMisurazione);
        //nomeFile.put(PART_INSERT_,  nomeFileResumeMeasure);
        // Carica la hashmap contenente le pagine da includere in funzione dei parametri sulla querystring
        titleFile.put(COMMAND_INDICATOR,            "Registro misure monitorate");
        titleFile.put(PART_MEASURES,                "Pagina iniziale monitoraggio");
        titleFile.put(PART_INDICATOR,               "Indicatori di monitoraggio");
        titleFile.put(PART_MONITOR,                 "Monitoraggi della misura");
        titleFile.put(PART_INSERT_MONITOR_DATA,     "Dettagli di monitoraggio");
        titleFile.put(PART_INSERT_INDICATOR,        "Nuovo indicatore");
        titleFile.put(PART_INSERT_MEASUREMENT,      "Nuova misurazione");
        titleFile.put(PART_SELECT_MEASUREMENT,      "Dettagli misurazione");*/
    }
    
    
    /* ******************************************************************** *
     *                          Costruttore (vuoto)                         *
     * ******************************************************************** */
    /** 
     * Crea una nuova istanza di  questa Command 
     */
    public ConventionCommand() {
        /*;*/   // It doesn't anything
    }
  
    
    /* ******************************************************************** *
     *                   Implementazione dell'interfaccia                   *
     * ******************************************************************** */
    /**
     * <p>Gestisce il flusso principale.</p>
     * <p>Prepara i bean.</p>
     * <p>Passa nella Request i valori che verranno utilizzati dall'applicazione.</p>
     * 
     * @param req la HttpServletRequest contenente la richiesta del client
     * @throws CommandException se si verifica un problema, tipicamente nell'accesso a campi non accessibili o in qualche altro tipo di puntamento 
     */
    @Override
    public void execute(HttpServletRequest req) 
                 throws CommandException {
        /* ******************************************************************** *
         *              Dichiara e inizializza variabili locali                 *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Utente loggato
        PersonBean user = null;
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        // Titolo pagina
        String tP = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        // Data di oggi sotto forma di oggetto Date
        java.util.Date today = Utils.convert(Utils.getCurrentDate());
        /* ******************************************************************** *
         *                    Recupera parametri e attributi                    *
         * ******************************************************************** */
        // Recupera o inizializza 'tipo pagina'   
        String part = parser.getStringParameter("p", DASH);
        // Flag di scrittura
        Boolean writeAsObject = (Boolean) req.getAttribute("w");
        // Explicit Unboxing
        boolean write = writeAsObject.booleanValue();
        // Recupera o inizializza 'id misura'
        String codeMis = parser.getStringParameter("mliv", DASH);
        // Recupera o inizializza 'id misurazione'
        int idMon = parser.getIntParameter("nliv", DEFAULT_ID);
        // Recupera o inizializza 'id fase di attuazione'
        int idFas = parser.getIntParameter("idF", DEFAULT_ID);
        // Recupera o inizializza 'id indicatore'
        int idInd = parser.getIntParameter("idI", DEFAULT_ID);
        /* ******************************************************************** *
         *      Instanzia nuova classe DBWrapper per il recupero dei dati       *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *         Previene il rischio di attacchi di tipo Garden Gate          *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            user = SessionManager.checkSession(req.getSession(IF_EXISTS_DONOT_CREATE_NEW));
        } catch (RuntimeException re) {
            throw new CommandException(FOR_NAME + "Problema a livello dell\'autenticazione utente!\n" + re.getMessage(), re);
        }
        /* ******************************************************************** *
         *                          Corpo del programma                         *
         * ******************************************************************** */
        // Decide il valore della pagina
        try {
            // Recupera i tipi di indicatore
            types = ConfigManager.getIndicatorTypes();
            // Recupera l'elenco completo degli indicatori di monitoraggio
            //indicators = db.getMeasures(user, VOID_SQL_STRING, Query.GET_ALL_BY_CLAUSE, survey);

                // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                params = new HashMap<>();
                // Carica in ogni caso i parametri di navigazione
                //loadParams(part, req, params);
                /* ======================= @PostMapping ======================= */
                if (write) {
//                    // Controlla quale azione vuole fare l'utente
//                    if (nomeFile.containsKey(part)) {
//                        // Controlla quale richiesta deve gestire
//                        if (part.equalsIgnoreCase(PART_INSERT_MONITOR_DATA)) {
//                            /* ------------------------------------------------ *
//                             * PROCESS Form to INSERT Measure Monitoring Details*
//                             * ------------------------------------------------ */
//                            db.insertMeasureDetails(user, params);
//                            // Prepara la redirect 
//                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_INDICATOR + 
//                                       AMPERSAND + "p" + EQ + PART_MEASURES +
//                                       AMPERSAND + "mliv" + EQ + codeMis + 
//                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur;
//                                       //+AMPERSAND + MESSAGE + EQ + "newMes";
//                        } else if (part.equalsIgnoreCase(PART_INSERT_INDICATOR)) {
//                            /* ------------------------------------------------ *
//                             *        PROCESS Form to INSERT an Indicator       *
//                             * ------------------------------------------------ */
//                            db.insertIndicatorMeasure(user, params);
//                            // Prepara la redirect 
//                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_INDICATOR + 
//                                       AMPERSAND + "p" + EQ + PART_MEASURES +
//                                       AMPERSAND + "mliv" + EQ + codeMis + 
//                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur;
//                                       //+AMPERSAND + MESSAGE + EQ + "newRel#rischi-fattori-misure";
//                        } else if (part.equalsIgnoreCase(PART_INSERT_MEASUREMENT)) {
//                            /* ------------------------------------------------ *
//                             * PROCESS Form to INSERT a Measurement (Monitoring)*
//                             * ------------------------------------------------ */
//                            db.insertMeasurement(user, params);
//                            // Prepara la redirect 
//                            redirect = ConfigManager.getEntToken() + EQ + COMMAND_INDICATOR + 
//                                       AMPERSAND + "p" + EQ + PART_INDICATOR +
//                                       AMPERSAND + "mliv" + EQ + codeMis + 
//                                       AMPERSAND + PARAM_SURVEY + EQ + codeSur;
//                                       //+AMPERSAND + MESSAGE + EQ + "newRel#rischi-fattori-misure";
//                        }
//                    } else {
//                        // Azione di default
//                        // do delete?
//                    }
                /* ======================== @GetMapping ======================= */
                } else {
                    /* ------------------------------------------------ *
                     *                        Part                      *
                     * ------------------------------------------------ */
//                    if (nomeFile.containsKey(part)) {
//                        // Recupera le breadcrumbs
//                        LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
//                        // Imposta il titolo pagina
//                        tP = titleFile.get(part);
//                        // Gestione rami
//                        if (part.equalsIgnoreCase(PART_MEASURES)) {
//                            // Controlla l'esistenza del codice di una misura
//                            if (codeMis.equals(DASH)) {
//                            /* ------------------------------------------------ *
//                             *      Elenco Misure raggruppate per struttura     *
//                             * ------------------------------------------------ */
//                                structs = db.getMeasuresByStructs(user, survey);
//                                // Aggiunge una foglia alle breadcrumbs
//                                bC = HomePageCommand.makeBreadCrumbs(breadCrumbs, NOTHING, "Misure x Struttura");
//                                // Imposta la pagina
//                                fileJspT = nomeFile.get(part);
//                            } else {
//                            /* ------------------------------------------------ *
//                             *             Dettagli misura monitorata           *
//                             * ------------------------------------------------ */
//                                // Recupera la misura di prevenzione/mitigazione
//                                measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
//                                // Recupera i rischi cui è associata
//                                risksByMeasure = db.getRisksByMeasure(user, codeMis, survey);
//                                // Personalizza le breadcrumbs
//                                bC = loadBreadCrumbs(breadCrumbs, part, survey); 
//                                // Imposta la pagina
//                                fileJspT = nomeFileMisura;
//                            }
//                        } else if (part.equalsIgnoreCase(PART_INDICATOR)) {
//                            measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
//                            if (idInd > DEFAULT_ID) {
//                            /* ------------------------------------------------ *
//                             *       Dettagli di un indicatore di dato id       *
//                             * ------------------------------------------------ */
//                                // Imposta la pagina
//                                fileJspT = nomeFileDettaglio;
//                            } else {
//                            /* ------------------------------------------------ *
//                             *          Elenco indicatori di una misura         *
//                             * ------------------------------------------------ */
//                                // Personalizza le breadcrumbs
//                                bC = loadBreadCrumbs(breadCrumbs, part, survey);
//                                // Imposta la pagina
//                                fileJspT = nomeFile.get(part);
//                            }
//                        } else if (part.equalsIgnoreCase(PART_MONITOR)) {
//                            
//                            /* ------------------------------------------------ *
//                             *    Elenco Misurazioni di una misura monitorata   *
//                             * ------------------------------------------------ */
//                            measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
//                            measurements = decantMeasurements(measure);
//                            // Imposta la pagina
//                            fileJspT = nomeFile.get(part);
//                        } else if (part.equalsIgnoreCase(PART_INSERT_MONITOR_DATA)) {
//                            /* ------------------------------------------------ *
//                             * Form aggiunta dettagli monitoraggio a una misura *
//                             * ------------------------------------------------ */
//                            if (!codeMis.equals(DASH)) {
//                                // Recupera la misura di prevenzione/mitigazione
//                                measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
//                                // Recupera i rischi cui è associata
//                                risksByMeasure = db.getRisksByMeasure(user, codeMis, survey);
//                                // Personalizza le breadcrumbs
//                                bC = loadBreadCrumbs(breadCrumbs, part, survey); 
//                                // Pagina
//                                fileJspT = nomeFile.get(part);
//                            }
//                        } else if (part.equalsIgnoreCase(PART_SELECT_MEASUREMENT)) {
//                            /* ------------------------------------------------ *
//                             *        Pagina riepilogo dettagli misurazione     *
//                             * ------------------------------------------------ */
//                            // Recupera gli estremi della misura, della fase e dell'indicatore
//                            measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
//                            // Recupera la misurazione cercata
//                            measurement = db.getMeasurement(user, idMon, survey);
//                            // Pagina
//                            fileJspT = nomeFile.get(part);
//                        } else if (part.equalsIgnoreCase(PART_INSERT_INDICATOR)) {
//                            /* ------------------------------------------------ *
//                             *      Maschera inserimento nuovo Indicatore       *
//                             * ------------------------------------------------ */
//                            // Recupera la fase cui si vuol aggiungere l'indicatore
//                            phase = db.getMeasureActivity(user, codeMis, idFas, survey);
//                            // Pagina
//                            fileJspT = nomeFile.get(part);
//                        } else if (part.equalsIgnoreCase(PART_INSERT_MEASUREMENT)) {
//                            /* ------------------------------------------------ *
//                             *       Maschera inserimento nuova Misurazione     *
//                             * ------------------------------------------------ */
//                            // Recupera l'indicatore cui si vuol aggiungere la misurazione
//                            measure = MeasureCommand.retrieveMeasure(user, codeMis, survey, db);
//                            // Breadcrumbs
//                            // Pagina
//                            fileJspT = nomeFile.get(part);
//                        }
//                    } else {
//                        /* ------------------------------------------------ *
//                         *      Elenco sole misure monitorate (Registro)    *
//                         * ------------------------------------------------ */
//                        measures = MeasureCommand.filter(db.getMeasures(user, VOID_SQL_STRING, Query.GET_ALL_BY_CLAUSE, survey));
//                        tP = titleFile.get(COMMAND_INDICATOR);
//                        fileJspT = nomeFileElenco;
//                    }
                }

//        }  catch (WebStorageException wse) {
//            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
//            LOG.severe(msg);
//            throw new CommandException(msg + wse.getMessage(), wse);
//        } catch (CommandException ce) {
//            String msg = FOR_NAME + "Si e\' tentato di effettuare un\'operazione non andata a buon fine.\n";
//            LOG.severe(msg);
//            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *              Settaggi in request dei valori calcolati                *
         * ******************************************************************** */
        // Imposta nella request elenco tipologie di indicatori
        if (types != null) {
            req.setAttribute("tipi", types);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }   
        // Imposta struttura contenente tutti i parametri di navigazione già estratti
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Titolo pagina in caso sia significativo
        if (tP != null && !tP.equals(VOID_STRING)) {
            req.setAttribute("tP", tP);
        }    
        // Imposta nella request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta nella request data di oggi 
        req.setAttribute("now", today);
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /* **************************************************************** *
     *  Metodi di caricamento dei parametri in strutture indicizzabili  *                     
     *                              (load)                              *
     * **************************************************************** */
    
    /**
     * Valorizza per riferimento una mappa contenente tutti i valori 
     * parametrici riscontrati sulla richiesta.
     * 
     * @param part          la sezione corrente del sito
     * @param req           la HttpServletRequest contenente la richiesta del client
     * @param formParams    mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
//    private static void loadParams(String part, 
//                                   HttpServletRequest req,
//                                   HashMap<String, LinkedHashMap<String, String>> formParams)
//                            throws CommandException,
//                                   AttributoNonValorizzatoException {
//        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
//        LinkedHashMap<String, String> measure = new LinkedHashMap<>();
//        LinkedHashMap<String, String> indicator = null;
//        LinkedHashMap<String, String> measurement = null;
//        // Parser per la gestione assistita dei parametri di input
//        ParameterParser parser = new ParameterParser(req);
//        /* ---------------------------------------------------- *
//         *     Caricamento parametro di Codice Rilevazione      *
//         * ---------------------------------------------------- */      
//        // Recupera o inizializza 'codice rilevazione' (Survey)
//        String codeSur = parser.getStringParameter("r", DASH);
//        // Recupera l'oggetto rilevazione a partire dal suo codice
//        CodeBean surveyAsBean = ConfigManager.getSurvey(codeSur);
//        // Inserisce l'ìd della rilevazione come valore del parametro
//        survey.put(PARAM_SURVEY, String.valueOf(surveyAsBean.getId()));
//        // Aggiunge il tutto al dizionario dei parametri
//        formParams.put(PARAM_SURVEY, survey);
//        /* -------------------------------------------------------- *
//         *  Ramo di INSERT di ulteriori informazioni da aggiungere  *
//         *      a una misura (dettagli relativi al monitoraggio)    *
//         * -------------------------------------------------------- */
//        if (part.equalsIgnoreCase(PART_INSERT_MONITOR_DATA)) {
//            GregorianCalendar date = Utils.getCurrentDate();
//            String dateAsString = Utils.format(date, DATA_SQL_PATTERN);
//            measure.put("code",         parser.getStringParameter("ms-code", VOID_STRING));
//            measure.put("data",         dateAsString);            
//            measure.put("piao",         parser.getStringParameter("ms-piao", VOID_STRING));
//            // Fasi di attuazione (Array)
//            String[] fasi = req.getParameterValues("ms-fasi");
//            // Aggiunge tutte le fasi di attuazione trovate
//            decantStructures("fase", fasi, measure);
//            // Aggiunge i dettagli monitoraggio al dizionario dei parametri
//            formParams.put(part, measure);
//        }
//        /* ---------------------------------------------------- *
//         *       Ramo di INSERT / UPDATE di un Indicatore       *
//         * ---------------------------------------------------- */
//        else if (part.equalsIgnoreCase(PART_INSERT_INDICATOR) /*|| part.equalsIgnoreCase(Query.MODIFY_PART)*/ ) {
//            indicator = new LinkedHashMap<>();            
//            GregorianCalendar date = Utils.getUnixEpoch();
//            String dateAsString = Utils.format(date, DATA_SQL_PATTERN);
//            indicator.put("fase",       parser.getStringParameter("ind-fase",       VOID_STRING));
//            indicator.put("tipo",       parser.getStringParameter("ind-tipo",       VOID_STRING));
//            indicator.put("nome",       parser.getStringParameter("ind-nome",       VOID_STRING));
//            indicator.put("desc",       parser.getStringParameter("ind-descr",      VOID_STRING));
//            indicator.put("base",       parser.getStringParameter("ind-baseline",   VOID_STRING));
//            indicator.put("database",   parser.getStringParameter("ind-database",   dateAsString));
//            indicator.put("targ",       parser.getStringParameter("ind-target",     VOID_STRING));
//            indicator.put("datatarg",   parser.getStringParameter("ind-datatarget", dateAsString));
//            formParams.put(part, indicator);
//        }
//        /* ---------------------------------------------------- *
//         *  Ramo di INSERT di una misurazione su un Indicatore  *
//         * ---------------------------------------------------- */
//        else if (part.equalsIgnoreCase(PART_INSERT_MEASUREMENT)) {
//            measurement = new LinkedHashMap<>();
//            GregorianCalendar date = Utils.getUnixEpoch();
//            String dateAsString = Utils.format(date, DATA_SQL_PATTERN);
//            measurement.put("valore",   parser.getStringParameter("mon-value", VOID_STRING));
//            measurement.put("azioni",   parser.getStringParameter("mon-descr", VOID_STRING));
//            measurement.put("motivi",   parser.getStringParameter("mon-infos", VOID_STRING));
//            measurement.put("domanda1", parser.getStringParameter("mon-quest1",VOID_STRING));
//            measurement.put("domanda2", parser.getStringParameter("mon-quest2",VOID_STRING));
//            measurement.put("domanda3", parser.getStringParameter("mon-quest3",VOID_STRING));
//            measurement.put("ultima",   parser.getStringParameter("mon-miles", String.valueOf(NOTHING)));
//            measurement.put("data",     parser.getStringParameter("mon-data", dateAsString));
//            measurement.put("ind",      parser.getStringParameter("mon-ind", VOID_STRING));
//            formParams.put(part, measurement);
//        }
//
//        /* ******************************************************** *
//         *  Ramo di UPDATE di ulteriori informazioni da aggiungere  *
//         *      a un Indicatore (p.es.: target rivisto, etc.)       *
//         * ******************************************************** *
//        else if (part.equalsIgnoreCase(Query.UPDATE_PART)) {
//            GregorianCalendar date = Utils.getUnixEpoch();
//            String dateAsString = Utils.format(date, Query.DATA_SQL_PATTERN);
//            HashMap<String, String> ind = new HashMap<String, String>();
//            ind.put("ind-id",           parser.getStringParameter("ind-id", Utils.VOID_STRING));
//            ind.put("prj-id",           parser.getStringParameter("prj-id", Utils.VOID_STRING));
//            ind.put("ext-target",       parser.getStringParameter("modext-target", Utils.VOID_STRING));
//            ind.put("ext-datatarget",   parser.getStringParameter("ext-datatarget", dateAsString));
//            ind.put("ext-annotarget",   parser.getStringParameter("ext-annotarget",  Utils.VOID_STRING));
//            ind.put("ext-note",         parser.getStringParameter("modext-note", Utils.VOID_STRING));
//            ind.put("modext-auto",      parser.getStringParameter("modext-auto", dateAsString));
//            formParams.put(Query.UPDATE_PART, ind);
//        }*/
//    }

    
}
