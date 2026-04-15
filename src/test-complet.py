
# TEST COMPLET - Mini Compilateur Python
# Syntaxe correcte 


# --- 1. VARIABLES & AFFECTATION ---
x = 10
y = 25
nom = "Bejaia"
actif = True
z = x + y
score = 100

# --- 2. COMPARAISON (==, !=) ---
if x == 10:
    print("x vaut 10")

if y != 20:
    print("y n'est pas egal a 20")

# --- 3. INCRÉMENTATION ---
compteur = 0
compteur++
compteur--
total = compteur + 1

# --- 4. IF / ELIF / ELSE (avec deux-points, SANS accolades) ---
note = 15

if note >= 16:
    print("Tres bien")
elif note >= 14:
    print("Bien")
elif note >= 12:
    print("Assez bien")
else:
    print("A revoir")

# --- 5. WHILE (avec deux-points, SANS accolades) ---
i = 3
while i > 0:
    print("Compte a rebours")
    i--

# --- 6. DO / WHILE (style C avec accolades) ---
j = 0
do
    print("Tour do while")
    j++
while j < 3

# --- 7. FOR / IN / RANGE (avec deux-points, SANS accolades) ---
for k in range(5):
    print("Iteration for")

# --- 8. SWITCH / CASE (avec ACCOLADES comme en C) ---
jour = 3

switch (jour) {
    case 1:
        print("Lundi")
        break
    case 2:
        print("Mardi")
        break
    case 3:
        print("Mercredi")
        break
    case 4:
        print("Jeudi")
        break
    case 5:
        print("Vendredi")
        break
    default:
        print("Week-end")
}

# --- 9. SWITCH AVEC EXPRESSION ---
valeur = 100

switch (valeur) {
    case 50:
        print("Petit")
        break
    case 100:
        print("Moyen")
        break
    case 200:
        print("Grand")
        break
    default:
        print("Inconnu")
}

# --- 10. IF IMBRIQUÉS ---
a = 5
b = 10

if a < b:
    if a != 0:
        print("a est positif et inferieur a b")
    else:
        print("a est nul")
else:
    print("a est superieur ou egal a b")

# --- 11. OPÉRATEURS LOGIQUES ---
est_etudiant = True
a_la_moyenne = True

if est_etudiant and a_la_moyenne:
    print("Eligible a la bourse")

if not est_etudiant or a_la_moyenne:
    print("Verification supplementaire")

# --- 12. AFFECTATIONS COMPOSÉES ---
somme = 0
somme += 10
somme -= 5
resultat = somme * 2

# ============================================
# FIN DU TEST
# ============================================
print("Tous les tests ont ete executes")