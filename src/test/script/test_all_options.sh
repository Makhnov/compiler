#!/bin/bash

# Auteur : gl25
# Date : 16/01/2026
#
# Script pour tester tous les fichiers .deca valides avec toutes les combinaisons d'options
# afin de maximiser la couverture JaCoCo.
#
# Utilise decac -P pour compiler tous les fichiers en parallèle (beaucoup plus rapide)
# Vérifie aussi que l'exécution avec ima produit le bon résultat
#
# Utilisation : test_all_options.sh [-so | -ao] [-q]
#   -so : ne tester que les fichiers sans objets
#   -ao : ne tester que les fichiers avec objets
#   -q  : mode quiet (moins de sortie)

# On se place dans le répertoire du projet
cd "$(dirname "$0")"/../../.. || exit 1
. ./src/test/script/tools/common.sh

PATH=./src/main/bin:"$PATH"

# Chemins
PATH_SO_VALID="src/test/deca/codegen/valid/created/sans-objet"
PATH_O_VALID="src/test/deca/codegen/valid/created/objet"
PATH_VALID="src/test/deca/codegen/valid/created/"
PATH_SYNTAX_VALID="src/test/deca/syntax/valid/created"
PATH_CONTEXT_VALID="src/test/deca/context/valid/created"

# Options
QUIET=0
SO_ONLY=0
AO_ONLY=0

# Compteurs globaux
TOTAL_OK=0
TOTAL_KO=0

# Parsing des arguments
while [ $# -gt 0 ]; do
    case "$1" in
        -so) SO_ONLY=1 ;;
        -ao) AO_ONLY=1 ;;
        -q)  QUIET=1 ;;
    esac
    shift
done

