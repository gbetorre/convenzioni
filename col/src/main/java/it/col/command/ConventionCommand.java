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
     *  SELECT Details page
     */    
    private static final CodeBean dettagli = new CodeBean("coConvenzione.jsp", "Dettagli convenzione");
    /**
     *  UPDATE Convention: assign one or more contractors to a single convention
     */    
    private static final CodeBean contraenti = new CodeBean("coFormContraenti.jsp", "Assegna contraente");
    

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
        // Which database kind of object is involved?
        String dbElement = parser.getStringParameter("data", DASH);
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
            /* ======================= @PostMapping ======================= */
            if (write) {
                // Carica i parametri di navigazione
                loadParams(object, req, params);
                // Which operationg has it to do?
                switch (object) {
                    case VOID_STRING:
                        // TODO
                        break;
                    case CONTRACTOR:
                        // Test if have to insert a relationship
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

                        break;
                    case "del":
                        // TODO
                        break;
                    default:
                        // Other values are not admitted

                }
            /* ======================== @GetMapping ======================= */
            } else {
                // Which operationg has it to do?
                switch (operation) {
                    case "ins":
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
                                    fileJspT = pages.get(object);
                                }
                            }
                        }
                        break;
                    case "upd":
                        // TODO
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
                            fileJspT = pages.get(SELECT);
                        } else {
                            // Get the conventions
                            conventions = db.getConventions(user);
                            // Show the landing page
                            fileJspT = pages.get(this.getNome());
                        }
                        break; // not required here, still here for consistency
                    }
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
     * @param obj           l'oggetto di interesse
     * @param req           la HttpServletRequest contenente la richiesta del client
     * @param formParams    mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    private static void loadParams(String obj, 
                                   HttpServletRequest req,
                                   HashMap<String, LinkedHashMap<String, String>> formParams)
                            throws CommandException,
                                   AttributoNonValorizzatoException {
        LinkedHashMap<String, String> convention = new LinkedHashMap<>();
        LinkedHashMap<String, String> contractor = new LinkedHashMap<>();
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        /* -------------------------------------------------------- *
         *  Ramo di INSERT di ulteriori informazioni da aggiungere  *
         *      a una misura (dettagli relativi al monitoraggio)    *
         * -------------------------------------------------------- */
        if (obj.equalsIgnoreCase(CONTRACTOR)) {
            // ID Convenzione
            contractor.put("conv",      req.getParameter("id"));
            // Contraenti (Array)
            String[] cont = req.getParameterValues("co-cont");
            // Aggiunge tutti gli id contraenti trovati
            int nCont = decantStructures(obj, cont, contractor);
            // Aggiunge il numero di contraenti da associare
            contractor.put("size",      String.valueOf(nCont));
            // Aggiunge gli estremi dei contraenti da associare all'id convenzione
            formParams.put(obj, contractor);
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
    
}
