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
 *   Copyright (C) 2025-2026 Giovanroberto Torre
 *   all right reserved
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
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.ParameterParser;

import it.col.ConfigManager;
import it.col.Data;
import it.col.SessionManager;
import it.col.bean.CodeBean;
import it.col.bean.CommandBean;
import it.col.bean.Convenzione;
import it.col.bean.PersonBean;
import it.col.db.DBManager;
import it.col.db.DBWrapper;
import it.col.exception.CommandException;
import it.col.exception.WebStorageException;
import it.col.util.Constants;
import it.col.util.DataUrl;
import it.col.util.MailManager;
import it.col.util.Utils;


/** 
 * <p><code>SchedulerCommand.java</code> (sc)<br />
 * It contains the logic to schedule the agreements.</p>
 * 
 * <p>First commit on 2025-10-01 18:40:28</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class SchedulerCommand extends CommandBean implements Command, Constants {

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
    static Logger log = Logger.getLogger(SchedulerCommand.class.getName());
    /**
     * <p>Given that one nanosecond equals 1, the same amount of time
     * expressed in other units can be obtained 
     * by using appropriate divisors, as in the following table:
     * <dl>
     * <dt>microseconds</dt>
     * <dd>10<sup>-3</sup></dd>
     * <dt>milliseconds</dt>
     * <dd>10<sup>-6</sup></dd>
     * <dt>seconds</dt>
     * <dd>10<sup>-9</sup></dd>
     * <dt>minutes</dt>
     * <dd>1.67 × 10<sup>-11</sup></dd>
     * <dt>hours</dt>
     * <dd>2.78 × 10<sup>-13</sup></dd>
     * <dt>days</dt>
     * <dd>1.16 × 10<sup>-14</sup></dd>
     * </dl>
     * Therefore, to convert a time expressed in milliseconds, 
     * such as that provided by the System class, 
     * simply define numbers with the size of the divisors, 
     * and place them in the denominator.
     */
    public static final double SECOND_DIVISOR = 1E9D;
    /**
     * <p>Time, in milliseconds, at which you want to refresh</p>
     * <p>From the documentation of the constant for the divisor of the time elapsed
     * for the execution of the thread, it can be seen that between seconds and milliseconds
     * there are 3 zeros to add to the exponent, so the milliseconds
     * are separated from seconds by “only” 3 orders of magnitude; 
     * on the other hand, a second is by definition composed
     * of 1 x 10³ ms.
     * <p>Therefore, to obtain the scheduled time for the refresh
     * in milliseconds, we multiply that time in minutes (e.g., 60)
     * by 60 (to get the seconds), then by 1000 
     * (to get the milliseconds).<br>
     * In practice:<dl>
     * <dt>SCHEDULED_TIME = 1000</dt>
     * <dd>→ 1"</dd>
     * <dt>SCHEDULED_TIME = 1000 * 60</dt>
     * <dd>→ 1'</dd>
     * <dt>SCHEDULED_TIME = 1000 * 60 * 60</dt>
     * <dd>→ 1h</dd>
     * <dt>SCHEDULED_TIME = 1000 * 60 * 60 * 6</dt>
     * <dd>→ 6h</dd>
     * and so on...
     * </dl>
     *  By adding additional factors,
     * the scheduling times can be increased.
     */
    static final long SCHEDULED_TIME = 1000 * 60 * 60 * 24 * 7; // ← 1 time a week
    /** 
     * Timer to schedule the update
     */
    private static Timer updateTimer = new Timer();
    /**
     * <p>Recupera da Servlet la stringa opportuna per il puntamento del DataSource.</p>
     */
    private static String contextDbName = DBManager.getDbName();
    /**
     *  Map of pages managed by this Command
     */    
    private static final HashMap<String, CodeBean> pages = new HashMap<>();
    /**
     *  SELECT List page
     */    
    private static final CodeBean elenco = new CodeBean("scElenco.jsp", "Scadenze [COL]");
    
    
    /**
     * Static initialization block to launch recalculation and
     * emailing of agreements expiring within a dynamically calculated interval.
     * Delay: 0 milliseconds
     * Repeat: 1 time each SCHEDULED_TIME milliseconds; particularly, 
     * 1000*60*60*168 are 604,800,000 milliseconds (roughly a half of a billion), 
     * which means 7 days.
     */
    static {
        log.info(FOR_NAME + "Blocco statico di inizializzazione. ");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long startTime = System.nanoTime();
                Date start = Utils.convert(Utils.getCurrentDate());
                Date end = Utils.convert(Utils.getDate(0, 12, 0));
                /* Disable e-mail for Development environment */
                if (!contextDbName.endsWith("dev")) {
                    // There is a naming here; you should improve that
                    int[] convenzioniUO = {1}; 
                    Data.handleSendEmail(convenzioniUO, start, end);
                    log.info(FOR_NAME + "E-mail inviata: " + convenzioniUO);
                    int[] organiUO = {3}; 
                    Data.handleSendEmail(organiUO, start, end);
                    log.info(FOR_NAME + "E-mail inviata: " + organiUO);
                    int[] headUO = {1, 3};
                    Data.handleSendEmail(headUO, start, end);
                    log.info(FOR_NAME + "E-mail inviata: " + headUO);
                    long elapsedTime = System.nanoTime() - startTime;
                    log.config(FOR_NAME + "Email inviate in " + elapsedTime / SECOND_DIVISOR + "\"");
                }
                /**/
                log.info(FOR_NAME + "End run()");
            }
        }, NOTHING, SCHEDULED_TIME); // Refresh in SCHEDULED_TIME milliseconds
    }
    

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
        pages.put(COMMAND_SCDL,     elenco);
    }
    
    
    /* ******************************************************************** *
     *                          Constructor (empty)                         *
     * ******************************************************************** */
    /** 
     * Create a new instance of this Command 
     */
    public SchedulerCommand() {
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
        //DataUrl dataUrl = new DataUrl();
        // Logged user
        PersonBean user = null;
        // Single Agreement
        Convenzione convention = null;
        // List of Agreements
        ArrayList<Convenzione> conventions = null;
        // List of Contractors
        ArrayList<PersonBean> contractors = null;
        // All the params coming from forms
        //HashMap<String, LinkedHashMap<String, String>> params = null;
        // List of agreement types
        final ArrayList<CodeBean> types = ConfigManager.getTypes();
        // List of agreement scopes
        final ArrayList<CodeBean> scopes = ConfigManager.getScopes();
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
        // From when gotta retrieve the conventions?
        String startAsString = parser.getStringParameter("start", UNIX_EPOCH);
        // To when gotta retrieve the conventions?
        String endAsString = parser.getStringParameter("end", THE_END_OF_TIME);
        // Convert string to date
        Date start = Utils.format(startAsString);
        Date end = Utils.format(endAsString);
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
            // Here the user must be logged, this Command isn't callable without authentication
            user = SessionManager.checkSession(req.getSession(IF_EXISTS_DONOT_CREATE_NEW));
        } catch (RuntimeException re) {
            throw new CommandException(FOR_NAME + "Problema a livello dell\'autenticazione utente!\n" + re.getMessage(), re);
        }
        /* ******************************************************************** *
         *                        Understand what to do                         *
         * ******************************************************************** */
        try {
            // Creazione della tabella che conterrà i valori dei parametri passati dalle form
            //params = new HashMap<>();
            // Carica in ogni caso i parametri di navigazione
            //loadParams(part, req, params);
            /* ======================= @PostMapping ======================= */
            if (write) {
                // ;
            /* ======================== @GetMapping ======================= */
            } else {
                // Which operationg has it to do?
                switch (operation) {
                    case INSERT:
                        // getInsert(req, res);
                        break;
                    case UPDATE:
                        // getUpdate(req, res);
                        break;
                    case DELETE:
                        // getDelete(req, res);
                        break;
                    case SEND:
                        //MailManager.sendEmail(); ← it doesn't work
                        break;
                    default:
                        // If there is no operation, there is a SELECT operation
                        // Show the landing page of this Command
                        fileJspT = pages.get(this.getNome());
                        break; // actually not required
                }
            }
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            log.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un puntamento a null.\n";
            log.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            log.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *              Settaggi in request dei valori calcolati                *
         * ******************************************************************** */
        // Redirect address, if it exists
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }   
        /* All navigation params
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }*/
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
    
}
