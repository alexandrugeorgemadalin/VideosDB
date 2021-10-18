# Object Oriented Programming Course
# VideosDB
## Implementare
Pentru fiecare tip de actiune: comanda, query, recomandare am creat cate un
pachet.

- Pachetul Commands contine o clasa numita la fel ce contine trei metode. Metodele
sunt urmatoarele :
    - 1. addfavorite - verifica daca videoclipul primit ca parametru exista in 
    lista de videoclipuri favorite ale user-ului si in caz negativ il adauga
    daca user-ul a vazut video-ul.
    - 2. view - verifica daca user-ul a vazut videoclipul primit ca parametru; in
    caz pozitiv, incrementeaza numarul de vizionari iar in caz negativ il adauga
    il lista.
    - 3. ratingmovie/ratingshow - aceste metode primesc ca parametrii doua liste
    in care se salveaza fiecare rating dat pentru filme/seriale pe care o sa le
    folosim mai tarziu pentru rezolvarea celorlalte cerinte ale temei.

- Pachetul Query contine:
  - Clasa Query - in care se calculeaza rating-ul mediu pentru fiecare film/serial
  Cate o clasa pentru fiecare tip de Query: pentru actori, videoclipuri si useri

  - Clasa ActorsQuery - contine o clasa interna ce retine pentru fiecare actor 
  numele, rating-ul mediu si numarul de premii primite si metodele :
    - 1. average - calculeaza pentru fiecare actor media rating-urilor filmelor si
    serialelor in care a jucat. In functie de tipul sortatii, ii sorteaza dupa
    media rating-urilor si alfabetic.
    - 2. awards - calculeaza pentru fiecare actor numarul de premii primite dintre
    cele primite ca parametru si ii sorteaza in functie de tipul sortarii.
    - 3. filter - pentru fiecare actor verifica in descrierea acestuia daca 
    contine cuvintele din filtru si creeaza un ArrayList cu numele acestora pe
    care il sorteaza in functie de tipul sortarii.

  - Clasa VideoQuery - contine o clasa interna ce retine pentru fiecare film sau
  serial numele, durata, rating-ul, genul, numarul de vizionari si de cate ori
  se regaseste in listele de favorite ale utilizatorilor si metodele:
    - 1. getdata - aceasta metoda primeste 2 liste in care calculam si salvam
    toate datele prezentate mai sus pentru filme si pentru seriale.
    - 2. rating - sorteaza baza de date construita mai sus  primita ca parametru
    dupa rating-uri si verifica ce filme/seriale se potrivesc filtrelor primite
    si construieste un arraylist cu acestea pe care le va scrie.
    - 3. longest - aceeasi idee de mai sus doar ca initial sortarea se face dupa
    dupa durata filmelor/serialelor
    - 4. mostviewed - aceeasi idee de mai sus, initial sortarea se face dupa 
    numarul de vizionari ale filmului/serialului
    - 5. favorite - sortarea se face initial  dupa numarul de aparitii in listele
    de favorite ale utilizatorilor

  - Clasa UsersQuery - contine o clasa interna ce retine username-ul si numarul
  de rating-uri pe care le-a dat si metoda:
    - 1. ratingsnumber - ce parcurge baza de date construita anterior ce continea
    pentru fiecare rating cine l-a dat si astfel calculeaza pentru fiecare
    utilizator cate rating-uri a dat si ii sorteaza in functie de acest criteriu

- Pachetul Recommendations contine:
  - Clasa BasicUser - contine o clasa interna Video ce retine pentru fiecare video
  numele si rating-ul si metodele:
    - 1. standard - aceasta metoda parcurge lista de filme/seriale primita ca date
    de intrare si intoarce primul video ce nu a fost vazut de user-ul respectiv
    - 2. bestunseen - aceasta metoda parcurge lista de filme/seriale primita ca 
    parametru si creaza un ArrayList<Video> in care salveaza toate filmele sau 
    serialele nevazute de user si rating-urile acestora pe care le sorteaza apoi
    si returneaza cel mai bun video in functie de rating

  - Clasa PremiumUser - contine o clasa interna Video ce retine pentru fiecare 
  video numele, rating-ul, numarul de vizionari si genul videoclipului si 
  metodele:
    - 1. search - aceasta metoda parcurge lista de filme si seriale si creaza un
    ArrayList<Video> in care salveaza numele si rating-ul filmelor nevazute de
    un user pe care il sorteaza dupa rating-ul si apoi dupa nume. 
    - 2. favorite - parcurge lista de filme si seriale si creaza ca metoda de mai
    sus o lista in care retine numele filmului si numarul de aparitii in listele
    de favorite ale utilizatorilor pe care o sorteaza descrescator dupa numarul
    de aparitii in listele de favorite si intoarce primul element ce apare
    in cel putin o lista de favorite ale unui utilizator si nu este vazut de 
    utilizatorul primit ca parametru.
    - 3. popular - prima data creaza o lista cu toate genurile intalnite la 
    filmele si serialele primite ca date de intrare. Apoi pentru fiecare gen
    gasit, parcurge din nou listele filmelor si serialelor si calculeaza cate
    filme apartin acelui gen si salveaza intr-un arraylist<Video> genul si 
    numarul filmelor din acel gen. Acest arraylist este sortat dupa numarul
    de filme din fiecare gen si returneaza primul videoclip nevazut din cel mai
    popular gen.
