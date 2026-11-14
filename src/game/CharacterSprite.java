package game;

import java.util.List;

public class CharacterSprite {
    public static final int SPRITE_HEIGHT = 8;
    public static final int SPRITE_WIDTH = 24;

    // --- PLAYER FRAMES ---
    // Frame 1: The Byte Mage standing proud with glowing runic staff and star
    public static final List<String> PLAYER_IDLE_1 = List.of(
        "          /\\            ",
        "        _/  \\_    *     ",
        "       /______\\  // {;} ",
        "      / (o.o) \\ //   |  ",
        "     / \\_═══_///    === ",
        "    |  | [JVM]|         ",
        "    |  | {;}  |         ",
        "   (__/======\\__)       "
    );

    // Frame 2: Hero blinking happily, staff crystal sparkling, tunic shimmering
    public static final List<String> PLAYER_IDLE_2 = List.of(
        "          /\\    *       ",
        "        _/  \\_          ",
        "       /______\\  // {;} ",
        "      / (^.^) \\ // ✦ |  ",
        "     / \\_═══_///    === ",
        "    |  |~[JVM]|         ",
        "    |  | {;}  |         ",
        "   (__/======\\__)       "
    );

    // Charge: Leaning forward, mana glyphs spinning, lightning crackling around staff
    public static final List<String> PLAYER_CHARGE = List.of(
        "          /\\   *  ✦  *  ",
        "        _/  \\_  \\ | /   ",
        "       /______\\ -{JVM}- ",
        "      / (O.O) \\  / | \\  ",
        "     <\\_═══_//========> ",
        "    |  | ⚡⚡ |   ⚡ ⚡     ",
        "    |  | {;}  |         ",
        "   (__/======\\__)       "
    );

    // Attack: Hero lunging forward in dynamic dash, staff thrust like lance, speed lines
    public static final List<String> PLAYER_ATTACK = List.of(
        "          /\\            ",
        "        _/  \\_  ======>>",
        "   >>==/______\\  >>>>>  ",
        "  >>> ( >O< )======={;}>",
        "   >>  \\_═══_\\   >>>>>  ",
        "      | [PASS]| ======>>",
        "      |  {;}  |         ",
        "     (__/  \\__)         "
    );

    // Hurt: Knocked backwards, hat askew, dizzy stars, runtime error glitched robe
    public static final List<String> PLAYER_HURT = List.of(
        "       *  /\\  *         ",
        "      * _/  \\_ *        ",
        "    <--/______\\   ?!?   ",
        "      / (X.X) \\         ",
        "     / \\_═══_/ \\        ",
        "    |  |[ERR]|  |       ",
        "     \\ | {;} | /        ",
        "      (__) (__)         "
    );

    // Victory: Cheering arms raised, victory trophy, stars raining down
    public static final List<String> PLAYER_VICTORY = List.of(
        "     \\(^o^)/   ★  ✦  ★  ",
        "        /\\      \\ | /   ",
        "      _/  \\_    --*--   ",
        "     /______\\  [WINNER] ",
        "    / (^_^) \\    ||     ",
        "   |  |[100%]|   || {;} ",
        "   |  | {;}  |  ====    ",
        "  (__/======\\__)        "
    );

    // =========================================================================
    // --- TIER 1: EASY (LEVELS 1 - 3) ---
    // =========================================================================

    // --- ENEMY 1: NullPointer Imp (Syntax Swamp) ---
    public static final List<String> IMP_IDLE_1 = List.of(
        "  /\\_             _/\\   ",
        " /   \\_  /\\_/\\  _/   \\  ",
        " \\  /  \\( 'o' )/  \\  /  ",
        "  \\/   /|  v  |\\   \\/   ",
        "       /_| === |_\\      ",
        "         | <0> |   ~§   ",
        "        /|     |\\   `>  ",
        "      (_/     \\_) <null>"
    );

    public static final List<String> IMP_IDLE_2 = List.of(
        "       /\\_/\\            ",
        "  /\\  ( -o- )  /\\   *?  ",
        " /  \\ /|  ~  |\\ /  \\    ",
        " \\  //_|  !  |_\\\\  / <0>",
        "  \\/   | === |   \\/  ~§ ",
        "       | <0> |       `> ",
        "      /|     |\\         ",
        "     (_/     \\_) <null> "
    );

