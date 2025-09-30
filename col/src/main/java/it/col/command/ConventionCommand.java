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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.ParameterParser;

import it.col.ConfigManager;
import it.col.Main;
import it.col.SessionManager;
import it.col.bean.CodeBean;
import it.col.bean.CommandBean;
import it.col.bean.Convenzione;
import it.col.bean.PersonBean;
import it.col.db.DBWrapper;
import it.col.exception.CommandException;
import it.col.exception.WebStorageException;
import it.col.util.Constants;
import it.col.util.DataUrl;
import it.col.util.Utils;


/** 
 * <p><code>ConventionCommand.java</code> (co)<br />
 * It contains the logic to manage the agreements.</p>
 * 
 * <p>Created on 2025-09-18 16:27:27</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ConventionCommand extends CommandBean implements Command, Constants {

    /**
     *  Long type constant for serialization
     */
    private static final long serialVersionUID = -7546430466772067442L;
    /**
     *  Name of this
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     *  Log for production debug
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     *  Map of pages managed by this Command
     */    
    private static final HashMap<String, CodeBean> pages = new HashMap<>();
    /**
     *  SELECT List page
     */    
    private static final CodeBean elenco = new CodeBean("landing.jsp", "COL [Convenzioni On Line]");
    /**
     *  SELECT Details page
     */    
    private static final CodeBean dettagli = new CodeBean("coConvenzione.jsp", "Dettagli convenzione");
    /**
     *  UPDATE Convention: assign one or more contractors to a single convention
     */    
    private static final CodeBean contraenti = new CodeBean("coFormContraente.jsp", "Assegna contraente");
    

    /** 
     * Initialize the Command
     *
     * @param voice CommandBean for the current Command
     * @throws CommandException if some attribute doesn't contain a correct value 
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
        // Hashmap containing pages
        pages.put(COMMAND_CONV,     elenco);
        pages.put(SELECT,           dettagli);
        pages.put(CONTRACTOR,       contraenti);
    }
    
    
    /* ******************************************************************** *
     *                          Constructor (empty)                         *
     * ******************************************************************** */
    /** 
     * Create a new instance of this Command 
     */
    public ConventionCommand() {
        /*;*/   // It doesn't anything
    }
  
    
    /* ******************************************************************** *
     *                            Implementation                            *
     * ******************************************************************** */
    /**
     * Manage the main flow.
     * Prepare the beans.
     * Transfer the parameters inserted by the user.
     * 
     * @param req HttpServletRequest the client request
     * @throws CommandException if some problem occurs 
     */
    @Override
    public void execute(HttpServletRequest req) 
                 throws CommandException {
        // Databound
        DBWrapper db = null;
        // DataUrl for URL management
        DataUrl dataUrl = new DataUrl();
        // Logged user
        PersonBean user = null;
        // Single Agreement
        Convenzione convention = null;
        // List of Agreements
        ArrayList<Convenzione> conventions = null;
        // List of Contractors
        ArrayList<PersonBean> contractors = null;
        // All the params coming from forms
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Page
        CodeBean fileJspT = new CodeBean();
        // Redirect from POST call to a GET request
        String redirect = null;
        // Date of the day
        java.util.Date today = Utils.convert(Utils.getCurrentDate());
        // List of agreement types
        final ArrayList<CodeBean> types = ConfigManager.getTypes();
        // List of agreement scopes
        final ArrayList<CodeBean> scopes = ConfigManager.getScopes();
        /* ******************************************************************** *
         *                  Retrieve attributes and parameters                  *
         * ******************************************************************** */
        // Flag of writing
        Boolean writeAsObject = (Boolean) req.getAttribute("w");
        // Its explicit Unboxing
        boolean write = writeAsObject.booleanValue();
        // Parser of parameters
        ParameterParser parser = new ParameterParser(req);
        // What do we do?
        String operation = parser.getStringParameter("op", SELECT);
        // Which one is involved to do what we do?
        String object = parser.getStringParameter("obj", DASH);
        // Retrieve, or initialize, 'id agreement'
        int idA = parser.getIntParameter("id", DEFAULT_ID);
        /* ******************************************************************** *
         *                          Build the db access                         *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *                  Manage Garden Gate kind of attack                   *
         * ******************************************************************** */
        try {
            // Here the user session must be active, otherwise something's odd
            user = SessionManager.checkSession(req.getSession(IF_EXISTS_DONOT_CREATE_NEW));
        } catch (RuntimeException re) {
            throw new CommandException(FOR_NAME + "Problema a livello dell\'autenticazione utente!\n" + re.getMessage(), re);
        }
        /* ******************************************************************** *
         *                        Understand what to do                         *
         * ******************************************************************** */
        try {


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
                // Which operationg has it to do?
                switch (operation) {
                    case "ins":
                        // TODO
                        break;
                    case "upd":
                        // Test if there is a convention id
                        if (idA > DEFAULT_ID) { 
                            // Get the convention
                            convention = db.getConvention(user, idA);
                            // Manage the contractor(s) of the idA convention
                            if (object.equalsIgnoreCase(CONTRACTOR)) {
                                // Get all the contractors
                                contractors = db.getContractors(user);
                                // Show the form to assign a consultant to a convention
                                fileJspT = pages.get(object);
                            }
                            
                        }
                        break;
                    case "del":
                        // TODO
                        break;
                    default:
                        // If there is no operation, there is a SELECT operation
                        if (idA > DEFAULT_ID) {
                            // Get the convention
                            convention = db.getConvention(user, idA);
                            // Show the details page
                            fileJspT = pages.get(operation);
                        } else {
                            // Get the conventions
                            conventions = db.getConventions(user);
                            // Show the landing page
                            fileJspT = pages.get(this.getNome());
                        }
                        break; // not required here, still here for consistency
                }
//                    if (nomeFile.containsKey(part)) {

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
        // Redirect address, if it exists
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }   
        // All navigation params
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Single convention, if it does exist
        if (convention != null) {
            req.setAttribute("convenzione", convention);
        }
        // List of agreements, if it does exist
        if (conventions != null) {
            req.setAttribute("convenzioni", conventions);
        }
        // List of contractors, if it does exist
        if (contractors != null) {
            req.setAttribute("contraenti", contractors);
        }
        // Agreement types
        req.setAttribute("tipi", types);
        // Agreement scopes
        req.setAttribute("finalita", scopes);
        // Date of today
        req.setAttribute("now", today);
        // Page title
        req.setAttribute("tP", fileJspT.getInformativa()); 
        // Page JSP to forward
        req.setAttribute("fileJsp", "/jsp/" + fileJspT.getNome());
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
