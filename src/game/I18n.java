package game;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Sistema de Internacionalização (i18n) com suporte a Português (pt-BR) e Inglês (en-US).
 */
public class I18n {

    public enum Language {
        PT_BR("Português (Brasil)", Locale.of("pt", "BR")),
        EN_US("English (US)", Locale.US);

        private final String displayName;
        private final Locale locale;

        Language(String displayName, Locale locale) {
            this.displayName = displayName;
            this.locale = locale;
        }

        public String getDisplayName() { return displayName; }
        public Locale getLocale() { return locale; }

        public static Language fromCode(String code) {
            if (code == null) return PT_BR;
            String c = code.trim().toLowerCase();
            if (c.startsWith("en") || c.equals("english") || c.equals("inglês") || c.equals("ingles")) {
                return EN_US;
            }
            return PT_BR;
        }
    }

    private static volatile Language currentLanguage = Language.EN_US;
    private static final Map<Language, Properties> bundleCache = new EnumMap<>(Language.class);

    static {
        loadBundles();
    }

    public static Language getLanguage() {
        return currentLanguage;
    }

    public static void setLanguage(Language lang) {
        if (lang != null) {
            currentLanguage = lang;
        }
    }

    public static Language toggleLanguage() {
        Language next = (currentLanguage == Language.PT_BR) ? Language.EN_US : Language.PT_BR;
        setLanguage(next);
        return next;
    }

    public static String get(String key) {
        if (key == null) return "";
        Properties props = bundleCache.get(currentLanguage);
        if (props != null && props.containsKey(key)) {
            return props.getProperty(key);
        }
        // Fallback to default in-memory translations
        return getDefaultMessage(key, currentLanguage);
    }

    public static String get(String key, Object... args) {
        String template = get(key);
        if (args == null || args.length == 0) {
            return template;
        }
        return MessageFormat.format(template, args);
    }

