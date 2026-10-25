#! /bin/bash

# On se place à la racine du projet
cd "$(dirname "$0")/../../../.."

decac="./src/main/bin/decac"
moitie="./src/test/deca/codegen/valid/provided/moitie.deca"

decac "$moitie"

cat "${moitie%.deca}.ass"
