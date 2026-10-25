#!/usr/bin/env bash

DECAC="./src/main/bin/decac"
. ./src/test/script/tools/common.sh
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "--- Début du test des arguments ---"


touch test.deca #fichier de test 
touch test.txt #fichier de test 

# --- TEST 1 : L'extension .deca est-elle obligatoire ? ---
echo -n "Vérification extension .txt (doit échouer) : "
$DECAC test.txt > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (Échec détecté comme prévu)${NC}"
else
    echo -e "${RED}KO (Le compilateur a accepté un .txt alors qu'il ne devrait pas)${NC}"
fi

# --- TEST 2 : L'option -b (banner) fonctionne-t-elle ? ---
echo -n "Vérification option -b (doit réussir) : "
$DECAC -b > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${RED}KO${NC}"
fi

# --- TEST 3 : Incompatibilité -p et -v (doit échouer) ---
echo -n "Vérification incompatibilité -p et -v : "
$DECAC -p -v test.deca > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (Échec détecté comme prévu)${NC}"
else 
    echo -e "${RED}KO (Le compilateur a accepté -p et -v en même temps alors qu'ils sont incompatibles)${NC}"
fi

# --- TEST 4 : Vérification -r avec valeur trop petite (doit échouer) ---
echo -n "Vérification -r avec valeur trop petite : "
$DECAC -r 2 test.deca > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (Erreur détectée pour -r 2)${NC}"
else
    echo -e "${RED}KO (Le compilateur a accepté -r 2 alors que le minimum est 4)${NC}"
fi

# --- TEST 5 : Vérification -r avec valeur trop grande (doit échouer) ---
echo -n "Vérification -r avec valeur trop grande : "
$DECAC -r 17 test.deca > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (Erreur détectée pour -r 17)${NC}"
else
    echo -e "${RED}KO (Le compilateur a accepté -r 17 alors que le maximum est 16)${NC}"
fi

# --- TEST 6 : Vérification -r avec valeur correcte ---
echo -n "Vérification -r avec valeur correcte : "
$DECAC -r 14 test.deca > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}OK (Correcte pour -r 14)${NC}"
else
    echo -e "${RED}KO (Le compilateur n'a pas accepté -r 14 alors que 4<=14<=16)${NC}"
fi


# --- TEST 7 : Option inexistante (doit échouer) ---
echo -n "Vérification option inconnue (-z) : "
$DECAC -z test.deca > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (Option inconnue rejetée)${NC}"
else
    echo -e "${RED}KO (Le compilateur a acceptée l'option -z)${NC}"
fi

# --- TEST 8 : Ajout d'une option en plus de b (doit échouer) ---
echo -n "Vérification -b fonctionne uniquement seul (sans autres options et sans fichier)"
$DECAC -b -v > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (La commande -b -v a été rejetée)${NC}"
else
    echo -e "${RED}KO (Le compilateur a acceptée une option en plus de -b)${NC}"
fi

echo -n "Test -b avec fichier (doit échouer) : "
$DECAC -b test.deca > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (La commande a été rejetée)${NC}"
else
    echo -e "${RED}KO (Le compilateur a acceptée un fichier en plus de -b)${NC}"
fi

# --- TEST 9 : Test doublon de fichier  ---
echo -n "Test doublon (decac test.deca test.deca) : "
$DECAC test.deca test.deca > /dev/null 2>&1


if [ $? -eq 0 ]; then
    echo -e "${GREEN}OK (Pas d'erreur)${NC}"
else
    echo -e "${RED}KO (Les doublons ne sont pas acceptés donc erreur)${NC}"
fi

# --- TEST 10 : Aucun affichage en l'absence des options -b, -p et -w  ---
echo "" > test_vide.deca #Programme valide
echo -n "Vérification du silence en cas de succès : "
OUTPUT=$($DECAC test_vide.deca)
if [ -z "$OUTPUT" ]; then
    echo -e "${GREEN}OK (Compilation silencieuse)${NC}"
    rm -f test_vide.deca
else
    echo -e "${RED}KO (Affichage détecté malgré le succès)${NC}"
    rm -f test_vide.deca
fi

# --- TEST 11 : Possibilité de répéter l'option -d  :---
echo -n "Vérification option -d répétée : "
$DECAC -d -d -d test.deca > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}OK (Pas d'erreur, il est bien possible de répéter -d)${NC}"
else
    echo -e "${RED}KO (Le compilateur n'accepte pas la répétition de l'option -d)${NC}"
fi

# --- TEST 12 : Possibilité d'utiliser  la combinaison -v et n :---
echo -n "Vérification combinaison -v et -n : "
$DECAC -v -n test.deca > /dev/null 2>&1
if [ $? -eq 0 ]; then 
    echo -e "${GREEN}OK (La combinaison est bien valide et acceptée par le compilateur)${NC}"
else 
    echo -e "${RED}KO (Le compilateur n'accepte pas la combinaison -v -n)${NC}"
fi

# --- TEST 13 : Possibilité d'utiliser  la combinaison -v et n :---

echo -n "Vérification option -p sans fichier (doit échouer ou aide) : "
$DECAC -p > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo -e "${GREEN}OK (Rejeté car manque fichier)${NC}"
else
    echo -e "${RED}KO (Le compilateur a accepté -p sans fichier)${NC}"
fi


# --- TEST 14 : Vérification de l'option -P  :---
echo -n "Vérification de l'option -P (parallel) : "
touch test1.deca test2.deca
$DECAC -P test1.deca test2.deca > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}OK (L'option -P est acceptée par le compilateur)${NC}"
else
    echo -e "${RED}KO (L'option n'est pas acceptée par le compilateur)${NC}"
fi

# Nettoyage
rm -f test.deca test.txt test.ass test_vide.ass test1.deca test2.deca test1.ass test2.ass

echo "--- Fin du test des arguments ---"