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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.col.ConfigManager;
import it.col.Main;
import it.col.bean.CommandBean;
import it.col.bean.Convenzione;
import it.col.bean.PersonBean;
import it.col.db.DBManager;
import it.col.db.DBWrapper;
import it.col.exception.CommandException;
import it.col.exception.WebStorageException;
import it.col.util.Constants;
import it.col.util.DataUrl;
import it.col.exception.AttributoNonValorizzatoException;
import it.col.bean.ItemBean;


/**
 * <p><code>HomePageCommand.java</code>
 * Manage the root applicazione at Command layer.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class HomePageCommand extends CommandBean implements Command, Constants {

    /** 
     * Seralization needs a long type const, identifying the serial version
     */
    private static final long serialVersionUID = -4437906730411178543L;
    /** 
     * The name of this (for the error messages)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": "; //$NON-NLS-1$
    /** 
     * Debug log
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /** 
     * Login page
     */
    private static final String nomeFileElenco = "/jsp/login.jsp";
    /**
     * Horizontal Menu
     */
    private static LinkedHashMap<String, ItemBean> menu = new LinkedHashMap<>();
    /** 
     * Database name
     */ 
    private static String dbName = null;


    /**
     * Create a new instance of HomePageCommand
     */
    public HomePageCommand() {
        /*;*/   // It doesn't anything
    }


    /**
     * <p>Initialize the Command by the values received from the voice.</p>
	 *
	 * @param voice the CommandBean representing this Command
	 * @throws it.col.exception.CommandException whether mandatory properties are not present
     */
    @Override
    public void init(CommandBean voice) throws CommandException {
        this.setId(voice.getId());
        this.setNome(voice.getNome());
        this.setLabel(voice.getLabel());
        this.setNomeClasse(voice.getNomeClasse());
        this.setPagina(voice.getPagina());
        this.setInformativa(voice.getInformativa());
        // Load horizontal menu containing the voices of the header
        menu = makeHorizontalMenu();
    }


    /**
     * <p>Gestisce il flusso principale.</p>
     * <p>Prepara i bean.</p>
     * <p>Passa nella Request i valori che verranno utilizzati dall'applicazione.</p>
     *
     * @param req HttpServletRequest contenente parametri e attributi, e in cui settare attributi
     * @throws CommandException incapsula qualunque genere di eccezione che si possa verificare in qualunque punto del programma
     */
    @Override
    public void execute(HttpServletRequest req)
                 throws CommandException {
        /* ******************************************************************** *
         *                    Dichiarazioni e inizializzazioni                  *
         * ******************************************************************** */
        // Utente loggato
        PersonBean user = null;
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Stringa per il redirect quando non ci sono le condizioni per il forward
        String redirect = null;
        /* ******************************************************************** *
         *           Questa command deve rispondere anche PRIMA del login       *
         * ******************************************************************** */
        try {
            if (isLoggedUser(req)) {
                redirect =  ConfigManager.getEntToken() + EQ + Constants.COMMAND_CONV;
            } else {
                fileJspT = nomeFileElenco;
            }
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema non meglio specificato.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *                          Recupera i parametri                        *
         * ******************************************************************** */
        /* Imposta una variabile di applicazione, se non è già stata valorizzata (singleton).
         * Il contenuto in sé della variabile è stato sicuramente creato, altrimenti
         * non sarebbe stato possibile arrivare a questo punto del codice,
         * ma, se questa è la prima richiesta che viene fatta all'applicazione
         * (e siamo quindi in presenza dell'"handicap del primo uomo")
         * non è detto che la variabile stessa sia stata memorizzata a livello
         * di application scope. Ci serve a questo livello per controllare,
         * in tutte le pagine dell'applicazione, che stiamo puntando al db giusto.  
         * ATTENZIONE: crea una variabile di applicazione                       */
        dbName = (String) req.getServletContext().getAttribute("dbName");
        if (dbName == null || dbName.isEmpty()) {
            // Uso la stessa stringa perché, se non valorizzata in application, non sarà mai empty ma sarà null
            dbName = DBManager.getDbName();
            // Attenzione: crea una variabile di APPLICAZIONE
            req.getServletContext().setAttribute("db", dbName);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
        /* ******************************************************************** *
         *              Settaggi in request dei valori calcolati                *
         * ******************************************************************** */

        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }   
    }

    
    /* ************************************************************************ *
     *          Metodi di controllo dello stato dell'utente in sessione         *
     * ************************************************************************ */
    
    /**
     * <p>Restituisce l'utente loggato, se lo trova nella sessione utente,
     * altrimenti lancia un'eccezione.</p>
     *
     * @param req HttpServletRequest contenente la sessione e i suoi attributi
     * @return <code>PersonBean</code> - l'utente loggatosi correntemente
     * @throws CommandException se si verifica un problema nel recupero della sessione o dei suoi attributi
     */
    public static PersonBean getLoggedUser(HttpServletRequest req)
                                    throws CommandException {
        // Utente loggato
        PersonBean user = null;
        /* ******************************************************************** *
         *                         Recupera la Sessione                         *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                String msg = FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n";
                LOG.severe(msg + "Sessione non trovata!\n");
                throw new CommandException();
            }
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                String msg = FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n";
                LOG.severe(msg + "Attributo \'utente\' non trovato in sessione!\n");
                throw new CommandException(msg);
            }
            return user;
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            LOG.severe(msg);
            throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Restituisce true se l'utente &egrave; loggato ed esiste 
     * nella sessione utente, altrimenti restituisce false.</p>
     *
     * @param req HttpServletRequest contenente la sessione e i suoi attributi
     * @return <code>boolean</code> - flag di utente trovato in sessione
     * @throws CommandException se si verifica un problema di puntamento
     */
    public static boolean isLoggedUser(HttpServletRequest req)
                                throws CommandException {
        // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
        try {
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                return false;
            }
            PersonBean user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                return false;
            }
            return true;
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            LOG.severe(msg);
            throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    

    /* ************************************************************************ *
     * Metodi di generazione di liste di voci (per MENU,submenu,breadcrumbs...) *
     * ************************************************************************ */
    
    /**
     * <p>Restituisce una struttura vettoriale <cite>(with insertion order)</cite> 
     * contenente le voci principali del menu orizzontale 
     * del sito di gestione delle convenzioni on-line <em>(COL-GECO)</em></p>.
     *
     * @return <code>LinkedHashMap&lt;String, ItemBean&gt;</code> - struttura vettoriale, rispettante l'ordine di inserimento, che contiene le voci del menu orizzontale
     * @throws CommandException nel caso in cui si verifichi un problema nel recupero di un attributo obbligatorio, o in qualche altro tipo di puntamento
     */
    private static LinkedHashMap<String, ItemBean> makeHorizontalMenu()
                                                               throws CommandException {
        LinkedHashMap<String, ItemBean> mO = new LinkedHashMap<>(7);
        DataUrl dataUrl = new DataUrl();
        // HOME (VOCE 1)
        dataUrl.put(ConfigManager.getEntToken(), COMMAND_CONV);
        ItemBean vO = null;
        vO = new ItemBean(COMMAND_CONV,         // "co" 
                          "Home",               // labelWeb
                          dataUrl.getUrl(),     // url
                          "Pagina Iniziale",    // informativa
                          ELEMENT_LEV_1);       // livello
        mO.put(vO.getNome(), vO);
        dataUrl = null;
        vO = null;
        // VOCE 2
        dataUrl = new DataUrl();
        dataUrl.put(ConfigManager.getEntToken(), COMMAND_CONV)
               .put(OPERATION, SELECT)
               .put(OBJECT, CONTRACTOR)
               .put(DB_CONSTRUCT, ENTITY);
        vO = new ItemBean(CONTRACTOR,           // "cont"
                          "Contraenti",         // labelWeb
                          dataUrl.getUrl(),     // url
                          "Registro Contraenti",// informativa
                          ELEMENT_LEV_1);       // livello
        mO.put(vO.getNome(), vO);
        dataUrl = null;
        vO = null;
        // VOCE 3
        dataUrl = new DataUrl();
        dataUrl.put(ConfigManager.getEntToken(), COMMAND_CONV)
               .put(OPERATION, SEARCH);
        vO = new ItemBean(SEARCH,               // "res" 
                          "Cerca",              // labelWeb
                          dataUrl.getUrl(),     // url
                          "Ricerca Avanzata",   // informativa
                          ELEMENT_LEV_1);       // livello
        mO.put(vO.getNome(), vO);
        dataUrl = null;
        vO = null;
        /* VOCE 4
        vO = new ItemBean("auth",               // nome 
                          "Esci",               // labelWeb
                          "auth",               // url
                          "Logout",             // informativa
                          ELEMENT_LEV_1);       // livello
        mO.put(vO.getNome(), vO);
        dataUrl = null;
        vO = null;*/
        return mO;
    }

    
    /* ************************************************************************ *
     *                             Metodi di debug                              *
     * ************************************************************************ */

    /**
     * <p>Restituisce i nomi e i valori degli attributi presenti in Request
     * in un dato momento e in un dato contesto, rappresentati dallo
     * stato del chiamante.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * quali attributi sono presenti in Request onde evitare duplicazioni
     * o ridondanze.</p>
     * </p>
     * Ad esempio, richiamando questo metodo dal ramo "didattica" del sito web
     * di ateneo, metodo <code>requestByPage</code>
     * e.g.: <pre>req.setAttribute("reqAttr", getAttributes(req));</pre>
     * e richiamandolo dalla pagina relativa, con la semplice:
     * <pre>${reqAttr}</pre>
     * si ottiene:
     * <pre style="border:solid gray;border-width:2px;padding:8px;">
     * <strong>dipartimento</strong> = it.univr.di.uol.bean.DipartimentoBean@518dd094
     * <strong>mO</strong> = {it.univr.di.uol.bean.SegnalibroBean@1ef0921d=[it.univr.di.uol.MenuVerticale@5ab38d6b, it.univr.di.uol.MenuVerticale@42099a52], it.univr.di.uol.bean.SegnalibroBean@4408bdc9=[it.univr.di.uol.MenuVerticale@4729f5d], it.univr.di.uol.bean.SegnalibroBean@19e3fa04=[it.univr.di.uol.MenuVerticale@13c94f3], it.univr.di.uol.bean.SegnalibroBean@463329e3=[it.univr.di.uol.MenuVerticale@3056de27]}
     * <strong>lingue</strong> = it.univr.di.uol.Lingue@3578ce60
     * <strong>FirstLanguage</strong> = it
     * <strong>flagsUrl</strong> = ent=home&page=didattica
     * <strong>SecondLanguage</strong> = en
     * <strong>logoFileDoc</strong> = [[it.univr.di.uol.bean.FileDocBean@5b11bbf9]]
     * <strong>currentYear</strong> = 2015
     * </pre></p>
     *
     * @param req HttpServletRequest contenente gli attributi che si vogliono conoscere
     * @return un unico oggetto contenente tutti i valori e i nomi degli attributi settati in request nel momento in cui lo chiede il chiamante
     */
    public static String getAttributes(HttpServletRequest req) {
        Enumeration<String> attributes = req.getAttributeNames();
        StringBuffer attributesName = new StringBuffer("<pre>");
        while (attributes.hasMoreElements()) {
            String attributeName = attributes.nextElement();
            attributesName.append("<strong><u>");
            attributesName.append(attributeName);
            attributesName.append("</u></strong>");
            attributesName.append(" = ");
            attributesName.append(req.getAttribute(attributeName));
            attributesName.append("<br />");
        }
        attributesName.append("</pre>");
        return String.valueOf(attributesName);
    }


    /**
     * <p>Restituisce i nomi e i valori dei parametri presenti in Request
     * in un dato momento e in un dato contesto, rappresentati dallo
     * stato del chiamante.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * quali parametri sono presenti in Request onde evitare duplicazioni
     * e/o ridondanze.</p>
     * <p>Esempi di richiamo:
     * String par = HomePageCommand.getParameters(req, MIME_TYPE_HTML);
     * String par = HomePageCommand.getParameters(req, MIME_TYPE_TEXT);
     * </p>
     * @param req HttpServletRequest contenente i parametri che si vogliono conoscere
     * @param mime argomento specificante il formato dell'output desiderato
     * @return un unico oggetto contenente tutti i valori e i nomi dei parametri settati in request nel momento in cui lo chiede il chiamante
     */
    public static String getParameters(HttpServletRequest req,
                                       String mime) {
        Enumeration<String> parameters = req.getParameterNames();
        StringBuffer parametersName = new StringBuffer();
        if (mime.equals(MIME_TYPE_HTML)) {
            parametersName.append("<pre>");
            while (parameters.hasMoreElements()) {
                String parameterName = parameters.nextElement();
                parametersName.append("<strong><u>");
                parametersName.append(parameterName);
                parametersName.append("</u></strong>");
                parametersName.append(" = ");
                parametersName.append(req.getParameter(parameterName));
                parametersName.append("<br />");
            }
            parametersName.append("</pre>");
        } else if (mime.equals(MIME_TYPE_TEXT)) {
            while (parameters.hasMoreElements()) {
                String parameterName = parameters.nextElement();
                parametersName.append(parameterName);
                parametersName.append(" = ");
                parametersName.append(req.getParameter(parameterName));
                parametersName.append("\n");
            }
        }
        return String.valueOf(parametersName);
    }


    /**
     * <p>Restituisce <code>true</code> se un nome di un parametro,
     * il cui valore viene passato come argomento del metodo, esiste
     * tra i parametri della HttpServletRequest; <code>false</code>
     * altrimenti.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * se un dato parametro sia stato passato in Request.</p>
     *
     * @param req HttpServletRequest contenente i parametri che si vogliono conoscere
     * @param paramName argomento specificante il nome del parametro cercato
     * @return un unico oggetto contenente tutti i valori e i nomi dei parametri settati in request nel momento in cui lo chiede il chiamante
     */
    public static boolean isParameter(HttpServletRequest req,
                                      String paramName) {
        Enumeration<String> parameters = req.getParameterNames();
        while (parameters.hasMoreElements()) {
            String parameterName = parameters.nextElement();
            if (parameterName.equalsIgnoreCase(paramName)) {
                return true;
            }
        }
        return false;
    }

    /* ************************************************************************ *
     *                    Getters sulle variabili di classe                     *
     * ************************************************************************ */

    /**
     * <p>Restituisce il menu orizzontale.</p>
     *
     * @return <code>LinkedHashMap&lt;String, ItemBean&gt;</code> - una mappa ordinata di voci di menu indicizzate per scala ordinale
     * @throws AttributoNonValorizzatoException se la mappa di voci di menu risulta vuota
     */
    public static LinkedHashMap<String, ItemBean> getHorizontalMenu()
                                                             throws AttributoNonValorizzatoException {
        if (menu.isEmpty()) {
            String msg = FOR_NAME + ": Si e\' verificato un problema nella generazione del menu orizzontale.\n";
            LOG.severe(msg);
            throw new AttributoNonValorizzatoException(msg);
        }
        return menu;
    }

    
}