    public static final List<String> IMP_ATTACK = List.of(
        " <<<<-- [NULL_POINTER]  ",
        "   /\\_ /\\___/\\ _/\\      ",
        "  <  \\(  >X<  )/  >     ",
        " <<   /|  ▼  |\\   >>    ",
        "     /_| === |_\\        ",
        "       | <X> |   ~§     ",
        "      /|     |\\   `>    ",
        "     (_/     \\_)        "
    );

    public static final List<String> IMP_HURT = List.of(
        "         *  #  *        ",
        "     /\\ /\\___/\\ /\\      ",
        "    (  ( >_< )  )  OUCH!",
        "     \\--|     |--/      ",
        "       / === \\          ",
        "       | [ERR] |   §    ",
        "       /       \\   v    ",
        "      (_/     \\_)       "
    );

    // --- ENEMY 2: TypeCast Gremlin (Casting Caves) ---
    public static final List<String> GREMLIN_IDLE_1 = List.of(
        "     /\\_         _/\\    ",
        "    /  \\_ (o.o) _/  \\   ",
        "    \\   / \\_=_/ \\   /   ",
        "     \\_/  |(int)| \\_/   ",
        "         /|{cast}|\\  *  ",
        "        /_| === |_\\ //  ",
        "          | <0> |  //   ",
        "         (_/   \\_)===   "
    );

    public static final List<String> GREMLIN_IDLE_2 = List.of(
        "     /\\_    ✦    _/\\    ",
        "    /  \\_ (^.^) _/  \\   ",
        "    \\   / \\_=_/ \\   /   ",
        "     \\_/  |(byte) \\_/   ",
        "         /|{auto}|\\ //  ",
        "        /_| === |_\\//   ",
        "          | <0> | ===   ",
        "         (_/   \\_)      "
    );

    public static final List<String> GREMLIN_ATTACK = List.of(
        " <<<<-- (ClassCastExc)  ",
        "     /\\_ ( >X< ) _/\\    ",
        "   << \\_/ |(int)| \\_/<< ",
        "  <<<    /|{CAST}|\\ <<  ",
        "        /_| === |_\\     ",
        "          | <X> |   ==>>",
        "         /|     |\\  ==>>",
        "        (_/     \\_)     "
    );

    public static final List<String> GREMLIN_HURT = List.of(
        "        * TYPE ERROR *  ",
        "     /\\_ ( X.X ) _/\\    ",
        "     \\  \\ \\_=_/ /  /    ",
        "      \\_/'-----'\\_/ ?!  ",
        "         / === \\        ",
        "         | [ERR] |      ",
        "        /|       |\\     ",
        "       (_/       \\_)    "
    );

    // --- ENEMY 3: Infinite Loop Serpent (Recursion Ravine) ---
    public static final List<String> SERPENT_IDLE_1 = List.of(
        "         .-''''-.  while",
        "       .'  (∞)   '. (t) ",
        "      /   (@) (@)  \\ {  ",
        "     |    \\_===_/   | ..",
        "      \\   / === \\  /  } ",
        "     (===)       (===)  ",
        "      \\  '--...--'  /   ",
        "       '-(_______)-' ~~~"
    );

    public static final List<String> SERPENT_IDLE_2 = List.of(
        "         .-''''-.   {   ",
        "       .'  (∞)   '.while",
        "      /   (^) (^)  \\(t) ",
        "     |    \\_===_/-=; .. ",
        "      \\   / === \\  /  } ",
        "     (===)       (===)  ",
        "      \\  '--...--'  /   ",
        "       '-(_______)-' ~~~"
    );

    public static final List<String> SERPENT_ATTACK = List.of(
        "  <<<<< while(true)     ",
        "   <<<<<  .-''''-.      ",
        "    <<<  / >0<0<  \\===> ",
        "     << |  \\===/   |===>",
        "      <  \\       / ===> ",
        "      (===)     (===)   ",
        "       \\ '--...--' /    ",
        "        '-(_______)-'   "
    );

