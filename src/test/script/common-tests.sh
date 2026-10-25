#!/bin/sh
# Decompresion faite par Gemini

# Fonction pour afficher une erreur en rouge et quitter
die () {
    echo "\033[1;31mERREUR :\033[0m $@"
    exit 1
}

# Configuration du PATH si l'évaluation n'est pas en cours
if [ "$GL_EVALUATION_ONGOING" = "" ]; then
    PATH=$(dirname "$0")/../../../src/main/bin:"$PATH"
fi

# Localisation de l'exécutable decac
DECAC=$(cd "$(dirname "$(command -v decac)")" && pwd)/decac

# Vérification de l'existence de decac
[ -x "$DECAC" ] || die "commande decac non-trouvée"

. ./src/test/script/tools/common.sh
log_dot "Test de l'executable:"
log_dot "$DECAC"

# Configuration du répertoire de test
test_dir=$(dirname "$DECAC")/../../test/deca
cd "$test_dir" 2> /dev/null || die "repertoire $test_dir inaccessible"

# Vérification des caractères illicites dans les noms de fichiers
(cd "$test_dir" && find . -name '*.deca' | egrep -v '^[0-9ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_/.+-]*$' 2> /dev/null) && die "caracteres illicites dans les noms des tests ou des repertoires"

# Wrapper pour exécuter decac avec des limites de ressources (ulimit)
decac () {
    (ulimit -t 10 -f 100 2>/dev/null || true; "$DECAC" "$@" 2>&1)
}

# Création d'un répertoire temporaire pour les tests
tmpdir=.minimal-test.$$
mkdir "$tmpdir" || die "Impossible de créer le répertoire de test"

# Lancement des tests dans un sous-shell
(
    cd "$tmpdir"
    mkdir source || die "Impossible de créer un répertoire dans $PWD"

    # Création des fichiers sources de test
    log_dot "Création des fichiers sources de test"
    echo '{println(}' > source/syntax-ko.deca
    echo '{println(x);}' > source/syntax-ok-verif-ko.deca
    echo '{println("Hello");}' > source/hello.deca

    # --- Test de l'option -p (Parser) ---
    log_dot "Test de l'option -p (Parser)"
    decac -p source/hello.deca > hello-p.deca || die "Échec de la compilation d'un programme Deca simple avec -p"
    [ -s hello-p.deca ] || die "decac -p n'a pas généré de sortie sur un programme Deca simple"
    decac -p hello-p.deca >/dev/null || die "decac -p a produit une sortie qui n'est pas un programme Deca valide"

    # Test erreur syntaxique 
    log_dot "Test erreur syntaxique"
    ! decac -p source/syntax-ko.deca > sortie.txt 2>&1 || die "decac -p n'a pas terminé avec un status différent de 0 sur un programme incorrect"
    grep -qi "source/syntax-ko.deca:1:" sortie.txt || die "decac -p n'a pas affiche de message d'erreur correctement"

    # Test syntaxe OK mais verif KO (doit passer le parser)
    log_dot "Test syntaxe OK mais verif KO (doit passer le parser)"
    decac -p source/syntax-ok-verif-ko.deca > sortie.txt 2>&1 || die "decac -p a échoue sur un programme invalide mais syntaxiquement correct"
    [ -s sortie.txt ] || die "decac -p n'a pas produit une sortie sur un programme invalide mais syntaxiquement correct"
    ! grep -qi "source/syntax-ok-verif-ko.deca:1:" sortie.txt || die "decac -p a affiché une erreur sur un programme syntaxiquement correct"


    # --- Test de l'option -v (Verification) ---
    log_dot "Test de l'option -v (Verification)"
    decac -v source/hello.deca > sortie.txt 2>&1 || die "Échec de la compilation d'un programme Deca simple avec -v"
    [ ! -s sortie.txt ] || die "decac -v a produit une sortie sur un fichier correct"

    # Test variable non déclarée
    log_dot "Test variable non déclarée"
    ! decac -v source/syntax-ok-verif-ko.deca > sortie.txt 2>&1 || die "decac -v n'a pas échoué sur une variable non-déclarée"
    [ -s sortie.txt ] || die "decac -v n'a pas produit de sortie sur une variable non-déclarée"
    grep -qi "source/syntax-ok-verif-ko.deca:1:" sortie.txt || die "decac -v n'a pas produit de message d'erreur sur une variable non-déclarée"


    # --- Test de compilation complète et exécution ---
    log_dot "Test de compilation complète et exécution"
    decac source/hello.deca > sortie.txt 2>&1 || die "Échec de la compilation d'un programme Deca simple"
    [ ! -s sortie.txt ] || die "deca a produit une sortie sur un programme correct"
    
    # Vérification de l'emplacement du fichier .ass
    log_dot "Vérification de l'emplacement du fichier .ass"
    [ ! -f hello.ass ] || die "decac a généré le fichier .ass dans le répertoire courant.;Il devrait être généré au même endroit que le fichier source."
    [ -f source/hello.ass ] || die "decac n'a pas généré le fichier .ass au bon endroit"

    # Exécution avec IMA    
    ima source/hello.ass > sortie || die "Erreur lors de l'exécution du code généré"
    echo "Hello" > attendu
    diff sortie attendu || die "L'exécution du code généré pour un hello-world est incorrecte"

    log_success "Les fonctionnalités de base semblent opérationnelles."
)

# Gestion du statut de sortie et nettoyage
status=$?
rm -fr "$tmpdir"
exit "$status"