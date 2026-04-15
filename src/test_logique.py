# test_logique.py
# Tests pour les opérateurs logiques : and, or, not

# 1. Cas de base
print(True and False)      # Attendu : False
print(True and True)       # Attendu : True

print(True or False)       # Attendu : True
print(False or False)      # Attendu : False

print(not True)            # Attendu : False
print(not False)           # Attendu : True

# 2. Priorité des opérateurs (not > and > or)
print(not True and False)  # Attendu : False  → équivaut à (not True) and False
print(not (True and False))# Attendu : True

# 3. Mélange avec des comparaisons
x = (5 > 3) and (2 < 4)
print(x)                   # Attendu : True

y = (10 == 10) or (3 > 5)
print(y)                   # Attendu : True

# 4. Chaînage sans parenthèses
z = True and False or True
print(z)                   # Attendu : True  → (True and False) or True