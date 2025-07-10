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

import it.col.util.Constants;


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


}
