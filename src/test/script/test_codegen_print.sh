#! /bin/sh

# Auteur : gl25
# Version initiale : 06/01/2026

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1
. ./src/test/script/tools/common.sh


PATH=./src/test/script/launchers:"$PATH"
PATH_TESTS="src/test/deca/codegen/print"
TESTS_KO=0
MAX_TEST=$(ls "$PATH_TESTS"/*.deca | wc -l)

# Gestion des options
r_option=""
o_option=""

for arg in "$@"; do
    if [ "$arg" = "-o" ]; then
        if [ -z "$o_option" ] ; then
            o_option="-n -o"
        else
            o_option="$o_option -o"
        fi
    fi
    if [ "$arg" = "-r" ]; then
        r_option="-r"
    fi
done

clean() {
    rm -f "$PATH_TESTS"/*.res "$PATH_TESTS"/*.ass
}

compare_files() {
    file_expected="$1"
    file_result="$2"
    if ! diff -u $file_expected $file_result | grep -q '^+'; then
        log_success "Fichiers $(basename "$file_result") et $(basename "$file_expected") sont identiques"
        return 0
    else
        log_fail " Fichiers différents : $(basename "$file_expected")"
        TESTS_KO=$((TESTS_KO + 1))
        return 1
    fi
}

run_test() {
    for file in "$PATH_TESTS"/*_args.deca
    do
        file_res="${file%.deca}.res"
        
        file_to_compare="${file%_args.deca}.deca"
        file_to_compare_res="${file_to_compare%.deca}.res"
        
        # # Debug info
        # echo "file : $file"
        # echo "file_to_compare : $file_to_compare"
        # echo "file_res : $file_res"
        # echo "file_to_compare_res : $file_to_compare_res"
        # Redirection de la sortie standard pour capturer les sorties des appels
        if ! test_codegen_print $file $o_option $r_option; then
            log_fail " Erreur lors de l'exécution de test_codegen_print sur $(basename "$file")"
            TESTS_KO=$((TESTS_KO + 1))
            continue
        fi
        if ! test_codegen_print "$file_to_compare" $o_option; then
            log_fail " Erreur lors de l'exécution de test_codegen_print sur $(basename "$file_to_compare")"
            log_info "sortie recu de $(basename "$file_res")"
            cat "$file_res"
            log_info "sortie recu de $(basename "$file_to_compare_res")"
            cat "$file_to_compare_res"
            TESTS_KO=$((TESTS_KO + 1))
            continue
        fi
        compare_files "$file_to_compare_res" "$file_res"
        # compare_files "$fichier_exp" "$fichier_res"
    done
}
run_test
if [ "$TESTS_KO" -ne 0 ]
then
    log_warn "Nombre de tests réussis du codegen : $((MAX_TEST - TESTS_KO))/$MAX_TEST"
    clean
    exit 1
else
    log_success "🎉 Tous les tests sont passés avec succès ($((MAX_TEST - TESTS_KO))/$MAX_TEST) !"
    clean
    exit 0
fi