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

import java.util.StringJoiner;

import it.col.util.Constants;
import it.col.util.Utils;


/**
 * <p>QueryImpl &egrave; l'implementazione dell'interfaccia {@link Query} 
 * la quale contiene, oltre alle query parametriche della della web-application 
 * &nbsp;<code>COL-GECO (Convenzioni On Line-Gestione Convenzioni)</code>,
 * anche l'interfaccia pubblica di alcuni metodi per la costruzione di
 * query a runtime &ndash; implementazione di cui si prende carico proprio
 * il presente oggetto.<br> 
 * Implementa pertanto alcuni metodi, dichiarati in quella, che costruiscono,
 * a runtime, query la cui esatta struttura non era possibile stabilire 
 * in anticipo (a meno di introdurre notevoli ridondanze e violare
 * il paradigma DRY - Don't Repeate Yourself).<br>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 * @see Query
 */
public class QueryImpl implements Query, Constants {

    
    /**
     * Auto-generated serial version ID: parent implements 
     * the Serializable interface.
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 7339151352217708748L;


    /** 
     * {@link Query#getQueryConventionsBySearch(String, String, String)} 
     * @see it.col.db.Query#getQueryConventionsBySearch(String, String, String)
     */
    @Override
    public String getQueryConventionsByKeys(String type, 
                                            String scope, 
                                            String key) {
        StringBuffer join = new StringBuffer();
        StringBuffer clause = new StringBuffer("WHERE");
        clause.append(BLANK_SPACE);
        if (type.equals(String.valueOf(NOTHING))) {
            //
        } else {
            clause.append("C.id_tipo = ")
                  .append(type)
                  .append(" AND ");
        }
        
        if (scope.equals(String.valueOf(NOTHING))) {
            //
        } else {
            join.append("INNER JOIN convenzione_finalita CF ON CF.id_convenzione = C.id");
            clause
                  .append("CF.id_finalita = ")
                  .append(scope)
                  .append(" AND ");
        }
        // The key is mandatory
        //clause.append("C.titolo ~* ANY(ARRAY[");
        String[] keys = Utils.tokenizeByComma(key);
        /* boilerplate code
        for (int i = 0; i < keys.length; i++) {
            clause.append("'")
                  .append(key)
                  .append("',");
        }
        clause.append("])");
            */
        StringJoiner joiner = new StringJoiner(COMMA);

        for (String k : keys) {
            joiner.add("'" + k + "'");
        }
        clause.append("C.titolo ~* ANY(ARRAY[" + joiner.toString() + "])")
              .append(" OR ")
              .append("C.informativa ~* ANY(ARRAY[" + joiner.toString() + "])")
              .append(" OR ")
              .append("C.note ~* ANY(ARRAY[" + joiner.toString() + "])")
              .append(" OR ")
              .append("C.nota_approvazione ~* ANY(ARRAY[" + joiner.toString() + "])")
              .append(" OR ")
              .append("C.nota_approvazione2 ~* ANY(ARRAY[" + joiner.toString() + "])")
              .append(" OR ")
              .append("C.nota_sottoscrizione ~* ANY(ARRAY[" + joiner.toString() + "])")
              .append(" OR ")
              .append("C.nota_scadenza ~* ANY(ARRAY[" + joiner.toString() + "])")
              .append(" OR ")
              .append("C.num_repertorio ~* ANY(ARRAY[" + joiner.toString() + "])");
        // Query
        final String GET_CONVENTIONS_BY_KEYS =
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
                    "   FROM convenzione C " +
                        join +
                        clause + 
                    "   ORDER BY C.ordinale, C.titolo";
        return GET_CONVENTIONS_BY_KEYS;
    }
    
}