    public static final List<String> SERPENT_HURT = List.of(
        "       * BREAK; *  !?!  ",
        "         .-''''-.       ",
        "       .'  X   X '.     ",
        "      /   (x)-(x)  \\    ",
        "     |    /=====\\   |   ",
        "      \\   \\ === /  /  # ",
        "       (X)=(X)=(X)      ",
        "        '-(____)-'      "
    );

    // =========================================================================
    // --- TIER 2: INTERMEDIATE (LEVELS 4 - 6) ---
    // =========================================================================

    // --- ENEMY 4: IndexOutOfBounds Golem (Array Archipelago) ---
    public static final List<String> GOLEM_IDLE_1 = List.of(
        "   [0]    .---.    [1]  ",
        "   /|\\   |[o_o]|   /|\\  ",
        "  / +---'-----'---+ \\   ",
        " [#]|   [INDEX]   |[#]  ",
        " [#]|   [< LEN]   |[#]  ",
        "  \\ +-------------+ /   ",
        "    | [i++] | [i++] |   ",
        "   [===]     [===]      "
    );

    public static final List<String> GOLEM_IDLE_2 = List.of(
        "   [0]   * .---. *  [1] ",
        "   /|\\   |[-o-]|   /|\\  ",
        "  / +---'-----'---+ \\   ",
        " [#]|   [ARRAY]   |[#]  ",
        " [#]|   [ SIZE]   |[#]  ",
        "  \\ +-------------+ /   ",
        "    | [i++] | [i++] |   ",
        "   [===]     [===]      "
    );

    public static final List<String> GOLEM_ATTACK = List.of(
        " [ARRAY_OUT_OF_BOUNDS]  ",
        "   [0]   |[O_O]|   ==>> ",
        "  / +---'-----'---+==>> ",
        " [#]|   [!SMASH!]==[#]>>",
        "    |   [i>=len] =====>>",
        "  \\ +-------------+     ",
        "    | [i++] | [i++] |   ",
        "   [===]     [===]      "
    );

    public static final List<String> GOLEM_HURT = List.of(
        "     * INDEX ERROR! *   ",
        "   [0]  * .---. *  [?]  ",
        "   /|\\   |[X_X]|   /|\\  ",
        "  / +-/-'-----'-\\-+ \\   ",
        " [#]|  *CRACK*    |[#]  ",
        "  \\ +------/------+ /   ",
        "    | [ERR] | [ERR] |   ",
        "   [===]     [===]      "
    );

    // --- ENEMY 5: NullReference Specter (Encapsulation Crypt) ---
    public static final List<String> SPECTER_IDLE_1 = List.of(
        "         .-----.   *?   ",
        "        / .---. \\       ",
        "       | ( O O ) |      ",
        "       |  \\_~_/  | {;}  ",
        "       / [GHOST] \\  //  ",
        "      /  / ~~~ \\  \\//   ",
        "      \\  \\ ~~~ /  /===  ",
        "       '~ '~~~' ~'      "
    );

    public static final List<String> SPECTER_IDLE_2 = List.of(
        "         .-----.   ✦    ",
        "        / .---. \\       ",
        "       | ( - - ) |      ",
        "       |  \\_~_/  |<null>",
        "       / [NULL]  \\  //  ",
        "      /  / ~~~ \\  \\//   ",
        "      \\  \\ ~~~ /  /===  ",
        "       '~ '~~~' ~'      "
    );

    public static final List<String> SPECTER_ATTACK = List.of(
        " <<<<< {NULL_POINTER}   ",
        "       <<.-----.<<      ",
        "      <</ >O<O< \\<<     ",
        "     <<|  \\_0_/  |====> ",
        "       / [HAUNT] \\===>  ",
        "      /  / ~~~ \\  \\     ",
        "      \\  \\ ~~~ /  /     ",
        "       '~ '~~~' ~'      "
    );

    public static final List<String> SPECTER_HURT = List.of(
        "       * DEREFERENCED * ",
        "         .-----.   !?!  ",
        "        / X   X \\       ",
        "       |  (x.x)  |      ",
        "       /  /---\\  \\      ",
        "      /  / ~~~ \\  \\ #   ",
        "      \\  \\ ~~~ /  /     ",
        "       '~ '~~~' ~'      "
    );