# Collecte tous les fichiers .deca d'un répertoire
collect_files() {
    for dir in "$@"; do
        [ -d "$dir" ] || continue
        for file in "$dir"/*.deca; do
            [ -f "$file" ] && echo "$file"
        done
    done
}

# Extrait la sortie attendue d'un fichier .deca
get_expected_output() {
    sed -n '/Resultats:/,/Historique:/ { /Resultats:/d; /Historique:/d; s|//||; p; }' "$1" | xargs
}

# Exécute un fichier .ass et retourne la sortie
run_ima() {
    local ass_file="$1"
    ima "$ass_file" 2>/dev/null | tr -d '"' | xargs
}

# Teste un fichier compilé : exécute avec ima et compare le résultat
# Usage: test_execution "fichier.deca"
# Retourne 0 si OK, 1 si KO
test_execution() {
    local deca_file="$1"
    local ass_file="${deca_file%.deca}.ass"

    # Vérifier que le .ass existe
    if [ ! -f "$ass_file" ]; then
        return 1
    fi

    local expected=$(get_expected_output "$deca_file")
    local actual=$(run_ima "$ass_file")

    # Nettoyage du .ass
    rm -f "$ass_file"

    if [ "$expected" = "$actual" ]; then
        return 0
    else
        return 1
    fi
}

# Exécute un batch de fichiers avec des options données
# Compile avec decac -P, puis vérifie chaque exécution
# Usage: run_batch "options" fichier1 fichier2 ...
run_batch() {
    local options="$1"
    shift
    local files=("$@")
    local ok_count=0
    local ko_count=0
    local display_opts="${options:-(default)}"

    [ ${#files[@]} -eq 0 ] && return 0

    # Compilation de tous les fichiers
    if ! decac -P $options "${files[@]}" > /dev/null 2>&1; then
        log_fail "Compilation échouée avec options: -P $display_opts"
        ko_count=${#files[@]}
        TOTAL_KO=$((TOTAL_KO + ko_count))
        return 1
    fi

    # Vérification de chaque exécution
    for f in "${files[@]}"; do
        if test_execution "$f"; then
            ok_count=$((ok_count + 1))
        else
            ko_count=$((ko_count + 1))
            if [ "$QUIET" -eq 0 ]; then
                log_fail "Exécution échouée: $(basename "$f") avec options: -P $display_opts"
            fi
        fi
    done

    TOTAL_OK=$((TOTAL_OK + ok_count))
    TOTAL_KO=$((TOTAL_KO + ko_count))

    if [ "$ko_count" -eq 0 ]; then
        log_success "${#files[@]}/${#files[@]} tests passés avec options: -P $display_opts"
    else
        log_warn "$ok_count/${#files[@]} tests passés avec options: -P $display_opts"
    fi

    [ "$ko_count" -eq 0 ]
}

# Exécute les tests pour une phase donnée (parse/verify - pas d'exécution ima)
# Usage: run_phase_no_exec "phase_name" "options" dir1 dir2 ...
run_phase_no_exec() {
    local phase_name="$1"
    local options="$2"
    shift 2

    local all_files=()
    while IFS= read -r file; do
        [ -n "$file" ] && all_files+=("$file")
    done < <(collect_files "$@")

    if [ ${#all_files[@]} -eq 0 ]; then
        log_info "$phase_name (aucun fichier)"
        return 0
    fi

    local display_opts="${options:-(default)}"

    if decac -P $options "${all_files[@]}" > /dev/null 2>&1; then
        TOTAL_OK=$((TOTAL_OK + ${#all_files[@]}))
        log_success "$phase_name ${#all_files[@]}/${#all_files[@]} avec: -P $display_opts"
    else
        TOTAL_KO=$((TOTAL_KO + ${#all_files[@]}))
        log_fail "$phase_name échec avec: -P $display_opts"
    fi
}

# Exécute les tests codegen avec toutes les combinaisons d'options
# Usage: run_codegen_tests dir1 dir2 ...
run_codegen_tests() {
    local all_files=()
    while IFS= read -r file; do
        [ -n "$file" ] && all_files+=("$file")
    done < <(collect_files "$@")

    [ ${#all_files[@]} -eq 0 ] && return 0

    # Liste des combinaisons d'options à tester
    local options_list=(
        ""
        "-n"
        "-o"
        "-o -o"
        "-o -o -o"
        "-o -o -o -n"
        "-d"
        "-d -d"
        "-r 4"
        "-r 16"
        "-r 4 -n -o"
        "-r 16 -n -o"
        "-d -d -d -o -o -o -r 4 -n"
    )

    for opts in "${options_list[@]}"; do
        run_batch "$opts" "${all_files[@]}"
    done
}

# ==================== MAIN ====================

log_info "=== Test de toutes les options (avec -P) ==="

# 1. Tests de parsing (-p) sur fichiers syntax
run_phase_no_exec "[Parsing]" "-p" \
    "$PATH_SYNTAX_VALID/lexer" \
    "$PATH_SYNTAX_VALID/parser/sans-objet" \
    "$PATH_SYNTAX_VALID/parser/objet"

# 2. Tests de vérification (-v) sur fichiers context
run_phase_no_exec "[Context -v]" "-v" \
    "$PATH_CONTEXT_VALID/sans-objet" \
    "$PATH_CONTEXT_VALID/objet"

run_phase_no_exec "[Context -v -d]" "-v -d" \
    "$PATH_CONTEXT_VALID/sans-objet" \
    "$PATH_CONTEXT_VALID/objet"

# 3. Tests de codegen complets avec toutes les combinaisons
log_info "[Codegen] Tests avec toutes les combinaisons d'options"

if [ "$AO_ONLY" -eq 0 ]; then
    if [ "$SO_ONLY" -eq 0 ]; then
        log_info "[Avec et Sans objet]"
        run_codegen_tests "$PATH_SO_VALID" "$PATH_O_VALID"
    else
        log_info "[Sans objet]"
        run_codegen_tests "$PATH_SO_VALID"
    fi
else
    if [ "$SO_ONLY" -eq 0 ]; then
        log_info "[Avec objet]"
        run_codegen_tests "$PATH_O_VALID"
    fi
fi

# ==================== RÉSUMÉ ====================

TOTAL=$((TOTAL_OK + TOTAL_KO))

echo ""
log_info "=== RÉSUMÉ ==="
log_warn "$TOTAL_OK/$TOTAL tests passés"

if [ "$TOTAL_KO" -eq 0 ]; then
    log_success "Tous les tests sont passés!"
    exit 0
else
    log_fail "$TOTAL_KO test(s) échoué(s)."
    exit 1
fi
