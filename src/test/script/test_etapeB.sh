#!/bin/bash

# --- Configuration ---
DECAC="./src/main/bin/decac"
# Mise à jour des chemins vers les tests de Contexte
PATH_INVALID="src/test/deca/context/invalid/"
PATH_VALID="src/test/deca/context/valid/"

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
declare -A file_paths        # Associe fichier -> nom affiché

ARGS=""
TOTAL_VALID=0
TOTAL_INVALID=0

# --- 1. Récupération des fichiers ---

add_files() {
    local path=$1
    local type=$2
    if [ -d "$path" ]; then
        while IFS= read -r file; do
            filename=$(basename "$file")
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

log_valid=""
log_invalid=""

passed_valid=0
passed_invalid=0

# Exécution de decac avec l'option -v (Verification) en plus de -P et -d
# 2>&1 permet de capturer stdout ET stderr (où s'affichent les erreurs de contexte)
output=$($DECAC -P -v -d $DECAC_OPTS $ARGS 2>&1)

# --- 3. Analyse des résultats ---

# Contrairement au parser, les erreurs de contexte ne sont pas toujours préfixées par [fichier].
# On itère donc sur les fichiers attendus et on cherche leur statut dans la sortie globale.

for fname in "${!file_expectations[@]}"; do
    expected="${file_expectations[$fname]}"
    display_name="${file_paths[$fname]}"
    
    # On cherche si le fichier a été "verified successfully" dans les logs
    # On échappe les crochets pour le grep car ils sont spéciaux en regex
    if echo "$output" | grep -q "\[$fname\].*Deca program verified successfully"; then
        status="SUCCESS"
    else
        status="FAILURE"
    fi

    current_log=""

    if [ "$expected" == "valid" ]; then
        if [ "$status" == "SUCCESS" ]; then
            current_log="[${GREEN}SUCCESS${NC}]  Succès attendu pour test_context sur $display_name"
            ((passed_valid++))
        else
            current_log="[${RED}FAILURE${NC}]  Échec inattendu pour test_context sur $display_name"
        fi
        log_valid+="$current_log"$'\n'

    elif [ "$expected" == "invalid" ]; then
        if [ "$status" == "FAILURE" ]; then
            current_log="[${GREEN}SUCCESS${NC}]  Échec attendu pour test_context sur $display_name"
            ((passed_invalid++))
        else
            current_log="[${RED}FAILURE${NC}]  Succès inattendu pour test_context sur $display_name"
        fi
        log_invalid+="$current_log"$'\n'
    fi
done

# --- 4. Affichage Groupé ---

# Affichage Invalides
echo -e "[${BLUE}INFO${NC}] [Invalides]"
if [ -n "$log_invalid" ]; then
    echo -e "$log_invalid" | sort
fi
echo -e "[${YELLOW}WARN${NC}] $passed_invalid/$TOTAL_INVALID passed"

# Affichage Valides
echo -e "[${BLUE}INFO${NC}] [Valides]"
if [ -n "$log_valid" ]; then
    echo -e "$log_valid" | sort
fi
echo -e "[${YELLOW}WARN${NC}] $passed_valid/$TOTAL_VALID passed"

# --- 5. Bilan Final ---

TOTAL_PASSED=$((passed_valid + passed_invalid))

echo ""
if [ "$TOTAL_PASSED" -eq "$TOTAL_FILES" ]; then
    echo -e "[${GREEN}SUCCESS${NC}] 🎉 Tous les tests de contexte sont passés avec succès !"
    exit 0
else
    FAILED=$((TOTAL_FILES - TOTAL_PASSED))
    echo -e "[${RED}FAILURE${NC}] $FAILED test(s) échoué(s)."
    exit 1
fi