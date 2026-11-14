#!/usr/bin/env bash
# ==============================================================================
# Java Quest: The Bytecode Chronicles - Script de Empacotamento Profissional
# Cria distribuição standalone com runtime Java embutido (jpackage / jlink)
# ==============================================================================
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

echo "================================================================================"
echo "    JAVA QUEST: BUILD & DISTRIBUIÇÃO AUTOMATIZADA COM JPACKAGE (LINUX)         "
echo "================================================================================"

# 1. Verificar ferramentas necessárias
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
LINUX_DIST="$DIST_DIR/linux/$APP_NAME"

echo "[1/5] Limpando diretórios temporários..."
rm -rf "$BUILD_DIR"
rm -rf "$DIST_DIR/linux"
mkdir -p "$BUILD_DIR/classes"
mkdir -p "$BUILD_DIR/jar"
mkdir -p "$DIST_DIR/linux"

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

echo "[4/5] Executando jpackage (empacotando runtime Java embutido)..."
jpackage \
    --type app-image \
    --input "$BUILD_DIR/jar" \
    --main-jar "$APP_NAME.jar" \
    --main-class "$MAIN_CLASS" \
    --name "$APP_NAME" \
    --app-version "$APP_VERSION" \
    --dest "$DIST_DIR/linux" \
    --add-modules java.base \
    --jlink-options "--strip-debug --no-man-pages --no-header-files"

echo "[5/5] Criando launchers de dois cliques (Play-JavaQuest.sh e .desktop)..."

# Criar script launcher com autodetecção de terminal para 2 cliques
cat <<'EOF' > "$LINUX_DIST/Play-JavaQuest.sh"
#!/usr/bin/env bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
BIN="$DIR/bin/JavaQuest"

# Se o jogo foi iniciado sem um terminal gráfico (ex: duplo clique no gerenciador de arquivos),
# detecta e abre automaticamente o emulador de terminal padrão do sistema:
if [ -z "$CI" ] && [ -z "$HEADLESS" ] && [ ! -t 0 ] && [ ! -t 1 ]; then
    for term in x-terminal-emulator gnome-terminal konsole ptyxis xfce4-terminal mate-terminal alacritty kitty foot xterm; do
        if command -v "$term" >/dev/null 2>&1; then
            case "$term" in
                gnome-terminal|mate-terminal|ptyxis)
                    exec "$term" -- bash -c "cd '$DIR' && '$BIN'; echo; read -n 1 -s -r -p 'Pressione qualquer tecla para sair...'"
                    ;;
                konsole)
                    exec "$term" --hold -e bash -c "cd '$DIR' && '$BIN'"
                    ;;
                xfce4-terminal)
                    exec "$term" --hold -e "bash -c \"cd '$DIR' && '$BIN'\""
                    ;;
                alacritty|kitty|foot)
                    exec "$term" -e bash -c "cd '$DIR' && '$BIN'; echo; read -n 1 -s -r -p 'Pressione qualquer tecla para sair...'"
                    ;;
                *)
                    exec "$term" -e bash -c "cd '$DIR' && '$BIN'; echo; read -n 1 -s -r -p 'Pressione qualquer tecla para sair...'"
                    ;;
            esac
        fi
    done
fi

# Se já estiver em um terminal ativo, executa diretamente
exec "$BIN" "$@"
EOF
chmod +x "$LINUX_DIST/Play-JavaQuest.sh"
chmod +x "$LINUX_DIST/bin/$APP_NAME"

# Criar atalho .desktop padrão Linux
cat <<EOF > "$LINUX_DIST/JavaQuest.desktop"
[Desktop Entry]
Version=1.0
Type=Application
Name=Java Quest: The Bytecode Chronicles
GenericName=RPG de Terminal
Comment=RPG de combate em turnos e quiz de Java no bytecode da JVM
Exec=bash -c '"\$(dirname "%k")/Play-JavaQuest.sh"'
Path=%k
Terminal=true
Icon=utilities-terminal
Categories=Game;RolePlaying;
StartupNotify=false
EOF
chmod +x "$LINUX_DIST/JavaQuest.desktop"

# Criar instruções rápidas
cat <<EOF > "$LINUX_DIST/COMO_JOGAR.txt"
================================================================================
          JAVA QUEST: THE BYTECODE CHRONICLES - INSTRUCOES RAPIDAS
================================================================================

COMO JOGAR COM 2 CLIQUES:
1. De dois cliques no arquivo 'Play-JavaQuest.sh' (ou 'JavaQuest.desktop').
2. O jogo abrira automaticamente no terminal com o Java embutido!
3. Nao e necessario instalar Java nem configurar nada no seu computador.

JOGAR VIA TERMINAL (OPCIONAL):
Se preferir abrir pelo terminal manualmente:
   ./Play-JavaQuest.sh
================================================================================
EOF

# Empacotar em tar.gz pronto para o GitHub Releases
TAR_NAME="JavaQuest-Linux-x64.tar.gz"
echo "Gerando pacote compactado para distribuição: $DIST_DIR/$TAR_NAME..."
tar -czf "$DIST_DIR/$TAR_NAME" -C "$DIST_DIR/linux" "$APP_NAME"

echo ""
echo "================================================================================"
echo "    BUILD CONCLUÍDO COM SUCESSO! PACOTE PRONTO PARA DISTRIBUIÇÃO!             "
echo "================================================================================"
echo "Pasta Standalone : $LINUX_DIST"
echo "Arquivo Release  : $DIST_DIR/$TAR_NAME ($(du -h "$DIST_DIR/$TAR_NAME" | cut -f1))"
echo "================================================================================"
