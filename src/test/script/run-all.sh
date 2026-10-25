#!/bin/sh

# Utilisation : run-all.sh [ -ao | -so ] [ -r ] [ -o* ]
#   -ao  : lance les tests uniquement sur la partie objet
#   -so : lance les tests uniquement sur la partie sans objet
#  
# (si aucune option n'est fournie, lance les deux)
#
# Auteur : Iles
# Version initiale : 08/01/2026

# Gestion des options
r_option=""
ao_option="-ao"
so_option="-so"
o_option=""

for arg in "$@"; do
    if [ "$arg" = "-o" ]; then
        # On ajoute "-o" à la chaîne existante (avec un espace avant)
        o_option="$o_option -o"
    fi
    if [ "$arg" = "-r" ]; then
        r_option="-r"
    fi
    if [ "$arg" = "-ao" ]; then
        ao_option="-ao"
    fi
    if [ "$arg" = "-so" ]; then
        so_option="-so"
    fi
    if [ "$arg" = "-h" ]; then
        echo "Usage: $0 <fichier.deca> [-o*] [-r] [-i]"
        echo "  -o* : active les optimisations (plusieurs -o possibles)"
        echo "  -r : teste avec différents nombres de registres (4, 8, 16)"
        echo "  -ao : lance les tests uniquement sur la partie objet"
        echo "  -so : lance les tests uniquement sur la partie sans objet"
        echo "  Sans argument : lance les avec 16 registres, sans optimisation, sur la partie objet et sans objet"
        exit 0
    fi
done

KO=0
# On se place dans le répertoire du projet (quel que soit le répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1
. ./src/test/script/tools/common.sh
PATH=./src/test/script/launchers:"$PATH"
echo
log_info "Arguments"
if ! ./src/test/script/test_arguments.sh; then
    log_fail "Erreur lors des tests des arguments."
    KO=1
fi
echo
log_info "basic-*:"
for basic_test in ./src/test/script/basic-*
do
    log_info "Lancement de $basic_test..."
    if ! bash "$basic_test"; then
        log_fail "Erreur lors du test $basic_test"
        KO=1
    fi
    echo
done
echo
log_info "common-test:"
pwd
if ! ./src/test/script/common-tests.sh; then
    log_fail "Erreur lors des common-tests."
    KO=1
fi
echo
log_info "Lexer:"
if ! ./src/test/script/test_lexer.sh; then
    log_fail "Erreur lors des tests de lexeur."
    KO=1
fi
echo
log_info "Parser:"
if ! ./src/test/script/test_parser.sh "$ao_option" "$so_option"; then
    log_fail "Erreur lors des tests de parser."
    KO=1
fi
echo
log_info "Context:"
if ! ./src/test/script/run_context_test.sh; then
    log_fail "Erreur lors des tests de sémantique."
    KO=1
fi
echo 
log_info "Codegen Print:"
if ! ./src/test/script/test_codegen_print.sh "$o_option" "$r_option"; then
    log_fail "Erreur lors des tests de génération de code avec print."
    KO=1
fi
echo
log_info "Codegen:"
if ! ./src/test/script/test_codegen.sh "$o_option" "$r_option" "$ao_option" "$so_option"; then
    log_fail "Erreur lors des tests de génération de code."
    KO=1
fi
if [ "$KO" -ne 0 ]; then
    log_fail "Des erreurs ont été détectées lors des tests."
    exit 1
fi
log_success "Tous les tests sont passés avec succès !"
exit 0


