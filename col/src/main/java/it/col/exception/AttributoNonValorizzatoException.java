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

package it.col.exception;


/**
 * Questa eccezione viene ritornata quando si tenta di accedere
 * ad un attributo di un Bean che non è stato inizializzato
 * (attributo del Bean non letto dal Database o in generale non valorizzato)
 */
public class AttributoNonValorizzatoException extends Exception {
    
    /**
     * Necessario in quanto si espande Exception
     */
    private static final long serialVersionUID = 1L;

    
    /**
     * Costruttore da superclasse
     */
    public AttributoNonValorizzatoException() {
        super();
    }

    
    /**
     * Costruttore parametrizzato da superclasse
     * @param message un messaggio da mostare nello stdout (p.es. log o console)
     */
    public AttributoNonValorizzatoException(String message) {
        super(message);
    }

    
    /**
     * @param message   un messaggio da mostare nello stdout (e.g. log o console)
     * @param cause     l\'eccezione catturata, che viene passata alla superclasse tramite l\'incapsulamento in this 
     */
    public AttributoNonValorizzatoException(String message, Throwable cause) {
        super(message, cause);
    }

    
    /**
     * @param cause la sola eccezione catturata, che viene passata alla superclasse tramite incapsulamento in this
     */
    public AttributoNonValorizzatoException(Throwable cause) {
        super(cause);
    }
    
}
