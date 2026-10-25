# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
. ./src/test/script/tools/common.sh
# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
ORANGE='\033[0;33m'
COLOR_RESET='\033[0m'


PATH_CONTEXT_INVALID="src/test/deca/context/invalid/created/"
PATH_CONTEXT_VALID="src/test/deca/context/valid/created/"
TESTS_KO=0
MAX_TEST=$(ls "$PATH_CONTEXT_INVALID"/*.deca "$PATH_CONTEXT_VALID"/*.deca | wc -l)

echo "[Invalides]"
for file in "$PATH_CONTEXT_INVALID"/*.deca
do
    # on attend une erreur -> donc une ligne du style "xxx.deca:ligne:"
    if test_context "$file" 2>&1 | grep -q -e "$(basename "$file"):[0-9]"
    then
        echo -e "${GREEN}✅ Échec attendu ${COLOR_RESET}pour test_context sur invalid/$(basename "$file")"
    else
        echo -e "${RED}❌ Erreur non détectée par test_context pour invalid/$(basename "$file")${COLOR_RESET}"
        TESTS_KO=$((TESTS_KO + 1))
    fi
done

echo
echo "[Valides]"
for file in "$PATH_CONTEXT_VALID"/*.deca
do
    # on n'attend PAS d'erreur
    if test_context "$file" 2>&1 | grep -q -e "$(basename "$file"):[0-9]"
    then
        echo -e "${RED}❌ Échec inattendu ${COLOR_RESET}pour test_context sur valid/$(basename "$file")"
        TESTS_KO=$((TESTS_KO + 1))
    else
        echo -e "${GREEN}✅ Succès attendu ${COLOR_RESET}pour test_context sur valid/$(basename "$file")"
    fi
done

if [ "$TESTS_KO" -ne 0 ]
then
    echo -e "${ORANGE}⚠️  Nombre de tests réussi pour le context : $((MAX_TEST - TESTS_KO))/$MAX_TEST${COLOR_RESET}"
    exit 1
else
    echo -e "${GREEN}🎉 Tous les tests de context sont passés avec succès !${COLOR_RESET}"
    exit 0
fi
