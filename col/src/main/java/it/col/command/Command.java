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

import javax.servlet.http.HttpServletRequest;

import it.col.bean.CommandBean;
import it.col.exception.CommandException;


/**
 * Command è una semplice interfaccia per realizzare un'applicazione
 * web servlet-centrica.
 *
 * Nell'approccio servlet-centrico esiste una servlet centrale che
 * gestisce la logica dell'applicazione. Ogni azione del web è
 * realizzata da una classe specifica e l'interazione tra la
 * servlet-centrale e le classi avviene tramite questa
 * interfaccia. Questo approccio è anche detto 'command pattern'.
 *
 * @author Roberto Posenato
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public interface Command {

    /**
     * Esegue tutte le operazioni necessarie per recuperare le informazioni
     * per la visualizzazione. Tutte le informazioni devono essere inserite
     * nella sessione o in request.
     *
     * @param req HttpServletRequest
     * @throws CommandException incapsula tutte le possibili eccezioni incontrate dalle classi implementanti
     */
    public void execute(HttpServletRequest req)
    throws CommandException;


    /**
     * init supplies a constructor that can't be used
     * directly. Remember that almost of the implementing classes are
     * instancied by the Class.newInstance().
     *
     * @param voce a ItemBean containing al the useful information for instantiation
     * @throws CommandException incapsulate each exception occurred in init by implementing classes
     */
    public void init(CommandBean voce)
    throws CommandException;
}