    // --- ENEMY 6: Spaghetti Code Chimera (OOP Citadel) ---
    public static final List<String> CHIMERA_IDLE_1 = List.of(
        "  (\\__/) (\\__/) (\\__/)  ",
        "  (>.<)  ( 'o')  (>.> ) ",
        "   \\\\//_  //\\\\  _//\\\\   ",
        "   ( SPAGHETTI_CODE )   ",
        "   / ~~~ {goto} ~~~ \\   ",
        "  ( ~~~~~~~~~~~~~~~~ )  ",
        "  //  /||\\  //||\\  \\\\   ",
        " (_)(_)(_)(_)(_)(_)(_)  "
    );

    public static final List<String> CHIMERA_IDLE_2 = List.of(
        "  (\\__/) (\\__/) (\\__/)  ",
        "  ( 'o') (>.<)   (^.^)  ",
        "   _//\\\\  \\\\//_  //\\\\_  ",
        "   ( TIGHT_COUPLING )   ",
        "   / ~~~ {copy} ~~~ \\   ",
        "  ( ~~~~~~~~~~~~~~~~ )  ",
        "  \\\\  /||\\  //||\\  //   ",
        " (_)(_)(_)(_)(_)(_)(_)  "
    );

    public static final List<String> CHIMERA_ATTACK = List.of(
        " <<<< {CIRCULAR_DEP}    ",
        " << (\\X/) (\\X/) (\\X/)   ",
        " <<< (>O<) (>O<) (>O<)  ",
        " <<<<== TANGLE ==<<<<   ",
        "  <<<  SPAGHETTI  <<<   ",
        "  ( ~~~~~~~~~~~~~~~~ )  ",
        "  //  /||\\  //||\\  \\\\   ",
        " (_)(_)(_)(_)(_)(_)(_)  "
    );

    public static final List<String> CHIMERA_HURT = List.of(
        "     * REFACTORED! *    ",
        "  (\\__/) (\\__/) (\\__/)  ",
        "  (x.x)  (x.x)  (x.x)   ",
        "   \\\\//   //\\\\   \\\\//   ",
        "   ( CLEAN_CODE!    )   ",
        "   /  -SOLID- -DRY- \\   ",
        "  (  DETANGLED!     )   ",
        " (_)(_)(_)(_)(_)(_)(_)  "
    );

    // =========================================================================
    // --- TIER 3: HARD (LEVELS 7 - 9) ---
    // =========================================================================

    // --- ENEMY 7: Deadlock Shadow (Concurrency Catacombs) ---
    public static final List<String> DEADLOCK_IDLE_1 = List.of(
        "    /\\__         __/\\   ",
        "   (>.<)=========(>.<)  ",
        "   /|\\  [LOCK_A]  /|\\   ",
        "  / | \\    ||    / | \\  ",
        " |  |  [DEADLOCK]  |  | ",
        "  \\ |   [LOCK_B]   | /  ",
        "   \\|/   //\\\\     \\|/   ",
        "   (_/  (_/\\_)     \\_)  "
    );

    public static final List<String> DEADLOCK_IDLE_2 = List.of(
        "    /\\__    ✦    __/\\   ",
        "   (-.-)=========(-.-)  ",
        "   /|\\  [SYNC_A]  /|\\   ",
        "  / | \\    ||    / | \\  ",
        " |  |  [WAITING ]  |  | ",
        "  \\ |   [SYNC_B]   | /  ",
        "   \\|/   //\\\\     \\|/   ",
        "   (_/  (_/\\_)     \\_)  "
    );

    public static final List<String> DEADLOCK_ATTACK = List.of(
        " <<<< [THREAD_FREEZE]   ",
        "  < (>O<)=======!(>O<)< ",
        " <<<|\\  [MUTEX]   /|<<< ",
        " <<<<<<=== BLOCK ===<<< ",
        " |  |  [STARVE]    |  | ",
        "  \\ |  [CRASH]     | /  ",
        "   \\|/   //\\\\     \\|/   ",
        "   (_/  (_/\\_)     \\_)  "
    );

