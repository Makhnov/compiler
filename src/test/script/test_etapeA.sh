#!/bin/bash

# --- Configuration ---
DECAC="./src/main/bin/decac"
PATH_INVALID="src/test/deca/syntax/invalid/"
PATH_VALID="src/test/deca/syntax/valid/"

# --- Couleurs et Formatage ---
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# --- Initialisation ---
if [ ! -f "$DECAC" ]; then
    echo -e "[${RED}ERROR${NC}] L'exécutable $DECAC est introuvable."
    exit 1
fi

declare -A file_expectations # Associe fichier -> "valid" ou "invalid"
declare -A file_paths        # Associe fichier -> chemin relatif pour affichage

ARGS=""
TOTAL_VALID=0
TOTAL_INVALID=0

# --- 1. Récupération des fichiers ---

# Fonction pour ajouter des fichiers à la liste
add_files() {
    local path=$1
    local type=$2
    if [ -d "$path" ]; then
        while IFS= read -r file; do
            filename=$(basename "$file")
            # On garde le chemin relatif par rapport au dossier de test pour l'affichage (ex: parser/test.deca)
            # Ici on prend juste le nom pour faire simple comme dans ton exemple
            file_expectations["$filename"]="$type"
            file_paths["$filename"]="$filename" 
            ARGS="$ARGS $file"
            
            if [ "$type" == "valid" ]; then ((TOTAL_VALID++)); else ((TOTAL_INVALID++)); fi
        done < <(find "$path" -name "*.deca")
    fi
}

add_files "$PATH_VALID" "valid"
add_files "$PATH_INVALID" "invalid"

TOTAL_FILES=$((TOTAL_VALID + TOTAL_INVALID))

if [ "$TOTAL_FILES" -eq 0 ]; then
    echo "Aucun fichier de test trouvé."
    exit 0
fi

# --- 2. Exécution Parallèle ---

# Variables pour stocker les logs afin de les afficher groupés plus tard
log_valid=""
log_invalid=""

passed_valid=0
passed_invalid=0
processed_count=0

# Exécution de decac
output=$($DECAC -P -p -d $DECAC_OPTS $ARGS 2>&1)

# Analyse ligne par ligne
while IFS= read -r line; do
    # Regex pour trouver [fichier.deca]
    if [[ "$line" =~ \[([a-zA-Z0-9_-]+\.deca)\] ]]; then
        fname="${BASH_REMATCH[1]}"
        
        # Détection du statut
        status=""
        if [[ "$line" == *"Parsing successful"* ]]; then
            status="SUCCESS"
        elif [[ "$line" == *"Parsing failed"* ]]; then
            status="FAILURE"
        fi

        if [ -z "$status" ]; then continue; fi

        expected="${file_expectations[$fname]}"
        display_name="${file_paths[$fname]}"
        
        # Construction du message de log
        current_log=""
        is_success=false

        if [ "$expected" == "valid" ]; then
            if [ "$status" == "SUCCESS" ]; then
                current_log="[${GREEN}SUCCESS${NC}]  Succès attendu pour test_syntax sur $display_name"
                ((passed_valid++))
                is_success=true
            else
                current_log="[${RED}FAILURE${NC}]  Échec inattendu pour test_syntax sur $display_name"
            fi
            # Ajout au buffer (avec saut de ligne)
            log_valid+="$current_log"$'\n'

        elif [ "$expected" == "invalid" ]; then
            if [ "$status" == "FAILURE" ]; then
                current_log="[${GREEN}SUCCESS${NC}]  Échec attendu pour test_syntax sur $display_name"
                ((passed_invalid++))
                is_success=true
            else
                current_log="[${RED}FAILURE${NC}]  Succès inattendu pour test_syntax sur $display_name"
            fi
            # Ajout au buffer
            log_invalid+="$current_log"$'\n'
        fi
        
        ((processed_count++))
    fi
done < <(echo "$output" | grep -E "Parsing successful|Parsing failed")

# --- 3. Affichage Groupé ---

# Affichage Invalides
echo -e "[${BLUE}INFO${NC}] [Invalides]"
if [ -n "$log_invalid" ]; then
    echo -e "$log_invalid" | sort # Tri alphabétique pour faire propre
fi
echo -e "[${YELLOW}WARN${NC}] $passed_invalid/$TOTAL_INVALID passed"

# Affichage Valides
echo -e "[${BLUE}INFO${NC}] [Valides]"
if [ -n "$log_valid" ]; then
    echo -e "$log_valid" | sort
fi
echo -e "[${YELLOW}WARN${NC}] $passed_valid/$TOTAL_VALID passed"

# --- 4. Bilan Final ---

TOTAL_PASSED=$((passed_valid + passed_invalid))

echo ""
if [ "$TOTAL_PASSED" -eq "$TOTAL_FILES" ]; then
    echo -e "[${GREEN}SUCCESS${NC}] 🎉 Tous les tests de syntaxe sont passés avec succès !"
    exit 0
else
    FAILED=$((TOTAL_FILES - TOTAL_PASSED))
    # On vérifie si des fichiers ont manqué à l'appel (crash)
    if [ "$processed_count" -lt "$TOTAL_FILES" ]; then
        MISSING=$((TOTAL_FILES - processed_count))
        echo -e "[${RED}FAILURE${NC}] $FAILED test(s) échoué(s) et $MISSING test(s) crashé(s) (pas de sortie)."
    else
        echo -e "[${RED}FAILURE${NC}] $FAILED test(s) échoué(s)."
    fi
    exit 1
fi