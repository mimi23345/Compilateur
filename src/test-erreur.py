# ============================================
# FICHIER DE TEST - ERREURS INTENTIONNELLES
# Pour tester la détection d'erreurs du compilateur
# ============================================

# --- ERREUR 1: Caractère invalide (lexicale) ---
x = 10 @ 20

# --- ERREUR 2: Chaîne non terminée (lexicale) ---
nom = "Bejaia

# --- ERREUR 3: if sans deux-points (syntaxique) ---
if x > 5
    print("x est grand")

# --- ERREUR 4: Parenthèse non fermée (syntaxique) ---
y = (10 + 20

# --- ERREUR 5: while mal formé (syntaxique) ---
while i < 10
    print(i)
    i++

# --- ERREUR 6: for sans 'in' (syntaxique) ---
for k range(5):
    print(k)

# --- ERREUR 7: switch sans accolade ouvrante (syntaxique) ---
switch (jour)
    case 1:
        print("Lundi")
        break
}

# --- ERREUR 8: case sans deux-points (syntaxique) ---
valeur = 100
switch (valeur) {
    case 50
        print("Petit")
        break
    default:
        print("Autre")
}

# --- ERREUR 9: Accolade non fermée (syntaxique) ---
switch (x) {
    case 1:
        print("Un")
        break

# --- ERREUR 10: elif sans if (syntaxique) ---
elif note >= 12:
    print("Assez bien")

# --- ERREUR 11: else sans if (syntaxique) ---
else:
    print("Sinon")

# --- ERREUR 12: Incrémentation invalide ---
compteur = 0
compteur+++

# --- ERREUR 13: Opérateur invalide ---
resultat = 10 ** 2

# --- ERREUR 14: Deux-points en trop ---
if x == 10::
    print("Dix")

# --- ERREUR 15: Mot-clé invalide ---
loop i in range(5):
    print(i)

# --- ERREUR 16: Expression invalide dans if ---
if:
    print("Condition vide")

# --- ERREUR 17: Affectation invalide ---
10 = x

# --- ERREUR 18: Crochet non fermé ---
liste = [1, 2, 3

# --- ERREUR 19: do/while incomplet ---
do
    print("Tour")
    j++

# --- ERREUR 20: break en dehors de switch ---
break

# ============================================
# FIN DU FICHIER DE TEST
# ============================================
print("Fin du fichier")