    public static final List<String> DEADLOCK_HURT = List.of(
        "     * LOCK RELEASED! * ",
        "    /\\__   !?!   __/\\   ",
        "   (x.x) - - - - (x.x)  ",
        "   /|\\  [UNLOCKED] /|\\  ",
        "  / | \\          / | \\  ",
        " |  |  [FREE_CPU]  |  | ",
        "   \\|/   //\\\\     \\|/   ",
        "   (_/  (_/\\_)     \\_)  "
    );

    // --- ENEMY 8: Memory Leak Hydra (The Heap Tomb) ---
    public static final List<String> HYDRA_IDLE_1 = List.of(
        "   /\\_      _       _/\\ ",
        "  (o o)   (o o)   (o o) ",
        "  (\\==/)  (\\==/)  (\\==/)",
        "   \\\\//    ||     \\\\//  ",
        "   /  [HEAP: 99%]   \\   ",
        "  | 0xDEAD 0xBEEF 0xCA |",
        "   \\  [OUT_OF_MEM]  /   ",
        "  ~~~'~~~~~~~~~~~~'~~~  "
    );

    public static final List<String> HYDRA_IDLE_2 = List.of(
        "   /\\_      _       _/\\ ",
        "  (- -)   (- -)   (- -) ",
        "  (\\==/)  (\\==/)  (\\==/)",
        "   //\\\\    ||     //\\\\  ",
        "   /  [LEAKING...]  \\   ",
        "  | 0xFE 0xED 0xBA 0xBE|",
        "   \\  [MEM: 100%]   /   ",
        "  o~~'~~~~~~~~~~~~'~~o  "
    );

    public static final List<String> HYDRA_ATTACK = List.of(
        " <<<< [OutOfMemory] <<  ",
        "  (O O)   (O O)   (O O) ",
        " <<\\00/<<<\\00/<<<\\00/<< ",
        " <<<<=== HEAP FLOOD === ",
        "   / [JAVA_HEAP_SPACE]\\ ",
        "  |  FULL EXHAUSTION  | ",
        "   \\                /   ",
        "  ~~~~'~~~~~~~~~~'~~~~  "
    );

    public static final List<String> HYDRA_HURT = List.of(
        "    * System.gc()! *    ",
        "   /\\_      _       _/\\ ",
        "  (X X)   (X X)   (X X) ",
        "  (\\==/)  (\\==/)  (\\==/)",
        "   \\\\//    ||     \\\\//  ",
        "   /  [PURGED: 0%]  \\   ",
        "  |  CLEAN HEAP MEM!  | ",
        "  ~~~'~~~~~~~~~~~~'~~~  "
    );

    // --- ENEMY 9: Bytecode Behemoth (Bytecode Badlands) ---
    public static final List<String> BEHEMOTH_IDLE_1 = List.of(
        "   <T>    .---.    <E>  ",
        "   /|\\   |[===]|   /|\\  ",
        "  / +---'-----'---+ \\   ",
        " [#]|  [OPCODE]   |[#]  ",
        " [#]|  [CAFEBABE] |[#]  ",
        "  \\ +-------------+ /   ",
        "    | [invok] | [ret] | ",
        "   [===]       [===]    "
    );

    public static final List<String> BEHEMOTH_IDLE_2 = List.of(
        "   <K>   * .---. * <V>  ",
        "   /|\\   |[---]|   /|\\  ",
        "  / +---'-----'---+ \\   ",
        " [#]|  [BYTECODE] |[#]  ",
        " [#]|  [GENERICS] |[#]  ",
        "  \\ +-------------+ /   ",
        "    | [iload] | [pop] | ",
        "   [===]       [===]    "
    );

    public static final List<String> BEHEMOTH_ATTACK = List.of(
        " [TYPE_ERASURE_SLAM]    ",
        "   <T>   |[O_O]|   ==>> ",
        "  / +---'-----'---+==>> ",
        " [#]|  [SMASH!] ==[#]>> ",
        "    |  [OPCODE] ======>>",
        "  \\ +-------------+     ",
        "    | [invok] | [ret] | ",
        "   [===]       [===]    "
    );

    public static final List<String> BEHEMOTH_HURT = List.of(
        "    * CLASS FORMAT ERR *",
        "   <?>   * .---. * <?>  ",
        "   /|\\   |[X_X]|   /|\\  ",
        "  / +-/-'-----'-\\-+ \\   ",
        " [#]|  *CORRUPT*  |[#]  ",
        "  \\ +------/------+ /   ",
        "    | [ERR] | [ERR] |   ",
        "   [===]       [===]    "
    );

