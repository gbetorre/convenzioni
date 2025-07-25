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

import java.io.Serializable;


/**
 * <p>Query &egrave; l'interfaccia contenente tutte le query, 
 * &quot;secche&quot; e parametriche, della web-application &nbsp;
 * <code>COL-GECO (Convenzioni On Line-Gestione Convenzioni)</code>, 
 * tranne quelle composte a runtime da metodi implementati, 
 * di cui comunque dichiara l'interfaccia pubblica.<br>
 * Utilizza degli speciali marcatori (question marks) nei punti
 * in cui verranno passati i parametri, sfruttando il classico meccanismo 
 * del {@link java.sql.PreparedStatement}.</p>
 * <p>Definisce inoltre alcune costanti di utilit&agrave;.</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 * @see java.sql.PreparedStatement
 */
public interface Query extends Serializable {
    /* ********************************************************************** *
     *               Costanti convenzionali per valori interi                 *
     * ********************************************************************** */
    /**
     * <p>Costante parlante per valore da passare sul secondo argomento
     * in query strutturate in modo da considerare il primo se il secondo
     * vale NOTHING oppure il secondo se il primo vale NOTHING e il secondo
     * vale GET_ALL_BY_CLAUSE.</p>
     */
    public static final int GET_ALL_BY_CLAUSE = -1;
    /* ********************************************************************** *
     *               Costanti convenzionali per valori boolean                *
     * ********************************************************************** */
    /**
     * <p>Costante parlante per flag da passare sul secondo argomento
     * in query strutturate al fine di recuperare tutti i record, oppure
     * da utilizzare in metodi per strutturare specifiche condizioni.</p>
     */
    public static final boolean GET_ALL = true;
    /* ********************************************************************** *
     *                    Query di selezione "di servizio"                    *
     * ********************************************************************** */
    /**
     * <p>Estrae le classi Command previste per la/le applicazione/i.</p>
     */
    public static final String LOOKUP_COMMAND =
            "SELECT " +
            "       id                  AS \"id\"" +
            "   ,   nome                AS \"nomeReale\"" +
            "   ,   nome                AS \"nomeClasse\"" +
            "   ,   token               AS \"nome\"" +
            "   ,   labelweb            AS \"label\"" +
            "   ,   jsp                 AS \"pagina\"" +
            "   ,   informativa         AS \"informativa\"" +
            "  FROM command";

    /**
     * <p>Estrae l'id massimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MAX_ID =
            "SELECT " +
            "       MAX(id)             AS \"max\"" +
            "   FROM ";

    /**
     * <p>Estrae l'id minimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MIN_ID =
            "SELECT " +
            "       MIN(id)             AS \"min\"" +
            "   FROM ";
    
    /**
     * <p>Estrae il numero di record di una tabella definita nel chiamante</p>
     */
    public static final String SELECT_COUNT =
            "SELECT " +
            "       count(*)            AS \"n\"" +
            "   FROM ";
    
    /**
     * <p>Estrae l'utente con username e password passati come parametri.</p>
     */
    public static final String GET_USR =
            "SELECT " +
            "       U.id                AS \"usrId\"" +
            "   ,   P.id                AS \"id\"" +
            "   ,   P.nome              AS \"nome\"" +
            "   ,   P.cognome           AS \"cognome\"" +
            "   ,   P.sesso             AS \"sesso\"" +
            "   ,   P.data_nascita      AS \"dataNascita\"" +
            "   ,   P.codice_fiscale    AS \"codiceFiscale\"" +
            "   ,   P.email             AS \"email\"" +
            "   ,   P.cittadinanza      AS \"cittadinanza\"" +
            "   ,   P.note              AS \"note\"" +
            "   FROM usr U" +
            "       INNER JOIN persona P ON P.id = U.id_persona" +
            "   WHERE   login = ?" +
            "       AND (( passwd IS NULL OR passwd = ? ) " +
            "           AND ( passwdform IS NULL OR passwdform = ? ))";

    /**
     * <p>Estrae il ruolo di una persona
     * avente login passato come parametro,
     * assumendo che sulla login ci sia un vincolo di UNIQUE.</p>
     */
    public static final String GET_RUOLOUTENTE = "";
            // TODO
            
    /**
     * <p>Estrae identificativo tupla ultimo accesso, se esiste
     * per l'utente il cui username viene passato come parametro.</p>
     */
    public static final String GET_ACCESSLOG_BY_LOGIN =
            "SELECT " +
            "       A.id                AS  \"id\"" +
            "   FROM access_log A " +
            "   WHERE A.login = ? ";

    /**
     * <p>Estrae la password criptata e il seme dell'utente,
     * identificato tramite username, passato come parametro.</p>
     */
    public static final String GET_ENCRYPTEDPASSWORD =
            "SELECT " +
            "       U.passwdform        AS \"nome\"" +
            "   ,   U.salt              AS \"informativa\"" +
            "   FROM usr U" +
            "   WHERE U.login = ?";

    /* ********************************************************************** *
     *                            Query di Selezione                          *
     * ********************************************************************** */

    
    /* ************************************************************************ *
     *  Interfacce di metodi che costruiscono dinamicamente Query di Selezione  *
     *    (in taluni casi non si riesce a prestabilire la query ma questa va    *
     *      assemblata in funzione dei parametri ricevuti)                      *
     * ************************************************************************ */

    
    /* ********************************************************************** *
     *                         Query di inserimento                           *
     * ********************************************************************** */
    /**
     * <p>Query per inserimento dell'ultimo accesso al sistema.</p>
     */
    public static final String INSERT_ACCESSLOG_BY_USER =
            "INSERT INTO access_log" +
            "   (   id" +
            "   ,   login" +
            "   ,   data_ultimo_accesso" +
            "   ,   ora_ultimo_accesso )" +
            "   VALUES (? " +          // id
            "   ,       ? " +          // login
            "   ,       ? " +          // dataultimoaccesso
            "   ,       ?)" ;          // oraultimoaccesso

    /* ********************************************************************** *
     *                         Query di aggiornamento                         *
     * ********************************************************************** */
    /**
     * <p>Query per aggiornamento di ultimo accesso al sistema.</p>
     */
    public static final String UPDATE_ACCESSLOG_BY_USER =
            "UPDATE access_log" +
            "   SET login  = ?" +
            "   ,   data_ultimo_accesso = ?" +
            "   ,   ora_ultimo_accesso = ?" +
            "   WHERE id = ? ";



    /* ********************************************************************** *
     *                         Query di eliminazione                          *
     * ********************************************************************** */

    
}
