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

import java.sql.Date;
import java.util.Vector;

import it.col.util.Constants;


/**
 * <p>PersonBean is the object which represents a person, 
 * whether natural or legal.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class PersonBean extends CodeBean {
    /** Serialization id (implementation of that interface by the superclass)   */
    private static final long serialVersionUID = -3415439696526030885L;
    /** Last name */
    private String cognome;
    /** Birth date */
    private Date dataNascita;
    /** Gender */
    private String sesso;
    /** Fiscal code */
    private String codiceFiscale;
    /** VAT number */
    private String partitaIva;
    /** Flag to hide */
    private boolean mostraPersona;
    /** E&ndash;mail */
    private String email;
    /** Age */
    private int eta;
    /** Logo/Photograph */
    private Vector<FileDocBean> foto;
    /** User */
    private int usrId;
    /** Url personal page */
    private String urlPersonalPage;
    /** Url official page */
    private String url;
    /** Note */
    private String note;
    /** Functional roles */
    private Vector<CodeBean> ruoli;
    /** Application role */
    private String ruolo;
    /** Membership groups */
    private Vector<CodeBean> gruppi;
    /** Person department id */
    protected int idDipartimento;
    /** Person department website */
    protected String urlDipartimento;


    /**
     * <p>Initialize the fields to defaults.</p>
     */
    public PersonBean() {
        super();
        idDipartimento = BEAN_DEFAULT_ID;
        cognome = codiceFiscale = partitaIva = email = urlPersonalPage = null;
        dataNascita = new Date(0);
        eta = BEAN_DEFAULT_ID;
        mostraPersona = true;
        sesso = null;
        url = null;
        note = null;
        foto = null;
        usrId = BEAN_DEFAULT_ID;
        gruppi = ruoli = null;
        ruolo = null;
        urlDipartimento = null;
    }


    /**
     * Gets the last name.
     * @return the last name (cognome)
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Sets the last name.
     * @param cognome the last name to set
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }


    /**
     * Gets the birth date.
     * @return the birth date (dataNascita)
     */
    public Date getDataNascita() {
        return dataNascita;
    }

    /**
     * Sets the birth date.
     * @param dataNascita the birth date to set
     */
    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * @return true if the birth date is empty or if it serves no purpose
     */
    public boolean isDataNascitaEmpty() {
        return (new Date(0).equals(dataNascita) || (dataNascita == null));
    }
    

    /**
     * Gets the gender.
     * @return the gender (sesso)
     */
    public String getSesso() {
        return sesso;
    }

    /**
     * Sets the gender.
     * @param sesso the gender to set
     */
    public void setSesso(String sesso) {
        this.sesso = sesso;
    }


    /**
     * Gets the fiscal code.
     * @return the fiscal code (codiceFiscale)
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * Sets the fiscal code.
     * @param codiceFiscale the fiscal code to set
     */
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }


    /**
     * Gets the VAT number.
     * @return the VAT number (partitaIva)
     */
    public String getPartitaIva() {
        return partitaIva;
    }

    /**
     * Sets the VAT number.
     * @param partitaIva the VAT number to set
     */
    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }


    /**
     * Gets the flag to hide.
     * @return true if the person is hidden (mostraPersona)
     */
    public boolean isMostraPersona() {
        return mostraPersona;
    }

    /**
     * Sets the flag to hide.
     * @param mostraPersona true to hide the person, false otherwise
     */
    public void setMostraPersona(boolean mostraPersona) {
        this.mostraPersona = mostraPersona;
    }


    /**
     * Gets the email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Gets the age.
     * @return the age (eta)
     */
    public int getEta() {
        return eta;
    }

    /**
     * Sets the age.
     * @param eta the age to set
     */
    public void setEta(int eta) {
        this.eta = eta;
    }


    /**
     * Gets the logo/photograph vector.
     * @return the logo/photograph vector (foto)
     */
    public Vector<FileDocBean> getFoto() {
        return foto;
    }

    /**
     * Sets the logo/photograph vector.
     * @param foto the logo/photograph vector to set
     */
    public void setFoto(Vector<FileDocBean> foto) {
        this.foto = foto;
    }


    /**
     * Gets the user ID.
     * @return the user id (usrId)
     */
    public int getUsrId() {
        return usrId;
    }

    /**
     * Sets the user ID.
     * @param usrId the user id to set
     */
    public void setUsrId(int usrId) {
        this.usrId = usrId;
    }


    /**
     * Gets the personal page URL.
     * @return the personal page URL (urlPersonalPage)
     */
    public String getUrlPersonalPage() {
        return urlPersonalPage;
    }

    /**
     * Sets the personal page URL.
     * @param urlPersonalPage the personal page URL to set
     */
    public void setUrlPersonalPage(String urlPersonalPage) {
        this.urlPersonalPage = urlPersonalPage;
    }


    /**
     * Gets the official page URL.
     * @return the official page URL (url)
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the official page URL.
     * @param url the official page URL to set
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * Gets the notes.
     * @return the notes
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the notes.
     * @param note the notes to set
     */
    public void setNote(String note) {
        this.note = note;
    }


    /**
     * Gets the functional roles vector.
     * @return the functional roles vector (ruoli)
     */
    public Vector<CodeBean> getRuoli() {
        return ruoli;
    }

    /**
     * Sets the functional roles vector.
     * @param ruoli the functional roles vector to set
     */
    public void setRuoli(Vector<CodeBean> ruoli) {
        this.ruoli = ruoli;
    }


    /**
     * Gets the application role.
     * @return the application role (ruolo)
     */
    public String getRuolo() {
        return ruolo;
    }

    /**
     * Sets the application role.
     * @param ruolo the application role to set
     */
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }


    /**
     * Gets the membership groups.
     * @return the membership groups vector (gruppi)
     */
    public Vector<CodeBean> getGruppi() {
        return gruppi;
    }

    /**
     * Sets the membership groups vector.
     * @param gruppi the membership groups vector to set
     */
    public void setGruppi(Vector<CodeBean> gruppi) {
        this.gruppi = gruppi;
    }
    
    
    /**
     * Gets the person department id.
     * @return the person department id (idDipartimento)
     */
    public int getIdDipartimento() {
        return idDipartimento;
    }

    /**
     * Sets the person department id.
     * @param idDipartimento the person department id to set
     */
    public void setIdDipartimento(int idDipartimento) {
        this.idDipartimento = idDipartimento;
    }


    /**
     * Gets the person department website URL.
     * @return the person department website URL (urlDipartimento)
     */
    public String getUrlDipartimento() {
        return urlDipartimento;
    }

    /**
     * Sets the person department website URL.
     * @param urlDipartimento the person department website URL to set
     */
    public void setUrlDipartimento(String urlDipartimento) {
        this.urlDipartimento = urlDipartimento;
    }

    /**
     * @return true if URL department's is pointless 
     */
    public boolean isUrlDipartimentoEmpty() {
           return (urlDipartimento == null || urlDipartimento.equals(Constants.VOID_STRING));
    }

}
