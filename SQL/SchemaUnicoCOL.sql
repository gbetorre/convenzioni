--  Torre, Thu 26 Jun 2025 01:49:43 PM CEST
--  Crea la parte di base di dati per l'applicazione web COL-GeCo
--  (Convenzioni On Line - Gestionale Convenzioni)
--  destinata a contenere i dati dell'applicazione di mappatura delle convenzioni
--  gestite e tracciate dall'ateneo.
--  Torre Thu Jun 26 01:56:42 PM CEST 2025: Prima versione
--  Lo script dev'essere imdempotente per essere compatibile con l'esecuzione da ETL
--  Pertanto la sintassi dell'SQL rispetta questo vincolo: ogni istruzione è condizionata
--  all'esistenza dell'entità o della relazione interessata dall'istruzione stessa
--  Torre, Tue Jul  1 03:33:06 PM CEST 2025
--  Torre, Wed Oct 29 10:49:48 CET 2025: aggiunta di un campo note, facoltativo, alla convenzione

------------------------------------------
--          ENUMERATIVI DINAMICI        --
------------------------------------------

-- Ogni tupla rappresenta un possibile tipo di convenzione
CREATE TABLE IF NOT EXISTS tipo_convenzione
(
    id                      SMALLSERIAL PRIMARY KEY ,
    nome                    VARCHAR(256)        NOT NULL,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    UNIQUE (nome)
);

-- Ogni tupla rappresenta un possibile stato di una convenzione
CREATE TABLE IF NOT EXISTS stato_convenzione
(
    id                      SMALLSERIAL PRIMARY KEY ,
    nome                    VARCHAR(256)        NOT NULL,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    UNIQUE (nome)
);

-- Ogni tupla rappresenta una possibile finalità di una convenzione
CREATE TABLE IF NOT EXISTS finalita
(
    id                      SMALLSERIAL PRIMARY KEY ,
    nome                    VARCHAR(256)        NOT NULL,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    UNIQUE (nome)
);

-- Ogni tupla rappresenta un possibile tipo di contraente
CREATE TABLE IF NOT EXISTS tipo_contraente
(
    id                      SMALLSERIAL PRIMARY KEY ,
    nome                    VARCHAR(256)        NOT NULL,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    UNIQUE (nome)
);


------------------------------------------
-- TABELLE DI SERVIZIO E AUTENTICAZIONE --
------------------------------------------

-- Ogni tupla rappresenta un ruolo applicativo ricoperto
CREATE TABLE IF NOT EXISTS ruolo_applicativo
(
    id                      SMALLSERIAL PRIMARY KEY,
    nome                    VARCHAR(256)        NOT NULL,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    UNIQUE (nome)
);

-- Ogni tupla rappresenta un ruolo giuridico csa
CREATE TABLE IF NOT EXISTS ruolo_giuridico
(
    codice_csa              CHAR(2) PRIMARY KEY ,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL
);

-- Ogni tupla rappresenta una classe Command
 CREATE TABLE IF NOT EXISTS command 
 (
    id                      SMALLINT PRIMARY KEY, 
    nome                    VARCHAR(64)         NOT NULL, 
    token                   VARCHAR(64)         , 
    jsp                     VARCHAR(128)        , 
    labelweb                VARCHAR(128)        , 
    informativa             VARCHAR(1024)       , 
    UNIQUE(nome)
);

-- Ogni tupla rappresenta una persona fisica
-- Inserita obbligatorietà sul campo e-mail perché potrebbe essere usato come login
CREATE TABLE IF NOT EXISTS persona
(
    id                      SERIAL PRIMARY KEY  ,
    cognome                 VARCHAR(128)        NOT NULL,
    nome                    VARCHAR(128)        NOT NULL,
    sesso                   CHAR(1)             NOT NULL,
    data_nascita            DATE                NOT NULL,
    codice_fiscale          CHAR(16)            NOT NULL,
    id_ab                   INT                 NOT NULL,
    matricola               INT                 ,
    cittadinanza            VARCHAR(128)        ,
    email                   VARCHAR(128)        NOT NULL,
    note                    TEXT                ,
    UNIQUE (codice_fiscale)
);

