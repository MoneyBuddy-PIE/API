#!/bin/bash

# Script de démarrage de MoneyBuddy avec bot Discord

echo "🚀 Démarrage de MoneyBuddy avec bot Discord..."
echo ""

# Aller dans le répertoire du projet
cd "$(dirname "$0")"

# Charger les variables d'environnement depuis .env
if [ -f ".env" ]; then
    echo "📋 Chargement des variables d'environnement..."
    export $(grep -v '^#' .env | xargs)
fi

# Configurer Java 21
export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"

echo "☕ Version de Java: $(java -version 2>&1 | head -1)"
echo ""

# Vérifier que le fichier mvnw existe
if [ ! -f "./mvnw" ]; then
    echo "❌ Erreur: mvnw non trouvé"
    exit 1
fi

# Rendre mvnw exécutable
chmod +x mvnw

echo "📦 Compilation et démarrage de l'application..."
echo ""

# Démarrer l'application
./mvnw spring-boot:run
