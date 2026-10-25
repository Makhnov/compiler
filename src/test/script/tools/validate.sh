#! /bin/sh
# Auteur : Iles

cd "$(dirname "$0")"/../../../.. || exit 1
. ./src/test/script/tools/common.sh
echo "[${BLUE}VALIDATE${RESET}] Vérification de la structure du projet..."

# Vérification de l'arborescence du projet
log_info "Vérification de l'arborescence du projet..."
EXPECTED_DIRS="src/main/java src/main/bin src/test/deca src/test/script src/test/script/launchers"

MISSING_DIR=0

# La boucle for va découper la chaîne aux espaces automatiquement
for dir in $EXPECTED_DIRS; do
    if [ ! -d "$dir" ]; then
        log_fail "Le dossier attendu '$dir' est manquant !"
        MISSING_DIR=1
    fi
done

if [ $MISSING_DIR -eq 1 ]; then
    log_fail "L'arborescence du projet est incomplète."
    exit 1
else
    log_success "Arborescence validée."
fi

# Vérification de l'absence de fichiers .class dans src/
log_info "Vérification de l'absence de fichiers .class dans src/..."
if find src -name "*.class" | grep -q .; then
    log_fail "Des fichiers .class ont été trouvés dans le dossier src/ !"
    echo "   Veuillez les supprimer (git clean -fdx est votre ami)."
    exit 1
fi

log_success "Validation structurelle terminée."
exit 0