-- Ogni tupla rappresenta un'utenza
-- Il mancato vincolo (NOT NULL) sulla chiave esterna (id_persona) permette l'esistenza delle utenze funzionali
CREATE TABLE IF NOT EXISTS usr
(
    id                      SERIAL PRIMARY KEY  ,
    login                   VARCHAR(256)        NOT NULL,
    passwd                  VARCHAR(256)        ,
    passwdForm              VARCHAR(256)        ,
    salt                    VARCHAR(256)        ,
    data_ultima_modifica    DATE                NOT NULL,
    ora_ultima_modifica     TIME                NOT NULL,
    id_usr_ultima_modifica  INT                 NOT NULL    REFERENCES usr (id), -- self-relationship
    id_persona              INT                             REFERENCES persona (id),
    id_ruolo                INT                             REFERENCES ruolo_applicativo (id),
    UNIQUE (login)
);

-- Ogni tupla rappresenta un gruppo di utenti
-- Siccome potrebbero essere previste delle funzioni di amministrazione 
-- (p.es. inserimento di nuovi gruppi), vengono aggiunti i campi di tracciamento della modifica
-- (data, ora e autore) 
CREATE TABLE IF NOT EXISTS grp
(
    id                      SERIAL PRIMARY KEY  ,
    nome                    VARCHAR(128)        NOT NULL,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    data_ultima_modifica    DATE                NOT NULL,
    ora_ultima_modifica     TIME                NOT NULL,
    id_usr_ultima_modifica  INT                 NOT NULL    REFERENCES usr (id),
    UNIQUE (nome)
);

-- Definisce la composizione dei gruppi
CREATE TABLE IF NOT EXISTS belongs 
(
   id_usr                   INT                 NOT NULL    REFERENCES usr (id),
   id_grp                   INT                 NOT NULL    REFERENCES grp (id),
   PRIMARY KEY(id_usr, id_grp)
);

-- Ogni tupla rappresenta un accesso al sistema
CREATE TABLE IF NOT EXISTS access_log
(
    id                      SERIAL PRIMARY KEY  ,
    data_ultimo_accesso     DATE                NOT NULL,
    ora_ultimo_accesso      TIME                NOT NULL,
    login                   VARCHAR(256)        NOT NULL    REFERENCES usr (login)
);

-- Ogni tupla rappresenta l'assegnazione di una persona a un ruolo giuridico
-- La relazione tra persona e ruolo giuridico è molti-a-molti
CREATE TABLE IF NOT EXISTS persona_ruolo
(
    id_persona             INT                  NOT NULL    REFERENCES persona (id),
    codice_csa             CHAR(2)              NOT NULL    REFERENCES ruolo_giuridico (codice_csa),
    informativa            VARCHAR(128)         ,
    PRIMARY KEY (id_persona, codice_csa)
);


-------------------------------------------
--    ENTITA' E RELAZIONI DEL DOMINIO    --
-------------------------------------------

-- Ogni tupla rappresenta una convenzione
CREATE TABLE IF NOT EXISTS convenzione
(
    id                      SERIAL PRIMARY KEY  ,
    titolo                  VARCHAR(1024)       NOT NULL,
    informativa             TEXT                ,
    note                    TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    data_approvazione       DATE                NOT NULL,
    nota_approvazione       TEXT                ,
    data_approvazione2      DATE                ,
    nota_approvazione2      TEXT                ,
    data_sottoscrizione     DATE                NOT NULL,
    nota_sottoscrizione     TEXT                ,
    data_scadenza           DATE                NOT NULL,
    nota_scadenza           TEXT                ,
    num_repertorio          VARCHAR(1024)       NOT NULL,
    link                    VARCHAR(1024)       ,
    carico_bollo            SMALLINT            ,
    bollo_pagato            BOOLEAN             ,
    data_ultima_modifica    DATE                NOT NULL,
    ora_ultima_modifica     TIME                NOT NULL,
    id_usr_ultima_modifica  INT                 NOT NULL    REFERENCES usr (id),
    id_tipo                 INT                 NOT NULL    REFERENCES tipo_convenzione (id),
    id_stato                INT                 NOT NULL    REFERENCES stato_convenzione (id),
    id_convenzione          INT                             REFERENCES convenzione (id) -- self-relationship
);

