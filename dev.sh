#!/bin/bash

# Script de développement MoneyBuddy
# Ce script facilite les tâches courantes de développement

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour afficher les messages
info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Fonction d'aide
show_help() {
    cat << EOF
Usage: ./dev.sh [COMMAND]

Commandes disponibles:

  build         Compile le projet (sans tests)
  test          Exécute les tests unitaires
  integration   Exécute les tests d'intégration
  lint          Vérifie le style de code et les bugs potentiels
  verify        Exécute tous les tests + lint + build
  clean         Nettoie les fichiers compilés
  run           Lance l'application
  install       Installe les dépendances
  docker        Build et lance l'application avec Docker
  help          Affiche cette aide

Exemples:
  ./dev.sh build          # Compile le projet
  ./dev.sh test           # Lance les tests unitaires
  ./dev.sh verify         # Lance tous les tests et vérifications
  ./dev.sh run            # Lance l'application

EOF
}

# Vérifie que mvnw est exécutable
ensure_mvnw_executable() {
    if [ ! -x "./mvnw" ]; then
        info "Rendre mvnw exécutable..."
        chmod +x ./mvnw
    fi
}

# Charge les variables d'environnement
load_env() {
    if [ -f ".env" ]; then
        info "Chargement des variables d'environnement depuis .env"
        export $(cat .env | grep -v '^#' | xargs)
    else
        warn "Fichier .env non trouvé. Certaines fonctionnalités peuvent ne pas fonctionner."
    fi
}

# Commandes
case "${1:-help}" in
    build)
        ensure_mvnw_executable
        info "Compilation du projet..."
        ./mvnw clean package -DskipTests
        info "✓ Build terminé avec succès!"
        ;;

    test)
        ensure_mvnw_executable
        info "Exécution des tests unitaires..."
        ./mvnw test
        info "✓ Tests unitaires terminés!"
        ;;

    integration)
        ensure_mvnw_executable
        info "Exécution des tests d'intégration..."
        ./mvnw verify
        info "✓ Tests d'intégration terminés!"
        ;;

    lint)
        ensure_mvnw_executable
        info "Vérification du style de code avec Checkstyle..."
        ./mvnw checkstyle:check || warn "Checkstyle a détecté des problèmes"

        info "Analyse avec SpotBugs..."
        ./mvnw spotbugs:check || warn "SpotBugs a détecté des problèmes"

        info "✓ Vérifications terminées!"
        ;;

    verify)
        ensure_mvnw_executable
        info "Exécution de toutes les vérifications..."
        ./mvnw clean verify
        info "✓ Toutes les vérifications sont passées!"
        ;;

    clean)
        ensure_mvnw_executable
        info "Nettoyage des fichiers compilés..."
        ./mvnw clean
        info "✓ Nettoyage terminé!"
        ;;

    run)
        ensure_mvnw_executable
        load_env
        info "Démarrage de l'application..."
        ./mvnw spring-boot:run
        ;;

    install)
        ensure_mvnw_executable
        info "Installation des dépendances..."
        ./mvnw clean install -DskipTests
        info "✓ Dépendances installées!"
        ;;

    docker)
        info "Construction de l'image Docker..."
        docker build -t moneybuddy-app .

        info "Démarrage du conteneur..."
        docker run --env-file .env -p 8080:8080 moneybuddy-app
        ;;

    help|--help|-h)
        show_help
        ;;

    *)
        error "Commande inconnue: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
