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
     * <p>Estrae i ruoli giuridici di una persona
     * avente login passato come parametro,
     * assumendo che sulla login ci sia un vincolo di UNIQUE.</p>
     */
    public static final String GET_RUOLI =
            "SELECT " +
            "       PR.id_persona       AS \"id\"" +
            "   ,   PR.codice_csa       AS \"nome\"" +
            "   ,   PR.informativa      AS \"informativa\"" +
            "   FROM persona_ruolo PR" +
            "   ,   persona P" +        // CLASSIC JOIN, for once
            "   WHERE PR.id_persona = P.id" +
            "       AND PR.id_persona = ? ";

    /**
     * <p>Estrae il ruolo applicativo di un utente
     * avente login passato come parametro,
     * assumendo che sulla login ci sia un vincolo di UNIQUE.</p>
     */
    public static final String GET_RUOLO =
            "SELECT " +
            "       RA.id               AS \"id\"" +
            "   ,   RA.nome             AS \"nome\"" +
            "   ,   RA.informativa      AS \"informativa\"" +
            "   ,   RA.ordinale         AS \"ordinale\"" +
            "   FROM ruolo_applicativo RA " +
            "       INNER JOIN usr U ON U.id_ruolo = RA.id" +
            "   WHERE U.login = ? ";
    
    /**
     * <p>Estrae i gruppi di appartenenza di un utente
     * avente id passato come parametro.</p>
     */
    public static final String GET_GRUPPI =
            "SELECT DISTINCT" +
            "       GR.id               AS \"id\"" +
            "   ,   GR.nome             AS \"nome\"" +
            "   ,   GR.informativa      AS \"informativa\"" +
            "   ,   GR.ordinale         AS \"ordiale\"" +
            "   FROM grp GR" +
            "       INNER JOIN belongs ON belongs.id_grp = GR.id" +
            "       INNER JOIN usr ON belongs.id_usr = usr.id" +
            "   WHERE usr.id = ? ";
    
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
     *                    Query di Selezione "applicative"                    *
     * ********************************************************************** */
    /**
     * <p>Estrae le convenzioni in stato attivo.</p>
     */
    public static final String GET_CONVENTIONS =
            "SELECT DISTINCT" +
            "       C.id                    AS \"id\"" +
            "   ,   C.titolo                AS \"titolo\"" +
            "   ,   C.informativa           AS \"informativa\"" +
            "   ,   C.ordinale              AS \"ordinale\"" +
            "   ,   C.note                  AS \"note\"" +
            "   ,   C.data_approvazione     AS \"dataApprovazione\"" +
            "   ,   C.nota_approvazione     AS \"notaApprovazione\"" +
            "   ,   C.data_approvazione2    AS \"dataApprovazione2\"" +
            "   ,   C.nota_approvazione2    AS \"notaApprovazione2\"" +
            "   ,   C.data_sottoscrizione   AS \"dataSottoscrizione\"" +
            "   ,   C.nota_sottoscrizione   AS \"notaSottoscrizione\"" +
            "   ,   C.data_scadenza         AS \"dataScadenza\"" +
            "   ,   C.nota_scadenza         AS \"notaScadenza\"" +
            "   ,   C.num_repertorio        AS \"numRepertorio\"" +
            "   ,   C.carico_bollo          AS \"caricoBollo\"" +
            "   ,   C.bollo_pagato          AS \"pagato\"" +
            "   ,   C.data_ultima_modifica  AS \"dataUltimaModifica\"" +
            "   ,   C.ora_ultima_modifica   AS \"oraUltimaModifica\"" +
            "   ,   C.id_usr_ultima_modifica                                    AS \"idUsrUltimaModifica\"" +
            "   ,   (SELECT nome FROM tipo_convenzione WHERE id = C.id_tipo)    AS \"tipo\"" +
            "   FROM convenzione C" +
            "   WHERE C.id_stato = (SELECT id FROM stato_convenzione WHERE nome = 'ATTIVO')" +
            "       AND C.id IN (SELECT CG.id_convenzione FROM convenzione_grp CG WHERE CG.id_grp = ANY(?))" +
            "   ORDER BY C.ordinale, C.data_scadenza DESC";
    
    /**
     * <p>Estrae le convenzioni in stato attivo entro un intervallo considerato.</p>
     */
    public static final String GET_CONVENTIONS_BY_DATES =
            "SELECT DISTINCT" +
            "       C.id                    AS \"id\"" +
            "   ,   C.titolo                AS \"titolo\"" +
            "   ,   C.informativa           AS \"informativa\"" +
            "   ,   C.ordinale              AS \"ordinale\"" +
            "   ,   C.note                  AS \"note\"" +
            "   ,   C.data_approvazione     AS \"dataApprovazione\"" +
            "   ,   C.nota_approvazione     AS \"notaApprovazione\"" +
            "   ,   C.data_approvazione2    AS \"dataApprovazione2\"" +
            "   ,   C.nota_approvazione2    AS \"notaApprovazione2\"" +
            "   ,   C.data_sottoscrizione   AS \"dataSottoscrizione\"" +
            "   ,   C.nota_sottoscrizione   AS \"notaSottoscrizione\"" +
            "   ,   C.data_scadenza         AS \"dataScadenza\"" +
            "   ,   C.nota_scadenza         AS \"notaScadenza\"" +
            "   ,   C.num_repertorio        AS \"numRepertorio\"" +
            "   ,   C.data_ultima_modifica  AS \"dataUltimaModifica\"" +
            "   ,   C.ora_ultima_modifica   AS \"oraUltimaModifica\"" +
            "   ,   C.id_usr_ultima_modifica                                    AS \"idUsrUltimaModifica\"" +
            "   ,   (SELECT nome FROM tipo_convenzione WHERE id = C.id_tipo)    AS \"tipo\"" +
            "   FROM convenzione C" +
            "   WHERE C.id_stato = (SELECT id FROM stato_convenzione WHERE nome = 'ATTIVO')" +
            "       AND C.id IN (SELECT CG.id_convenzione FROM convenzione_grp CG WHERE CG.id_grp = ANY(?))" +
            "       AND (C.data_scadenza > ? AND C.data_scadenza < ?)" + 
            "   ORDER BY C.data_scadenza, C.titolo";
    
    /**
     * <p>Estrae una convenzione di dato id.</p>
     */
    public static final String GET_CONVENTION =
            "SELECT " +
            "       C.id                    AS \"id\"" +
            "   ,   C.titolo                AS \"titolo\"" +
            "   ,   C.informativa           AS \"informativa\"" +
            "   ,   C.ordinale              AS \"ordinale\"" +
            "   ,   C.note                  AS \"note\"" +
            "   ,   C.data_approvazione     AS \"dataApprovazione\"" +
            "   ,   C.nota_approvazione     AS \"notaApprovazione\"" +
            "   ,   C.data_approvazione2    AS \"dataApprovazione2\"" +
            "   ,   C.nota_approvazione2    AS \"notaApprovazione2\"" +
            "   ,   C.data_sottoscrizione   AS \"dataSottoscrizione\"" +
            "   ,   C.nota_sottoscrizione   AS \"notaSottoscrizione\"" +
            "   ,   C.data_scadenza         AS \"dataScadenza\"" +
            "   ,   C.nota_scadenza         AS \"notaScadenza\"" +
            "   ,   C.num_repertorio        AS \"numRepertorio\"" +
            "   ,   C.carico_bollo          AS \"caricoBollo\"" +
            "   ,   C.bollo_pagato          AS \"pagato\"" +
            "   ,   C.data_ultima_modifica  AS \"dataUltimaModifica\"" +
            "   ,   C.ora_ultima_modifica   AS \"oraUltimaModifica\"" +
            "   ,   C.id_usr_ultima_modifica                                    AS \"idUsrUltimaModifica\"" +
            "   ,   (SELECT nome FROM tipo_convenzione WHERE id = C.id_tipo)    AS \"tipo\"" +
            "   FROM convenzione C" +
            "   WHERE C.id = ?";
    
    /**
     * <p>Estrae le convenzioni associate a un contraente di dato id.</p>
     */
    public static final String GET_CONVENTIONS_BY_CONTRACTOR =
            "SELECT DISTINCT" +
            "       C.id                    AS \"id\"" +
            "   ,   C.titolo                AS \"titolo\"" +
            "   ,   C.informativa           AS \"informativa\"" +
            "   ,   C.ordinale              AS \"ordinale\"" +
            "   ,   C.note                  AS \"note\"" +
            "   ,   C.data_approvazione     AS \"dataApprovazione\"" +
            "   ,   C.nota_approvazione     AS \"notaApprovazione\"" +
            "   ,   C.data_approvazione2    AS \"dataApprovazione2\"" +
            "   ,   C.nota_approvazione2    AS \"notaApprovazione2\"" +
            "   ,   C.data_sottoscrizione   AS \"dataSottoscrizione\"" +
            "   ,   C.nota_sottoscrizione   AS \"notaSottoscrizione\"" +
            "   ,   C.data_scadenza         AS \"dataScadenza\"" +
            "   ,   C.nota_scadenza         AS \"notaScadenza\"" +
            "   ,   C.num_repertorio        AS \"numRepertorio\"" +
            "   ,   C.carico_bollo          AS \"caricoBollo\"" +
            "   ,   C.bollo_pagato          AS \"pagato\"" +
            "   ,   C.data_ultima_modifica  AS \"dataUltimaModifica\"" +
            "   ,   C.ora_ultima_modifica   AS \"oraUltimaModifica\"" +
            "   ,   C.id_usr_ultima_modifica                                    AS \"idUsrUltimaModifica\"" +
            "   ,   (SELECT nome FROM tipo_convenzione WHERE id = C.id_tipo)    AS \"tipo\"" +
            "   FROM convenzione C" +
            "       INNER JOIN contraente_convenzione CC ON CC.id_convenzione = C.id" +
            "   WHERE CC.id_contraente = ?" +
            "   ORDER BY C.ordinale, C.titolo";
    
    /**
     * <p><dl>
     * <dt>Estrae tutti i contraenti salvo quelli collegati a una data convenzione</dt>
     * <dd>se viene passato l'id della convenzione sul primo e secondo parametro</dd>
     * <dt>altrimenti estrae tutti i contraenti <em>tout-court</em></dt>
     * <dd>se viene passato un valore qualunque sul primo parametro e -1 sul secondo parametro.</dd>
     * </dl>
     * Esempio:<br><pre>
     * SELECT P.nome FROM contraente P 
     * WHERE P.id NOT IN
     *      (SELECT CC.id_contraente FROM contraente_convenzione CC WHERE CC.id_convenzione = 58)
     * OR -1 = 58</pre>
     * restituisce 128 contraenti mentre con:<pre>
     * OR -1 = -1</pre> 
     * restituisce 133 contraenti (tutti).</p>
     * <p>Si poteva anche implementare con:<pre>
     * SELECT P.nome FROM contraente P 
     * WHERE P.id NOT IN
     *      (SELECT CC.id_contraente FROM contraente_convenzione CC WHERE CC.id_convenzione = 58)
     * OR true = false</pre>
     * oppure (rispettivamente)<pre>
     * OR true = true</pre>
     * ma uso -1 = -1 per chiarezza (o, forse, per tradizione...).
     * </p>
     */
    public static final String GET_CONTRACTORS =
            "SELECT DISTINCT" +
            "       P.id                    AS \"id\"" +
            "   ,   P.nome                  AS \"nome\"" +
            "   ,   P.informativa           AS \"informativa\"" +
            "   ,   P.ordinale              AS \"ordinale\"" +
            "   ,   P.codice_fiscale        AS \"codiceFiscale\"" +
            "   ,   P.partita_iva           AS \"partitaIva\"" +
            "   ,   P.email                 AS \"email\"" +
            "   ,   (SELECT nome FROM tipo_contraente WHERE id = P.id_tipo)    AS \"note\"" +
            "   FROM contraente P" +
            "   WHERE P.id NOT IN " +
            "        (SELECT CC.id_contraente FROM contraente_convenzione CC WHERE CC.id_convenzione = ?)" +  
            "         OR -1 = ?" +
            "   ORDER BY P.ordinale, P.nome";
    
    /**
     * <p>Estrae un contraente di dato id</p>
     */
    public static final String GET_CONTRACTOR =
            "SELECT DISTINCT" +
            "       P.id                    AS \"id\"" +
            "   ,   P.nome                  AS \"nome\"" +
            "   ,   P.informativa           AS \"informativa\"" +
            "   ,   P.ordinale              AS \"ordinale\"" +
            "   ,   P.codice_fiscale        AS \"codiceFiscale\"" +
            "   ,   P.partita_iva           AS \"partitaIva\"" +
            "   ,   P.email                 AS \"email\"" +
            "   ,   (SELECT nome FROM tipo_contraente WHERE id = P.id_tipo)    AS \"note\"" +
            "   FROM contraente P" +
            "   WHERE P.id = ?";
    
    /**
     * <p>Estrae i contraenti collegati ad una data convenzione.</p>
     */
    public static final String GET_CONTRACTORS_BY_CONVENTION =
            "SELECT DISTINCT" +
            "       P.id                    AS \"id\"" +
            "   ,   P.nome                  AS \"nome\"" +
            "   ,   P.informativa           AS \"informativa\"" +
            "   ,   P.ordinale              AS \"ordinale\"" +
            "   ,   P.codice_fiscale        AS \"codiceFiscale\"" +
            "   ,   P.partita_iva           AS \"partitaIva\"" +
            "   ,   P.email                 AS \"email\"" +
            "   ,   (SELECT nome FROM tipo_contraente WHERE id = P.id_tipo)    AS \"note\"" +
            "   FROM contraente P" +
            "       INNER JOIN contraente_convenzione CC ON CC.id_contraente = P.id" +
            "   WHERE CC.id_convenzione = ?" +
            "   ORDER BY P.ordinale, P.nome";
    
    /**
     * <p>Estrae le tipologie.</p>
     */
    public static final String GET_CONVENTION_TYPES =
            "SELECT DISTINCT" +
            "       T.id                    AS \"id\"" +
            "   ,   T.nome                  AS \"nome\"" +
            "   ,   T.informativa           AS \"informativa\"" +
            "   ,   T.ordinale              AS \"ordinale\"" +
            "   FROM tipo_convenzione T" +
            "   ORDER BY T.ordinale, T.nome";
    
    /**
     * <p>Estrae le finalit&agrave;.</p>
     */
    public static final String GET_CONVENTION_SCOPES =
            "SELECT DISTINCT" +
            "       F.id                    AS \"id\"" +
            "   ,   F.nome                  AS \"nome\"" +
            "   ,   F.informativa           AS \"informativa\"" +
            "   ,   F.ordinale              AS \"ordinale\"" +
            "   FROM finalita F" +
            "   ORDER BY F.ordinale, F.nome";
    
    /**
     * <p>Estrae le finalit&agrave; di una data convenzione.</p>
     */
    public static final String GET_SCOPES_BY_CONVENTION =
            "SELECT DISTINCT" +
            "       F.id                    AS \"id\"" +
            "   ,   F.nome                  AS \"nome\"" +
            "   ,   F.informativa           AS \"informativa\"" +
            "   ,   F.ordinale              AS \"ordinale\"" +
            "   FROM finalita F" +
            "       INNER JOIN convenzione_finalita CF ON CF.id_finalita = F.id" +
            "   WHERE CF.id_convenzione = ?" +
            "   ORDER BY F.ordinale, F.nome";
    
    /**
     * <p>Estrae i gruppi cui &egrave; associata una data convenzione.</p>
     */
    public static final String GET_GROUPS_BY_CONVENTION =
            "SELECT DISTINCT" +
            "       CG.id_grp               AS \"id\"" +
            "   FROM convenzione_grp CG" +
            "   WHERE CG.id_convenzione = ?" +
            "   ORDER BY CG.id_grp";
    
    /* ************************************************************************ *
     *  Interfacce di metodi che costruiscono dinamicamente Query di Selezione  *
     *    (in taluni casi non si riesce a prestabilire la query ma questa va    *
     *      assemblata in funzione dei parametri ricevuti)                      *
     * ************************************************************************ */

    /**
     * <p>Costruisce dinamicamente la query che seleziona le convenzioni 
     * sulla base dei parametri di ricerca immessi
     * 
     * @param type  tipologia selezionata
     * @param scope finalit&agrave; selezionata
     * @param key   chiave/i di ricerca immesse
     * @return <code>String</code> - la query che seleziona le convenzioni desiderate
     */
    public String getQueryConventionsByKeys(String type, String scope, String key);
    
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
    
    /**
     * <p>Query per inserimento della relazione tra convenzione e contraente.</p>
     */
    public static final String INSERT_CONVENTION_CONTRACTOR =
            "INSERT INTO contraente_convenzione" +
            "   (   id_convenzione" +
            "   ,   id_contraente" +
            "   ,   data_ultima_modifica" +
            "   ,   ora_ultima_modifica" +
            "   ,   id_usr_ultima_modifica)" +
            "   VALUES (? " +          // id_convenzione
            "   ,       ? " +          // id_contraente
            "   ,       ? " +          // data_ultima_modifica
            "   ,       ? " +          // ora_ultima_modifica
            "   ,       ?)" ;          // id_usr_ultima_modifica
    
    /**
     * <p>Query per inserimento della relazione tra convenzione e finalità.</p>
     */
    public static final String INSERT_CONVENTION_SCOPE =
            "INSERT INTO convenzione_finalita" +
            "   (   data_ultima_modifica" +
            "   ,   ora_ultima_modifica" +
            "   ,   id_usr_ultima_modifica" +
            "   ,   id_convenzione" +
            "   ,   id_finalita)" +
            "   VALUES (? " +          // data_ultima_modifica
            "   ,       ? " +          // ora_ultima_modifica
            "   ,       ? " +          // id_usr_ultima_modifica
            "   ,       ? " +          // id_convenzione
            "   ,       ?)" ;          // id_finalita

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

    /**
     * <p>Query per aggiornamento di una convenzione di dato id.</p>
     */
    public static final String UPDATE_CONVENTION =
            "UPDATE convenzione" +
            "   SET titolo  = ?" +
            "   ,   num_repertorio = ?" +                    
            "   ,   informativa = ?" +
            "   ,   note = ?" +
            "   ,   data_approvazione = ?" +
            "   ,   nota_approvazione = ?" +
            "   ,   data_approvazione2 = ?" +
            "   ,   nota_approvazione2 = ?" +
            "   ,   data_sottoscrizione = ?" +
            "   ,   nota_sottoscrizione = ?" +
            "   ,   data_scadenza = ?" +
            "   ,   nota_scadenza = ?" +
            "   ,   carico_bollo = ?" +
            "   ,   bollo_pagato = ?" +
            "   ,   data_ultima_modifica = ?" +
            "   ,   ora_ultima_modifica = ?" +
            "   ,   id_usr_ultima_modifica = ?" +
            "   WHERE id = ?";

    /* ********************************************************************** *
     *                         Query di eliminazione                          *
     * ********************************************************************** */

    /**
     * <p>Query per la cancellazione delle relazioni tra una convenzione e le sue finalità.</p>
     */
    public static final String DELETE_CONVENTION_SCOPE =
            "DELETE FROM convenzione_finalita" +
            "   WHERE id_convenzione = ? ";
    
}
