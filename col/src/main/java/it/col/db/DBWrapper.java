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

package it.col.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import it.col.bean.BeanUtil;
import it.col.bean.CodeBean;
import it.col.bean.CommandBean;
import it.col.bean.ItemBean;
import it.col.bean.PersonBean;
import it.col.exception.AttributoNonValorizzatoException;
import it.col.exception.WebStorageException;
import it.col.util.Utils;


/**
 * <p><code>DBWrapper.java</code> &egrave; la classe che implementa
 * l'accesso ai database utilizzati dall'applicazione nonch&eacute;
 * l'esecuzione delle query e la gestione dei risultati restituiti,
 * che impacchetta in oggetti di tipo JavaBean e restituisce al chiamante.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DBWrapper extends QueryImpl {

    /**
     * <p>La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.<br />
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera
     * automatica dalla JVM, e questo potrebbe portare a errori
     * riguardo alla serializzazione).</p>
     * <p><small>I moderni IDE, come Eclipse, permettono di assegnare
     * questa costante in due modi diversi:
     *  <ul>
     *  <li>o attraverso un valore di default assegnato a questa costante<br />
     *  (p.es. <code>private static final long serialVersionUID = 1L;</code>)
     *  </li>
     *  <li>o attraverso un valore calcolato tramite un algoritmo implementato
     *  internamente<br /> (p.es.
     *  <code>private static final long serialVersionUID = -8762739881448133461L;</code>)
     *  </li></small></p>
     */
    private static final long serialVersionUID = -8762739881448133461L;
    /**
     * <p>Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    protected static Logger LOG = Logger.getLogger(DBWrapper.class.getName());
    /**
     * <p>Nome di questa classe
     * (viene utilizzato per contestualizzare i messaggi di errore).</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * <p>Connessione db postgres di col.</p>
     */
    protected static DataSource col_manager = null;
    /**
     * <p>Recupera da Servlet la stringa opportuna per il puntamento del DataSource.</p>
     */
    private static String contextDbName = DBManager.getDbName();


    /**
     * <p>Costruttore. Prepara le connessioni.</p>
     * <p>Viene usata l'interfaccia
     * <a href="https://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html">DataSource</a>
     * per ottenere le connessioni.</p>
     * <p>Usa il pattern Singleton per evitare di aprire connessioni inutili
     * in caso di connessioni gi&agrave; aperte.</p>
     *
     * @throws WebStorageException in caso di mancata connessione al database per errore password o dbms down
     * @see DataSource
     */
    public DBWrapper() throws WebStorageException {
        if (col_manager == null) {
            try {
                col_manager = (DataSource) new InitialContext().lookup(contextDbName);
                if (col_manager == null)
                    throw new WebStorageException(FOR_NAME + "La risorsa " + contextDbName + "non e\' disponibile. Verificare configurazione e collegamenti.\n");
            } catch (NamingException ne) {
                throw new WebStorageException(FOR_NAME + "Problema nel recuperare la risorsa jdbc/col per problemi di naming: " + ne.getMessage());
            } catch (Exception e) {
                throw new WebStorageException(FOR_NAME + "Errore generico nel costruttore: " + e.getMessage(), e);
            }
        }
    }


    /* ********************************************************** *
     *                     Metodi di SELEZIONE                    *
     * ********************************************************** */
    
    

    /* ********************************************************** *
     *                    Metodi di INSERIMENTO                   *
     * ********************************************************** */
    

    
    /* ********************************************************** *
     *                  Metodi di AGGIORNAMENTO                   *
     * ********************************************************** */
    

    
    /* ********************************************************** *
     *                  Metodi di ELIMINAZIONE                    *
     * ********************************************************** */
    

    
    /* ********************************************************** *
     *                      Metodi "di servizio"                  *
     * ********************************************************** */

    /**
     * <p>Restituisce un Vector di Command.</p>
     *
     * @return <code>Vector&lt;ItemBean&gt;</code> - lista di ItemBean rappresentanti ciascuno una Command dell'applicazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public Vector<CommandBean> lookupCommand()
                                      throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            CommandBean cmd = null;
            Vector<CommandBean> commands = new Vector<>();
            try {
                pst = con.prepareStatement(LOOKUP_COMMAND);
                pst.clearParameters();
                rs = pst.executeQuery();
                while (rs.next()) {
                    cmd = new CommandBean();
                    BeanUtil.populate(cmd, rs);
                    commands.add(cmd);
                }
                return commands;
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = "Connessione al database in stato inconsistente!\nAttenzione: la connessione vale " + con + "\n";
                    LOG.severe(msg);
                    throw new WebStorageException(FOR_NAME + msg + npe.getMessage(), npe);
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage(), sqle);
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }


    /**
     * <p>Restituisce
     * <ul>
     * <li>il massimo valore del contatore identificativo di una
     * tabella il cui nome viene passato come argomento</li>
     * <li>oppure zero se nella tabella non sono presenti record.</li>
     * </ul></p>
     *
     * @param table nome della tabella di cui si vuol recuperare il max(id)
     * @return <code>int</code> - un intero che rappresenta il massimo valore trovato, oppure zero se non sono stati trovati valori
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method", "null" })
    public int getMax(String table)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            int count = 0;
            String query = SELECT_MAX_ID + table;
            con = col_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }  catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare il max(id).\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce
     * <ul>
     * <li>il minimo valore del contatore identificativo di una
     * tabella il cui nome viene passato come argomento</li>
     * <li>oppure zero se nella tabella non sono presenti record.</li>
     * </ul></p>
     *
     * @param table nome della tabella di cui si vuol recuperare il min(id)
     * @return <code>int</code> - un intero che rappresenta il minimo valore trovato, oppure zero se non sono stati trovati valori
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method", "null" })
    public int getMin(String table)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            int count = 0;
            String query = SELECT_MIN_ID + table;
            con = col_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }  catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare il max(id).\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }
    
    
    /**
     * <p>Restituisce
     * <ul>
     * <li>il numero di record di una
     * tabella il cui nome viene passato come argomento</li>
     * <li>oppure zero se nella tabella non sono presenti record.</li>
     * </ul></p>
     *
     * @param table nome della tabella di cui si vuol recuperare il numero di tuple
     * @return <code>int</code> - un intero che rappresenta il numero di tuple trovato, oppure zero se non sono stati trovati record
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method", "null" })
    public int getCount(String table)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            int count = NOTHING;
            String query = SELECT_COUNT + table;
            con = col_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }  catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare il numero di tuple di " + table + ".\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }
    
    
    /**
     * <p>Restituisce un valore boolean <code>true</code> se esiste un file
     * di dato nome, in data entit&agrave; e dato attributo, 
     * tutti passati come argomenti.</p>
     *
     * @param nomeEntita prefisso del nome dell'entit&agrave; in cui verificare l'esistenza dell'allegato
     * @param nomeAttributo suffisso del nome dell'entit&agrave; in cui verificare l'esistenza dell'allegato
     * @param nomeFile nome del file di cui verificare la presenza nell'entit&agrave; di nome ricavabile dai parametri
     * @return <code>boolean</code> - true se il nome del file &egrave; gi&agrave; presente, false altrimenti
     * @throws WebStorageException se si verifica un problema di tipo SQL o in qualche puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public boolean existsFileName(String nomeEntita, 
                                  String nomeAttributo,
                                  String nomeFile)
                           throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String table = nomeEntita + "_" + nomeAttributo;        
        String query =
                "SELECT " +  table + ".*" +
                "  FROM "  + table +
                "   WHERE file = ?";
        try {
            con = col_manager.getConnection();
            try {
                pst = con.prepareStatement(query);
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nel puntamento della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            }
            pst.clearParameters();
            pst.setString(1, nomeFile);
            rs = pst.executeQuery();
            // Il nome esiste già
            if (rs.next()) {
                return true;
            }
            // Il nome non esiste
            return false;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema nella query.\n";
            LOG.severe(msg); 
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg); 
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce il primo valore trovato data una query 
     * passata come parametro</p>
     *
     * @param query da eseguire
     * @return <code>String</code> - stringa restituita
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public String get(String query)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String value = null;
        try {
            con = col_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                value = rs.getString(1);
            }
            return value;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare un valore.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce un CodeBean contenente la password criptata e il seme
     * per poter verificare le credenziali inserite dall'utente.</p>
     *
     * @param username   username della persona che ha richiesto il login
     * @return <code>CodeBean</code> - CodeBean contenente la password criptata e il seme
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public CodeBean getEncryptedPassword(String username)
                                  throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean password = null;
        int nextInt = 0;
        try {
            con = col_manager.getConnection();
            pst = con.prepareStatement(GET_ENCRYPTEDPASSWORD);
            pst.clearParameters();
            pst.setString(++nextInt, username);
            rs = pst.executeQuery();
            if (rs.next()) {
                password = new CodeBean();
                BeanUtil.populate(password, rs);
            }
            return password;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce un PersonBean rappresentante un utente loggato.</p>
     *
     * @param username  username della persona che ha eseguito il login
     * @param password  password della persona che ha eseguito il login
     * @return <code>PersonBean</code> - PersonBean rappresentante l'utente loggato
     * @throws it.rol.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.rol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "null", "static-method" })
    public PersonBean getUser(String username,
                              String password)
                       throws WebStorageException, AttributoNonValorizzatoException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1 = null;
        PersonBean usr = null;
        int nextInt = 0;
        Vector<CodeBean> vRuoli = new Vector<>();
        try {
            con = col_manager.getConnection();
            pst = con.prepareStatement(GET_USR);
            pst.clearParameters();
            pst.setString(++nextInt, username);
            pst.setString(++nextInt, password);
            pst.setString(++nextInt, password);
            rs = pst.executeQuery();
            if (rs.next()) {
                usr = new PersonBean();
                BeanUtil.populate(usr, rs);
                // Se ha trovato l'utente, ne cerca il ruolo
                pst = null;
                pst = con.prepareStatement(GET_RUOLOUTENTE);
                pst.clearParameters();
                pst.setString(1, username);
                rs1 = pst.executeQuery();
                while(rs1.next()) {
                    CodeBean ruolo = new CodeBean();
                    BeanUtil.populate(ruolo, rs1);
                    vRuoli.add(ruolo);
                }
                usr.setRuoli(vRuoli);
            }
            // Just tries to engage the Garbage Collector
            pst = null;
            // Get Out
            return usr;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Problema in una conversione di tipi nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + cce.getMessage(), cce);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Verifica se per l'utente loggato esiste una tupla che indica
     * un precedente login.
     * <ul>
     * <li>Se non esiste una tupla per l'utente loggato, la inserisce.</li>
     * <li>Se esiste una tupla per l'utente loggato, la aggiorna.</li>
     * </ul>
     * In questo modo, il metodo gestisce nella tabella degli accessi
     * sempre l'ultimo accesso e non quelli precedenti.</p>
     *
     * @param username      login dell'utente (username usato per accedere)
     * @throws WebStorageException se si verifica un problema SQL o in qualche tipo di puntamento
     */
    public void manageAccess(String username)
                      throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            CodeBean accessRow = null;
            int nextParam = NOTHING;
            try {
                // Verifica se la login abbia già fatto un accesso
                pst = con.prepareStatement(GET_ACCESSLOG_BY_LOGIN);
                pst.clearParameters();
                pst.setString(++nextParam, username);
                rs = pst.executeQuery();
                if (rs.next()) {    // Esiste già un accesso: lo aggiorna
                    accessRow = new CodeBean();
                    BeanUtil.populate(accessRow, rs);
                    pst = null;
                    con.setAutoCommit(false);
                    pst = con.prepareStatement(UPDATE_ACCESSLOG_BY_USER);
                    pst.clearParameters();
                    pst.setString(nextParam, username);
                    // Campi automatici: ora ultimo accesso, data ultimo accesso
                    pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                    pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                    pst.setInt(++nextParam, accessRow.getId());
                    pst.executeUpdate();
                    con.commit();
                } else {            // Non esiste un accesso: ne crea uno nuovo
                    // Chiude e annulla il PreparedStatement rimasto inutilizzato
                    pst.close();
                    pst = null;
                    // BEGIN;
                    con.setAutoCommit(false);
                    pst = con.prepareStatement(INSERT_ACCESSLOG_BY_USER);
                    pst.clearParameters();
                    int nextVal = getMax("access_log") + 1;
                    pst.setInt(nextParam, nextVal);
                    pst.setString(++nextParam, username);
                    pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate())));
                    pst.setTime(++nextParam, Utils.getCurrentTime());
                    pst.executeUpdate();
                    // END;
                    con.commit();
                }
                String msg = "Si e\' loggato l\'utente: " + username +
                             " in data:" + Utils.format(Utils.getCurrentDate()) +
                             " alle ore:" + Utils.getCurrentTime() +
                             ".\n";
                LOG.info(msg);
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Probabile problema nel recupero dell'id dell\'ultimo accesso\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } catch (NumberFormatException nfe) {
                String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + nfe.getMessage(), nfe);
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage(), npe);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }

}
