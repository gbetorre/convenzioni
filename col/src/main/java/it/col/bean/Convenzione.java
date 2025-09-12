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

import java.util.Date;
import java.time.LocalTime;

import it.col.exception.AttributoNonValorizzatoException;

/**
 * 
 */
public class Convenzione extends CodeBean {

    private String titolo;
    private String informativa;
    private Integer ordinale;
    private Date dataApprovazione;
    private String notaApprovazione;
    private Date dataApprovazione2;
    private String notaApprovazione2;
    private Date dataSottoscrizione;
    private String notaSottoscrizione;
    private Date dataScadenza;
    private String notaScadenza;
    private String numRepertorio;
    private Date dataUltimaModifica;
    private LocalTime oraUltimaModifica;
    private Integer idUsrUltimaModifica;
    private Integer idTipo;
    private Integer idStato;

    
    /**
     * Default constructor initializing fields to default values.
     * Objects (String, Date, LocalTime) to null; Integers to -2 (conventional default).
     */
    public Convenzione() {
        super();
        this.titolo = null;
        this.informativa = null;
        this.ordinale = -2;
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
        this.idUsrUltimaModifica = -2;
        this.idTipo = -2;
        this.idStato = -2;
    }

    
    /**
     * Parameterized constructor which calls setters for each field.
     */
    public Convenzione(int id, String titolo, String informativa, Integer ordinale,
                      Date dataApprovazione, String notaApprovazione,
                      Date dataApprovazione2, String notaApprovazione2,
                      Date dataSottoscrizione, String notaSottoscrizione,
                      Date dataScadenza, String notaScadenza,
                      String numRepertorio, Date dataUltimaModifica,
                      LocalTime oraUltimaModifica, Integer idUsrUltimaModifica,
                      Integer idTipo, Integer idStato) {
        setId(id);
        setTitolo(titolo);
        setInformativa(informativa);
        setOrdinale(ordinale);
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
        setIdTipo(idTipo);
        setIdStato(idStato);
    }


    /**
     * @param o
     * @throws AttributoNonValorizzatoException
     */
    public Convenzione(it.col.bean.CodeBean o) throws AttributoNonValorizzatoException {
        super(o);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param id
     * @param nome
     * @param informativa
     * @param ordinale
     */
    public Convenzione(int id, String nome, String informativa, int ordinale) {
        super(id, nome, informativa, ordinale);
        // TODO Auto-generated constructor stub
    }


    /** @return the titolo */
    public String getTitolo() {
        return titolo;
    }

    /** @param titolo the titolo to set */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /** @return the dataApprovazione */
    public Date getDataApprovazione() {
        return dataApprovazione;
    }

    /** @param dataApprovazione the dataApprovazione to set */
    public void setDataApprovazione(Date dataApprovazione) {
        this.dataApprovazione = dataApprovazione;
    }

    /** @return the notaApprovazione */
    public String getNotaApprovazione() {
        return notaApprovazione;
    }

    /** @param notaApprovazione the notaApprovazione to set */
    public void setNotaApprovazione(String notaApprovazione) {
        this.notaApprovazione = notaApprovazione;
    }

    /** @return the dataApprovazione2 */
    public Date getDataApprovazione2() {
        return dataApprovazione2;
    }

    /** @param dataApprovazione2 the dataApprovazione2 to set */
    public void setDataApprovazione2(Date dataApprovazione2) {
        this.dataApprovazione2 = dataApprovazione2;
    }

    /** @return the notaApprovazione2 */
    public String getNotaApprovazione2() {
        return notaApprovazione2;
    }

    /** @param notaApprovazione2 the notaApprovazione2 to set */
    public void setNotaApprovazione2(String notaApprovazione2) {
        this.notaApprovazione2 = notaApprovazione2;
    }

    /** @return the dataSottoscrizione */
    public Date getDataSottoscrizione() {
        return dataSottoscrizione;
    }

    /** @param dataSottoscrizione the dataSottoscrizione to set */
    public void setDataSottoscrizione(Date dataSottoscrizione) {
        this.dataSottoscrizione = dataSottoscrizione;
    }

    /** @return the notaSottoscrizione */
    public String getNotaSottoscrizione() {
        return notaSottoscrizione;
    }

    /** @param notaSottoscrizione the notaSottoscrizione to set */
    public void setNotaSottoscrizione(String notaSottoscrizione) {
        this.notaSottoscrizione = notaSottoscrizione;
    }

    /** @return the dataScadenza */
    public Date getDataScadenza() {
        return dataScadenza;
    }

    /** @param dataScadenza the dataScadenza to set */
    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    /** @return the notaScadenza */
    public String getNotaScadenza() {
        return notaScadenza;
    }

    /** @param notaScadenza the notaScadenza to set */
    public void setNotaScadenza(String notaScadenza) {
        this.notaScadenza = notaScadenza;
    }

    /** @return the numRepertorio */
    public String getNumRepertorio() {
        return numRepertorio;
    }

    /** @param numRepertorio the numRepertorio to set */
    public void setNumRepertorio(String numRepertorio) {
        this.numRepertorio = numRepertorio;
    }

    /** @return the dataUltimaModifica */
    public Date getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    /** @param dataUltimaModifica the dataUltimaModifica to set */
    public void setDataUltimaModifica(Date dataUltimaModifica) {
        this.dataUltimaModifica = dataUltimaModifica;
    }

    /** @return the oraUltimaModifica */
    public LocalTime getOraUltimaModifica() {
        return oraUltimaModifica;
    }

    /** @param oraUltimaModifica the oraUltimaModifica to set */
    public void setOraUltimaModifica(LocalTime oraUltimaModifica) {
        this.oraUltimaModifica = oraUltimaModifica;
    }

    /** @return the idUsrUltimaModifica */
    public Integer getIdUsrUltimaModifica() {
        return idUsrUltimaModifica;
    }

    /** @param idUsrUltimaModifica the idUsrUltimaModifica to set */
    public void setIdUsrUltimaModifica(Integer idUsrUltimaModifica) {
        this.idUsrUltimaModifica = idUsrUltimaModifica;
    }

    /** @return the idTipo */
    public Integer getIdTipo() {
        return idTipo;
    }

    /** @param idTipo the idTipo to set */
    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    /** @return the idStato */
    public Integer getIdStato() {
        return idStato;
    }

    /** @param idStato the idStato to set */
    public void setIdStato(Integer idStato) {
        this.idStato = idStato;
    }
    
}
