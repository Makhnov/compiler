#! /bin/sh

# Test de l'interface en ligne de commande de decac.
# On ne met ici qu'un test trivial, a vous d'en ecrire de meilleurs.

PATH=./src/main/bin:"$PATH"
. ./src/test/script/tools/common.sh

decac_moins_b=$(decac -b)

if [ "$?" -ne 0 ]; then
    log_fail "ERREUR: decac -b a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_b" = "" ]; then
    log_fail "ERREUR: decac -b n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_b" | grep -i -e "erreur" -e "error"; then
    log_fail "ERREUR: La sortie de decac -b contient erreur ou error"
    exit 1
fi

log_success "Pas de probleme detecte avec decac -b."

# ... et ainsi de suite.
