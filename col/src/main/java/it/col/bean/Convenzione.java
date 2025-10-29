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

package it.col.bean;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Time;

import it.col.exception.AttributoNonValorizzatoException;

/**
 * JavaBean representing an agreement
 */
public class Convenzione extends CodeBean {

    /** A serializable class must declare a static final serialVersionUID field */
    private static final long serialVersionUID = 7412922227541757849L;
    /** Title of the agreement */
    private String titolo;
    /** Furter information about the agreement */
    private String note;
    /** Approval date */
    private Date dataApprovazione;
    /** Notes about approval */
    private String notaApprovazione;
    /** Second approval date */
    private Date dataApprovazione2;
    /** Notes about second approval date */
    private String notaApprovazione2;
    /** Subscription date */
    private Date dataSottoscrizione;
    /** Subscription notes */
    private String notaSottoscrizione;
    /** Expiration date */
    private Date dataScadenza;
    /** Expiration notes */
    private String notaScadenza;
    /** Protocol number */
    private String numRepertorio;
    /** Last modified date */
    private Date dataUltimaModifica;
    /** Last modified time */
    private Time oraUltimaModifica;
    /** Last modified author */
    private int idUsrUltimaModifica;
    /** Type label */
    private String tipo;
    /** State label */
    private String stato;
    /** List of Contractors */
    private ArrayList<PersonBean> contraenti;

    
    /* ======================= Constructors ======================= */
    
    /**
     * Default constructor: initializing fields to default values.
     * - Objects (String, Date, LocalTime) to null; 
     * - Integers to -2 (conventional default);
     * - Booleans to false
     */
    public Convenzione() {
        super();
        this.titolo = null;
        this.note = null;
        this.dataApprovazione = null;
        this.notaApprovazione = null;
        this.dataApprovazione2 = null;
        this.notaApprovazione2 = null;
        this.dataSottoscrizione = null;
        this.notaSottoscrizione = null;
        this.dataScadenza = null;
        this.notaScadenza = null;
        this.numRepertorio = null;
        this.dataUltimaModifica = null;
        this.oraUltimaModifica = null;
        this.idUsrUltimaModifica = BEAN_DEFAULT_ID;
        this.tipo = null;
        this.stato = null;
        this.setContraenti(null);
    }

    
    /**
     * Parameterized constructor which calls setters for each field.
     * 
     * @param id agreement id
     * @param titolo agreement title
     * @param informativa agreement description
     * @param ordinale agreement ordering number
     * @param note further information
     * @param dataApprovazione date of approval
     * @param notaApprovazione notes about approval
     * @param dataApprovazione2 2nd date of approval
     * @param notaApprovazione2 notes about second date of approval
     * @param dataSottoscrizione date of subscription
     * @param notaSottoscrizione notes about subscription
     * @param dataScadenza expiration date
     * @param notaScadenza notes about expiration date
     * @param numRepertorio protocol number
     * @param dataUltimaModifica last date modified
     * @param oraUltimaModifica last time modified
     * @param idUsrUltimaModifica last user which modified
     * @param tipo label for tipology 
     * @param stato label for state of agreement 
     * @param contraenti list of contractors bound by the agreement
     */
    public Convenzione(int id, 
                       String titolo, 
                       String informativa, 
                       int ordinale,
                       String note,
                       Date dataApprovazione, 
                       String notaApprovazione,
                       Date dataApprovazione2, 
                       String notaApprovazione2,
                       Date dataSottoscrizione, 
                       String notaSottoscrizione,
                       Date dataScadenza, 
                       String notaScadenza,
                       String numRepertorio, 
                       Date dataUltimaModifica,
                       Time oraUltimaModifica, 
                       int idUsrUltimaModifica,
                       String tipo, 
                       String stato,
                       ArrayList<PersonBean> contraenti) {
        setId(id);
        setTitolo(titolo);
        setInformativa(informativa);
        setOrdinale(ordinale);
        setNote(note);
        setDataApprovazione(dataApprovazione);
        setNotaApprovazione(notaApprovazione);
        setDataApprovazione2(dataApprovazione2);
        setNotaApprovazione2(notaApprovazione2);
        setDataSottoscrizione(dataSottoscrizione);
        setNotaSottoscrizione(notaSottoscrizione);
        setDataScadenza(dataScadenza);
        setNotaScadenza(notaScadenza);
        setNumRepertorio(numRepertorio);
        setDataUltimaModifica(dataUltimaModifica);
        setOraUltimaModifica(oraUltimaModifica);
        setIdUsrUltimaModifica(idUsrUltimaModifica);
        setTipo(tipo);
        setStato(stato);
        setContraenti(contraenti);
    }


