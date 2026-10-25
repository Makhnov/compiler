#! /bin/bash

# On se place à la racine du projet
cd "$(dirname "$0")/../../../.."

. ./src/test/script/tools/common.sh

decac="./src/main/bin/decac"
tests_no_paral="./src/test/deca/demo/"
tests_paral="./src/test/deca/demo/paral"

# Compilation sur 10 fichiers de demo (pas de demo/paral)
log_info "Compilation sans parallélisation"
for file in "$tests_no_paral"/*.deca
do
    if $decac "$file" 2>&1 > /dev/null
    then
        log_success "$file compiled successfully"
    else
        log_failure "$file failed to compile"
    fi
done

# Pause, attendre une entrée utilisateur
read -p "Appuyez sur Entrée pour continuer la compilation avec parallélisation..."

# Compilation sur 30 fichiers de demo (tout demo/* et demo/paral/*)
log_info "Compilation avec parallélisation 30 fichiers"
tests_files_paral=""
for file in "$tests_no_paral"/*.deca "$tests_paral"/*.deca
do
    tests_files_paral="$tests_files_paral $file"
done
if $decac -P -d $DECAC_OPTS $tests_files_paral 2>&1 > /dev/null
then
    log_success "All files compiled successfully"
else
    log_failure "Some files failed to compile with parallelization"
fi

# Pause, attendre une entrée utilisateur
read -p "Appuyez sur Entrée pour terminer..."

# Affichage des fichiers .ass générés
find "$tests_no_paral" "$tests_paral" -name "*.ass" -print

# Nettoyage
find "$tests_no_paral" "$tests_paral" -name "*.ass" -delete
