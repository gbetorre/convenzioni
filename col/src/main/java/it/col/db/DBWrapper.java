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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import it.col.bean.BeanUtil;
import it.col.bean.CodeBean;
import it.col.bean.CommandBean;
import it.col.bean.Convenzione;
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
                    String msg = "Connessione al database in stato inconsistente!\nLa connessione vale " + con + "\nIl database potrebbe essere non raggiugibile via rete.\n";
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
    @SuppressWarnings({ "static-method" })
    public int getMax(String table)
               throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                int count = 0;
                String query = SELECT_MAX_ID + table;
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
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
    @SuppressWarnings({ "static-method" })
    public int getMin(String table)
               throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                int count = 0;
                String query = SELECT_MIN_ID + table;
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
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
    @SuppressWarnings({ "static-method" })
    public CodeBean getEncryptedPassword(String username)
                                  throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            CodeBean password = null;
            int nextInt = 0;
            try {
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }


    /**
     * <p>Restituisce un PersonBean rappresentante un utente loggato.</p>
     *
     * @param username  username della persona che ha eseguito il login
     * @param password  password della persona che ha eseguito il login
     * @return <code>PersonBean</code> - PersonBean rappresentante l'utente loggato
     * @throws it.col.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.col.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "static-method" })
    public PersonBean getUser(String username,
                              String password)
                       throws WebStorageException, 
                              AttributoNonValorizzatoException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1, rs2, rs3 = null;
            PersonBean usr = null;
            int nextInt = NOTHING;
            Vector<CodeBean> vRuoli = new Vector<>();
            Vector<CodeBean> vGruppi = new Vector<>();
            String ruoloApplicativo = null;
            try {
                pst = con.prepareStatement(GET_USR);
                pst.clearParameters();
                pst.setString(++nextInt, username);
                pst.setString(++nextInt, password);
                pst.setString(++nextInt, password);
                rs = pst.executeQuery();
                if (rs.next()) {
                    usr = new PersonBean();
                    BeanUtil.populate(usr, rs);
                    // Aggiusta i nomi (prima lettera grande, altre piccole)
                    usr.setNome(Utils.formatNames(usr.getNome()));
                    // Recupera i ruoli giuridici
                    pst = null;
                    pst = con.prepareStatement(GET_RUOLI);
                    pst.clearParameters();
                    pst.setInt(1, usr.getId());
                    rs1 = pst.executeQuery();
                    while (rs1.next()) {
                        CodeBean ruolo = new CodeBean();
                        BeanUtil.populate(ruolo, rs1);
                        vRuoli.add(ruolo);
                    }
                    usr.setRuoli(vRuoli);
                    // Recupera il ruolo applicativo
                    pst = null;
                    pst = con.prepareStatement(GET_RUOLO);
                    pst.clearParameters();
                    pst.setString(1, username);
                    rs2 = pst.executeQuery();
                    if (rs2.next()) {
                        CodeBean ruolo = new CodeBean();
                        BeanUtil.populate(ruolo, rs2);
                        ruoloApplicativo = (ruolo.getNome() != null) ? ruolo.getNome() : ND;
                    }
                    usr.setRuolo(ruoloApplicativo);
                    // Recupera i gruppi dell'utente
                    pst = null;
                    pst = con.prepareStatement(GET_GRUPPI);
                    pst.clearParameters();
                    pst.setInt(1, usr.getUsrId());
                    rs3 = pst.executeQuery();
                    while (rs3.next()) {
                        CodeBean gruppo = new CodeBean();
                        BeanUtil.populate(gruppo, rs3);
                        vGruppi.add(gruppo);
                    }
                    usr.setGruppi(vGruppi);
                }
                // Try to engage the Garbage Collector
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }

    
    /**
     * <p>Restituisce la lista delle convenzioni attive.</p>
     *
     * @param user utente che ha effettuato la richiesta
     * @return <code>ArrayList&lt;Convenzione&gt;</code> - lista convenzioni trovate
     * @throws it.col.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.col.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<Convenzione> getConventions(PersonBean user)
                                          throws WebStorageException, 
                                                 AttributoNonValorizzatoException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1 = null;
            int nParam = NOTHING; 
            Convenzione c = null;
            ArrayList<PersonBean> contraenti = null;
            ArrayList<Convenzione> convenzioni = new ArrayList<>();
            try {
                pst = con.prepareStatement(GET_CONVENTIONS);
                pst.clearParameters();
                // Per il momento, assume che l'utente abbia uno e un solo gruppo
                pst.setInt(++nParam, user.getGruppi().get(NOTHING).getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    c = new Convenzione();
                    BeanUtil.populate(c, rs);
                    // Recupera i contraenti collegati alla convenzione
                    contraenti = new ArrayList<>();
                    pst = null;
                    pst = con.prepareStatement(GET_CONTRACTORS_BY_CONVENTION);
                    pst.clearParameters();
                    pst.setInt(1, c.getId());
                    rs1 = pst.executeQuery();
                    while (rs1.next()) {
                        PersonBean contraente = new PersonBean();
                        BeanUtil.populate(contraente, rs1);
                        contraenti.add(contraente);
                    }
                    // Li aggiunge alla convenzione
                    c.setContraenti(contraenti);
                    // Aggiunge la convenzione alla lista
                    convenzioni.add(c);
                }
                // Try to engage the Garbage Collector
                pst = null;
                // Get Out
                return convenzioni;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nella query delle convenzioni.\n";
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce la lista delle convenzioni attive entro un intervallo considerato.</p>
     *
     * @param user utente che ha effettuato la richiesta
     * @param start 
     * @param end 
     * @return <code>ArrayList&lt;Convenzione&gt;</code> - lista convenzioni trovate
     * @throws it.col.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.col.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<Convenzione> getConventions(PersonBean user,
                                                 Date start,
                                                 Date end)
                                          throws WebStorageException, 
                                                 AttributoNonValorizzatoException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1 = null;
            int nParam = NOTHING; 
            Convenzione c = null;
            ArrayList<PersonBean> contraenti = null;
            ArrayList<Convenzione> convenzioni = new ArrayList<>();
            try {
                pst = con.prepareStatement(GET_CONVENTIONS_BY_DATES);
                pst.clearParameters();
                // Per il momento, assume che l'utente abbia uno e un solo gruppo
                pst.setInt(++nParam, user.getGruppi().get(NOTHING).getId());
                // Non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                pst.setDate(++nParam, Utils.convert(Utils.convert(start))); 
                pst.setDate(++nParam, Utils.convert(Utils.convert(end)));
                rs = pst.executeQuery();
                while (rs.next()) {
                    c = new Convenzione();
                    BeanUtil.populate(c, rs);
                    // Recupera i contraenti collegati alla convenzione
                    contraenti = new ArrayList<>();
                    pst = null;
                    pst = con.prepareStatement(GET_CONTRACTORS_BY_CONVENTION);
                    pst.clearParameters();
                    pst.setInt(1, c.getId());
                    rs1 = pst.executeQuery();
                    while (rs1.next()) {
                        PersonBean contraente = new PersonBean();
                        BeanUtil.populate(contraente, rs1);
                        contraenti.add(contraente);
                    }
                    // Li aggiunge alla convenzione
                    c.setContraenti(contraenti);
                    // Aggiunge la convenzione alla lista
                    convenzioni.add(c);
                }
                // Try to engage the Garbage Collector
                pst = null;
                // Get Out
                return convenzioni;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nella query delle convenzioni.\n";
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce una convenzione di dato id.</p>
     *
     * @param user utente che ha effettuato la richiesta
     * @param idConvention identificativo della convenzione che si vuole recuperare
     * @return <code>Convenzione</code> - convenzione trovata
     * @throws it.col.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.col.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "static-method" })
    public Convenzione getConvention(PersonBean user,
                                     int idConvention)
                              throws WebStorageException, 
                                     AttributoNonValorizzatoException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1 = null;
            Convenzione c = null;
            ArrayList<PersonBean> contractors = new ArrayList<>();
            try {
                // TODO: Controllare i diritti dell'utente
                pst = con.prepareStatement(GET_CONVENTION);
                pst.clearParameters();
                pst.setInt(1, idConvention);
                rs = pst.executeQuery();
                if (rs.next()) {
                    c = new Convenzione();
                    BeanUtil.populate(c, rs);
                    // Recupera i contraenti collegati alla convenzione
                    pst = null;
                    pst = con.prepareStatement(GET_CONTRACTORS_BY_CONVENTION);
                    pst.clearParameters();
                    pst.setInt(1, idConvention);
                    rs1 = pst.executeQuery();
                    while (rs1.next()) {
                        PersonBean contractor = new PersonBean();
                        BeanUtil.populate(contractor, rs1);
                        contractors.add(contractor);
                    }
                    c.setContraenti(contractors);
                }
                // Try to engage the Garbage Collector
                pst = null;
                // Get Out
                return c;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nella query della convenzione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } catch (ClassCastException cce) {
                String msg = FOR_NAME + "Problema in una conversione di tipi.\n";
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce la lista completa dei contraenti oppure la lista
     * dei contraenti meno quelli gi&agrave; associati alla convenzione
     * passata come parametro, a seconda del valore del parametro getAll:
     * <dl>
     * <dt>se <code>getAll = true</code></dt>  
     * <dd>restituisce tutti i contraenti</dd>
     * <dt>se <code>getAll = false</code></dt> 
     * <dd>restituisce tutti i contraenti meno quelli
     * gi&agrave; associati alla convenzione di id conv.getId()</dd></dl></p>
     *
     * @param user utente che ha effettuato la richiesta
     * @param conv convenzione data
     * @param getAll se true il metodo restituisce tutti i contraenti indipendentemente da conv; se false, restituisce tutti meno quelli gia' associati a conv
     * @return <code>ArrayList&lt;PersonBean&gt;</code> - lista contraenti trovati
     * @throws it.col.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.col.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<PersonBean> getContractors(PersonBean user,
                                                Convenzione conv,
                                                boolean getAll)
                                         throws WebStorageException, 
                                                AttributoNonValorizzatoException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            int numParam = NOTHING; 
            PersonBean p = null;
            ArrayList<PersonBean> contraenti = new ArrayList<>();
            try {
                // TODO: Controllare i diritti dell'utente
                
                // Converte il flag in valore intero (per chiarezza)
                int getAllByClause = (getAll ? -1 : conv.getId());
                pst = con.prepareStatement(GET_CONTRACTORS);
                pst.clearParameters();
                pst.setInt(++numParam, conv.getId());
                pst.setInt(++numParam, getAllByClause);
                rs = pst.executeQuery();
                while (rs.next()) {
                    p = new PersonBean();
                    BeanUtil.populate(p, rs);
                    contraenti.add(p);
                }
                // Try to engage the Garbage Collector
                pst = null;
                // Get Out
                return contraenti;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nella query.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } catch (ClassCastException cce) {
                String msg = FOR_NAME + "Problema in una conversione di oggetti.\n";
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce le tipologie delle convenzioni.</p>
     *
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - lista tipologie trovate
     * @throws it.col.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<CodeBean> getTypes()
                                 throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            CodeBean type = null;
            ArrayList<CodeBean> types = new ArrayList<>();
            try {
                // TODO: Controllare i diritti dell'utente
                pst = con.prepareStatement(GET_CONVENTION_TYPES);
                pst.clearParameters();
                rs = pst.executeQuery();
                while (rs.next()) {
                    type = new CodeBean();
                    BeanUtil.populate(type, rs);
                    types.add(type);
                }
                // Try to engage the Garbage Collector
                pst = null;
                // Get Out
                return types;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nella query dei tipi di convenzione.\n";
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce le finalit&agrave; delle convenzioni.</p>
     *
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - lista tipologie trovate
     * @throws it.col.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method" })
    public ArrayList<CodeBean> getScopes()
                                  throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            CodeBean scope = null;
            ArrayList<CodeBean> scopes = new ArrayList<>();
            try {
                // TODO: Controllare i diritti dell'utente
                pst = con.prepareStatement(GET_CONVENTION_SCOPES);
                pst.clearParameters();
                rs = pst.executeQuery();
                while (rs.next()) {
                    scope = new Convenzione();
                    BeanUtil.populate(scope, rs);
                    scopes.add(scope);
                }
                // Try to engage the Garbage Collector
                pst = null;
                // Get Out
                return scopes;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nella query delle convenzioni.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } catch (ClassCastException cce) {
                String msg = FOR_NAME + "Problema in una conversione di tipi.\n";
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }


    /* ********************************************************** *
     *                    Metodi di INSERIMENTO                   *
     * ********************************************************** */
    
    /**
     * <p>Metodo per fare l'inserimento delle relazioni tra uno o pi&uacute; 
     * contraenti e una e una sola convenzione.</p>
     *  
     * @param user      utente loggato
     * @param params    mappa contenente i parametri di navigazione
     * @throws WebStorageException se si verifica un problema nel cast da String a Date, nell'esecuzione della query, nell'accesso al db o in qualche puntamento
     */
    @SuppressWarnings("static-method")
    public void insertConventionContractors(PersonBean user, 
                                            HashMap<String, LinkedHashMap<String, String>> params) 
                                     throws WebStorageException {
        try (Connection con = col_manager.getConnection()) {
            PreparedStatement pst = null;
            // Dizionario dei parametri contenente l'identificativo dei contraenti da associare
            LinkedHashMap<String, String> contractor = params.get(CONTRACTOR);
            // Indice di parametro
            int index = NOTHING;
            try {
                // Begin: ==>
                con.setAutoCommit(false);
                // TODO: Controllare se user è superuser
                pst = con.prepareStatement(INSERT_CONVENTION_CONTRACTOR);
                pst.clearParameters();
                try {
                    // Prepara i parametri per l'inserimento
                    int idConv = Integer.parseInt(contractor.get("conv"));
                    // Recupera il numero di contraenti
                    int nContr = Integer.parseInt(contractor.get("size"));
                    // Recupera gli id contraente
                    while (index < nContr) {
                        // Numero di parametro da passare alla query
                        int nextParam = NOTHING;
                        // Incrementa il suffisso della chiave
                        index++;
                        // Chiave associata a id del contraente
                        String key = CONTRACTOR + index;
                        // Valore id del contraente
                        int idCont = Integer.parseInt(contractor.get(key));
                        // Per ogni contraente trovato deve inserire 1 tupla
                        if (idCont > NOTHING) {
                            // === Id Convenzione === 
                            pst.setInt(++nextParam, idConv);
                            // === Id Contraente === 
                            pst.setInt(++nextParam, idCont);
                            // === Campi automatici: id utente, ora ultima modifica, data ultima modifica ===
                            pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                            pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                            pst.setInt(++nextParam, user.getUsrId());
                            // CR (Carriage Return) o 0DH
                            pst.addBatch();
                        }
                    }
                    // Execute the batch updates
                    int[] updateCounts = pst.executeBatch();
                    LOG.info(updateCounts.length + " relazioni in transazione attiva.\n");
                } catch (NumberFormatException nfe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di interi.\n" + nfe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, nfe);
                } catch (ClassCastException cce) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nella conversione di tipo.\n" + cce.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, cce);
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema nello scorrimento di liste.\n" + aiobe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, aiobe);
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Si e\' verificato un problema in un puntamento a null.\n" + npe.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, npe);
                } catch (Exception e) {
                    String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getMessage();
                    LOG.severe(msg);
                    throw new WebStorageException(msg, e);
                }
                // End: <==
                con.commit();
                pst.close();
                pst = null;
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Problema nel codice SQL o nella chiusura dello statement.\n";
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
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }

    
    /* ********************************************************** *
     *                  Metodi di AGGIORNAMENTO                   *
     * ********************************************************** */

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


    /* ********************************************************** *
     *                  Metodi di ELIMINAZIONE                    *
     * ********************************************************** */

}
