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

/**
 * <p>Package contenente eccezioni personalizzate dell'applicazione.</li> 
 * <p>Tutte le eccezioni personalizzate devono sempre avere <code>Throwable</code> 
 * come superclasse, altrimenti esse non definiranno un'eccezione.<br />
 * Sebbene una eccezione personalizzata possa essere derivata da una qualsiasi
 * delle eccezioni standard, la miglior strategia &egrave; derivarla direttamente
 * dalla classe <code>Exception</code>.<br />
 * Ci&ograve; permetter&agrave; al compilatore di tener traccia di dove tali
 * eccezioni siano lanciate nel flusso del programma, e di identificare se esse
 * devono essere catturate oppure dichiarate come propagabili in un metodo.<br />
 * Se si usasse <code>RuntimeException</code> o una delle sue sottoclassi, il 
 * controllo del compilatore nei blocchi <code>catch</code> per le eccezioni
 * personalizzate sarebbe soppresso.</p>  
 * 
 * <code>Elementi inclusi nel package:
 * <ul>
 * <li>AttributoNonValorizzatoException</li>
 * <li>CommandException</li>
 * <li>NotFoundException</li>
 * <li>WebStorageException</li>
 * </ul></code>
 * 
 * <p>Created on Wed Jul  9 10:04:58 AM CEST 2025</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */

package it.col.exception;
