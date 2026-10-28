## INTRODUZIONE 

Questo documento rappresenta la documentazione relativa alla realizzazione di un sistema software per il censimento, la gestione ed il monitoraggio on line delle convenzioni di ateneo [COL-GeCo].


### OBIETTIVO 

Le convenzioni sono contesti di rilievo per la vita di ateneo e, al contempo, rappresentano elementi molto liquidi e di cui tenere traccia è piuttosto oneroso.

Una funzione classica della gestione delle comunicazione e delle scadenze legate alle convenzioni - la funzione temporale - passa attraverso la calendarizzazione delle scadenze cui sono soggette le convenzioni ed il monitoraggio degli eventi che le riguardano.

Ciò implica, tra l’altro, la necessità di: 
- inserire in uno strumento, come un calendario, le scadenze previste;
- predisporre appositi trigger (remind) per essere informati circa un’imminente scadenza;
- predisporre specifici cruscotti (dashboard) per tenere traccia di tutte le convenzioni in un unico layout;
- gestire lo scadenziario al fine di tenere le informazioni sempre aggiornate.

Quelle illustrate sono solo alcune delle operazioni che è necessario porre in atto per gestire il flusso informativo relativo alle convenzioni.

In questo scenario, quanto maggiori sono le interazioni, e quanto più viene demandata all’utente la gestione del workflow, tanto maggiori sono i rischi di errori, dimenticanze od omissioni.<br> 
Inoltre, a prescindere dall’errore umano, gestire manualmente il workflow delle convenzioni obbliga il personale degli uffici preposti a impiegare ampie quote parte del proprio lavoro al fine di gestire il workflow stesso.

L’obiettivo del progetto di mappatura e gestione On Line delle convezioni di ateneo (COL-GeCo) è fornire un aiuto rispetto a questi _tasks_ e a questo dominio informativo. Attraverso il ricorso ad un unico _hub_ in cui convergono tutte le informazioni relative alle convenzioni, diventa possibile estrarre tali informazioni, trasmettendole su molteplici canali, come ad esempio: 
- e-mail, 
- calendario, 
- interfaccia web, 

e così via.<br>
Inoltre, la versatilità legata ad un sistema informativo consente di accedere al sistema per l’alimentazione delle informazioni attraverso vari profili e di ottenere cruscotti personalizzati.


### PIANIFICAZIONE DEL PROGETTO 

La schematizzazione dei lavori prevede che vengano effettuate _in primis_ le seguenti attività:

i. Analisi di un’applicazione finalizzata alla mappatura delle convenzioni di ateneo

ii. Implementazione del database

iii. Implementazione dello strato applicativo

iv. Test e debug

v.  Iterazione

