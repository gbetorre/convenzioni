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

package it.col.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import it.col.ConfigManager;
import it.col.exception.CommandException;


/**
 * <p>DataUrl.java &egrave; una classe di servizio.</p> 
 * <p>Permette di costruire in modo semplice un url relativo o assoluto 
 * (comprensivo di context path) o la parte 'dati' di
 * un url sostituendo i caratteri speciali come ' ' e '&amp;' con caratteri
 * equivalenti adatti per URL.</p>
 * <p>Questa classe si basa sull'idea di una tabella, o mappa, in cui sono
 * memorizzati tutti i parametri possibili presenti sulla query string.<br />
 * Mette a disposizione metodi per inserire, aggiornare o eliminare tali
 * parametri, nonch&eacute; costruttori che inizializzano tale tabella, 
 * da zero oppure proprio a partire da una query string presente in una
 * richiesta <code>GET</code>.<br />
 * Dispone, inoltre, di metodi che permettono di generare URL http 'puliti',
 * cio&egrave; conformi alle regole di codifica degli URI e depurati di 
 * eventuali caratteri erronei in taluni contesti (come la context root '/'
 * dove gi&agrave; presente).<br />
 * Infine &ndash; ma non ultimo &ndash; costruire gli URL con i metodi forniti
 * da questa classe permette di ottenere sui valori degli <code>href</code> 
 * tutti gli eventuali parametri aggiuntivi opzionali presenti in query string 
 * (<em>in primis</em>, la eventuale lingua, che, nelle applicazioni
 * multilingua, &egrave; esplicitata facoltativamente - se non esplicitata
 * viene solitamente ricavata dalla lingua del sistema operativo, 
 * dalla localizzazione del client o da un valore di default).</p> 
 * 
 * <p>Created on Thu Jul 10 03:50:19 PM CEST 2025</p>
 * 
 * @author Roberto Posenato
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DataUrl implements Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = 1413064779905762448L;
    /**
     * ContextPath dell'applicazione privato dello '/' iniziale.<br>
     * Si ricorda che il context path inizia sempre con / e non termina mai con
     * /.<br>
     * Il valore da porre in questa variabile &egrave;
     * <code>
     * javax.servlet.GenericServlet.getServletContext().getContextPath()
     * </code>
     */
    private static String contextPathToken = VOID_STRING;
    /**
     * Token per definire la servlet centrale
     */
    private static String servletToken = ConfigManager.getAppName();
    /**
     * Token per la classe command
     */
    private static String commandToken = ConfigManager.getEntToken();
    /**
     * Mappa dei parametri della query string
     */
    private LinkedHashMap<String, String> map;

    
    /* **************************************************** *
     *                      Costruttori                     *
     * **************************************************** */
    /**
     * <p>Costruttore senza argomenti.
     * Istanzia la mappa che conterr&agrave; i parametri della query string.</p>
     */
    public DataUrl() {
        map = new LinkedHashMap<>();
    }

    
    /**
     * Costruttore:<br> 
     * Inserisce il parametro <code>'parametro'</code> 
     * con il valore <code>'valore'</code> nel
     * formato parametro '&amp;parametro=valore'</cite>
     * 
     * @param parametro il nome del parametro di cui deve essere valutato il valore ai fini del parsing
     * @param valore il valore da impostare come valore del parametro
     */
    public DataUrl(final String parametro, 
                   final String valore) {
        map = new LinkedHashMap<>();
        map.put(DataUrl.encodeURL(parametro), DataUrl.encodeURL(valore));
    }

    
    /**
     * <p>Costruttore da query string.</p>
     * Costruisce l'oggetto prendendo i parametri e i valori dalla queryString, 
     * eliminando, eventualmente, la sottostringa iniziale 'q=*&'
     * 
     * @param queryString la query string completa 'as is'
     */
    public DataUrl(String queryString) {
        map = new LinkedHashMap<>();
        int i = 0, j = -1;
        String nome, valore;
        // Controllo sull'input
        if (queryString != null) {
            try {
                queryString = java.net.URLDecoder.decode(queryString, "UTF-8");
            } catch (final java.io.UnsupportedEncodingException ignored) {
                /* questa eccezione è ignorata in quanto impossibile 
                 * dal momento che UTF-8 è una codifica obbligatoria    */
            }
        }
        if (queryString != null && queryString.length() > 0) {
            if (queryString.startsWith(commandToken + "=")) {
                i = queryString.indexOf('&'); // vado al primo parametro dopo ent=*
            }
            if (i != -1) {  // ci sono parametri da usare!
                if (i > 0)
                    i++;    // mi posiziono al primo carattere utile.
                while ((j = queryString.indexOf('=', i)) != -1) { // c'e` un
                    // parametro
                    // (string=string)
                    // System.err.println("Valori per nome di i, j "+i+", "+j);
                    nome = queryString.substring(i, j);
                    i = j + 1;
                    j = queryString.indexOf('&', i);
                    if (j == -1)
                        j = queryString.length();// e' l'ultimo
                    // parametro
                    // System.err.println("Valori per valore di i, j "+i+", "+j);
                    valore = queryString.substring(i, j);
                    i = j + 1;
                    if (nome.startsWith("?")) {
                        // Capita che un URL sia stato formato accodando due
                        // queryString (che iniziano con ?) per errore.
                        // Per esempio: URL che torna dalla banca per la
                        // notifica di pagamento con carta di credito.
                        // Aggiusto per semplificare il resto del codice.
                        // [24/05/2014] Posenato
                        nome = nome.substring(1);
                    }
                    nome = encodeURL(nome);
                    valore = encodeURL(valore);
                    map.put(nome, valore);
                }
            }
        }
    }

    
    /* **************************************************** *
     *      Metodi per la manipolazione dei caratteri       *
     * **************************************************** */
    /**
     * <p>Metodo statico per convertire i caratteri IS0-8859-1 nel formato adatto
     * per gli URL.</p>
     * 
     * @param s Stringa da convertire
     * @return <code>Stringa</code> - la string input codificata correttamente per essere usata in un URL
     */
    public static String encodeURL(final String s) {
        String url = null;
        try {
            url = java.net.URLEncoder.encode(s, "UTF-8");
        } catch (final java.io.UnsupportedEncodingException ignored) {
            /* questa eccezione viene ignorata in quanto impossibile 
             * dal momento che UTF-8 e' una codifica obbligatoria    */
        }
        return url;
    }

    
    /**
     * Restituisce la stringa 'input' codificata in modo tale da poter inserire
     * la stringa all'interno di un documento html evitando l'interpretazione
     * dei caratteri speciali. Rimpiazza tutti i seguenti caratteri speciali
     * html con le loro entit&agrave;: 
     * <ul>
     * <li><code>&amp;</code> diventa <code>&amp;amp;</code></li> 
     * <li><code>"</code> diventa <code>&amp;quot;</code></li> 
     * <li>' diventa &amp;#039; </li>
     * <li>\n diventa &lt;br /&gt;</li> 
     * </ul>
     * Se il parametro
     * 'tutti' è true, allora anche i caratteri &lt; e &gt; vengono tradotti. In
     * questo modo la funzione converte eventuali tag html presenti in input che
     * vengono resi caratteri nella stringa restituita.
     * 
     * @param input
     *            stringa da codificare
     * @param tutti
     *            i caratteri &lt; e &gt; vengono tradotti come entit&agrave; html
     * @return <code>String</code> - la string input codificata in html
     */
    public static String encodeHTML(final String input, 
                                    final boolean tutti) {
        /* Uso le espressioni regolari per riuscire a catturare
         * il maggior numero di casi.                                           */
        String risultato = null;
        if (input != null) {
            // sostituisco tutti gli & con &amp;
            // per questioni di efficienza... prima rimpiazzo tutti gli & anche
            // se già
            // convertiti... e poi pulisco le doppie sostituzioni
            risultato = input.replaceAll("&", "&amp;").replaceAll("&amp;amp;", "&amp;");
            // sostituisco tutte le " con &quot;
            risultato = risultato.replaceAll("\"", "&quot;");
            // sostituisco tutte le ' con &#039;;
            risultato = risultato.replaceAll("'", "&#039;");
            if (tutti) {
                // sostituisco tutte le < con &lt;
                risultato = risultato.replaceAll("<", "&lt;");
                // sostituisco tutte le > con &gt;
                risultato = risultato.replaceAll(">", "&gt;");
            }
            // sostituisco tutte i new line con <br />
            risultato = risultato.replaceAll("\n|\r\n|\r", "<br />");
        }
        return (risultato);
    }

    
    /**
     * Overloading di encodeHTML(s,b) con b=true
     * 
     * @see #encodeHTML(String, boolean)
     * @param input stringa da codificare
     * @return stringa con i caratteri speciali HTML codificati come entità HTML
     */
    public static String encodeHTML(final String input) {
        return DataUrl.encodeHTML(input, true);
    }


    /* **************************************************** *
     *      Metodi per la manipolazione dei parametri       *
     * **************************************************** */
    /**
     * <p>Inserisce la coppia 
     * <pre>'parametro'='valore'</pre> sostituendo eventualmente il valore del
     * parametro, se gi&agrave; presente.</p>
     * 
     * @param parametro il parametro il cui valore deve essere inserito
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl put(final String parametro, 
                       final String valore) {
        if (parametro != null)
            map.put(encodeURL(parametro), encodeURL(valore));
        return this;
    }
    

    /**
     * <p>Inserisce la coppia 
     * <pre>'parametro'='valore numerico'</pre> sostituendo eventualmente 
     * il valore numerico del parametro, se gi&agrave; presente.</p>
     * <p>Quantunque l'argomento passato come valore del parametro sia un numero,
     * esso viene inserito nella tabella dei parametri sotto forma di String.</p>
     * 
     * @param parametro il parametro il cui valore deve essere inserito
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl put(final String parametro, 
                       final int valore) {
        if (parametro != null)
            map.put(encodeURL(parametro), Integer.toString(valore));
        return this;
    }

    
    /**
     * <p>Aggiunge la coppia <pre>'parametro'='valore'</pre></p>
     * <p>
     * Facade di
     * {@link #put(String, String)}</p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiunto
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl add(final String parametro, 
                       final String valore) {
        this.put(parametro, valore);
        return this;
    }

    
    /**
     * <p>Aggiunge la coppia <pre>'parametro'='valore'</pre>
     * dove <code>'valore'</code> &egrave; un numero.</p> 
     * <p>Quantunque l'argomento passato come valore del parametro sia un numero,
     * esso alla fine verr&agrave; inserito nella tabella dei parametri 
     * sotto forma di String.</p>
     * <p>
     * Façade di
     * {@link #put(String, int)}</p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiunto
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl add(final String parametro, 
                       final int valore) {
        this.put(parametro, valore);
        return this;
    }
    

    /**
     * <p>Imposta il parametro <code>'parametro'</code> 
     * al valore <code>'valore'</code></p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiornato
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl set(final String parametro, 
                       final String valore) {
        this.put(parametro, valore);
        return this;
    }

    
    /**
     * <p>Imposta il parametro <code>'parametro'</code> 
     * al valore <code>'valore'</code>
     * dove <code>'valore'</code> &egrave; un numero.</p>
     * <p>Quantunque l'argomento passato come valore del parametro sia un numero,
     * esso alla fine verr&agrave; inserito nella tabella dei parametri 
     * sotto forma di String.</p>
     * 
     * @param parametro il parametro il cui valore deve essere aggiornato
     * @param valore    il valore da inserire nel parametro indicato
     * @return <code>DataUrl</code> - dataUrl modificato nel valore del parametro indicato
     */
    public DataUrl set(final String parametro, 
                       final int valore) {
        this.put(parametro, valore);
        return this;
    }

    
    /**
     * <p>Rimuove il parametro <code>'parametro'</code> se esiste, 
     * nulla altrimenti.</p>
     * 
     * @param parametro il parametro che deve essere eliminato
     * @return <code>DataUrl</code> - dataUrl depurato del parametro indicato
     */
    public DataUrl remove(final String parametro) {
        if (parametro != null) {
            map.remove(parametro);
        }
        return this;
    }

    
    /**
     * <p>Restituisce il parametro <code>'parametro'</code> se esiste, 
     * stringa "" (vuota) altrimenti.</p> 
     * <p>Il formato della stringa restituita &egrave; pari a quello restituito da
     * {@link java.net.URLEncoder}.</p>
     * 
     * @param parametro
     *            da ricercare. Se null, ritorna "";
     * @return <code>String</code> - il valore di 'parametro' se esiste, stringa "" altrimenti.
     */
    public String getEncoded(final String parametro) {
        if (parametro == null)
            return "";
        String value = map.get(parametro);
        if (value == null)
            return "";
        return value;
    }
    

    /**
     * <p>Restituisce il parametro <code>'parametro'</code> se esiste, 
     * stringa "" (vuota) altrimenti.</p>
     * 
     * @param parametro
     *            da ricercare. Se null, ritorna "";
     * @return <code>String</code> - il valore di 'parametro' se esiste, stringa "" altrimenti.
     */
    public String get(final String parametro) {
        String value = this.getEncoded(parametro);
        if (value.isEmpty())
            return value;
        try {
            return java.net.URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    
    /**
     * <p>merge(DataUrl d) restituisce un DataUrl dato dall'unione del DataUrl 'd'
     * con i valori presenti nel data url attuale.</p> 
     * <p>Lo stato interno sia di <code><strong>this</strong></code>
     * sia di <code><strong>'d'</strong></code> non vengono modificati.</p>
     * 
     * @param dataU
     *            dataUrl in ingresso
     * @return <code>DataUrl</code> - DataUrl modificato
     */
    public DataUrl merge(final DataUrl dataU) {
        final LinkedHashMap<String, String> temp = new LinkedHashMap<>(map);
        temp.putAll(dataU.map);
        final DataUrl nuovo = new DataUrl();
        nuovo.map = temp;
        return nuovo;
    }
  

    /* **************************************************** *
     *         Metodi per la generazione degli URL          *
     * **************************************************** */
    
    public String getUrl() {
        // Definisce una stringa dinamica da costruire tramite il contenuto della mappa
        StringBuffer url = new StringBuffer();
        // Cicla sui parametri da restituire
        
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();


        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            // Chiave e valore
            String key = entry.getKey();
            String value = entry.getValue();
            url.append(key)
               .append(EQ)
               .append(value);
            // Check if there are more elements
            if (iterator.hasNext()) {
                url.append(AMPERSAND);
            }
            
        }
        /*
        for (java.util.Map.Entry<String, String> entry : mappa.entrySet()) {
            // Chiave e valore
            String key = entry.getKey();
            String value = entry.getValue();
            url.append(key)
               .append(EQ)
               .append(value);
            if (mappa.)
        }*/
        return url.toString();
    }


    /**
     * <p>Restituisce il contenuto di questo oggetto in codifica html:
     * <pre>param1=value1&amp;param2=value2&...</pre>
     * Se non ci sono parametri, restituisce stringa vuota ("").</p>
     * 
     * @return <code>DataUrl</code> - DataUrl corrente in formato HTML senza '?' inziale.
     */
    public String getQueryString() {
        final StringBuffer buffer = new StringBuffer();
        for (Entry<String, String> entry : map.entrySet()) {
            buffer.append(entry.getKey() + "=" + entry.getValue() + "&amp;");
        }
        // Il ciclo while ha aggiunto un &amp; di troppo
        if (map.size() > 0)
            buffer.setLength(buffer.length() - 5);
        return buffer.toString();
    }


    /**
     * <p>Restituisce l'url: 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals + 
     *        valore di 'ent'</pre>
     * senza modificare lo stato interno.</p>
     * <p>In altri termini, genera link del tipo:
     * <pre>/main?ent=persona</pre>
     * che vanno a invocare la ServletCommand senza passarle ulteriori valori.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @return <code>String</code> - l'url: DataUrl.contextPathAndservletTokenAndCommandTokenEquals + 'ent' senza modificare lo stato interno
     */
    public String getUrl(final String ent) {
        return this.getUrl(ent, false);
    }


    /**
     * <p>Restituisce l'url 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        valore di &quot;parametri interni&quot;</pre> 
     * senza modificare lo stato interno se 'ext' &egrave; <code>true</code>,<br />
     * <strong><em>oppure</em></strong> l'url 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals + 
     *       'ent'</pre> 
     * altrimenti.</p>
     * <p>Esempio:
     * <ul>
     * <li>se contextPathToken = new String("");</li>
     * <li>e servletToken = "main"; //(Token per definire la servlet centrale)</li>
     * <li>e commandTokenEquals = "ent" + "="; //(Token per la classe command con '=' attaccato in fondo)</li>
     * <li>allora contextPathAndservletTokenAndCommandTokenEquals = "" + "/" + "main" + "?" + ent=;</li>
     * </ul>
     * cio&egrave; vale: <pre>/main?ent=</pre>
     * A questo campo, questo metodo aggiunge valore di 'ent' e valore 
     * di parametri interni se 'ext' &egrave; 'true'; quindi, nell'esempio, 
     * posti ent a "persona" e parmetri interni a "id=100", si avr&agrave; 
     * come valore di ritorno:
     * <pre>/main?ent=persona&amp;id=100</pre></p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param ext boolean flag che specifica se aggiungere tutti i parametri aggiuntivi o meno
     * @return <code>String</code> - valore di un link ipertestuale costruito sulla base degli argomenti passati, da stampare come valore di un attributo html <code>href</code>
     */
    public String getUrl(final String ent, 
                         final boolean ext) {
        final StringBuffer temp = new StringBuffer(new String(""));
        if ((ent != null) && (ent.length() > 0)) {
            temp.append(contextPathToken + 
                        "/" + 
                        servletToken + 
                        "?" + 
                        commandToken + 
                        "=");
            temp.append(encodeURL(ent));
            if (ext) {
                if (map.size() > 0) {
                    temp.append("&amp;");
                    temp.append(this.getQueryString());
                }
            }
        }
        return temp.toString();
    }


    /**
     * <p>Restituisce la stringa: 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        &quot;&amp;&quot; +
     *        'data' (valore di &quot;parametri interni&quot;)</pre>
     * in codifica html, senza modificare lo stato interno.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param data
     *            una sequenza di parametro=valore separati da & (singolo
     *            carattere)
     * @return <code>String</code> - valore di un link ipertestuale costruito sulla base degli argomenti passati, da stampare come valore di un attributo html <code>href</code>
     */
    public String getUrl(final String ent, 
                         final String data) {
        final DataUrl tempData = new DataUrl(data);
        if ((ent != null) && (ent.length() > 0)) {
            if ((data != null) && (data.length() > 0))
                return DataUrl.contextPathToken + 
                        "/" + 
                        servletToken + 
                        "?" + 
                        commandToken + 
                        "=" +
                        encodeURL(ent) + "&amp;" + tempData.getQueryString();
            return DataUrl.contextPathToken + 
                    "/" + 
                    servletToken + 
                    "?" + 
                    commandToken + 
                    "=" +
                    encodeURL(ent);
        }
        if ((data != null) && (data.length() > 0))
            return tempData.getQueryString();
        return new String("");
    }


    /**
     * <p>Restituisce la stringa: 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        &quot;&amp;&quot; +
     *        'data' (valore di &quot;parametri interni&quot;) +
     *        &quot;buffer interno&quot;
     *        </pre>
     * senza modificare lo stato interno se
     * <pre>'ext' &egrave; true</pre> 
     * <strong><em>oppure</em></strong> la string 
     * <pre>DataUrl.contextPathAndservletTokenAndCommandTokenEquals +
     *        valore di 'ent' +
     *        &quot;&amp;&quot; +
     *        'data' (valore di &quot;parametri interni&quot;)</pre>
     * altrimenti.</p>
     * 
     * @param ent valore del 'Token', cio&egrave; della ServletCommand richiesta
     * @param data
     *            una sequenza di parametro=valore separati da & (singolo
     *            carattere).
     * @param ext boolean flag che specifica se aggiungere tutti i parametri aggiuntivi o meno
     * @return <code>String</code> - valore di un link ipertestuale costruito sulla base degli argomenti passati, da stampare come valore di un attributo html <code>href</code>
     */
    public String getUrl(final String ent, 
                         final String data, 
                         final boolean ext) {
        if (ext) {
            final DataUrl temp = this.merge(new DataUrl(data));
            return temp.getUrl(ent, true);
        }
        return this.getUrl(ent, data);
    }
    
    
    /**
     * 
     * @param scheme
     * @return
     * @throws IllegalArgumentException
     */
    public static String parseScheme(String scheme)
                       throws IllegalArgumentException {
        if (!scheme.equals("http") && !scheme.equals("https")) {
            /* Se viene passato uno schema contenente i separatori di protocollo, 
             * ammetto la buona fede e ti grazio, facendo io la correzione
             * (mortacci tua)                                                   */
            if (scheme.equals("http://") || scheme.equals("https://")) {
                return scheme.substring(0,  scheme.indexOf("://"));
            }
            /* Altrimenti vuol dire che ci stai a provà, 
             * e ti sollevo un'eccezione!!!                                     */
            else
                throw new IllegalArgumentException("Valore dell'argomento \'scheme\' (" + scheme + ") non valido!!\n");
        }
        return scheme;
    }
    
    
    private String parseDomain(String domain) 
                        throws IllegalArgumentException {
        if (!domain.startsWith("http") && !domain.startsWith("www")) {
            // Che mi stai a passa'??
            throw new IllegalArgumentException("Valore dell'argomento \'dominio\' (" + domain + ") non valido!!\n");
        }
        // Recupera il dominio per la costruzione dell'URL assoluto a partire da 'www'
        String parsedDomain = domain.substring(domain.indexOf("www"), domain.length());
        return parsedDomain;
    }
    
}