    private static void loadBundles() {
        for (Language lang : Language.values()) {
            Properties p = new Properties();
            String resourceName = (lang == Language.PT_BR) ? "messages_pt_BR.properties" : "messages_en.properties";
            try (InputStream is = I18n.class.getClassLoader().getResourceAsStream(resourceName)) {
                if (is != null) {
                    try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        p.load(isr);
                    }
                }
            } catch (Exception ignored) {}
            bundleCache.put(lang, p);
        }
    }

    private static String getDefaultMessage(String key, Language lang) {
        boolean isEn = (lang == Language.EN_US);
        return switch (key) {
            case "menu.title" -> isEn ? "THE BYTECODE CHRONICLES: CONQUER THE JVM" : "THE BYTECODE CHRONICLES: CONQUISTE A JVM";
            case "menu.subtitle" -> isEn ? "Solve Java Puzzles • Cast Bytecode Spells • Defeat Legendary Bugs"
                                         : "Resolva Desafios Java • Lance Magias de Bytecode • Derrote Bugs Lendários";
            case "menu.start" -> isEn ? "Start New Adventure" : "Iniciar Nova Aventura";
            case "menu.resume" -> isEn ? "Resume Saved Campaign" : "Continuar Campanha Salva";
            case "menu.resume.details" -> isEn ? "Resume Campaign (Lvl {0} - {1} | HP: {2}/{3})"
                                               : "Continuar Campanha (Nível {0} - {1} | HP: {2}/{3})";
            case "menu.howtoplay" -> isEn ? "How to Play & Spells Guide" : "Como Jogar & Guia de Feitiços";
            case "menu.cheatsheet" -> isEn ? "Java Quick Reference & Lore" : "Referência Rápida de Java & Lore";
            case "menu.difficulty" -> isEn ? "Select Difficulty Mode" : "Selecionar Modo de Dificuldade";
            case "menu.lab" -> isEn ? "Bytecode Lab: Free Practice Mode (No Game Over)"
                                    : "Bytecode Lab: Modo de Treino Livre (Sem Game Over)";
            case "menu.achievements" -> isEn ? "Bytecode Badges: Achievements" : "Bytecode Badges: Conquistas";
            case "menu.leaderboard" -> isEn ? "Hall of Fame // Local Leaderboard (Top 10)"
                                            : "Hall da Fama // Local Leaderboard (Top 10)";
            case "menu.themes" -> isEn ? "Visual Themes, Language & Accessibility"
                                       : "Paleta Visual, Idioma & Acessibilidade";
            case "menu.special" -> isEn ? "Special Modes (Boss Rush & Endless Dungeon)"
                                        : "Modos Especiais (Boss Rush & Endless Dungeon)";
            case "menu.exit" -> isEn ? "Exit Game" : "Sair do Jogo";

            case "combat.action.prompt" -> isEn ? "Choose your action for this turn:" : "Escolha sua ação neste turno:";
            case "combat.cast" -> isEn ? "Cast Code Spell (Answer Java Quiz)" : "Lançar Feitiço de Código (Responder Quiz)";
            case "combat.debug" -> isEn ? "Use Debug Power (Refactor Heal / Hint / Shield)"
                                        : "Usar Poderes Debug (Cura/Dica/Escudo)";
            case "combat.inspect" -> isEn ? "Inspect Enemy Lore & Weakness" : "Inspecionar Fraquezas do Inimigo";
            case "combat.surrender" -> isEn ? "Surrender Battle" : "Fugir da Batalha";
            case "combat.backpack" -> isEn ? "Dev Backpack & Items" : "Dev Backpack & Consumíveis";

            case "theme.title" -> isEn ? "THEMES, LANGUAGE & ACCESSIBILITY" : "PALETAS, IDIOMA & ACESSIBILIDADE";
            case "theme.subtitle" -> isEn ? "Configure Palettes, High Contrast, i18n & Audio"
                                          : "Configurar Cores, Alto Contraste, i18n e Áudio";
            case "theme.highcontrast" -> isEn ? "High Contrast" : "Alto Contraste";
            case "theme.nocolor" -> isEn ? "No Color" : "Sem Cores";
            case "theme.active" -> isEn ? "ACTIVE" : "ATIVO";
            case "theme.disabled" -> isEn ? "DISABLED" : "DESATIVADO";
            case "theme.audio.on" -> isEn ? "AUDIO ON" : "ÁUDIO LIGADO";
            case "theme.audio.off" -> isEn ? "AUDIO MUTED" : "ÁUDIO MUTADO";
            case "theme.color.on" -> isEn ? "COLORS ACTIVE" : "CORES ATIVAS";
            case "theme.color.off" -> isEn ? "NO-COLOR (PLAIN TEXT)" : "NO-COLOR (TEXTO PURO)";
            case "theme.toggle.contrast" -> isEn ? "Toggle High Contrast Mode" : "Alternar Modo Alto Contraste";
            case "theme.toggle.nocolor" -> isEn ? "Toggle ANSI Color Support" : "Alternar Suporte a Cores ANSI";
            case "theme.toggle.audio" -> isEn ? "Toggle Game Audio" : "Alternar Áudio do Jogo";
            case "theme.toggle.language" -> isEn ? "Toggle Language / Idioma" : "Alternar Idioma / Language";
            case "theme.back" -> isEn ? "Return to Main Menu" : "Voltar ao Menu Principal";
            case "theme.updated" -> isEn ? "Preferences updated successfully!" : "Preferências atualizadas com sucesso!";
            case "theme.language.switched" -> isEn ? "Language set to: {0}" : "Idioma definido para: {0}";

            case "difficulty.intern" -> isEn ? "Intern Mode (Casual / -35% Dmg)" : "Modo Estagiário (Casual / -35% Dano)";
            case "difficulty.mid" -> isEn ? "Mid-level Developer (Normal / 1.0x Dmg)" : "Desenvolvedor Pleno (Normal / 1.0x Dano)";
            case "difficulty.senior" -> isEn ? "Senior Architect (Hardcore / Permadeath)" : "Arquiteto Sênior (Hardcore / Permadeath)";

            case "modes.title" -> isEn ? "SPECIAL GAME MODES // BYTECODE WARS" : "MODOS ESPECIAIS DE REJOGABILIDADE";
            case "modes.subtitle" -> isEn ? "Advanced Replayability and JVM Challenges" : "Rejogabilidade Avançada e Desafios da JVM";
            case "modes.rush" -> isEn ? "Boss Rush Mode (6-Boss Gauntlet with Partial Heal)"
                                      : "Boss Rush Mode (Gauntlet dos 6 Chefes e Cura Parcial)";
            case "modes.endless" -> isEn ? "Endless Bytecode Dungeon (Infinite Floors with Scaling Difficulty)"
                                         : "Endless Bytecode Dungeon (Andares Infinitos com Dificuldade Escalar)";
            case "modes.back" -> isEn ? "Return to Main Menu" : "Voltar ao Menu Principal";

            default -> key;
        };
    }
}
