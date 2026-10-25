#!/bin/bash

# Définition des couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
RESET='\033[0m'

# Fonction pour simuler le log Maven [INFO]
log_info() {
    printf "%b\n" "[${BLUE}INFO${RESET}] $1"
}

log_warn() {
    printf "%b\n" "[${ORANGE}WARN${RESET}] $1"
}

log_error() {
    printf "%b\n" "[${RED}ERROR${RESET}] $1"
}

log_dot() {
    printf "%b\n" "[${BLUE}...${RESET}] $1"
}

# Fonction pour Succès
log_success() {
    printf "%b\n" "[${GREEN}SUCCESS${RESET}] $1"
}

# Fonction pour Echec
log_fail() {
    printf "%b\n" "[${RED}FAIL${RESET}] $1"
}