    /**
     * Parameterized constructor which calls the mother class
     * 
     * @param o object which to clone properties
     * @throws AttributoNonValorizzatoException if some mandatory properties is not correctly set
     */
    public Convenzione(it.col.bean.CodeBean o) 
                throws AttributoNonValorizzatoException {
        super(o);
    }

    
    /**
     * @param id agreement id
     * @param nome agreement name
     * @param informativa agreement description
     * @param ordinale agreement ordering number
     */
    public Convenzione(int id, String nome, String informativa, int ordinale) {
        super(id, nome, informativa, ordinale);
    }

    
    /* ======================= Getters / Setters ======================= */

    /** @return the title of the agreement */
    public String getTitolo() {
        return this.titolo;
    }

    /** @param titolo the title to set */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    
    
    /** @return further information about the agreement */
    public String getNote() {
        return this.note;
    }

    /** @param note the information to set */
    public void setNote(String note) {
        this.note = note;
    }
    

    /** @return the approval date */
    public Date getDataApprovazione() {
        return this.dataApprovazione;
    }

    /** @param dataApprovazione the approval date to set */
    public void setDataApprovazione(Date dataApprovazione) {
        this.dataApprovazione = dataApprovazione;
    }
    

    /** @return the approval note */
    public String getNotaApprovazione() {
        return this.notaApprovazione;
    }

    /** @param notaApprovazione the approval note to set */
    public void setNotaApprovazione(String notaApprovazione) {
        this.notaApprovazione = notaApprovazione;
    }

    
    /** @return the approvale 2nd date */
    public Date getDataApprovazione2() {
        return this.dataApprovazione2;
    }

    /** @param dataApprovazione2 the approval 2nd date to set */
    public void setDataApprovazione2(Date dataApprovazione2) {
        this.dataApprovazione2 = dataApprovazione2;
    }

    
    /** @return the approval 2nd notes */
    public String getNotaApprovazione2() {
        return this.notaApprovazione2;
    }

    /** @param notaApprovazione2 the approval 2nd notes to set */
    public void setNotaApprovazione2(String notaApprovazione2) {
        this.notaApprovazione2 = notaApprovazione2;
    }

    
    /** @return the subscription date */
    public Date getDataSottoscrizione() {
        return this.dataSottoscrizione;
    }

    /** @param dataSottoscrizione the subscription date to set */
    public void setDataSottoscrizione(Date dataSottoscrizione) {
        this.dataSottoscrizione = dataSottoscrizione;
    }

    
    /** @return the subscription notes */
    public String getNotaSottoscrizione() {
        return this.notaSottoscrizione;
    }

    /** @param notaSottoscrizione the subscription notes to set */
    public void setNotaSottoscrizione(String notaSottoscrizione) {
        this.notaSottoscrizione = notaSottoscrizione;
    }

    
    /** @return the expiration date */
    public Date getDataScadenza() {
        return this.dataScadenza;
    }

    /** @param dataScadenza the expiration date to set */
    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    
    /** @return the expiration notes */
    public String getNotaScadenza() {
        return this.notaScadenza;
    }

    /** @param notaScadenza the expiration notes to set */
    public void setNotaScadenza(String notaScadenza) {
        this.notaScadenza = notaScadenza;
    }

    
    /** @return the protocol number */
    public String getNumRepertorio() {
        return this.numRepertorio;
    }

    /** @param numRepertorio the protocol number to set */
    public void setNumRepertorio(String numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    
    /** @return the last modified date */
    public Date getDataUltimaModifica() {
        return this.dataUltimaModifica;
    }

    /** @param dataUltimaModifica the last modified date to set */
    public void setDataUltimaModifica(Date dataUltimaModifica) {
        this.dataUltimaModifica = dataUltimaModifica;
    }

    
    /** @return the last modified time */
    public Time getOraUltimaModifica() {
        return this.oraUltimaModifica;
    }

    /** @param oraUltimaModifica the last modified time to set */
    public void setOraUltimaModifica(Time oraUltimaModifica) {
        this.oraUltimaModifica = oraUltimaModifica;
    }

    
    /** @return the last modified author */
    public int getIdUsrUltimaModifica() {
        return this.idUsrUltimaModifica;
    }

    /** @param idUsrUltimaModifica the last modified id user to set */
    public void setIdUsrUltimaModifica(int idUsrUltimaModifica) {
        this.idUsrUltimaModifica = idUsrUltimaModifica;
    }

    /** @return the tipology of the agreement */
    public String getTipo() {
        return this.tipo;
    }

    /** @param tipo the tipology to set */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    
    /** @return the stato */
    public String getStato() {
        return this.stato;
    }

    /** @param stato the stato to set */
    public void setStato(String stato) {
        this.stato = stato;
    }


    /**
     * @return the contraenti
     */
    public ArrayList<PersonBean> getContraenti() {
        return contraenti;
    }

    /**
     * @param contraenti the contraenti to set
     */
    public void setContraenti(ArrayList<PersonBean> contraenti) {
        this.contraenti = contraenti;
    }
    
}
