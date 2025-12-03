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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.ParameterParser;

import it.col.ConfigManager;
import it.col.Main;
import it.col.SessionManager;
import it.col.bean.CodeBean;
import it.col.bean.CommandBean;
import it.col.bean.Convenzione;
import it.col.bean.ItemBean;
import it.col.bean.PersonBean;
import it.col.db.DBWrapper;
import it.col.db.Query;
import it.col.exception.AttributoNonValorizzatoException;
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
     *  SELECT Convention Details page (read-only)
     */    
    private static final CodeBean dettagli = new CodeBean("coConvenzione.jsp", "Dettagli convenzione");
    /**
     *  SELECT Convention Details page (updateable)
     */    
    private static final CodeBean convenzione = new CodeBean("coFormConvenzione.jsp", "Modifica convenzione");
    /**
     *  SELECT Contractor page
     */
    private static final CodeBean contraente = new CodeBean("coContraente.jsp", "Scheda contraente");
    /**
     *  SELECT Contractors page
     */
    private static final CodeBean contraenti = new CodeBean("coContraenti.jsp", "Registro contraenti");
    /**
     *  UPDATE Convention: assign one or more contractors to a single convention
     */    
    private static final CodeBean contraenti_ins = new CodeBean("coFormContraenti.jsp", "Assegna contraente");
    /**
     *  Search results
     */    
    private static final CodeBean ricerca = new CodeBean("coFormRicerca.jsp", "Ricerca avanzata");
    

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
        pages.put(SEARCH,           ricerca);
        pages.put(SELECT,           dettagli);
        pages.put(UPDATE,           convenzione);
        pages.put(CONTRACTOR+INSERT,contraenti_ins);
        pages.put(CONTRACTOR+SELECT,contraenti);
        pages.put(CONTRACTOR       ,contraente);
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
        // Single Contractor
        PersonBean contractor = null;
        // List of Agreements
        ArrayList<Convenzione> conventions = null;
        // List of Contractors
        ArrayList<PersonBean> contractors = null;
        // All the params coming from forms
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // List of agreement types
        final ArrayList<CodeBean> types = ConfigManager.getTypes();
        // List of agreement scopes
        final ArrayList<CodeBean> scopes = ConfigManager.getScopes();
        // Header Menu customized by the current Command
        LinkedHashMap<String, ItemBean> menu = null;
        // Page
        CodeBean fileJspT = new CodeBean();
        // Redirect from POST call to a GET request
        String redirect = null;
        // Date of the day
        java.util.Date today = Utils.convert(Utils.getCurrentDate());
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
        // Which database kind of object is involved?
        String dbElement = parser.getStringParameter("data", DASH);
        // From when gotta retrieve the conventions?
        String startAsString = parser.getStringParameter("start", UNIX_EPOCH);
        // To when gotta retrieve the conventions?
        String endAsString = parser.getStringParameter("end", THE_END_OF_TIME);
        // Convert string to date
        Date start = Utils.format(startAsString);
        Date end = Utils.format(endAsString);
        // Retrieve, or initialize, 'id agreement'
        int idA = parser.getIntParameter("id", DEFAULT_ID);
        // The exposed parameter for idContractor (idC) is still 'id'
        int idC = idA;
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
            /* ======================= @PostMapping ======================= */
            if (write) {
                // Carica i parametri di navigazione
                loadParams(operation, object, req, params);
                // Which operationg has it to do?
                switch (operation) {
                    case VOID_STRING:
                        // TODO
                        break;
                    case INSERT:
                        if (object.equalsIgnoreCase(CONTRACTOR)) {
                            // Test if gotta insert a relationship
                            if (dbElement.equalsIgnoreCase(RELATIONSHIP)) {
                                // Insert the relationship
                                db.insertConventionContractors(user, params);
                                // Prepare the redirect
                                dataUrl.put(ConfigManager.getEntToken(), COMMAND_CONV)
                                       .put(OPERATION, INSERT)
                                       .put(OBJECT, CONTRACTOR)
                                       .put(DB_CONSTRUCT, RELATIONSHIP)
                                       .put("id", idA);
                                redirect = dataUrl.getUrl();
                            }
                        }
                        break;
                    case UPDATE:
                        // Test if there is a convention id
                        if (idA > DEFAULT_ID) { 
                            // Update the convention
                            convention = db.updateConvention(user, params);
                            // Update the scopes checking the current convention scopes
                            //convention.setFinalita(updateScopes(scopes, convention));
                            fileJspT = pages.get(operation);
                        }
                        break;
                    case SEARCH:
                        // Search the conventions
                        conventions = db.getConventions(user, params);
                        fileJspT = pages.get(operation);
                        break;
                    default:
                        // Other values are not admitted

                }
            /* ======================== @GetMapping ======================= */
            } else {
                // Which operationg has it to do?
                switch (operation) {
                    case INSERT:
                        // Test if there is a convention id
                        if (idA > DEFAULT_ID) { 
                            // Get the convention
                            convention = db.getConvention(user, idA);
                            // Manage the contractor(s) of the idA convention
                            if (object.equalsIgnoreCase(CONTRACTOR)) {
                                // Test if the INSERT is about an entity or a relationship
                                if (dbElement.equals(RELATIONSHIP)) {
                                    // Get all the contractors
                                    contractors = db.getContractors(user, convention, !Query.GET_ALL);
                                    // Show the form to assign a consultant to a convention
                                    fileJspT = pages.get(object + operation);
                                }
                            }
                        }
                        break;
                    case UPDATE:
                        // Test if there is a convention id
                        if (idA > DEFAULT_ID) { 
                            // Get the convention
                            convention = db.getConvention(user, idA);
                            // Update the scopes checking the current convention scopes
                            convention.setFinalita(updateScopes(scopes, convention));
                            fileJspT = pages.get(operation);
                        }
                        break;
                    case DELETE:
                        // TODO
                        break;
                    case SEARCH:
                        /* Make active the appropriate voice of menu
                        menu = (LinkedHashMap<String, ItemBean>) req.getAttribute("menu");
                        ItemBean current = menu.get(operation);
                        current.setExtraInfo("active");
                        menu.put(current.getNome(), current);*/
                        fileJspT = pages.get(operation);
                        break;
                    default:
                        // If there is no operation, we have a SELECT operation
                        operation = SELECT;
                        // Test if we are dealing with some contractor
                        if (object.equalsIgnoreCase(CONTRACTOR)) {
                            if (idC > DEFAULT_ID) { // Single contractor
                                // Select the contractor of the idC id
                                contractor = db.getContractor(user, idC);
                                // Show the contractor's page
                                fileJspT = pages.get(object);
                            } else {                // List of contractors
                                // Get all the contractors
                                contractors = db.getContractors(user, new Convenzione(DEFAULT_ID), Query.GET_ALL);
                                /* Customize the header horizontal menuto make active the appropriate voice
                                menu = (LinkedHashMap<String, ItemBean>) req.getAttribute("menu");
                                current = menu.get(object);
                                current.setExtraInfo("active");
                                menu.put(current.getNome(), current);*/
                                // Show the form to assign a consultant to a convention
                                fileJspT = pages.get(object + operation);
                            }
                        } else { // Not a contractor focus: must be an agreement focus
                            if (idA > DEFAULT_ID) {
                                // Get the convention
                                convention = db.getConvention(user, idA);
                                // Show the details page
                                fileJspT = pages.get(SELECT);
                            } else {
                                // Get the conventions
                                conventions = db.getConventions(user);
                                // Show the landing page
                                fileJspT = pages.get(this.getNome());
                            }
                        }
                        break; // not required here, still here for consistency
                    }
                }
        // We have a situation here...
        }  catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' tentato di effettuare un\'operazione non andata a buon fine.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
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
        // Overwrite the voices of the header in case of customization from this
        if (menu != null) {
            req.removeAttribute("menu");
            req.setAttribute("menu", menu);
        }
        // Single convention, if it does exist
        if (convention != null) {
            req.setAttribute("convenzione", convention);
        }
        // List of conventions, if they do exist
        if (conventions != null) {
            req.setAttribute("convenzioni", conventions);
        }
        // Single contractor, if it does exist
        if (contractor != null) {
            req.setAttribute("contraente", contractor);
        }
        // List of contractors, if they do exist
        if (contractors != null) {
            req.setAttribute("contraenti", contractors);
        }
        // Agreement types
        req.setAttribute("tipi", types);
        // Agreement scopes
        req.setAttribute("finalita", scopes);
        // Date of today
        req.setAttribute("now", today);
        // Horizontal menu
        req.setAttribute("mO", menu);
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
     * @param operation     l'operazione da eseguire
     * @param obj           l'oggetto su cui eseguire l'operazione
     * @param req           la HttpServletRequest contenente la richiesta del client
     * @param formParams    mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    private static void loadParams(String operation,
                                   String obj, 
                                   HttpServletRequest req,
                                   HashMap<String, LinkedHashMap<String, String>> formParams)
                            throws CommandException,
                                   AttributoNonValorizzatoException {
        LinkedHashMap<String, String> convention = new LinkedHashMap<>();
        LinkedHashMap<String, String> contractor = new LinkedHashMap<>();
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        switch (operation) {
            case INSERT:
                /* -------------------------------------------------------- *
                 *      Ramo di INSERT relazione convenzione-contraenti     *
                 * -------------------------------------------------------- */
                if (obj.equalsIgnoreCase(CONTRACTOR)) {
                    // ID Convenzione
                    contractor.put("conv",  req.getParameter("id"));
                    // Contraenti (Array)
                    String[] cont = req.getParameterValues("co-cont");
                    // Aggiunge tutti gli id contraenti trovati
                    int nCont = decantStructures(obj, cont, contractor);
                    // Aggiunge il numero di contraenti da associare
                    contractor.put("size",      String.valueOf(nCont));
                    // Aggiunge gli estremi dei contraenti da associare all'id convenzione
                    formParams.put(obj, contractor);
                }
                break;
            case UPDATE:
                /* -------------------------------------------------------- *
                 *                  Ramo di UPDATE convenzione              *
                 * -------------------------------------------------------- */
                if (obj.equalsIgnoreCase(CONVENTION)) {
                    // ID Convenzione
                    convention.put("conv",  req.getParameter("co-id"));
                    // Titolo Convenzione
                    convention.put("titl",  req.getParameter("co-titl"));
                    // Protocollo
                    convention.put("prot",  req.getParameter("co-prot"));
                    // Oggetto
                    convention.put("info",  req.getParameter("co-info"));
                    // Note
                    convention.put("note",  req.getParameter("co-note"));
                    // Finalità (Array)
                    String[] scopes = req.getParameterValues("co-scop");
                    // Travasa l'array di finalità su chiavi diverse (appiattisce i valori)
                    int nScopes = decantStructures(obj, scopes, convention);
                    // Aggiunge il numero di finalità da associare
                    convention.put("scop",  String.valueOf(nScopes));
                    // Data Approvazione
                    convention.put("dat1",  req.getParameter("co-dat1"));
                    // Nota Approvazione
                    convention.put("not1",  req.getParameter("co-not1"));
                    // Data Approvazione 2
                    convention.put("dat2",  req.getParameter("co-dat2"));
                    // Nota Approvazione 2
                    convention.put("not2",  req.getParameter("co-not2"));
                    // Data Sottoscrizione
                    convention.put("dat3",  req.getParameter("co-dat3"));
                    // Nota Sottoscrizione
                    convention.put("not3",  req.getParameter("co-not3"));
                    // Data Scadenza
                    convention.put("dat4",  req.getParameter("co-dat4"));
                    // Nota Scadenza
                    convention.put("not4",  req.getParameter("co-not4"));
                    // Ripartizione Bolli
                    convention.put("fees",  req.getParameter("co-boll"));
                    // Pagato
                    convention.put("payd",  req.getParameter("co-pago"));
                    // Aggiunge gli estremi dei contraenti da associare all'id convenzione
                    formParams.put(obj, convention);
                }
                break;
            case DELETE:
                // TODO
                break;
            case SEARCH:
                // Se non è specificato oggetto, assume oggetto = convenzione
                if (obj.equalsIgnoreCase(DASH)) {
                    // Tipo Convenzione
                    convention.put("type", req.getParameter("co-tipo"));
                    // Finalità
                    convention.put("scop", req.getParameter("co-fine"));
                    // Chiave di ricerca
                    convention.put("keys", Utils.escapeRegex(req.getParameter("co-nome")));
                    // Aggiunge le chiavi di ricerca ai parametri
                    formParams.put(operation, convention);
                }
                break;
            default:
                // If there is no operation, there is a SELECT operation
                break; // not required here, still here for consistency
        }
        
    }

    
    /**
     * Dati in input un array di valori e un livello numerico, distribuisce
     * tali valori in una struttura dictionary, passata come parametro, 
     * assegnandone ciascuno a una chiave diversa, costruita
     * in base a un progressivo ed un'etichetta passata come parametro.
     * In pratica, questo metodo serve a trasformare una serie di valori
     * di campi aventi nomi uguali in una form, che quindi vengono passati
     * in un unico array avente lo stesso nome dei campi omonimi, 
     * in una serie di parametri distinti, ciascuno avente per nome 
     * la stessa radice ma contraddistinto da un progressivo.
     * 
     * @param label     etichetta per i nomi dei parametri
     * @param values    valori dei campi selezionati
     * @param params    mappa dei parametri della richiesta
     * @return <code>int</code> - il numero di contraenti che l'utente vuol associare alla convenzione
     */
    public static int decantStructures(String label,
                                       String[] values,
                                       LinkedHashMap<String, String> params) {
        int index = NOTHING;
        int size = ELEMENT_LEV_1; 
        if (values != null) { // <- Controllo sull'input
            while (index < values.length) {
                params.put(label + size,  values[index]);
                size++;
                index++;
            }
        }
        return index;
    }
    
    
    /* **************************************************************** *
     *          Data retrieve methods and other utility methods         *
     * **************************************************************** */
    
    /**
     * <p>Façade di retrieveConventions.</p>
     *
     * @param user  utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param start data scadenza iniziale         
     * @param end   data scadenza finale
     * @return <code>ArrayList&lt;Convenzione&gt;</code> - lista di convenzioni recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<Convenzione> retrieveConventions(PersonBean user,
                                                             Date start,
                                                             Date end)
                                                      throws CommandException {
        try {
            DBWrapper db = new DBWrapper();
            return retrieveConventions(user, start, end, db);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nella creazione del databound.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        }
    }
    
    
    /**
     * <p>Restituisce un ArrayList (albero, vista gerarchica) 
     * di tutte le convenzioni in base a una data finestra temporale.</p>
     *
     * @param user  utente loggato; viene passato ai metodi del DBWrapper per controllare che abbia i diritti di fare quello che vuol fare
     * @param start data scadenza iniziale         
     * @param end   data scadenza finale
     * @param db    WebStorage per l'accesso ai dati
     * @return <code>ArrayList&lt;Convenzione&gt;</code> - lista di convenzioni recuperate
     * @throws CommandException se si verifica un problema nell'estrazione dei dati, o in qualche tipo di puntamento
     */
    public static ArrayList<Convenzione> retrieveConventions(PersonBean user,
                                                             Date start,
                                                             Date end,
                                                             DBWrapper db)
                                                      throws CommandException {
        ArrayList<Convenzione> conventions = null;
        try {
            conventions = db.getConventions(user, start, end);
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return conventions;
    }
    
    
    private static ArrayList<CodeBean> updateScopes(final ArrayList<CodeBean> generalPurposes,
                                                    final Convenzione c) 
                                             throws CommandException {
        ArrayList<CodeBean> updatedScopes = new ArrayList<>(generalPurposes.size());
        try {
            // Input validation
            if (c == null || generalPurposes == null) {
                String msg = FOR_NAME + "Parametri di input non corretti.\n";
                LOG.severe(msg);
                throw new CommandException(msg);
            }
            ArrayList<CodeBean> currentScopes = c.getFinalita();
            if (currentScopes == null || currentScopes.isEmpty()) {
                return new ArrayList<>();
            }
            // O(1) lookup optimization - HashSet for generalPurposes IDs
            Set<Integer> currentScopeIds = new HashSet<>();
            for (CodeBean scope : currentScopes) {
                currentScopeIds.add(scope.getId());
            }
            // Single pass: O(n) instead of O(n*m)
            for (CodeBean purpose : generalPurposes) {
                CodeBean newScope = new CodeBean(purpose);  // Copy constructor
                if (currentScopeIds.contains(purpose.getId())) {
                    newScope.setInformativa("checked");
                }/*
                for (CodeBean scope : currentScopes) {
                    if (scope.getId() == purpose.getId()) {
                        newScope.setInformativa("checked");
                    }
                }*/
                updatedScopes.add(newScope);
            }

        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        return updatedScopes;
    }
    
    /*
    private static List<CodeBean> updateScopes(final ArrayList<CodeBean> generalPurposes,
                                               final Convenzione c) 
                                             throws CommandException {
        ArrayList<CodeBean> updatedScopes = null;
        try {
            // Input control
            if (c == null || generalPurposes == null) {
                String msg = FOR_NAME + "Parametri di input non corretti.\n";
                LOG.severe(msg);
                throw new CommandException(msg);
            }
            if (c.getFinalita() == null || c.getFinalita().isEmpty()) {
                return new ArrayList<>();
            }
            // Create HashSet for O(1) lookup of generalPurposes
            Set<Integer> generalPurposeIds = generalPurposes.stream()
                                                            .map(t -> {
                                                                        try {
                                                                            return t.getId();
                                                                        } catch (AttributoNonValorizzatoException anve) {
                                                                            String msg = FOR_NAME + "Attributo id del CodeBean non valorizzato.\n";
                                                                            LOG.severe(msg);
                                                                        }
                                                                        return null;
                                                                      })
                                                            .collect(Collectors.toSet());
            // Create shallow copy of original scopes and update only matching ones
            return c.getFinalita().stream()
                           .filter(scope -> {
                            try {
                                return generalPurposeIds.contains(scope.getId());
                            } catch (AttributoNonValorizzatoException anve) {
                                String msg = FOR_NAME + "Attributo id del CodeBean non valorizzato.\n";
                                LOG.severe(msg);
                            }
                            return false;
                           })
                           .map(scope -> {
                               CodeBean newScope = new CodeBean();
                               try {
                                newScope = new CodeBean(scope);
                               } catch (AttributoNonValorizzatoException anve) {
                                   String msg = FOR_NAME + "Attributo id del CodeBean non valorizzato.\n";
                                   LOG.severe(msg);
                               }
                               newScope.setInformativa("checked");
                               return newScope;
                           })
                           .collect(Collectors.toList());
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    */
}