-- Ogni tupla rappresenta un contraente
CREATE TABLE IF NOT EXISTS contraente
(
    id                      SERIAL PRIMARY KEY  ,
    nome                    VARCHAR(1024)       NOT NULL,
    informativa             TEXT                ,
    ordinale                INT     DEFAULT 10  NOT NULL,
    codice_fiscale          VARCHAR(128)        ,
    partita_iva             VARCHAR(128)        ,
    email                   VARCHAR(128)        ,
    data_ultima_modifica    DATE                NOT NULL,
    ora_ultima_modifica     TIME                NOT NULL,
    id_usr_ultima_modifica  INT                 NOT NULL    REFERENCES usr (id),
    id_tipo                 INT                 NOT NULL    REFERENCES tipo_contraente (id)
);

-- Ogni tupla rappresenta un contributo economico
CREATE TABLE IF NOT EXISTS contributo_economico
(
    id                      SERIAL PRIMARY KEY  ,
    importo                 NUMERIC(12,2)       NOT NULL,
    data_versamento         DATE                ,
    motivazione             TEXT                ,
    data_ultima_modifica    DATE                NOT NULL,
    ora_ultima_modifica     TIME                NOT NULL,
    id_usr_ultima_modifica  INT                 NOT NULL    REFERENCES usr (id),
    id_convenzione          INT                 NOT NULL    REFERENCES convenzione (id),
    id_contraente           INT                 NOT NULL    REFERENCES contraente (id)
);

-- Relazione tra convenzione e finalità.
-- Le finalità di una convenzione: 
-- 1. non sono obbligatorie (non è necessario che almeno una sia specificata) 
-- 2. possono essere più di una per una stessa convenzione.
CREATE TABLE IF NOT EXISTS convenzione_finalita
(
    data_ultima_modifica    DATE DEFAULT CURRENT_DATE   NOT NULL,
    ora_ultima_modifica     TIME DEFAULT CURRENT_TIME   NOT NULL,
    id_usr_ultima_modifica  INT                         NOT NULL    REFERENCES usr (id),
    id_convenzione          INT                         NOT NULL    REFERENCES convenzione (id),
    id_finalita             INT                         NOT NULL    REFERENCES finalita (id),
    PRIMARY KEY (id_convenzione, id_finalita)
);

-- Relazione tra convenzione e contraenti.
-- Il contraente è parte integrante di una convenzione, per cui le date di validità di
-- questa relazione vengono ricavate dalle date (sottoscrizione e scadenza) della
-- convenzione stessa. 
-- Inoltre, qualora una convenzione venisse rinnovata sia pure con lo stesso contraente, 
-- cambierebbe l'id della convenzione stessa, da cui la consistenza della chiave primaria.
CREATE TABLE IF NOT EXISTS contraente_convenzione
(
    data_ultima_modifica    DATE DEFAULT CURRENT_DATE   NOT NULL,
    ora_ultima_modifica     TIME DEFAULT CURRENT_TIME   NOT NULL,
    id_usr_ultima_modifica  INT                         NOT NULL    REFERENCES usr (id),
    id_convenzione          INT                         NOT NULL    REFERENCES convenzione (id),
    id_contraente           INT                         NOT NULL    REFERENCES contraente (id),
    PRIMARY KEY (id_convenzione, id_contraente)
);

