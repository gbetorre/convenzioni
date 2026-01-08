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

package it.col.bean;

import java.io.Serializable;

import it.col.exception.AttributoNonValorizzatoException;


/** 
 * <p>Classe per rappresentare oggetti generici o raggruppamenti di elementi,
 * ispirata proprio al design pattern dei componenti generici.</p>
 * </p>In virt&uacute; della sua struttura generica, pu&ograve; essere utilizzato 
 * per veicolare valori di molteplici entit&agrave;, come per esempio:
 * <ul>
 * <li>gruppo di altre entit&agrave;</li>
 * <li>rilevazioni</li>
 * <li>classificazioni</li>
 * <li>etc...</li>
 * </ul>
 * ecc.<br />
 * Inoltre, rappresenta un ottimo genitore di nodi figli nelle gerarchie
 * di ereditariet&agrave; (nodo radice di strutture ad albero) perch&eacute;
 * ogni oggetto, bene o male, deve pur avere un id, un nome e qualche altro 
 * attributo di base...</p>   
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class CodeBean implements Serializable {
    
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = 1L;
    /**
     *  Nome di questa classe. 
     *  Viene utilizzato per contestualizzare i messaggi di errore.
     */
    private final String FOR_NAME = "\n" + this.getClass().getName() + ": "; //$NON-NLS-1$
    /**
     * Valore convenzionale definito nel bean padre CodeBean per comodit&agrave; 
     * di accesso dal di fuori del package bean e delle sottoclassi di CodeBean
     */
    public static final int BEAN_DEFAULT_ID = -2;
    /**
     * Valore convenzionale definito nel bean CodeBean per comodit&agrave; 
     * di accesso dal di fuori del package bean e delle sottoclassi di CodeBean
     */
    public static final float BEAN_DEFAULT_FLOAT = -2.0f;
    
    /* **************************************************** *
     *                  Variabili d'istanza                 *
     * **************************************************** */
    /**
     * Identificativo del gruppo di elementi
     */
    private int id;
    /**
     * Nome del gruppo di elementi
     */
    private String nome;
    /**
     * Descrizione del gruppo di elementi
     */
    private String informativa;
    /**
     * Numero d'ordine del gruppo di elementi rispetto agli altri
     */
    private int ordinale;
    
    
    /* **************************************************** *
     *                      Costruttori                     *
     * **************************************************** */
    /**
     * Costruttore di CodeBean.
     * Inizializza i campi a valori convenzionali di default.
     */
    public CodeBean() {
        id = ordinale = BEAN_DEFAULT_ID;
        nome = informativa = null;
    }
    
    
    /**
     * <p>Costruttore per clonazione.</p>
     * <p>Inizializza i campi a valori presi da un altro bean &dash; se sono valorizzati
     * &dash;, altrimenti li inizializza a valori di default.
     * In questo modo non si dovrebbe verificare alcun problema con i puntatori.</p>
     * <p>Utilizzare questo metodo <strong>esclusivamente</strong> per clonare un oggetto, 
     * non assegnarlo semplicemente!<br />
     * Ad esempio:
     * <pre>
     * CodeBean object = new CodeBean();<s>
     * CodeBean another = object;</s>
     * </pre>
     * <strong>is wrong!!!</strong>
     * perch&eacute; <em>another</em> punta a object e, se si modifica questo, 
     * si modifica "retroattivamente" anche object!<br />
     * Il codice corretto &egrave; il seguente:
     * <pre>
     * // Costruisce un nuovo oggetto, uguale a uno dei due considerati:
     * CodeBean object = new CodeBean();
     * CodeBean another = new CodeBean(object);
     * </pre>   
     *                      
     * @param o oggetto CodeBean i cui valori devono essere copiati
     * @throws AttributoNonValorizzatoException se l'identificativo dell'oggetto passato come argomento non e' valorizzato!
     */
    public CodeBean(CodeBean o) 
             throws AttributoNonValorizzatoException {
        // Ammette la mancata valorizzazione di quasi tutti i valori, ma non transige sull'id!
        id = o.getId();
        // Tenta di recuperare: Nome
        nome = o.getNome();
        // Tenta di recuperare: Informativa
        informativa = o.getInformativa();
        // Attributi che non sollevano eccezione se non inizializzati
        ordinale = o.getOrdinale();
    }
    
    
    /**
     * Costruttore parametrico.
     * 
     * CodeBean(int id, String nome, String informativa, int ordinale)
     * 
     * @param id            identificativo dell'oggetto da istanziare
     * @param nome          nome dell'oggetto da istanziare
     * @param informativa   descrizione estesa dell'oggetto da istanziare
     * @param ordinale      livello di indentazione dell'oggetto da istanziare
     */
    public CodeBean(int id, String nome, String informativa, int ordinale) {
        this.id = id;
        this.nome = nome;
        this.informativa = informativa;
        this.ordinale = ordinale;
    }
    
    
    /**
     * Costruttore parametrico in forma ridotta.
     * Prende in input i valori da memorizzare; imposta gli altri 
     * a valori di default. 
     * 
     * @param nome          nome dell'oggetto da istanziare
     * @param informativa   descrizione estesa dell'oggetto da istanziare
     */
    public CodeBean(String nome, String informativa) {
        this.id = BEAN_DEFAULT_ID;
        this.nome = nome;
        this.informativa = informativa;
        this.ordinale = BEAN_DEFAULT_ID;
    }

    
    /* **************************************************** *
     *           Metodi getter e setter per id              *
     * **************************************************** */
    /**
     * Restituisce l'identificativo di un raggruppamento.
     * @return <code>id</code> - l'id del gruppo 
     * @throws AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public int getId() throws AttributoNonValorizzatoException {
        if (id == BEAN_DEFAULT_ID) {
            throw new AttributoNonValorizzatoException(FOR_NAME + "Attributo id non valorizzato!");
        }
        return this.id;
    }
    
    /**
     * Imposta  l'id di un raggruppamento.
     * @param i l'id del gruppo da impostare
     */
    public void setId(int i) {
        id = i;
    }
    
    
    /* **************************************************** *
     *           Metodi getter e setter per nome            *
     * **************************************************** */
    /**
     * Restituisce il nome.
     * @return <code>nome</code> - la denominazione
     */
    public String getNome()  {
        return this.nome;
    }
    
    /**
     * Imposta la denominazione.
     * @param string il nome da impostare
     */
    public void setNome(String string) {
        nome = string;
    }    
        
    
    /* **************************************************** *
     *           Metodi getter e setter per informativa     *
     * **************************************************** */
    /**
     * Restituisce la descrizione.
     * @return <code>informativa</code> - la descrizione
     */
    public String getInformativa() {
        return this.informativa;
    }
    
    /**
     * Imposta la descrizione.
     * @param string il nome da impostare per la descrizione
     */
    public void setInformativa(String string) {
        informativa = string;
    }    
    
    
    /* **************************************************** *
     *           Metodi getter e setter per ordinale        *
     * **************************************************** */
    /**
     * Restituisce l'ordinale di un raggruppamento rispetto agli altri.
     * @return <code>ordinale</code> - il valore del numero d'ordine del gruppo 
     */
    public int getOrdinale() {
        return this.ordinale;
    }
    
    /**
     * Imposta  il numero d'ordine di un raggruppamento.
     * @param i l'ordinale del gruppo da impostare
     */
    public void setOrdinale(int i) {
        ordinale = i;
    }

}