    // =========================================================================
    // --- TIER 4: EXTREMELY HARD (LEVELS 10 - 12) ---
    // =========================================================================

    // --- ENEMY 10: ClassCast Manticore (Reflection Rift) ---
    public static final List<String> MANTICORE_IDLE_1 = List.of(
        "    /\\_    _~->   _/\\   ",
        "   /  \\_  (o.o)  _/  \\  ",
        "   \\  / \\_═══_/  \\  /   ",
        "    \\/  /|STREAM|\\ \\/   ",
        "       /_|LAMBDA|_\\     ",
        "         | .map |  ~§   ",
        "        /|      |\\  `>  ",
        "       (_/      \\_)     "
    );

    public static final List<String> MANTICORE_IDLE_2 = List.of(
        "    /\\_    _~->✦  _/\\   ",
        "   /  \\_  (^.^)  _/  \\  ",
        "   \\  / \\_═══_/  \\  /   ",
        "    \\/  /|FILTER|\\ \\/   ",
        "       /_|REDUCE|_\\     ",
        "         | .flat|  ~§   ",
        "        /|      |\\  `>  ",
        "       (_/      \\_)     "
    );

    public static final List<String> MANTICORE_ATTACK = List.of(
        " <<<< .filter(x->crash) ",
        "    /\\_  ( >O< )  _/\\   ",
        "  <<< \\/|PIPELINE|\\/<<< ",
        " <<<<<<=== STING ==<<<< ",
        "       /_|LAMBDA|_\\     ",
        "         | .exec|  ==>> ",
        "        /|      |\\ ==>> ",
        "       (_/      \\_)     "
    );

    public static final List<String> MANTICORE_HURT = List.of(
        "    * PIPELINE BROKEN * ",
        "    /\\_   * !?! * _/\\   ",
        "   /  \\_  (X.X)  _/  \\  ",
        "   \\  / \\_═══_/  \\  /   ",
        "    \\/  /|[FAIL]|\\ \\/   ",
        "       /_| ==== |_\\     ",
        "        /|      |\\      ",
        "       (_/      \\_)     "
    );

    // --- ENEMY 11: OutOfMemory Leviathan (Metaspace Abyss) ---
    public static final List<String> LEVIATHAN_IDLE_1 = List.of(
        "       _..-------.._    ",
        "     .' (o)(O)(o)   '.  ",
        "    /  (  \\_▼_/  )    \\ ",
        "   |  [METASPACE]      |",
        "   |  [LEVIATHAN]      |",
        "    \\  (~~~~~~~~)     / ",
        "    // \\\\ //\\\\ // \\\\    ",
        "   (_)(_)(_)(_)(_)(_)   "
    );

    public static final List<String> LEVIATHAN_IDLE_2 = List.of(
        "       _..-------.._  ✦ ",
        "     .' (-)(O)(-)   '.  ",
        "    /  (  \\_▼_/  )    \\ ",
        "   |  [JVM_CRASH]      |",
        "   |  [NATIVE_MEM]     |",
        "    \\  (~~~~~~~~)     / ",
        "    \\\\ // \\\\// \\\\ //    ",
        "   (_)(_)(_)(_)(_)(_)   "
    );

    public static final List<String> LEVIATHAN_ATTACK = List.of(
        " <<<< [METASPACE_OOM]   ",
        "  << .' (O)(O)(O) '. <<<",
        " <<</  (  \\_0_/  )  \\<<<",
        " <<|  [ABYSS_MAW]   |<<<",
        " <<|  [OVERFLOW ]   |<<<",
        "    \\  (~~~~~~~~)   /   ",
        "    // \\\\ //\\\\ // \\\\    ",
        "   (_)(_)(_)(_)(_)(_)   "
    );

    public static final List<String> LEVIATHAN_HURT = List.of(
        "     * GC PURGE ABYSS * ",
        "       _..-------.._ !?!",
        "     .' (X)(X)(X)   '.  ",
        "    /  (  /---\\  )    \\ ",
        "   |  [HEAP_DRAIN]     |",
        "   |  [METACLEAN ]     |",
        "    \\  (~~~~~~~~)     / ",
        "   (_)(_)(_)(_)(_)(_)   "
    );

