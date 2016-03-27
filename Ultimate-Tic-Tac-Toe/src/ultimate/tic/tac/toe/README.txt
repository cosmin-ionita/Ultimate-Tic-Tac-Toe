Etapa_1

Nume echipa: Autobots
Numele bot-ului: CCAutobots

Membrii echipei: 

-> Ionita Costel-Cosmin 325CC
-> Pandelica Adrian-Eduard 325CC
-> Stan Cristiana-Stefania 325CC
-> Spiru Cristian 325CC


Pentru aceasta etapa echipa noastra a decis abordarea unei strategii procedurale orientata pe adaptabilitate.

Jocul incepe prin apelul functiei makeTurn de catre engine-ul pus la dispozitie pe platforma asociata jocului.
Aici primim ca parametru un obiect de tip field care contine, printre altele, si starea curenta a tablei de joc. 
Metoda atTheBeginning() returneaza true daca ne aflam la inceputul jocului (tabla complet goala) si trebuie sa facem prima mutare.
Am decis ca prima mutare sa o facem in casuta din stanga sus al microBoard-ului din centru (abordam o strategie prin care dorim
ca noi sa obtinem patratul din mijloc, fapt ce atrage dupa sine un numar relativ mare de sanse de castig).

Daca nu suntem la inceput, verificam daca suntem in situatia in care avem la dispozitie toata tabla (adversarul ne-a trimis sa mutam
intr-un patrat deja inchis), caz in care actionam astfel: cautam primul patrat pe care il putem inchide, si il inchidem. Daca nu exista
un astfel de patrat, atunci mutam in prima casuta disponibila.



