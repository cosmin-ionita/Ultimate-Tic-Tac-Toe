Student1: Ionita Costel-Cosmin
Student2: Pandelica Adrian-Eduard
Student3: Spiru Vasile-Cristian
Student4: Stan Cristiana-Stefania

Grupa: 325CC

			Ultimate Tic Tac Toe - etapa 2

	
	Pentru aceasta etapa a proiectului, am ales sa implementam un algoritm
care se bazeaza pe folosirea urmatoarei tehnici: aplicam Monte Carlo pe primele mutari,
iar apoi aplicam Negamax cu Alpha-Beta pruning. Am ales aceasta abordare,
deoarece pentru primele mutari, negamaxul nu functiona corect intrucat nu are
o adancime suficient de mare ca sa poate anticipa mutari semnificative, care sa 
influenteze puternic decursul jocului.

	De asemenea, mentionam ca am decis sa pastram functiile implementate
pentru prima etapa a proiectului, chiar daca nu toate acestea sunt apelate pe
parcursul algoritmilor folositi. Am decis sa facem acest lucru, deoarece unele
dintre ele ne-au fost folositoare in continuare si nu am mai sters niciuna.

Pentru primele 24 de mutari, algoritmul folosit este o adapatare a 
algoritmului Monte Carlo.
	Pentru fiecare posibilitate din MicroBoard, se calculeaza un scor, iar
fiecare mutare care are scorul maxim este pusa intr - o lista.
	Se alege fiecare element din lista si se aplica Monte Carlo: Se alege random
o miscare dintre toate cele posibile avand scor maxim si se updateaza scorul total al
mutarii initiale.
	Adancimea algoritmului este de 10 pasi. In final, se alege mutarea care are 
cel mai mare scor total.
	Metoda make_Monte primeste starea curenta a jocului si MicroBoardul in care
trebuie sa mute. Pentru fiecare miscare posibila, calculeaza un scor. Fiecare miscare cu 
scor maxim este pusa intr-o lista.
	Pentru fiecare element din lista obtinuta, metoda monteCarlo aplica algoritmul:
alege la intamplare o miscare din cele cu scor maxim, o aplica si continua procesul. In final
se updateaza scorul total al fiecarei miscari initiale. Generarea miscariilor la intamplare
se realizeaza cu ajutorul metodei random_value.
	In final se alege miscarea din lista care are cel mai mare scor total, in cazul in care 
sunt mai multe, se alege ultima miscare.

	In ceea ce priveste implementarea algoritmului de alpha-beta am
procedat astfel: la intrarea in functie, verificam starea lui deph. In cazul in
care depth-ul este 0 inseamna ca am atins adancimea maxima a minimax-ului
si returnam rezultatul intors de functia de evaluarea. De asemenea, returnam
acelasi rezultat si in cazul in care nu mai avem mutari disponibile de facut
(s-a terminat jocul). In cazul in care nu am ajuns la finalul depth-ului, avem
urmatoarele cazuri: daca functia de evaluarea a intros rezultatul -1000 sau 
1000, inseamna ca un player a castigat jocul (este scorul pe care noi l-am 
atribuit jucatorului care inchide jocul), asa ca returnam direct acest rezultat
pentru ca am ajuns la cel mai bun/rau caz pentru noi si nu mai are sens sa 
analizam si alte mutari. 
	Daca nu ne aflam pe niciunul dintre cazurile descrise mai sus, inseamna
ca trebuie sa inaintam in adancime, continuand cu verificarea tuturor mutarilor
disponibile (similar cu algoritmul folosit la laboratorul de minimax). Inainte
de a face acest lucru, clonam starea curenta a tablei de joc, pentru a nu 
altera adevarata tabla a jocului.
	Pentru functia de evaluare am decis sa procedam astfel: am stabilit
id-ul plyaer-ului curent si al adversarului sau si am ales sa atribuim 
urmatoarele scoruri (este analizata tabla mare ca un X si O normal). O casuta
luata de un anumit player inseamna un joc mic castigat de player-ul respectiv.

- pentru liniile/coloanele sau diagonalele pe care player-ul are o casuta luata   
  si celelalte sunt inca disponibile de jucat, avem scorul 10
- pentru liniile/coloanele sau diagonalele pe care player-ul are 2 casute
  luate si cea de-a treia este libera de jucat, avem scorul 100
- pentru liniile/coloanele sau diagonalele inchise de player, avem scorul 1000
  adica player-ul a castigat jocul
- pentru celelalte cazuri de linii/coloane sau diagonale avem scorul 0
	
    Toate aceste scoruri se adauga (se analizeaza toate liniile, coloanele
si diagonalele pentru stabilirea scorului corespunzator fiecareia). La finalul
analizei, se returneaza scorul obtinut, in cazul in care player nu a castigat
jocul, caz in care se returneaza doar 1000.