    // --- ENEMY 12: THE STACKOVERFLOW DRAGON (The Monolith Core) ---
    public static final List<String> DRAGON_IDLE_1 = List.of(
        "/\\__                __/\\",
        "\\_  \\_  /\\___/\\  _/  _/ ",
        " \\_  \\ ( O   O ) /  _/  ",
        "   / _ \\_ \\_^_/ _/ _ \\  ",
        "  / /  |{CALL_STACK}| \\\\",
        " ( (   |[OVERFLOW]|  ) )",
        "  \\ \\_ |===========|_/ /",
        "   (__/            \\__) "
    );

    public static final List<String> DRAGON_IDLE_2 = List.of(
        "/\\__       *        __/\\",
        "\\_  \\_  /\\___/\\  _/  _/ ",
        " \\_  \\ ( ~   ~ ) /  _/  ",
        "   / _ \\_ \\_^_/ _/ _ \\  ",
        "  / /  |{INFINITE }| \\\\ ",
        " ( (   |[RECURSION]| ) )",
        "  \\ \\_ |===========|_/ /",
        "   (__/     ~      \\__) "
    );

    public static final List<String> DRAGON_ATTACK = List.of(
        " <<<<< RECURSION_LIMIT  ",
        " <</\\__             __/\\",
        " <<\\_  \\( >O<0< )/  _/  ",
        " <<<<=={FIRE_BREATH}==> ",
        " <<<<================<< ",
        " ( (   [DESTROY]     ) )",
        "  \\ \\_ |===========|_/ /",
        "   (__/            \\__) "
    );

    public static final List<String> DRAGON_HURT = List.of(
        "   * BASE CASE FOUND! * ",
        "/\\__   *       *    __/\\",
        "\\_  \\_  /\\___/\\  _/  _/ ",
        " \\_  \\ ( X   X ) /  _/  ",
        "   / _ \\_ \\_^_/ _/ _ \\  ",
        "  / /  | TERMINATED| \\\\ ",
        " ( (   | STACK SAFE| ) )",
        "   (__/            \\__) "
    );

    // --- UNIVERSAL DEATH FRAMES ---
    // Frame 1: Fatal Glitch & Stack Trace Dump
    public static final List<String> DEATH_FRAME_1 = List.of(
        "     * FATAL EXCEPTION *",
        "    ( X _ X )   [ERR]   ",
        "   /| 01010101010101 |\\ ",
        "  /_| STACK OVERFLOW |_\\",
        "    | java.lang.Bug  |  ",
        "    | Nullified!     |  ",
        "   /|       --       |\\ ",
        "  (_/                \\_)"
    );

    // Frame 2: Matrix Binary Rain Dissolution
    public static final List<String> DEATH_FRAME_2 = List.of(
        "   0 1  0  1 0  1 0 1   ",
        " 1  0  1  0  1  0  1  0 ",
        "   0  1 [DECOMPILE] 0 1 ",
        " 1  0  1  0  1  0  1  0 ",
        "   0  1  0  1  0  1  0  ",
        " 1  0 [SWEEP GC]  1 0 1 ",
        "   0 1  0  1 0  1 0 1 0 ",
        " .   .   .    .   .   . "
    );

    // Frame 3: Exit Code 0 Pass / Bug Eradicated
    public static final List<String> DEATH_FRAME_3 = List.of(
        "                        ",
        "    .   .    .   .    . ",
        "      .   ✦    ✦   .    ",
        "    [EXIT CODE: 0 PASS] ",
        "      .   ✦    ✦   .    ",
        "    .   .    .   .    . ",
        "                        ",
        "  ~~~~~~~~~~~~~~~~~~~~  "
    );

    // Frame 4: Heap Cleaned & Deallocated Fanfare
    public static final List<String> DEATH_FRAME_4 = List.of(
        "                        ",
        "        ✦    *    ✦     ",
        "      *  [DEALLOCATED] *",
        "        ✦    *    ✦     ",
        "     [HEAP: 100% CLEAN] ",
        "          .  ·  .       ",
        "                        ",
        "                        "
    );
}
