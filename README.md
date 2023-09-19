# Game Board Implementation

[![License: MIT][license-image]][license]

Final project of "**Ingegneria del Software**" 2021-2022 held by Professor <a href="https://cugola.faculty.polimi.it/">**Gianpaolo Cugola**</a> at Politecnico di Milano.

![alt text](src/main/resources/Eriantys.jpg)

Il progetto consiste nell'implementazione del gioco da tavolo [**ERIANTYS**](http://www.craniocreations.it/prodotto/eriantys/) in linguaggio Java.<br>
In particolare si tratta di un sistema distribuito composto da un singolo server in grado di gestire più partite contemporaneamente e multipli client (CLI o GUI) che possono partecipare ad una sola partita alla volta.<br>
Sono stati utilizzati il pattern MVC (Model-View-Controller) per progettare l'intero sistema e i socket TCP per la gestione della rete.<br>
Server e client si scambiano messaggi in formato Json per una migliore gestione della comunicazione.

#### Regole del gioco:
- [Italiano](src/main/resources/documents/eriantys_regole.pdf)
- [English](src/main/resources/documents/eriantys_rules.pdf)

#### Requisiti del progetto:
- [Requisiti](src/main/resources/documents/requirements.pdf)

## Documentazione

### UML
I seguenti diagrammi delle classi rappresentano rispettivamente il diagramma UML riassuntivo e il diagramma UML di dettaglio che mostrano come è stato progettato il software:
- [UML alto livello](deliverables/uml_high_level)
- [UML basso livello](deliverables/uml_low_level)

### Peer review
Review al progetto del gruppo 10:
- [Peer review 1](deliverables/peer_review/review1.pdf)
- [Peer review 2](deliverables/peer_review/review2.pdf)

Review del gruppo 55 al nostro pregetto:
- [Peer review 1](deliverables/peer_review/ReviewGC65.pdf)
- [Peer review 2](deliverables/peer_review/ReviewGC65_Connessione.pdf)

### JavaDoc
La seguente documentazione include una descrizione delle classi e dei metodi utilizzati, può essere consultata al seguente indirizzo: [Javadoc](deliverables/javadoc)

### Test di unità
I test di Junit puntano a massimizzare la coverage del model (99%) e del controller (100%). Al seguente link è possibile consultare il report della coverage dei test effettuati: [Report](deliverables/report)

### Librerie e Plugins
|Libreria/Plugin|Descrizione|
|---------------|-----------|
|__Maven__|Strumento di automazione della compilazione utilizzato principalmente per progetti Java.|
|__Swing__|Framework utilizzato per lo svilppo di interfacce grafiche.|
|__Gson__|Libreria per il supporto al parsing di file in formato json.|
|__JUnit__|Framework di unit testing.|
|__Jacoco__|Strumento di supporto al testing per evidenziare le linee di codice coperte dagli unit test.|

## Funzionalità

### Funzionalità Sviluppate
- __Regole Complete:__ si considerino le regole per lo svolgimento di partite da 2 o da 3 giocatori,
includendo la variante per esperti (si richiede l'implementazione di almeno 8 diverse carte personaggio,
a scelta del gruppo).
- __CLI:__ vengono utilizzati ANSI escape codes per stampare lo stato del gioco sul terminale e riscrivere sopra allo stato del turno precedente per una migliore compensione di esso.
- __GUI:__ realizzata con la libreria Swing.
- __Socket:__ gestisce la comunicazione tra il server e i client tramite una connessione TCP.
- __3 Funzionalità Avanzate:__
    - __Carte personaggio:__ Implementazione di tutte e 12 le carte personaggio.
    - __Partita a 4 giocatori:__ Implementazione delle partite a 2, 3 o 4 giocatori.
    - __Partite multiple:__ Realizzazione del server in modo che possa gestire più partite contemporaneamente.

## Esecuzione dei JAR
Questo progetto richiede una versione di Java 17 o superiore per essere eseguito correttamente.

### Client
Il client viene eseguito scegliendo l'interfaccia con cui giocare, le possibili scelte sono da linea di comando (CLI) o interfaccia grafica (GUI).

- #### CLI
Per una migliore esperienza di gioco da linea di comando è necessario lanciare il client con un terminale che supporti la codifica UTF-8 e gli ANSI escape codes. Per lanciare il client in modalità CLI digitare su terminale il seguente comando:
```
java -jar clientcli.jar
```

- #### GUI
Per lanciare il client in modalità GUI sono isponibili due opzioni:
- effettuare doppio click sull'eseguibile clientgui.jar
- digitare su terminale il seguente comando:
```
java -jar clientgui.jar
```

### Server
Per lanciare il server digitare su terminale il seguente comando:
```
java -jar server.jar [<port_number>]
```
Se non si specifica il valore della porta viene usato il valore di default: __61863__.

## Componenti del gruppo
- [__Luca Petrucci__](https://github.com/l-pt)
- [__Simone Ponginibbio__](https://github.com/simoneponginibbio)
- [__Paolo Secci__](https://github.com/paolosecci)
- [__Matteo Passoni__](https://github.com/matteopassoni)

<h2>Copyright and license</h2>

This project is copyright 2023.

Licensed under the **[MIT License][license]**; you may not use this software except in compliance with the License.

[license]: https://github.com/simoneponginibbio/Board_Game_Implementation/blob/main/LICENSE
[license-image]: https://img.shields.io/badge/License-MIT-blue.svg
