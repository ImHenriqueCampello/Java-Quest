#!/usr/bin/env bash
# ==============================================================================
# Java Quest: The Bytecode Chronicles - Script de Empacotamento para macOS
# Gera distribuição standalone com Java embutido e launcher .command de 2 cliques
# ==============================================================================
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "================================================================================"
echo "    JAVA QUEST: BUILD & DISTRIBUIÇÃO AUTOMATIZADA COM JPACKAGE (MACOS)         "
echo "================================================================================"

command -v javac >/dev/null 2>&1 || { echo "ERRO: javac não encontrado no PATH."; exit 1; }
command -v jar >/dev/null 2>&1 || { echo "ERRO: jar não encontrado no PATH."; exit 1; }
command -v jpackage >/dev/null 2>&1 || { echo "ERRO: jpackage não encontrado no PATH."; exit 1; }

APP_NAME="JavaQuest"
MAIN_CLASS="Main"

# Determinar versão (suporta argumento de linha de comando ou tag do GitHub Actions)
VERSION="${1:-${GITHUB_REF_NAME:-1.0.0}}"
VERSION="${VERSION#v}"
VERSION="${VERSION#V}"
if [[ "$VERSION" =~ ^([0-9]+(\.[0-9]+)*) ]]; then
    VERSION="${BASH_REMATCH[1]}"
else
    VERSION="1.0.0"
fi
APP_VERSION="$VERSION"

BUILD_DIR="$DIR/build"
DIST_DIR="$DIR/dist"
MACOS_DIST="$DIST_DIR/macos/$APP_NAME"

echo "[1/5] Limpando diretórios temporários..."
rm -rf "$BUILD_DIR"
rm -rf "$DIST_DIR/macos"
mkdir -p "$BUILD_DIR/classes"
mkdir -p "$BUILD_DIR/jar"
mkdir -p "$DIST_DIR/macos/$APP_NAME"

echo "[2/5] Compilando código-fonte de 'src'..."
javac -d "$BUILD_DIR/classes" $(find src -name "*.java")
cp -r src/*.properties "$BUILD_DIR/classes/" 2>/dev/null || true

echo "[3/5] Gerando arquivo JAR executável ($APP_NAME.jar)..."
cat <<EOF > "$BUILD_DIR/manifest.txt"
Manifest-Version: 1.0
Main-Class: $MAIN_CLASS
Created-By: Java Quest Build System
EOF
jar --create --file "$BUILD_DIR/jar/$APP_NAME.jar" --manifest="$BUILD_DIR/manifest.txt" -C "$BUILD_DIR/classes" .

echo "[4/5] Executando jpackage (empacotando runtime Java embutido para macOS)..."
jpackage \
    --type app-image \
    --input "$BUILD_DIR/jar" \
    --main-jar "$APP_NAME.jar" \
    --main-class "$MAIN_CLASS" \
    --name "$APP_NAME" \
    --app-version "$APP_VERSION" \
    --dest "$MACOS_DIST" \
    --add-modules java.base \
    --jlink-options "--strip-debug --no-man-pages --no-header-files"

echo "[5/5] Criando launcher de dois cliques (Play-JavaQuest.command)..."

cat <<'EOF' > "$MACOS_DIST/Play-JavaQuest.command"
#!/bin/bash
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"
clear
exec "$DIR/JavaQuest.app/Contents/MacOS/JavaQuest" "$@"
EOF
chmod +x "$MACOS_DIST/Play-JavaQuest.command"

cat <<EOF > "$MACOS_DIST/COMO_JOGAR.txt"
================================================================================
           JAVA QUEST: THE BYTECODE CHRONICLES - INSTRUCOES MACOS
================================================================================

COMO JOGAR COM 2 CLIQUES:
1. De dois cliques no arquivo 'Play-JavaQuest.command'.
2. O macOS abrira automaticamente o Terminal.app com o jogo rodando!
3. Nao e necessario instalar Java nem configurar variaveis de ambiente.

Nota de seguranca do macOS (Gatekeeper):
Na primeira execucao, se o macOS exibir aviso de desenvolvedor nao identificado:
- Clique com o botao direito (Control + Clique) em 'Play-JavaQuest.command' e escolha 'Abrir'.
================================================================================
EOF

ARCH="$(uname -m)"
TAR_NAME="JavaQuest-macOS-${ARCH}.tar.gz"
echo "Gerando pacote compactado para distribuição: $DIST_DIR/$TAR_NAME..."
tar -czf "$DIST_DIR/$TAR_NAME" -C "$DIST_DIR/macos" "$APP_NAME"

echo ""
echo "================================================================================"
echo "    BUILD CONCLUÍDO COM SUCESSO! PACOTE MACOS PRONTO PARA DISTRIBUIÇÃO!       "
echo "================================================================================"
echo "Pasta Standalone : $MACOS_DIST"
echo "Arquivo Release  : $DIST_DIR/$TAR_NAME"
echo "================================================================================"
