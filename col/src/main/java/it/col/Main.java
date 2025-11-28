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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.col.bean.ItemBean;
import it.col.command.Command;
import it.col.command.HomePageCommand;
import it.col.exception.AttributoNonValorizzatoException;
import it.col.exception.CommandException;
import it.col.util.Utils;


/**
 * <p>Main is the main Servlet of the
 * <code>Convenzioni On Line [COL-GeCo]</code>
 * web application.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class Main extends HttpServlet {

    /**
     * The serialization needs a long type constant.
     */
    private static final long serialVersionUID = 1L;
    /**
     * <p>All logging goes through this logger.</p>
     * <p>To avoid the 'Read access to enclosing field Main.log
     * is emulated by a synthetic accessor method' warning,
     * the visibility is changed to 'friendly' (id est 'default',
     * id est 'visible from the same package').</p>
     */
    static Logger log = Logger.getLogger(Main.class.getName());
    /**
     *  Nome of this (for error messages)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";


    /**
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     *
     * @param config the configuration used by the servlet container to pass information to the Servlet during the initialization
     * @throws ServletException exception that can be raised in case of trouble
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // Init from superclass
        super.init(config);
    }


    /**
     * <p>Manage the GET requests.</p>
     * <p><cite id="malacarne" data-page="99">
     *  This method, (...) acts as a dispatcher: based on the HTTP code operation
     *  received, it activates the more appropriate available method (...)
     * </cite></p>
     * <cite id="malacarne" data-page="100">
     * <p>A subclass of HttpServlet should preferably
     *  override one of the previous methods<br />
     *  (editor's note: <code>doGet | doPost | doOption | doPut | doTrace</code>)</p>
     * <p>In some cases (...) it may be more
     *  convenient, <strong>but this must be a well-considered choice</strong>,
     *  to directly override the <code>service</code> method.</p>
     * </cite>
     *
     * @param req HttpServletRequest containing the client request
     * @param res HttpServletResponse containing the server response
     * @throws ServletException exception that is raised if a problem occurs in forwarding the request/response
     * @throws IOException      exception that is raised if a problem occurs in forwarding the request/response
     */
    @Override
    public void doGet(HttpServletRequest req,
                      HttpServletResponse res)
               throws ServletException, IOException {
        // Command
        String q = null;
        // Domain
        String o = null;
        // Output page
        String fileJsp = null;
        // Get the error page
        String errorJsp = ConfigManager.getErrorJsp();
        /* 
         * Get the token associated with the wanted Command
         */
        try {
            q = req.getParameter(ConfigManager.getEntToken());
        } catch (NullPointerException npe) {
            req.setAttribute("javax.servlet.jsp.jspException", npe);
            log(FOR_NAME + "Problema di puntamento: applicazione terminata!" + npe);
            flush(req, res, errorJsp);
        } catch (Exception e) { // Just in case
            req.setAttribute("javax.servlet.jsp.jspException", e);
            log(FOR_NAME + "Eccezione generica: " + e);
            flush(req, res, errorJsp);
        }
        /* 
         * Get the header voices
         */
        try {
            LinkedHashMap<String, ItemBean> mO = HomePageCommand.getHorizontalMenu();
            req.setAttribute("menu", mO);
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME +
                         "L\'errore e\' stato generato dalla seguente chiamata: " +
                         "HomePageCommand.getHorizontalMenu()";
            log.log(Level.SEVERE, msg, anve);
            req.setAttribute("message", anve.getMessage());
            req.setAttribute("javax.servlet.jsp.jspException", anve);
            flush(req, res, errorJsp);
        }
        // Get the Command and try to invoke its execute method
        try {
            req.setAttribute("w", false);
            Command cmd = lookupCommand(q);
            cmd.execute(req);
        } catch (CommandException ce) {
            String msg = FOR_NAME +
                         "L\'errore e\' stato generato dalla seguente chiamata: " +
                         req.getQueryString() +
                         ", presente nella pagina: " +
                         req.getHeader("Referer");
            log.log(Level.WARNING, msg, ce);
            req.setAttribute("message", ce.getMessage());
            req.setAttribute("javax.servlet.jsp.jspException", ce);
            flush(req, res, errorJsp);
        } catch (Exception e) {
            String msg = FOR_NAME +
                         "L\'errore e\' stato generato dalla seguente chiamata: " +
                         req.getQueryString() +
                         ", presente nella pagina: " +
                         req.getHeader("Referer");
            log.log(Level.SEVERE, msg, e);
            req.setAttribute("message", e.getMessage());
            req.setAttribute("javax.servlet.jsp.jspException", e);
            flush(req, res, errorJsp);
        }
        // Get the navigation params
        req.setAttribute("queryString", req.getQueryString());
        // Template view
        fileJsp = ConfigManager.getTemplate();
        // Fixed infos regarding the view layer (header, current date, baseHref and so on)
        retrieveFixedInfo(req);
        /*
         * Disable the Cache
         */
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0
        res.setDateHeader("Expires", 0); // Proxies.
        /*
         * Finally, it ends in a proper way
         */
        flush(req, res, fileJsp);
    }


    /**
     * <p>Gestisce le richieste del client effettuate con il metodo POST.</p>
     *
     * @param req la HttpServletRequest contenente la richiesta del client
     * @param res la HttpServletResponse contenente la risposta del server
     * @throws ServletException eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     * @throws IOException      eccezione che viene sollevata se si verifica un problema nell'inoltro (forward) della richiesta/risposta
     */
    @Override
    public void doPost(HttpServletRequest req,
                       HttpServletResponse res)
               throws ServletException, IOException {
        /*
         * Dichiara le variabili in base a cui ricercare la Command
         */
        String q = null;
        /*
         * Recupera il nome della pagina di errore
         */
        String errorJsp = ConfigManager.getErrorJsp();
        /*
         * Cerca la command associata al parametro 'ent'
         * e, se la trova, ne invoca il metodo execute()
         */
        try {
            q = req.getParameter(ConfigManager.getEntToken());
        } catch (NullPointerException npe) { // Potrebbe già uscire qui
            req.setAttribute("javax.servlet.jsp.jspException", npe);
            req.setAttribute("message", npe.getMessage());
            log(FOR_NAME + "Problema di puntamento: applicazione terminata!" + npe);
            flush(req, res, errorJsp);
            return;
        } catch (NumberFormatException nfe) { // Controllo sull'input
            req.setAttribute("javax.servlet.jsp.jspException", nfe);
            req.setAttribute("message", nfe.getMessage());
            log(FOR_NAME + "Parametro in formato non valido: applicazione terminata!" + nfe);
            flush(req, res, errorJsp);
            return;
        } catch (Exception e) { // Just in case
            req.setAttribute("javax.servlet.jsp.jspException", e);
            req.setAttribute("message", e.getMessage());
            log(FOR_NAME + "Eccezione generica: " + e);
            flush(req, res, errorJsp);
            return;
        }
        try {
            /*
             * Cerca la command associata al parametro 'ent'
             * e, se la trova, ne invoca il metodo execute()
             */
            req.setAttribute("w", true);
            Command cmd = lookupCommand(q);
            cmd.execute(req);
        } catch (CommandException ce) { // Potrebbe già uscire qui
            req.setAttribute("javax.servlet.jsp.jspException", ce);
            req.setAttribute("message", ce.getMessage());
            log("Problema: " + ce);
            flush(req, res, errorJsp);
            return;
        } catch (Exception e) {
            req.setAttribute("javax.servlet.jsp.jspException", e);
            req.setAttribute("message", e.getMessage());
            log("Problema: " + e);
            e.printStackTrace();
            flush(req, res, errorJsp);
            return;
        }
        retrieveFixedInfo(req);

        /*
         * Disabilita Cache
         */
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0
        res.setDateHeader("Expires", 0); // Proxies.
        /*
         * Finally, it ends in a proper way
         */
        flush(req, res, ConfigManager.getTemplate());
    }


    /**
     * lookupCommand restituisce la classe Command associata parametro
     * d'input <code>cmd</code>, come specificato nella HashTable
     * <code>command</code>. Se il parametro è nullo, allora
     * restituisce la classe <code>homepage</code> che
     * punta alla home page del sito. Se la stringa non è presente
     * nella hashtable command, allora lancia l'eccezione
     * <code>CommandException</code>.
     *
     * @param cmd string che individua la classe Command da creare.
     * @return a Command class
     * @exception CommandException se il parametro <code>cmd</code> non corrisponde a nessuna classe.
     */
    private static Command lookupCommand(String cmd)
                           throws CommandException {
        ConcurrentHashMap<String, Command> commands = ConfigManager.getCommands();
        // Controllo sull'input
        if (cmd == null)
            cmd = ConfigManager.getHomePage();
        // Ottenuto un token valido tenta di recuperare la Command...
        if (commands.containsKey(cmd))
            return commands.get(cmd);
        throw new CommandException(FOR_NAME + "Classe Command non valida: " + cmd);
    }


    /**
     * <p>Esegue pezzi di codice richiamabili direttamente dalla Main,
     * che &egrave; invocata ad ogni richiesta del client e quindi gi&agrave;
     * presente in memoria.<br />
     * Pu&ograve; essere invocata, implicitamente, dal template per
     * recuperare
     * <ul>
     * <li>elenchi di oggetti che servono all'header
     * per popolare le liste eventualmente in esso mostrate</li>
     * <li>il baseHref</li>
     * <li>il flagsUrl</li>
     * <br />
     * e altri valori utili.
     * </ul></p>
     * <p>L'invocazione &egrave; implicita nel senso che il presente
     * metodo non viene richiamato direttamente dal template,
     * ma indirettamente attraverso l'invocazione della Main.
     * A ogni richiesta standard del client, infatti, ovvero
     * ogni richiesta effettuata per visualizzare un output html
     * (per output diversi quali files CSV o simili &egrave; possibile
     * invocare servlet diverse da Main), pu&ograve; implicitamente essere
     * richiamato dal metodo service o - meglio - dalla sovrascrittura
     * dei metodi <code>doGet</code> e/o <code>doPost</code>.<br />
     * Dialoga con l'HttpServletRequest attingendo a dati eventualmente
     * valorizzati in essa e valorizzando nella stessa parametri da passare.</p>
     *
     * @param req  la HttpServletRequest contenente gli header HTTP e alcuni parametri
     */
    private static void retrieveFixedInfo(HttpServletRequest req) {
        // Costruisce qui il valore del <base href... /> piuttosto che nelle pagine
        String baseHref = getBaseHref(req);
        // Setta nella request il valore del <base href... />
        req.setAttribute("baseHref", baseHref);
        // Valorizza l'anno corrente: utile al footer
        String currentYear = Utils.getCurrentYear();
        req.setAttribute("theCurrentYear", currentYear);
        // Cerca o inizializza flag di visualizzazione header
        if (req.getAttribute("header") == null) {
            req.setAttribute("header", true);
        }
        // Cerca o inizializza flag di visualizzazione footer
        if (req.getAttribute("footer") == null) {
            req.setAttribute("footer", true);
        }
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


    /**
     * <p>Inoltra la richiesta ad una pagina passata come argomento.</p>
     *
     * @param req  HttpServletRequest contenente i parametri sulla QueryString
     * @param res  HttpServletResponse per inoltrare la chiamata
     * @param fileJspT pagina JSP a cui puntare nell'inoltro
     * @throws ServletException se si verifica un'eccezione nella redirezione
     * @throws IOException se si verifica un problema di input/output
     * @throws IllegalStateException se la Response era committata o se un URL parziale e' fornito e non puo' essere convertito in un URL valido (v. {@link HttpServletResponse#sendRedirect(String)})
     */
    private void flush(HttpServletRequest req,
                       HttpServletResponse res,
                       String fileJspT)
                throws ServletException,
                       IOException,
                       IllegalStateException {
        if (req.getAttribute("redirect") == null) {
            final RequestDispatcher rd = getServletContext().getRequestDispatcher(fileJspT + "?" + req.getQueryString());
            rd.forward(req, res);
            return;
        }
        res.sendRedirect(getServletContext().getInitParameter("appName") + "/?" + (String) req.getAttribute("redirect"));
    }

}
