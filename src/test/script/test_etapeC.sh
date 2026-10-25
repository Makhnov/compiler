#!/bin/bash

# --- Configuration ---
PATH=./src/main/bin:"$PATH"
DECAC="./src/main/bin/decac"
IMA="ima" # Assurez-vous que 'ima' est dans votre PATH, sinon mettez le chemin absolu

# Chemins vers les tests de Génération de Code
# PATH_INVALID="src/test/deca/codegen/invalid/created/sans-objet/"
# PATH_VALID="src/test/deca/codegen/valid/created/sans-objet/"

PATH_INVALID="src/test/deca/codegen/invalid/created/"
PATH_VALID="src/test/deca/codegen/valid/created/"

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

# Vérification de la présence de ima
if ! command -v $IMA &> /dev/null; then
    echo -e "[${RED}ERROR${NC}] La machine virtuelle '$IMA' est introuvable dans le PATH."
    exit 1
fi

declare -A file_expectations # Associe fichier -> "valid" ou "invalid"
declare -A file_paths        # Associe fichier -> nom affiché
declare -A file_full_paths   # Associe fichier -> chemin complet (pour lecture sortie attendue)

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
            file_full_paths["$filename"]="$file"
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

# --- 2. Compilation Groupée ---

# On compile tout d'un coup pour tester la robustesse du compilateur
# 2>&1 > /dev/null pour cacher la sortie standard de compilation (on vérifie l'existence du .ass après)
$DECAC -P -d $DECAC_OPTS $ARGS > /dev/null 2>&1

# --- 3. Exécution et Vérification ---

log_valid=""
log_invalid=""

passed_valid=0
passed_invalid=0

# Fichiers temporaires pour la comparaison
EXPECTED_TMP=$(mktemp)
ACTUAL_TMP=$(mktemp)

# Fonction de nettoyage (supprime les .ass générés et les tmp)
cleanup() {
    find "$PATH_VALID" "$PATH_INVALID" -name "*.ass" -delete
}
trap cleanup EXIT

for fname in "${!file_expectations[@]}"; do
    expected_type="${file_expectations[$fname]}"
    display_name="${file_paths[$fname]}"
    full_path="${file_full_paths[$fname]}"
    
    # Chemin du fichier .ass attendu (même dossier que le .deca)
    ass_file="${full_path%.deca}.ass"
    
    current_log=""
    status="" # SUCCESS ou FAILURE

    # 1. Vérification de la compilation (existence du .ass)
    if [ ! -f "$ass_file" ]; then
        # --- MODIFICATION ICI ---
        if [ "$expected_type" == "invalid" ]; then
            # Si le fichier est invalide et ne compile pas, c'est un succès (rejeté à la compilation)
            status="SUCCESS"
            current_log="[${GREEN}SUCCESS${NC}]  Rejeté à la compilation (attendu) pour $display_name"
        else
            # Si le fichier est valide et ne compile pas, c'est un échec
            status="FAILURE"
            current_log="[${RED}FAILURE${NC}]  Compilation échouée (pas de .ass) pour $display_name"
        fi
    else
        # 2. Exécution avec IMA (si la compilation a réussi)
        ima_output=$($IMA "$ass_file" 2>&1)
        ima_exit_code=$?

        if [ "$expected_type" == "valid" ]; then
            # --- Cas VALIDE ---
            
            # Extraction de la sortie attendue depuis le fichier .deca

            sed -n '/\/\/ Resultats:/,/\/\/ Historique:/p' "$full_path" | \
            sed '1d;$d' | \
            sed '${/^\/\/ *$/d;}' | \
            sed 's/^\/\/    //' | sed 's/^\/\///' > "$EXPECTED_TMP"


            echo "$ima_output" > "$ACTUAL_TMP"

            # Comparaison
            if diff -w -B -q "$EXPECTED_TMP" "$ACTUAL_TMP" > /dev/null; then
                status="SUCCESS"
                current_log="[${GREEN}SUCCESS${NC}]  Exécution conforme pour $display_name"
            else
                status="FAILURE"
                current_log="[${RED}FAILURE${NC}]  Sortie incorrecte pour $display_name"

                # --- AJOUT DEBUG ---
                # echo "---------------------------------------------------"
                # echo "DEBUG: ECHEC pour $display_name"
                # echo ">>> ATTENDU (depuis .deca) :"
                # cat -e "$EXPECTED_TMP"
                # echo ">>> RECU (depuis ima) :"
                # cat -e "$ACTUAL_TMP"
                # echo ">>> DIFF :"
                # diff "$EXPECTED_TMP" "$ACTUAL_TMP"
                # echo "---------------------------------------------------"
                # -------------------
            fi

        elif [ "$expected_type" == "invalid" ]; then
            # --- Cas INVALIDE (Runtime Error) ---
            # Si le fichier a compilé, on vérifie maintenant s'il plante à l'exécution
            
            if [ $ima_exit_code -ne 0 ] || [[ "$ima_output" == *"Error"* ]]; then
                status="SUCCESS"
                current_log="[${GREEN}SUCCESS${NC}]  Erreur runtime détectée (attendue) pour $display_name"
            else
                status="FAILURE"
                current_log="[${RED}FAILURE${NC}]  Pas d'erreur runtime détectée pour $display_name"
            fi
        fi
    fi

    # Enregistrement du résultat
    if [ "$expected_type" == "valid" ]; then
        if [ "$status" == "SUCCESS" ]; then
            ((passed_valid++))
        fi
        log_valid+="$current_log"$'\n'
    else
        if [ "$status" == "SUCCESS" ]; then
            ((passed_invalid++))
        fi
        log_invalid+="$current_log"$'\n'
    fi

done

# --- 4. Affichage Groupé ---

# Affichage Invalides
echo -e "[${BLUE}INFO${NC}] [Invalides (Erreurs Compilation ou Runtime)]"
if [ -n "$log_invalid" ]; then
    echo -e "$log_invalid" | sort
fi
echo -e "[${YELLOW}WARN${NC}] $passed_invalid/$TOTAL_INVALID passed"

# Affichage Valides
echo -e "[${BLUE}INFO${NC}] [Valides (Sortie Conforme)]"
if [ -n "$log_valid" ]; then
    echo -e "$log_valid" | sort
fi
echo -e "[${YELLOW}WARN${NC}] $passed_valid/$TOTAL_VALID passed"

# --- 5. Bilan Final ---

TOTAL_PASSED=$((passed_valid + passed_invalid))

echo ""
if [ "$TOTAL_PASSED" -eq "$TOTAL_FILES" ]; then
    echo -e "[${GREEN}SUCCESS${NC}] 🎉 Tous les tests de génération de code sont passés avec succès !"
    exit 0
else
    FAILED=$((TOTAL_FILES - TOTAL_PASSED))
    echo -e "[${RED}FAILURE${NC}] $FAILED test(s) échoué(s)."
    exit 1
fi