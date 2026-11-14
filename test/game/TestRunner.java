package game;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestRunner {

    public static void main(String[] args) {
        Ansi.FAST_MODE = true;

        System.out.println(Ansi.BRIGHT_CYAN + "================================================================================" + Ansi.RESET);
        System.out.println(Ansi.BRIGHT_YELLOW + Ansi.BOLD + "       JAVA QUEST: THE BYTECODE CHRONICLES - EXHAUSTIVE TEST SUITE RUNNER       " + Ansi.RESET);
        System.out.println(Ansi.CYAN + "       Catálogo Exaustivo de Testes Unitários, Integração e Invariantes        " + Ansi.RESET);
        System.out.println(Ansi.BRIGHT_CYAN + "================================================================================" + Ansi.RESET);
        System.out.println();

        long startTime = System.currentTimeMillis();

        Class<?>[] testClasses = {
            PlayerTest.class,
            EnemyTest.class,
            QuestionTest.class,
            QuestionBankTest.class,
            AnsiTest.class,
            CharacterSpriteTest.class,
            BattleRendererTest.class,
            GameEngineLogicTest.class,
            CombatTurnIntegrationTest.class,
            DebugPowersIntegrationTest.class,
            FullCampaignProgressionTest.class,
            GameOverAndRetryIntegrationTest.class,
            ConcurrencyAndThreadSafetyIntegrationTest.class,
            EndToEndHeadlessSessionTest.class,
            MutationTargetsTest.class,
            SystemInvariantsTest.class,
            PolishingFeaturesTest.class,
            Phase3FeaturesTest.class,
            ComprehensivePolishingFeaturesTest.class,
            ComprehensiveGameFlowValidationTest.class
        };

        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();
        for (Class<?> testClass : testClasses) {
            builder.selectors(DiscoverySelectors.selectClass(testClass));
        }
        LauncherDiscoveryRequest request = builder.build();

        Launcher launcher = LauncherFactory.create();
        TestPlan testPlan = launcher.discover(request);

        AtomicInteger testsSucceeded = new AtomicInteger(0);
        AtomicInteger testsFailed = new AtomicInteger(0);
        AtomicInteger testsSkipped = new AtomicInteger(0);
        List<String> failureDetails = new ArrayList<>();

        launcher.registerTestExecutionListeners(new TestExecutionListener() {
            @Override
            public void executionStarted(TestIdentifier testIdentifier) {
                if (testIdentifier.isContainer() && testIdentifier.getParentId().isPresent() &&
                    testIdentifier.getDisplayName().startsWith("2.") ||
                    testIdentifier.getDisplayName().startsWith("3.") ||
                    testIdentifier.getDisplayName().startsWith("4.") ||
                    testIdentifier.getDisplayName().startsWith("5.") ||
                    testIdentifier.getDisplayName().startsWith("6.") ||
                    testIdentifier.getDisplayName().startsWith("7.") ||
                    testIdentifier.getDisplayName().startsWith("8.") ||
                    testIdentifier.getDisplayName().startsWith("9.")) {
                    System.out.println(Ansi.BRIGHT_WHITE + Ansi.BOLD + "\n──▶ " + testIdentifier.getDisplayName() + Ansi.RESET);
                }
            }

            @Override
            public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
                if (testIdentifier.isTest()) {
                    if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
                        testsSucceeded.incrementAndGet();
                        System.out.println("  " + Ansi.BRIGHT_GREEN + "✔ " + Ansi.RESET + Ansi.DIM + testIdentifier.getDisplayName() + Ansi.RESET);
                    } else if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
                        testsFailed.incrementAndGet();
                        System.out.println("  " + Ansi.BRIGHT_RED + Ansi.BOLD + "✘ FAIL: " + testIdentifier.getDisplayName() + Ansi.RESET);
                        Throwable t = testExecutionResult.getThrowable().orElse(null);
                        if (t != null) {
                            StringWriter sw = new StringWriter();
                            t.printStackTrace(new PrintWriter(sw));
                            failureDetails.add(testIdentifier.getDisplayName() + "\n" + sw);
                        }
                    } else if (testExecutionResult.getStatus() == TestExecutionResult.Status.ABORTED) {
                        testsSkipped.incrementAndGet();
                        System.out.println("  " + Ansi.YELLOW + "⊘ SKIPPED: " + testIdentifier.getDisplayName() + Ansi.RESET);
                    }
                }
            }
        });

        launcher.execute(request);

        long elapsed = System.currentTimeMillis() - startTime;

        System.out.println();
        System.out.println(Ansi.BRIGHT_CYAN + "================================================================================" + Ansi.RESET);
        System.out.println(Ansi.BOLD + "                              RELATÓRIO FINAL DE EXECUÇÃO                       " + Ansi.RESET);
        System.out.println(Ansi.BRIGHT_CYAN + "================================================================================" + Ansi.RESET);

        int total = testsSucceeded.get() + testsFailed.get() + testsSkipped.get();
        System.out.printf("  Total de Testes Executados : %s%d%s\n", Ansi.BRIGHT_WHITE + Ansi.BOLD, total, Ansi.RESET);
        System.out.printf("  Testes Bem-Sucedidos (PASS): %s%d%s\n", Ansi.BRIGHT_GREEN + Ansi.BOLD, testsSucceeded.get(), Ansi.RESET);
        System.out.printf("  Testes Falhos       (FAIL): %s%d%s\n", (testsFailed.get() > 0 ? Ansi.BRIGHT_RED + Ansi.BOLD : Ansi.DIM), testsFailed.get(), Ansi.RESET);
        System.out.printf("  Testes Ignorados    (SKIP): %s%d%s\n", Ansi.YELLOW, testsSkipped.get(), Ansi.RESET);
        System.out.printf("  Tempo Total de Execução    : %s%d ms%s\n", Ansi.BRIGHT_YELLOW + Ansi.BOLD, elapsed, Ansi.RESET);
        System.out.println(Ansi.BRIGHT_CYAN + "================================================================================" + Ansi.RESET);

        if (!failureDetails.isEmpty()) {
            System.out.println(Ansi.BRIGHT_RED + "\nDETALHES DAS FALHAS:" + Ansi.RESET);
            for (String failure : failureDetails) {
                System.out.println(Ansi.RED + "--------------------------------------------------------------------------------" + Ansi.RESET);
                System.out.println(failure);
            }
            System.exit(1);
        } else {
            System.out.println(Ansi.BRIGHT_GREEN + Ansi.BOLD + "  🏆 TODOS OS TESTES PASSARAM COM SUCESSO! QUALIDADE 100% GARANTIDA! 🏆" + Ansi.RESET);
            System.out.println();
            System.exit(0);
        }
    }
}