-- Relazione tra convenzione e persona.
-- Il referente NON è parte integrante di una convenzione.
-- Ciò significa che ha date di inizio e fine associazione indipendenti da quelle della convenzione,
-- che possono essere ereditate tramite meccanismi applicativi (hook tramite cui, quando
-- viene inserita una persona su una convenzione, prende di default come data inizio la data
-- di sottoscrizione della convenzione e come data fine la data di scadenza della convenzione)
-- ma che sono concettualmente ortogonali tra loro.
-- Ciò implica che un referente potrebbe scadere prima della data di scadenza della convenzione
-- o potrebbe essere attivato dopo la data di sottoscrizione. Inoltre, un referente potrebbe
-- potenzialmente anche essere riattivato a distanza di tempo. Pertanto, la chiave non 
-- è univocamente identificata dalla coppia PRIMARY KEY (id_convenzione, id_persona) 
-- ma è necessario rilassare questo vincolo introducendo una chiave numerica 
-- oppure inserendo in chiave anche le date.
CREATE TABLE IF NOT EXISTS referente
(
    id                      SERIAL PRIMARY KEY  ,
    data_inizio             DATE                NOT NULL,
    data_fine               DATE                NOT NULL,
    nota                    TEXT                ,
    data_ultima_modifica    DATE DEFAULT CURRENT_DATE   NOT NULL,
    ora_ultima_modifica     TIME DEFAULT CURRENT_TIME   NOT NULL,
    id_usr_ultima_modifica  INT                         NOT NULL    REFERENCES usr (id),
    id_convenzione          INT                                     REFERENCES convenzione (id),
    id_persona              INT                                     REFERENCES persona (id),
    UNIQUE (id_convenzione, id_persona, data_inizio, data_fine)
);

-- Relazione tra convenzione e gruppo di utenti.
-- Permette di stabilire ogni gruppo che diritti ha su una convenzione di dato id.
CREATE TABLE IF NOT EXISTS convenzione_grp
(
    id_convenzione          INT                         NOT NULL    REFERENCES convenzione (id),
    id_grp                  INT                         NOT NULL    REFERENCES grp (id),    
    notifica                BOOLEAN DEFAULT FALSE       NOT NULL,
    selezione               BOOLEAN DEFAULT FALSE       NOT NULL,
    aggiornamento           BOOLEAN DEFAULT FALSE       NOT NULL,
    inserimento             BOOLEAN DEFAULT FALSE       NOT NULL,
    eliminazione            BOOLEAN DEFAULT FALSE       NOT NULL,
    data_ultima_modifica    DATE DEFAULT CURRENT_DATE   NOT NULL,
    ora_ultima_modifica     TIME DEFAULT CURRENT_TIME   NOT NULL,
    id_usr_ultima_modifica  INT                         NOT NULL    REFERENCES usr (id),
    PRIMARY KEY (id_convenzione, id_grp)
);


------------------------
--        GRANT       --
------------------------
-- Diritti dell'OWNER
-- REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM gtorre;
GRANT ALL ON ALL TABLES IN SCHEMA public TO gtorre;
-- Diritti degli Amministratori
GRANT ALL ON ALL TABLES IN SCHEMA public TO epuddu;
GRANT ALL ON ALL TABLES IN SCHEMA public TO flimberto;
GRANT ALL ON ALL TABLES IN SCHEMA public TO golivieri;
GRANT ALL ON ALL TABLES IN SCHEMA public TO lfrigo;
-- Diritti dell'utente pubblico (www)
-- REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM www;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO www;
GRANT INSERT, UPDATE ON access_log, convenzione, contraente, contraente_convenzione, convenzione_finalita, referente, contributo_economico TO www;
-- Deve valorizzare il gruppo all'inserimento della convenzione; in futuro potrebbe essere utile anche l'aggiornamento
GRANT INSERT, UPDATE ON convenzione_grp TO www;
-- www: Permission to change the password!
GRANT UPDATE ON usr TO www;    


------------------------
--  CREAZIONE INDICI  --
------------------------
--
-- Postgres crea automaticamente gli indici per le chiavi primarie, ma non per le chiavi esterne, dove vanno esplicitati
-- Maschera di input: 
-- id_<nome_tabella_contenente_FK>_<nome_tabella_puntata_da_FK>_index ON <nome_tabella_contenente_FK>(<nome_chiave_esterna>)

-- INDEXES ON usr
CREATE INDEX IF NOT EXISTS id_usr_usr_index ON usr (id_usr_ultima_modifica); -- self-relationship
CREATE INDEX IF NOT EXISTS id_usr_persona_index ON usr (id_persona);
CREATE INDEX IF NOT EXISTS id_usr_ruolo_index ON usr (id_ruolo);

