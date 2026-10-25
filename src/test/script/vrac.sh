#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1
. ./src/test/script/tools/common.sh

# Gestion des options
# options possibles :
# -g pour lancer les tests de codegen
# -c pour lancer les tests de context
# -s pour lancer les tests de syntax
# sans options pour lancer tous les tests
g_option=0
c_option=0
s_option=0
if [ "$#" -eq 0 ]; then
    g_option=1
    c_option=1
    s_option=1
else
    for arg in "$@"; do
        if [ "$arg" = "-g" ]; then
            g_option=1
        fi
        if [ "$arg" = "-c" ]; then
            c_option=1
        fi
        if [ "$arg" = "-s" ]; then
            s_option=1
        fi
    done
fi

# Codegen
pwd
PATH_CODEGEN="src/test/vrac/codegen"

run_codegen_valid() {
    files=$(find "$PATH_CODEGEN/valid" -name "*.deca")
    log_info  "[Codegen]"
    for file in $files
    do
        if test_codegen "$file" 2>&1 >/dev/null;
        then
            log_success " Succès pour test_codegen sur $file"
        else
            log_fail " Échec pour test_codegen sur $file"
            TESTS_KO=$((TESTS_KO + 1))
        fi
    done
}

run_codegen_invalid() {
    files=$(find "$PATH_CODEGEN/invalid" -name "*.deca")
    log_info  "[Codegen Invalides]"
    for file in $files
    do
        if test_codegen "$file" 2>&1 | grep -q -e ':[0-9][0-9]*' >/dev/null;
        then
            log_success " Échec attendu pour test_codegen sur $file"
        else
            log_fail " Succès inattendu pour test_codegen sur $file"
            TESTS_KO=$((TESTS_KO + 1))
        fi
    done
}

# Context
PATH_CONTEXT="src/test/vrac/context"

run_context_valid() {
    files=$(find "$PATH_CONTEXT/valid" -name "*.deca")
    log_info  "[Context Valides]"
    for file in $files
    do
        if test_context "$file" 2>&1 >/dev/null;
        then
            log_success " Succès attendu pour $(basename "$file")"
        else
            log_fail " Échec inattendu pour $(basename "$file")"
            TESTS_KO=$((TESTS_KO + 1))
        fi
    done
}

run_context_invalid() {
    files=$(find "$PATH_CONTEXT/invalid" -name "*.deca")
    log_info  "[Context Invalides]"
    for file in $files
    do
        if test_context "$file" 2>&1 | grep -q -e ':[0-9][0-9]*' >/dev/null;
        then
            log_success " Échec attendu pour $(basename "$file")"
        else
            log_fail " Succès inattendu pour $(basename "$file")"
            TESTS_KO=$((TESTS_KO + 1))
        fi
    done
}

# Syntax
PATH_SYNTAX="src/test/vrac/syntax"

run_syntax_valid () {
    files=$(find "$PATH_SYNTAX/valid" -name "*.deca")
    log_info  "[Syntax Valides]"
    for file in $files
    do
        FAILED_LEXER=0
        FAILED_PARSER=0
        # test_lex
        if test_lex $file 2>&1 | grep -q -e "$(basename "$file"):" >/dev/null;
        then
            FAILED_LEXER=1
        else
            FAILED_LEXER=0
        fi

        # test_synt
        if test_synt $file 2>&1 | grep -q -e ':[0-9][0-9]*' >/dev/null;
        then
            FAILED_PARSER=1
        else
            FAILED_PARSER=0
        fi

        if [ $FAILED_LEXER -eq 1 ]; then
            log_fail " Echec inattendu pour $(basename "$file")"
            TESTS_KO=$((TESTS_KO + 1))
        elif [ $FAILED_PARSER -eq 1 ]; then
            log_fail " Echec inattendu pour $(basename "$file")"
            TESTS_KO=$((TESTS_KO + 1))
        else
            log_success " Succès attendu pour $(basename "$file")"
        fi
    done
}

run_syntax_invalid () {
    files=$(find "$PATH_SYNTAX/invalid" -name "*.deca")
    log_info  "[Syntax Invalides]"
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
        if test_synt $file 2>&1 | grep -q -e ':[0-9][0-9]*' >/dev/null;
        then
            FAILED_PARSER=1
        else
            FAILED_PARSER=0
        fi

        if [ $FAILED_LEXER -eq 0 ]; then
            log_fail " Erreur non detectee pour $(basename "$file")"
            test_lex "$file"
            TESTS_KO=$((TESTS_KO + 1))
        elif [ $FAILED_LEXER -eq 1 ]; then
            log_success " Echec attendu pour $(basename "$file")"
        elif [ $FAILED_PARSER -eq 0 ]; then
            log_fail " Erreur non detectee pour $(basename "$file")"
            test_synt "$file"
            TESTS_KO=$((TESTS_KO + 1))
        elif [ $FAILED_PARSER -eq 1 ]; then
            log_success " Echec attendu pour $(basename "$file")"
        else
            log_success " Succès attendu pour $(basename "$file")"
        fi
    done
}

# Lancement des tests
if [ $s_option -eq 1 ]; then
    run_syntax_invalid
    run_syntax_valid
fi
if [ $c_option -eq 1 ]; then
    run_context_invalid
    run_context_valid
fi
if [ $g_option -eq 1 ]; then
    run_codegen_invalid
    run_codegen_valid
fi
