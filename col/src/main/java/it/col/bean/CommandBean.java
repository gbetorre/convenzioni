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

import java.io.Serializable;


/**
 * <p>Classe che serve a rappresentare classi Command.</p>
 * 
 * <p>Created on Thu Jul 10 11:51:41 AM CEST 2025</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class CommandBean implements Serializable {
    
    /**     Costante di serializzazione.                                    */
    private static final long serialVersionUID = -4800164838294374204L;
    /**     Nome (Java) di questa classe                                    */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": ";//$NON-NLS-1$
    /**     Suffisso per le Command                                         */
    static final String COMMAND_SUFFIX = "Command";
    /**     Identificativo della Command                                    */
    private int id;
    /**     Nome della Command                                              */
    private String nome;
    /**     Nome reale della Command                                        */
    private String nomeReale;
    /**     Nome di entita' associata                                       */ 
    private String nomeClasse;
    /**     Etichetta per la Command                                        */
    private String label;
    /**     Attributo per memorizzare il valore di una pagina associata     */
    private String pagina;
    /**     Attributo che pu&ograve; contenere informazioni descrittive     */
    private String informativa;
    /**     Numero d'ordine dell'elemento rispetto agli altri               */
    private int ordinale;
    
    
    /* ************************************************************************ * 
     *                                  Costruttori                             *
     * ************************************************************************ */
    
    /**
     * <p>Override Costruttore di Default</p>
     * <p>Inizializza le variabili di classe a valori convenzionali</p>
     */
    public CommandBean() {
        id = ordinale = CodeBean.BEAN_DEFAULT_ID;
        nome = nomeReale = nomeClasse = label = pagina = informativa = null;
    }
    
    
    /**
     * <p>Costruttore da CommandBean</p>
     * <p>Inizializza le variabili di classe a valori presi da
     * un CommandBean passato come argomento</p>
     *  
     * @param old CommandBean di cui si vuol recuperare i valori
     */
    public CommandBean(final CommandBean old) {
        this.id = old.getId();
        this.nome = old.getNome();
        this.nomeReale = old.getNomeReale();
        this.nomeClasse = old.getNomeClasse();
        this.label = old.getLabel();
        this.pagina = old.getPagina();
        this.informativa = old.getInformativa();
        this.ordinale = old.getOrdinale();
    }
    
    
    /**
     * <p>Costruttore parametrizzato</p>
     * <p>CommandBean(String nome, String label)</p>
     * 
     * @param nome      nome della Command da creare; puo' corrispondere al token
     * @param label     etichetta per la Command
     */
    public CommandBean(String nome, String label) {
        this.nome = nome;
        this.label = label;
    }
    
    
    /**
     * <p>Costruttore parametrizzato</p>
     * <p>
     * CommandBean(String nome, String label, String informativa, int ordinale)
     * </p>
     * 
     * @param nome          nome della voce da creare; puo' corrispondere al token
     * @param label         etichetta da mostrare per l'url della voce da creare
     * @param informativa   una descrizione della voce da creare
     * @param ordinale      numero d'ordine dell'elemento rispetto agli altri
     */
    public CommandBean(String nome, String label, String informativa, int ordinale) {
        this.nome = nome;
        this.label = label;
        this.informativa = informativa;
        this.ordinale = ordinale;
    }
    
    
    /**
     * <p>Costruttore parametrizzato</p>
     * <p>
     * CommandBean(String nome, String nomeReale, String label, String pagina, String informativa,  int ordinale)
     * </p>
     * 
     * @param nome          nome della voce da creare; puo' corrispondere al token
     * @param nomeReale     nome che dovrebbe essere 'reale' nel senso che e' il nome dell'attributo 'nome' nella tabella
     * @param label         etichetta associata alla Command corrente
     * @param pagina        nome di una pagina associata alla Command corrente
     * @param informativa   una descrizione della voce da creare
     * @param ordinale      numero d'ordine dell'elemento rispetto agli altri
     */
    public CommandBean(String nome, String nomeReale, String label,  String pagina, String informativa, int ordinale) {
        this.nome = nome;
        this.nomeReale = nomeReale;
        this.label = label;
        this.pagina = pagina;
        this.informativa = informativa;
        this.ordinale = ordinale;
    }
    
    
    /* ************************************************************************ * 
     *                              Metodi Ovverride                            *
     * ************************************************************************ */
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("javadoc")
    @Override
    public String toString() {
        return FOR_NAME + "@" + this.nome + String.valueOf(this.id);
    }
    
    
    /* ************************************************************************ *  
     *                          Accessori e Mutatori                            *
     * ************************************************************************ */
    
    /**
     * @return <code>id</code> - l'identificativo
     */
    public int getId() {
        return this.id;
    }
    

    /**
     * @param i un intero rappresentante l'identificativo da impostare
     */
    public void setId(int i) {
        id = i;
    }
    
    
    /**
     * @return <code>nome</code> - il token usato per individuare questa entita' nella query string
     */
    public String getNome() {
        return this.nome;
    }
    
    
    /**
     * @param v un oggetto String usato per impostare il valore di 'ent'
     */
    public void setNome(String v) {
        this.nome = v;
    }
    
    
    /**
     * <p>Nome reale della voce, in caso di corrispondenze logiche</p>
     * 
     * @return <code>nomeReale</code> - un oggetto String usato per impostare il nome della voce
     */
    public String getNomeReale() {
        return this.nomeReale;
    }
    
    
    /**
     * @param v un oggetto String usato per impostare il nome della voce
     */
    public void setNomeReale(String v) {
        this.nomeReale = v;
    }
    
    
    /**
     * <p>Etichetta da visualizzare per la voce</p>
     * 
     * @return <code>labelWeb</code> - etichetta da mostrare nel menu, per rappresentare la voce
     */
    public String getLabel() {
        return this.label;
    }
    
    
    /**
     * @param v un oggetto String contenente l'etichetta per la voce
     */
    public void setLabel(String v) {
        this.label = v;
    }
    
    
    /**
     * <p>Attributo che pu&ograve; essere usato per memorizzare
     * il nome di una entit&agrave; associata alla voce
     * oppure altre informazioni attinenti </p>
     * 
     * @return <code>nomeClasse</code> - un oggetto String contenente tradizionalmente il nome della Command associata alla voce
     */
    public String getNomeClasse() {
        return this.nomeClasse + COMMAND_SUFFIX;
    }
    
    
    /**
     * @param v un oggetto String usato tradizionalmente per impostare il nome della Command associata alla voce
     */
    public void setNomeClasse(String v) {
        this.nomeClasse = v;
    }
    
    
    /**
     * <p>Attributo che pu&ograve; essere usato per memorizzare
     * il valore della pagina associata ad una classe referenziata
     * dalla voce di menu, oppure altre informazioni attinenti.</p>
     * 
     * @return <code>paginaJsp</code> - oggetto String usato tradizionalmente per memorizzare il nome della pagina jsp associata alla it.univr.di.uol.command.Command {@link #getNomeClasse()}
     */
    public String getPagina() {
        return this.pagina;
    }
    
    
    /**
     * @param v oggetto String usato per impostare il nome della pagina jsp associata alla it.univr.di.uol.command.Command {@link #getNomeClasse()}
     */
    public void setPagina(String v) {
        this.pagina = v;
    }
    
    
    /**
     * @return <code>informativa</code> - un campo note descrittivo
     */
    public String getInformativa() {
        return this.informativa;
    }
    
    
    /**
     * @param string un oggetto String contenente informazioni da impostare
     */
    public void setInformativa(String string) {
        informativa = string;
    }
    
    
    /**
     * @return <code>ordinale</code> - il numero d'ordine della voce
     */
    public int getOrdinale() {
        return this.ordinale;
    }
    
    
    /**
     * @param i un intero rappresentante il numero d'ordine della voce da impostare
     */
    public void setOrdinale(int i) {
        ordinale = i;
    }

}
