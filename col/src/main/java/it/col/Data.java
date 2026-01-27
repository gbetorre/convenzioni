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

package it.col;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.ParameterParser;

import it.col.bean.Convenzione;
import it.col.bean.PersonBean;
import it.col.command.ConventionCommand;
import it.col.db.DBManager;
import it.col.db.DBWrapper;
import it.col.exception.CommandException;
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
 * con o senza metadati, serialization formats, JSON, TOON <em>and so on</em>), 
 * oppure in output gi&agrave; autoformattati (p.es. testo formattato 
 * Rich Text Format).</li>
 * <li>Nel secondo caso, la servlet specifica una pagina di output, contenente
 * la formattazione, che per&ograve; verr&agrave; invocata asincronamente.</li>
 * </ul></p>
 * <p>Questa servlet estrae l'azione dall'URL, ne verifica la
 * correttezza, quindi in base al valore del parametro <code>'entToken'</code> ricevuto
 * richiama le varie classi che devono eseguire i comandi specifici.
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
     * Serialization requires the declaration
     * of a long constant identifying the serial version.
     * (If this data is not entered, it will be calculated automatically
     * by the JVM, and this could lead to errors regarding serialization).
     */
    private static final long serialVersionUID = -7053908837630394953L;
    /**
     * Static name of this class
     */
    private static final String FOR_NAME = "\n" + Logger.getLogger(Data.class.getName()) + COLON + BLANK_SPACE;
    /**
     * All logging goes through this logger.
     */
    private static Logger log = Logger.getLogger(Data.class.getName());
    /**
     * Used to initialize redirects with the servletToken
     */
    private ServletContext servletContext;


    /**
     * Static initialization of global variables and parameters.
     *
     * @param config the configuration used by the servlet container to pass information to the servlet <strong>during initialization</strong>
     * @throws ServletException an exception that can be raised when the servlet encounters difficulties
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // Initialization from its parent
        super.init(config);
        // ServletToken Initialization
        servletContext = getServletContext();
    }


    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("javadoc")
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
                    throws ServletException, IOException {
        // Parser of parameters
        ParameterParser parser = new ParameterParser(req);
        // Retrieve/initialize the operation got to do
        String operation = parser.getStringParameter(OPERATION, VOID_STRING);
        // Determine the page to forward, if there is one of those
        String fileJsp = determinePage(operation);
        // Here we are
        log.info("===> Data servlet - operation: { " + operation + " } <===");
        // Message
        switch (operation) {
            case INSERT:
                // handleInsert(req, res);
                break;
            case UPDATE:
                // handleUpdate(req, res);
                break;
            case DELETE:
                // handleDelete(req, res);
                break;
            case SEND:
                handleSendEmail(req, res);  // ← Extracted externally
                return; // Early return since email completes response
            default:
                log.warning("Unknown operation: { " + operation + " }");
                break; // Not required here, still here for consistency
        }
        if (fileJsp != null) {
            // Common Forward logic (only reached for non-SEND operations)
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher(fileJsp);
            dispatcher.forward(req, res);
        }
    }

    
    /**
     * Determines the JSP page to forward to based on the requested operation.
     * Returns <code>null</code> for operations that complete their own response
     * (like SEND). Maps operations to their corresponding JSP views.
     * 
     * <p><strong>Mapping:</strong></p>
     * <ul>
     * <li>{@link #INSERT} → <code>/insert.jsp</code></li>
     * <li>{@link #UPDATE} → <code>/update.jsp</code></li>
     * <li>{@link #DELETE} → <code>/delete.jsp</code></li>
     * <li>Other operations → <code>null</code> (no forward needed)</li>
     * </ul>
     * 
     * @param operation the operation parameter from request (e.g. "insert", "update")
     * @return JSP path (e.g. "/insert.jsp") or <code>null</code> if no forward needed
     */
    private static String determinePage(String operation) {
        return switch (operation) {
            case INSERT -> "/jsp/insert.jsp";
            case UPDATE -> "/jsp/update.jsp";
            case DELETE -> "/jsp/delete.jsp";
            default -> null;  // SEND or unknown ops handle their own response
        };
    }

    
    /**
     * Forwards the request to a common error page, preserving error information
     * as a request attribute for JSP display. Sets HTTP 400 status for bad requests.
     * 
     * <p>Used by handler methods when business logic fails (validation, database,
     * email sending, etc.). The error page can display a message
     * to users and log details for debugging.</p>
     * 
     * <pre>
     * req.setAttribute("error", "Email sending failed: Connection timeout");
     * forwardToErrorPage(req, res);
     * </pre>
     * 
     * @param req the request with "error" attribute already set by caller
     * @param res the response (status set to 400 Bad Request)
     * @throws ServletException if forward fails
     * @throws IOException if forward fails
     */
    private void forwardToErrorPage(HttpServletRequest req, HttpServletResponse res) 
                             throws ServletException, IOException {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 400
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/error.jsp");  // Common error page
        dispatcher.forward(req, res);
    }
    
    
    /* **************************************************************** *
     * Methods for generating tuples with some presentation (RTF, HTML) *
     * **************************************************************** */
    
    /**
     * Handles the SEND operation by extracting the email message from the request,
     * sending it via MailManager, and generating a confirmation TXT response.
     * Completes the HTTP response directly - no JSP forward required.
     * 
     * <p><strong>Flow:</strong></p>
     * <ul>
     * <li>Extracts email body from request parameters</li>
     * <li>Delegates to {@link #sendEmail(String)} for environment-aware sending</li>
     * <li>Calls {@link #makeTXT} for response logging</li>
     * <li>Early return - bypasses JSP forwarding</li>
     * </ul>
     * 
     * @param req the HTTP request containing email message parameters
     * @param res the HTTP response for TXT confirmation output
     * @throws ServletException if servlet processing fails
     * @throws IOException if response writing fails
     */
    private void handleSendEmail(HttpServletRequest req, HttpServletResponse res) 
                          throws ServletException, IOException {
        try {
            String body = getMessage(req);
            sendEmail(body);  // Single responsibility
            makeTXT(req, res, body + DBManager.getDbName());
            log.info("===> Email sent successfully <===");
        } catch (Exception e) {
            log.severe("Failed to send email: " + e.getLocalizedMessage());
            // Set error attribute for JSP
            req.setAttribute("error", "Email sending failed: " + e.getMessage());
            // Forward to error page instead of crashing
            forwardToErrorPage(req, res);
        }
    }


    /**
     * Sends email using environment-specific configuration.
     * 
     * <p>In <strong>development</strong> (DB name ends with "dev"), 
     * uses SMTP2GO credentials from {@link #getCredentials()} properties. 
     * In <strong>production</strong>, uses default MailManager configuration 
     * (likely application server mail session).</p>
     * 
     * <pre>
     * DEV:   MailManager.sendEmail(body, username, password)
     * PROD:  MailManager.sendEmail(body)
     * </pre>
     * 
     * @param body the email content to send
     * @throws IOException if email delivery fails (logged by caller)
     * @throws Exception if some pointer is wrong
     */
    private void sendEmail(String body) 
            throws IOException, Exception {
        String subject = "Riepilogo Convenzioni in scadenza del " + Utils.format(Utils.getCurrentDate());
        // Development environment
        if (DBManager.getDbName().endsWith("dev")) {
            Properties credentials = getCredentials();
            final String username = credentials.getProperty("username"); // your SMTP2GO username
            final String password = credentials.getProperty("password"); // your SMTP2GO password or API key
            MailManager.sendEmail(body, username, password);
        } else {    // Production environment
            MailManager.sendEmail(subject, body);
        }
    }
    
    
    /**
     * Extracts parameters from the given HttpServletRequest and 
     * returns a message string to send about the data selected via
     * those parameters.
     * This method reads data from the HTTP request (such as parameters),
     * processes it, and returns a message string. 
     * It throws CommandException on failure
     * to report problems during message composition or parameters processing.
     *
     * @param req the HttpServletRequest object containing the client request data
     * @return the generated message as a String
     * @throws CommandException if there is an error processing the request or componing the message
     */
    private static String getMessage(HttpServletRequest req) 
                              throws CommandException {
        // Parser of parameters
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza parametri per identificare la pagina
        String obj = parser.getStringParameter(OBJECT, VOID_STRING);
        String data = parser.getStringParameter(DB_CONSTRUCT, VOID_STRING);
        String start = parser.getStringParameter("start", VOID_STRING);
        String end = parser.getStringParameter("end", VOID_STRING);
        StringBuffer message = new StringBuffer("<p>Convenzioni in scadenza nell\'intervallo considerato: ");
        
        // Gestisce la richiesta
        try {
            // Here the user session must be active, otherwise something's odd
            PersonBean user = SessionManager.checkSession(req.getSession(IF_EXISTS_DONOT_CREATE_NEW));
            Date from = Utils.format(start);
            Date to = Utils.format(end);
            message.append("<strong>da ")
                   .append(Utils.format(from))
                   .append(" a ")
                   .append(Utils.format(to))
                   .append("</strong></p><hr><ul>");
            ArrayList<Convenzione> conventions = ConventionCommand.retrieveConventions(user, from, to);
            for (Convenzione c : conventions) {
                message.append("<li> <a href='https://at.univr.it/col/?q=co&id=")
                       .append(c.getId())
                       .append("'>")
                       .append(c.getTitolo())
                       .append("</a> (<strong>scade il: ")
                       .append(Utils.format(c.getDataScadenza()))
                       .append("</strong>)</li>");
            }
            message.append("</ul>");
        } catch (RuntimeException re) {
            throw new CommandException(FOR_NAME + "Problema a livello dell\'autenticazione utente!\n" + re.getMessage(), re);
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Si e\' verificato un problema. Impossibile visualizzare i risultati.\n" + ce.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        }

        return String.valueOf(message);
    }
    

    /* **************************************************************** *
     *         Methods for serving asynchronous requests (XHR)          *
     * **************************************************************** */

    
    /* **************************************************************** *
     *    Methods for generating presentation-free tuples  (TEXT, CSV)  *
     * **************************************************************** */
    

    /* **************************************************************** *
     *   Utility methods for generating output  *
     * **************************************************************** */

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
     * <p>Gestisce la generazione dell&apos;output in formato di uno stream TESTO 
     * che sar&agrave; recepito come tale dal browser e trattato di conseguenza 
     * (normalmente con il download).</p>
     * <p>Usa altri metodi, interni, per ottenere il nome del file, che dev&apos;essere un
     * nome univoco, e per la stampa vera e propria nel PrintWriter.</p>
     * <p>Un&apos;avvertenza importante riguarda il formato del character encoding!
     * Il db di processi anticorruttivi &egrave; codificato in UTF&ndash;8;
     * pertanto nell'implementazione potrebbe sembrare ovvio che
     * il characterEncoding migliore da impostare sia il medesimo, cosa che si fa
     * con la seguente istruzione:
     * <pre>res.setCharacterEncoding("UTF-8");</pre>
     * Tuttavia, le estrazioni sono destinate ad essere visualizzate
     * attraverso fogli di calcolo che, per impostazione predefinita,
     * assumono che il charset dei dati sia il latin1, non UTF-8,
     * perlomeno per la nostra utenza e per la maggior parte
     * delle piattaforme, per cui i dati, se espressi in formato UTF-8,
     * risultano codificati in maniera imprecisa, perch&eacute;, come al solito,
     * quando un dato UTF-8 viene codificato in latin1
     * (quest'ultimo anche identificato come: l1, csISOLatin1, iso-ir-100,
     * IBM819, CP819, o &ndash; ultimo ma non ultimo &ndash; ISO-8859-1)
     * i caratteri che escono al di fuori dei primi 128 caratteri,
     * che sono comuni (in quanto UTF-8 usa un solo byte per
     * codificare i primi 128 caratteri) non vengono visualizzati
     * correttamente ma vengono espressi con caratteri che in ASCII sono
     * non corrispondenti.</p>
     *
     * @param req HttpServletRequest da passare al metodo di stampa
     * @param res HttpServletResponse per impostarvi i valori che la predispongono a servire csv anziche' html
     * @param qToken token della commmand in base al quale bisogna preparare la lista di elementi
     * @throws ServletException eccezione eventualmente proveniente dalla fprinf, da propagare
     * @throws IOException  eccezione eventualmente proveniente dalla fprinf, da propagare
     */
    private static void makeTXT(HttpServletRequest req, HttpServletResponse res, String content)
                         throws ServletException, IOException {
        // Genera un nome univoco per il file che verrà servito
        String fileName = makeFilename("email");
        // Configura il response per il browser
        res.setContentType(MIME_TYPE_TEXT);
        // Configura il characterEncoding (v. commento)
        res.setCharacterEncoding("UTF-8");
        // Configura l'header
        res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + TEXT);
        
        req.setAttribute("body", content);
        // Stampa il file sullo standard output
        printf(req, res);
    }
    
    
    /**
     * <p>Gestisce la generazione dell&apos;output in formato di uno stream CSV 
     * che sar&agrave; recepito come tale dal browser e trattato di conseguenza 
     * (normalmente con il download).</p>
     * <p>Usa altri metodi, interni, per ottenere il nome del file, che dev&apos;essere un
     * nome univoco, e per la stampa vera e propria nel PrintWriter.</p>
     * <p>Un&apos;avvertenza importante riguarda il formato del character encoding!
     * Il db di processi anticorruttivi &egrave; codificato in UTF&ndash;8;
     * pertanto nell'implementazione potrebbe sembrare ovvio che
     * il characterEncoding migliore da impostare sia il medesimo, cosa che si fa
     * con la seguente istruzione:
     * <pre>res.setCharacterEncoding("UTF-8");</pre>
     * Tuttavia, le estrazioni sono destinate ad essere visualizzate
     * attraverso fogli di calcolo che, per impostazione predefinita,
     * assumono che il charset dei dati sia il latin1, non UTF-8,
     * perlomeno per la nostra utenza e per la maggior parte
     * delle piattaforme, per cui i dati, se espressi in formato UTF-8,
     * risultano codificati in maniera imprecisa, perch&eacute;, come al solito,
     * quando un dato UTF-8 viene codificato in latin1
     * (quest'ultimo anche identificato come: l1, csISOLatin1, iso-ir-100,
     * IBM819, CP819, o &ndash; ultimo ma non ultimo &ndash; ISO-8859-1)
     * i caratteri che escono al di fuori dei primi 128 caratteri,
     * che sono comuni (in quanto UTF-8 usa un solo byte per
     * codificare i primi 128 caratteri) non vengono visualizzati
     * correttamente ma vengono espressi con caratteri che in ASCII sono
     * non corrispondenti.</p>
     *
     * @param req HttpServletRequest da passare al metodo di stampa
     * @param res HttpServletResponse per impostarvi i valori che la predispongono a servire csv anziche' html
     * @param qToken token della commmand in base al quale bisogna preparare la lista di elementi
     * @throws ServletException eccezione eventualmente proveniente dalla fprinf, da propagare
     * @throws IOException  eccezione eventualmente proveniente dalla fprinf, da propagare
     */
    private static void makeCSV(HttpServletRequest req, HttpServletResponse res, String qToken)
                         throws ServletException, IOException {
        // Genera un nome univoco per il file che verrà servito
        String fileName = makeFilename(qToken);
        // Configura il response per il browser
        res.setContentType("text/x-comma-separated-values");
        // Configura il characterEncoding (v. commento)
        res.setCharacterEncoding("ISO-8859-1");
        // Configura l'header
        res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + CSV);
        // Stampa il file sullo standard output
        printf(req, res);
    }
    
    
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
        res.setContentType(MIME_TYPE_HTML);
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

    
    private static int printf(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Genera l'oggetto per lo standard output
        PrintWriter out = res.getWriter();
        Calendar now = Calendar.getInstance();
        String header = "Email inviata alle " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + " " + now.get(Calendar.SECOND) + "\"";
        String content = header + req.getAttribute("body");
        out.println(content);
        return DEFAULT_ID;
    }
    
    
    /**
     * Loads username and password from a properties file named "credentials.properties"
     * located in the classpath root.
     * Example properties file contents:
     *   username = your_username
     *   password = your_password
     *
     * @return a java.util.Properties object containing the loaded username and password keys
     * @throws IOException if the properties file is not found or cannot be read
     */
    public Properties getCredentials() 
                              throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("credentials.properties")) {
            if (input == null) {
                throw new IOException("Credentials file not found in classpath");
            }
            props.load(input);
        }
        return props;
    }

}
