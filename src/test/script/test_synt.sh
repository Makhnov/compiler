#! /bin/sh

# Auteur : gl25
# Version initiale : 01/01/2026

# Utilisation : ./test_synt.sh [-ao | -so]
#   -ao : lance les tests uniquement sur la partie objet
#   -so : lance les tests uniquement sur la partie sans objet
# (si aucune option n'est fournie, lance les deux)


# Objectif : lancer tous les tests syntaxiques (valides et invalides) (i.e. valid/* et invalid/*)
# test_lex -> test_synt
# Pour chaque fichiers, test_lex puis test_synt
# Afficher le nombre de tests réussis / total

# Gestion des options
ao_option=1
so_option=1
o_option=""
for arg in "$@"; do
    if [ "$arg" = "-ao" ]; then
        ao_option=1
        so_option=0
    fi
    if [ "$arg" = "-so" ]; then
        ao_option=0
        so_option=1
    fi
done

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1
. ./src/test/script/tools/common.sh
PATH=./src/test/script/launchers:"$PATH"
COLOR_RESET='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'
ORANGE='\033[0;33m'
PATH_VALID="src/test/deca/syntax/valid/created"
PATH_INVALID="src/test/deca/syntax/invalid/created"
TESTS_KO=0
FAILED_LEXER=0
FAILED_PARSER=0


run_invalid() {
    files=$(find "$PATH_INVALID" -name "*.deca")
    log_info  "[Invalides]"
    # log_info "test_lex"
    # for file in $files
    # do
    #     # test_lex
    #     if test_lex "$file" 2>&1 | grep -q -e "$(basename "$file"):"
    #     then
    #         # Afficher le succes attendu comme suit : Echec attendu pour test_lex sur invalid/<parser|lexer>/<filename>
    #         log_success " Echec attendu ${COLOR_RESET}pour test_lex sur $file"
    #     else
    #         log_fail "Erreur non detectee par test_lex pour $file"
    #         echo "Retour de test_lex :"
    #         test_lex "$file"
    #         TESTS_KO=$((TESTS_KO + 1))
    #     fi
    # done
    # log_info "test_synt"
    # for file in $files
    # do
    #     # test_synt
    #     if test_synt "$file" 2>&1 | grep -q -e ':[0-9][0-9]*'
    #     then
    #         log_success " Echec attendu pour test_synt sur $file"
    #     else
    #         log_fail " Erreur non detectee par test_synt pour $file"
    #         log_info "Retour de test_synt :"
    #         test_synt "$file"
    #         TESTS_KO=$((TESTS_KO + 1))
    #     fi
    # done


    for file in $files
    do
        # Si FAILED_LEXER=1 ou FAILED_PARSER=1 alors OK (les deux doivent fail)
        # Si FAILED_LEXER=0 et FAILED_PARSER=0 alors KO
        # Si FAILED_LEXER=1 et FAILED_PARSER=0 alors KO
        # Si FAILED_LEXER=0 et FAILED_PARSER=1 alors OK (fail pas détécté par le lexer mais par le parser, donc ok)
        FAILED_LEXER=0
        FAILED_PARSER=0
        # test_lex
        if test_lex $file 2>&1 | grep -q -e "$(basename "$file"):"
        then
            FAILED_LEXER=1
            
        else
            FAILED_LEXER=0
        fi

        # test_synt
        if test_synt $file 2>&1 | grep -q -e ':[0-9][0-9]*';
        then
            FAILED_PARSER=1
        else
            FAILED_PARSER=0
        fi

        if [ $FAILED_PARSER -eq 0 ]; then
            log_fail " Erreur non detectee pour $(basename "$file")"
            # log_info "Retour de test_synt :"
            test_synt "$file"
            TESTS_KO=$((TESTS_KO + 1))
        elif [ $FAILED_PARSER -eq 1 ]; then
            log_success " Echec attendu pour $(basename "$file")"
            # log_info " Retour de test_synt :"
            # test_synt "$file"
        else
            log_success " Succès attendu pour $(basename "$file")"
        fi
    done
}

run_valid() {
    # si test_lex ok et test_synt ok alors ok
    # si test_lex ok et test_synt fail alors fail
    files=$@
    log_info  "[Valides]"
    for file in $files
    do
        FAILED_LEXER=0
        FAILED_PARSER=0
        # test_lex
        if test_lex $file 2>&1 | grep -q -e "$(basename "$file"):"
        then
            FAILED_LEXER=1
        else
            FAILED_LEXER=0
        fi

        # test_synt
        if test_synt $file 2>&1 | grep -q -e ':[0-9][0-9]*';
        then
            FAILED_PARSER=1
        else
            FAILED_PARSER=0
        fi
        # echo "FAILED_LEXER=$FAILED_LEXER, FAILED_PARSER=$FAILED_PARSER"

        if [ $FAILED_LEXER -eq 1 ]; then
            log_fail " Echec inattendu pour $(basename "$file")"
            # log_info " Retour de test_lex :"
            # test_lex "$file"
            TESTS_KO=$((TESTS_KO + 1))
        elif [ $FAILED_PARSER -eq 1 ]; then
            log_fail " Echec inattendu pour $(basename "$file")"
            # log_info " Retour de test_synt :"
            # test_synt "$file"
            TESTS_KO=$((TESTS_KO + 1))
        else
            log_success " Succès attendu pour $(basename "$file")"
        fi
    done
}


# === RUN TESTS ===
files_invalid=$(find "$PATH_INVALID" -name "*.deca")
files_valid=$(find "$PATH_VALID" -name "*.deca")
files_sans_objet=$(find "$PATH_VALID" -name "*.deca" -not -path "*/parser/objet/*")
files_objet=$(find "$PATH_VALID" -name "*.deca" -not -path "*/parser/sans-objet/*")
PATH_LEXER="src/test/deca/syntax/valid/created/lexer"
files_lexer=$(find "$PATH_LEXER" -name "*.deca")
run_invalid
MAX_TEST=0
if [ "$ao_option" -eq 1 ] && [ "$so_option" -eq 1 ]; then
    MAX_TEST=$(($(echo $files_invalid | wc -w) + $(echo $files_sans_objet | wc -w) + $(echo $files_objet | wc -w)))
    run_valid $files_valid
elif [ "$ao_option" -eq 1 ]; then
    MAX_TEST=$(($(echo $files_invalid | wc -w) + $(echo $files_objet | wc -w)))
    log_info "[Objet]"
    run_valid $files_objet
else
    MAX_TEST=$(($(echo $files_invalid | wc -w) + $(echo $files_sans_objet | wc -w)))
    log_info "[Sans objet]"
    run_valid $files_sans_objet
fi

if [ "$TESTS_KO" -ne 0 ]
then
    log_info "Nombre de success : $((MAX_TEST - TESTS_KO))/$MAX_TEST"
    log_warn "Nombre de tests échoués : $TESTS_KO/$MAX_TEST"
    exit 1
else
    log_success "🎉 Tous les tests du parser sont passés avec succès !"
    exit 0
fi