-- INDEXES ON login
CREATE INDEX IF NOT EXISTS id_login_usr_index ON usr (login);

-- INDEXES ON relazione tra persona e ruolo giuridico
CREATE INDEX IF NOT EXISTS id_personaruolo_persona_index ON persona_ruolo (id_persona);
CREATE INDEX IF NOT EXISTS id_personaruolo_codicecsa_index ON persona_ruolo (codice_csa);

-- INDEXES ON convenzione
CREATE INDEX IF NOT EXISTS id_convenzione_usr_index ON convenzione (id_usr_ultima_modifica);
CREATE INDEX IF NOT EXISTS id_convenzione_tipoconvenzione_index ON convenzione (id_tipo);
CREATE INDEX IF NOT EXISTS id_convenzione_statoconvenzione_index ON convenzione (id_stato);
CREATE INDEX IF NOT EXISTS id_convenzione_convenzione_index ON convenzione (id_convenzione); -- self-relationship    
-- CREATE INDEX IF NOT EXISTS id_convenzione_finalita_index ON convenzione (id_scopo);

-- INDEXES ON contraente
CREATE INDEX IF NOT EXISTS id_contraente_usr_index ON contraente (id_usr_ultima_modifica);
CREATE INDEX IF NOT EXISTS id_contraente_tipocontraente_index ON contraente (id_tipo);

-- INDEXES ON relazione tra convenzione e finalità
CREATE INDEX IF NOT EXISTS id_convenzionefinalita_usr_index ON convenzione_finalita (id_usr_ultima_modifica);
CREATE INDEX IF NOT EXISTS id_convenzionefinalita_convenzione_index ON convenzione_finalita (id_convenzione);
CREATE INDEX IF NOT EXISTS id_convenzionefinalita_finalita_index ON convenzione_finalita (id_finalita);    

-- INDEXES ON relazione tra contraenti e convenzione
CREATE INDEX IF NOT EXISTS id_contraenteconvenzione_usr_index ON contraente_convenzione (id_usr_ultima_modifica);
CREATE INDEX IF NOT EXISTS id_contraenteconvenzione_convenzione_index ON contraente_convenzione (id_convenzione);
CREATE INDEX IF NOT EXISTS id_contraenteconvenzione_contraente_index ON contraente_convenzione (id_contraente);

-- INDEXES ON referente
CREATE INDEX IF NOT EXISTS id_referente_usr_index ON referente (id_usr_ultima_modifica);
CREATE INDEX IF NOT EXISTS id_referente_convenzione_index ON referente (id_convenzione);
CREATE INDEX IF NOT EXISTS id_referente_persona_index ON referente (id_persona);

-- INDEXES ON contributo_economico
CREATE INDEX IF NOT EXISTS id_contributoeconomico_usr_index ON contributo_economico (id_usr_ultima_modifica);
CREATE INDEX IF NOT EXISTS id_contributoeconomico_convenzione_index ON contributo_economico (id_convenzione);
CREATE INDEX IF NOT EXISTS id_contributoeconomico_contraente_index ON contributo_economico (id_contraente);

-- INDEXES ON grp
CREATE INDEX IF NOT EXISTS id_grp_usr_index ON grp (id_usr_ultima_modifica);

-- INDEXES ON belongs
CREATE INDEX IF NOT EXISTS id_belongs_usr_index ON belongs (id_usr);
CREATE INDEX IF NOT EXISTS id_belongs_grp_index ON belongs (id_grp);

-- INDEXES ON convenzione_grp
CREATE INDEX IF NOT EXISTS id_convenzionegrp_convenzione_index ON convenzione_grp (id_convenzione);
CREATE INDEX IF NOT EXISTS id_convenzionegrp_grp_index ON convenzione_grp (id_grp);
CREATE INDEX IF NOT EXISTS id_convenzionegrp_usr_index ON convenzione_grp (id_usr_ultima_modifica);
