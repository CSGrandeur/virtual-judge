package judge.remote.shared;

import org.apache.struts2.json.JSONUtil;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LanguageUtil {

    private static Map<String, LinkedHashMap<String, String>> ojLanguageMap = new HashMap<>();
    private static final LinkedHashMap<String, String> emptyMap = new LinkedHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(LanguageUtil.class);

    static {
        try{
            updateFromLocalConfigFile();
        } catch (Exception e){
            if(e instanceof FileNotFoundException){
                log.warn("loading LanguageMap from config file failed, " + e.getMessage());
            } else {
                log.warn("loading LanguageMap from config file failed", e);
            }
            try {
                updateFromVjudgeDotNet();
            } catch (Exception ex) {
                log.warn("loading LanguageMap from vjudge.net failed,", ex);
                try {
                    updateFromDefault();
                } catch (Exception exc) {
                    log.warn("loading LanguageMap from default value failed: ", exc);
                    log.error("loading LanguageMap ALL FAILED! You CAN'T submit any code! This should't happen.");
                }
            } 
        }
    }

    private static void updateFromLocalConfigFile() throws Exception {
        URL ojLanguagesConfig = Thread.currentThread().getContextClassLoader().getResource("../oj_languages.json");
        if(ojLanguagesConfig == null){
            throw new FileNotFoundException("File oj_languages.json not exists");
        }
        byte[] bytes = Files.readAllBytes(Paths.get(ojLanguagesConfig.toURI()));
        String json = new String(bytes, Charset.forName("UTF-8"));
        updateOjLanguageMap(json);
        log.info("LanguageMap loaded from config file");
    }

    public static void updateFromVjudgeDotNet() throws Exception {
        @SuppressWarnings("deprecation")
        String json = Jsoup.connect("https://vjudge.net/util/remoteOJs")
                .ignoreContentType(true)
                .timeout(1000 * 10)
                .validateTLSCertificates(false)
                .execute()
                .body();
        updateOjLanguageMap(json);
        log.info("LanguageMap loaded from vjudge.net");
    }

    private static void updateFromDefault() throws Exception {
        String defaultJson = "{\"SGU\":{\"name\":\"SGU\",\"languages\":{\"43\":\"GNU GCC C11 5.1.0\",\"52\":\"Clang++17 Diagnostics\",\"42\":\"GNU G++11 5.1.0\",\"50\":\"GNU G++14 6.4.0\",\"54\":\"GNU G++17 7.3.0\",\"2\":\"Microsoft Visual C++ 2010\",\"59\":\"Microsoft Visual C++ 2017\",\"9\":\"C# Mono 5.18\",\"28\":\"D DMD32 v2.086.0\",\"32\":\"Go 1.12.6\",\"12\":\"Haskell GHC 8.6.3\",\"36\":\"Java 1.8.0_162\",\"48\":\"Kotlin 1.3.10\",\"19\":\"OCaml 4.02.1\",\"3\":\"Delphi 7\",\"4\":\"Free Pascal 3.0.2\",\"51\":\"PascalABC.NET 3.4.2\",\"13\":\"Perl 5.20.1\",\"6\":\"PHP 7.2.13\",\"7\":\"Python 2.7.15\",\"31\":\"Python 3.7.2\",\"40\":\"PyPy 2.7 (7.1.1)\",\"41\":\"PyPy 3.6 (7.1.1)\",\"8\":\"Ruby 2.0.0p645\",\"49\":\"Rust 1.35.0\",\"20\":\"Scala 2.12.8\",\"34\":\"JavaScript V8 4.8.0\",\"55\":\"Node.js 9.4.0\"}},\n" +
                "\"FZU\":{\"name\":\"FZU\",\"languages\":{\"0\":\"GNU C++\",\"1\":\"GNU C\",\"2\":\"Pascal\",\"3\":\"Java\",\"4\":\"Visual C++\",\"5\":\"Visual C\"}},\n" +
                "\"UVA\":{\"name\":\"UVA\",\"languages\":{\"1\":\"ANSI C 5.3.0\",\"2\":\"JAVA 1.8.0\",\"3\":\"C++ 5.3.0\",\"4\":\"PASCAL 3.0.0\",\"5\":\"C++11 5.3.0\",\"6\":\"PYTH3 3.5.1\"}},\n" +
                "\"HRBUST\":{\"name\":\"HRBUST\",\"languages\":{\"2\":\"G++\",\"1\":\"GCC\",\"3\":\"JAVA\",\"4\":\"PHP\",\"5\":\"Python2\",\"7\":\"Haskell\"}},\n" +
                "\"SCU\":{\"name\":\"SCU\",\"languages\":{\"C++\":\"C++\",\"C\":\"C\",\"Java\":\"Java\",\"Pascal\":\"Pascal\"}},\n" +
                "\"51Nod\":{\"name\":\"51Nod\",\"languages\":{\"1\":\"C\",\"2\":\"C 11\",\"11\":\"C++\",\"12\":\"C++ 11\",\"21\":\"C#\",\"31\":\"Java\",\"41\":\"Python2\",\"42\":\"Python3\",\"45\":\"PyPy2\",\"46\":\"PyPy3\",\"51\":\"Ruby\",\"61\":\"Php\",\"71\":\"Haskell\",\"81\":\"Scala\",\"91\":\"Javascript\",\"101\":\"Go\",\"111\":\"Visual C++\",\"121\":\"Objective C\",\"131\":\"Pascal\"}},\n" +
                "\"TopCoder\":{\"name\":\"TopCoder\",\"languages\":{\"1\":\"Java\",\"3\":\"C++\",\"4\":\"C#\",\"5\":\"VB\",\"6\":\"Python\"}},\n" +
                "\"Z_trening\":{\"name\":\"Z_trening\",\"languages\":{\"1\":\"Pascal fpc 3.0.0\",\"2\":\"C gcc 6.3.1\",\"3\":\"C99 gcc 6.3.1\",\"4\":\"C++98 gcc 6.3.1\",\"5\":\"C++11 gcc 6.3.1\",\"6\":\"C++14 gcc 6.3.1\",\"7\":\"Java gcc-gcj 6.3.1\"}},\n" +
                "\"HUST\":{\"name\":\"HUST\",\"languages\":{\"0\":\"C\",\"1\":\"C++\",\"2\":\"Pascal\",\"3\":\"Java\"}},\n" +
                "\"EOlymp\":{\"name\":\"EOlymp\",\"languages\":{\"docker.io/eolymp/runtime-csharp\":\"C#\",\"docker.io/eolymp/runtime-gpp\":\"C++\",\"docker.io/eolymp/runtime-go\":\"Go\",\"docker.io/eolymp/runtime-haskell\":\"Haskell\",\"docker.io/eolymp/runtime-java\":\"Java\",\"docker.io/eolymp/runtime-js\":\"JavaScript\",\"docker.io/eolymp/runtime-kotlin\":\"Kotlin\",\"docker.io/eolymp/runtime-fpc\":\"Pascal\",\"docker.io/eolymp/runtime-php\":\"PHP\",\"docker.io/eolymp/runtime-python\":\"Python\",\"docker.io/eolymp/runtime-ruby\":\"Ruby\",\"docker.io/eolymp/runtime-rust\":\"Rust\"}},\n" +
                "\"HackerRank\":{\"name\":\"HackerRank\",\"languages\":{\"c\":\"c\",\"cpp\":\"cpp\",\"java\":\"java\",\"csharp\":\"csharp\",\"php\":\"php\",\"ruby\":\"ruby\",\"python\":\"python\",\"perl\":\"perl\",\"haskell\":\"haskell\",\"clojure\":\"clojure\",\"scala\":\"scala\",\"lua\":\"lua\",\"go\":\"go\",\"javascript\":\"javascript\",\"erlang\":\"erlang\",\"d\":\"d\",\"ocaml\":\"ocaml\",\"pascal\":\"pascal\",\"python3\":\"python3\",\"groovy\":\"groovy\",\"objectivec\":\"objectivec\",\"fsharp\":\"fsharp\",\"visualbasic\":\"visualbasic\",\"lolcode\":\"lolcode\",\"smalltalk\":\"smalltalk\",\"tcl\":\"tcl\",\"whitespace\":\"whitespace\",\"sbcl\":\"sbcl\",\"java8\":\"java8\",\"octave\":\"octave\",\"racket\":\"racket\",\"rust\":\"rust\",\"bash\":\"bash\",\"r\":\"r\",\"swift\":\"swift\",\"fortran\":\"fortran\",\"cpp14\":\"cpp14\",\"coffeescript\":\"coffeescript\",\"ada\":\"ada\",\"pypy\":\"pypy\",\"pypy3\":\"pypy3\",\"julia\":\"julia\",\"elixir\":\"elixir\"}},\n" +
                "\"LibreOJ\":{\"name\":\"LibreOJ\",\"languages\":{\"cpp\":\"C++ GCC 8.2.0\",\"cpp11\":\"C++ 11 GCC 8.2.0\",\"cpp17\":\"C++ 17 GCC 8.2.0\",\"cpp-noilinux\":\"C++ (NOI) GCC 4.8.4 (NOILinux 1.4.1)\",\"cpp11-noilinux\":\"C++ 11 (NOI) GCC 4.8.4 (NOILinux 1.4.1)\",\"cpp11-clang\":\"C++ 11 (Clang) Clang 7.0.1\",\"cpp17-clang\":\"C++ 17 (Clang) Clang 7.0.1\",\"c\":\"C Clang 7.0.1\",\"c-noilinux\":\"C (NOI) GCC 4.8.4 (NOILinux 1.4.1)\",\"csharp\":\"C# Mono 5.16.0.220\",\"java\":\"Java OpenJDK 10.0.2\",\"pascal\":\"Pascal Free Pascal 3.0.4\",\"python2\":\"Python 2 PyPy 6.0.0 (Python 2.7.13)\",\"python3\":\"Python 3 PyPy 6.0.0 (Python 3.5.3)\",\"nodejs\":\"Node.js 10.14.0\",\"ruby\":\"Ruby 2.5.1\",\"haskell\":\"Haskell GHC 8.6.2\"}},\n" +
                "\"OpenJ_Bailian\":{\"name\":\"OpenJ_Bailian\",\"languages\":{\"G++\":\"G++(4.5)\",\"GCC\":\"GCC(4.5)\",\"Java\":\"Java()\",\"Pascal\":\"Pascal(FreePascal)\"}},\n" +
                "\"HDU\":{\"name\":\"HDU\",\"languages\":{\"0\":\"G++\",\"1\":\"GCC\",\"2\":\"C++\",\"3\":\"C\",\"4\":\"Pascal\",\"5\":\"Java\",\"6\":\"C#\"}},\n" +
                "\"UESTC_old\":{\"name\":\"UESTC_old\",\"languages\":{}},\n" +
                "\"AtCoder\":{\"name\":\"AtCoder\",\"languages\":{\"3003\":\"C++14 (GCC 5.3.0)\",\"3001\":\"Bash (GNU bash v4.3.11)\",\"3002\":\"C (GCC 5.3.0)\",\"3004\":\"C (Clang 3.8.0)\",\"3005\":\"C++14 (Clang 3.8.0)\",\"3006\":\"C# (Mono 4.2.2.30)\",\"3007\":\"Clojure (1.8.0)\",\"3008\":\"Common Lisp (SBCL 1.1.14)\",\"3009\":\"D (DMD64 v2.070.1)\",\"3010\":\"D (LDC 0.17.0)\",\"3011\":\"D (GDC 4.9.3)\",\"3012\":\"Fortran (gfortran v4.8.4)\",\"3013\":\"Go (1.6)\",\"3014\":\"Haskell (GHC 7.10)\",\"3015\":\"Java7 (OpenJDK 1.7.0)\",\"3016\":\"Java8 (OpenJDK 1.8.0)\",\"3017\":\"JavaScript (node.js v5.7)\",\"3018\":\"OCaml (4.02.3)\",\"3019\":\"Pascal (FPC 2.6.2)\",\"3020\":\"Perl (v5.18.2)\",\"3021\":\"PHP (5.6.18)\",\"3022\":\"Python2 (2.7.6)\",\"3023\":\"Python3 (3.4.3)\",\"3024\":\"Ruby (2.3.0)\",\"3025\":\"Scala (2.11.7)\",\"3026\":\"Scheme (Gauche 0.9.3.3)\",\"3027\":\"Text (cat)\",\"3028\":\"Visual Basic (Mono 4.2.2.30)\",\"3501\":\"Objective-C (GCC 5.3.0)\",\"3502\":\"Objective-C (Clang3.7.1)\",\"3503\":\"Swift (swift-2.2-RELEASE)\",\"3504\":\"Rust (1.7.0)\",\"3505\":\"Sed (GNU sed 4.2.2)\",\"3506\":\"Awk (mawk 1.3.3)\",\"3507\":\"Brainfuck (bf 20041219)\",\"3508\":\"Standard ML (MLton 20100608)\",\"3509\":\"PyPy2 (4.0.1)\",\"3510\":\"PyPy3 (2.4.0)\",\"3511\":\"Crystal (0.12.0)\",\"3512\":\"F# (Mono 4.2.2.30)\",\"3513\":\"Unlambda (0.1.3)\",\"3514\":\"Lua (5.3.2)\",\"3515\":\"LuaJIT (2.0.2)\",\"3516\":\"MoonScript (0.4.0)\",\"3517\":\"Ceylon (1.2.1)\",\"3518\":\"Julia (0.4.2)\",\"3519\":\"Octave (4.0.0)\",\"3520\":\"Nim (0.13.0)\",\"3521\":\"TypeScript (1.8.2)\",\"3522\":\"Perl6 (rakudo-star 2016.01)\",\"3523\":\"Kotlin (1.0.0)\",\"3524\":\"PHP7 (7.0.4)\"}},\n" +
                "\"HYSBZ\":{\"name\":\"HYSBZ\",\"languages\":{\"0\":\"C\",\"1\":\"C++\",\"2\":\"Pascal\",\"3\":\"Java\",\"4\":\"Ruby\",\"5\":\"Bash\",\"6\":\"Python\"}},\n" +
                "\"Gym\":{\"name\":\"Gym\",\"languages\":{\"43\":\"GNU GCC C11 5.1.0\",\"52\":\"Clang++17 Diagnostics\",\"42\":\"GNU G++11 5.1.0\",\"50\":\"GNU G++14 6.4.0\",\"54\":\"GNU G++17 7.3.0\",\"2\":\"Microsoft Visual C++ 2010\",\"59\":\"Microsoft Visual C++ 2017\",\"61\":\"GNU G++17 9.2.0 (64 bit, msys 2)\",\"9\":\"C# Mono 5.18\",\"28\":\"D DMD32 v2.091.0\",\"32\":\"Go 1.14\",\"12\":\"Haskell GHC 8.6.3\",\"60\":\"Java 11.0.5\",\"36\":\"Java 1.8.0_162\",\"48\":\"Kotlin 1.3.70\",\"19\":\"OCaml 4.02.1\",\"3\":\"Delphi 7\",\"4\":\"Free Pascal 3.0.2\",\"51\":\"PascalABC.NET 3.4.2\",\"13\":\"Perl 5.20.1\",\"6\":\"PHP 7.2.13\",\"7\":\"Python 2.7.15\",\"31\":\"Python 3.7.2\",\"40\":\"PyPy 2.7 (7.2.0)\",\"41\":\"PyPy 3.6 (7.2.0)\",\"8\":\"Ruby 2.0.0p645\",\"49\":\"Rust 1.42.0\",\"20\":\"Scala 2.12.8\",\"34\":\"JavaScript V8 4.8.0\",\"55\":\"Node.js 9.4.0\"}},\n" +
                "\"Aizu\":{\"name\":\"Aizu\",\"languages\":{\"C\":\"C\",\"C++\":\"C++\",\"JAVA\":\"JAVA\",\"C++11\":\"C++11\",\"C++14\":\"C++14\",\"C#\":\"C#\",\"D\":\"D\",\"Ruby\":\"Ruby\",\"Python\":\"Python\",\"Python3\":\"Python3\",\"PHP\":\"PHP\",\"JavaScript\":\"JavaScript\",\"Scala\":\"Scala\",\"Haskell\":\"Haskell\",\"OCaml\":\"OCaml\",\"Rust\":\"Rust\",\"Go\":\"Go\",\"Kotlin\":\"Kotlin\"}},\n" +
                "\"SPOJ\":{\"name\":\"SPOJ\",\"languages\":{\"7\":\"Ada95 (gnat 6.3)\",\"59\":\"Any document (no testing)\",\"13\":\"Assembler 32 (nasm 2.12.01)\",\"45\":\"Assembler 32 (gcc 6.3 )\",\"42\":\"Assembler 64 (nasm 2.12.01)\",\"105\":\"AWK (mawk 1.3.3)\",\"104\":\"AWK (gawk 4.1.3)\",\"28\":\"Bash (bash 4.4.5)\",\"110\":\"BC (bc 1.06.95)\",\"12\":\"Branf**k (bff 1.0.6)\",\"81\":\"C (clang 4.0)\",\"11\":\"C (gcc 6.3)\",\"27\":\"C# (gmcs 4.6.2)\",\"1\":\"C++ (gcc 6.3)\",\"41\":\"C++ (g++ 4.3.2)\",\"82\":\"C++14 (clang 4.0)\",\"44\":\"C++14 (gcc 6.3)\",\"34\":\"C99 (gcc 6.3)\",\"14\":\"Clips (clips 6.24)\",\"111\":\"Clojure (clojure 1.8.0)\",\"118\":\"Cobol (opencobol 1.1.0)\",\"91\":\"CoffeeScript (coffee 1.12.2)\",\"31\":\"Common Lisp (sbcl 1.3.13)\",\"32\":\"Common Lisp (clisp 2.49)\",\"102\":\"D (dmd 2.072.2)\",\"84\":\"D (ldc 1.1.0)\",\"20\":\"D (gdc 6.3)\",\"48\":\"Dart (dart 1.21)\",\"96\":\"Elixir (elixir 1.3.3)\",\"36\":\"Erlang (erl 19)\",\"124\":\"F# (mono 4.0.0)\",\"92\":\"Fantom (fantom 1.0.69)\",\"107\":\"Forth (gforth 0.7.3)\",\"5\":\"Fortran (gfortran 6.3)\",\"114\":\"Go (go 1.7.4)\",\"98\":\"Gosu (gosu 1.14.2)\",\"121\":\"Groovy (groovy 2.4.7)\",\"21\":\"Haskell (ghc 8.0.1)\",\"16\":\"Icon (iconc 9.5.1)\",\"9\":\"Intercal (ick 0.3)\",\"24\":\"JAR (JavaSE 6)\",\"10\":\"Java (HotSpot 8u112)\",\"112\":\"JavaScript (SMonkey 24.2.0)\",\"35\":\"JavaScript (rhino 1.7.7)\",\"47\":\"Kotlin (kotlin 1.0.6)\",\"26\":\"Lua (luac 5.3.3)\",\"30\":\"Nemerle (ncc 1.2.0)\",\"25\":\"Nice (nicec 0.9.13)\",\"122\":\"Nim (nim 0.16.0)\",\"56\":\"Node.js (node 7.4.0)\",\"43\":\"Objective-C (gcc 6.3)\",\"83\":\"Objective-C (clang 4.0)\",\"8\":\"Ocaml (ocamlopt 4.01)\",\"22\":\"Pascal (fpc 3.0.0)\",\"2\":\"Pascal (gpc 20070904)\",\"60\":\"PDF (ghostscript 8.62)\",\"3\":\"Perl (perl 5.24.1)\",\"54\":\"Perl (perl 6)\",\"29\":\"PHP (php 7.1.0)\",\"94\":\"Pico Lisp (pico 16.12.8)\",\"19\":\"Pike (pike 8.0)\",\"61\":\"PostScript (ghostscript 8.62)\",\"15\":\"Prolog (swi 7.2.3)\",\"108\":\"Prolog (gnu prolog 1.4.5)\",\"4\":\"Python (cpython 2.7.13)\",\"99\":\"Python (PyPy 2.6.0)\",\"116\":\"Python 3 (python  3.5)\",\"126\":\"Python 3 nbc (python 3.4)\",\"117\":\"R (R 3.3.2)\",\"95\":\"Racket (racket 6.7)\",\"17\":\"Ruby (ruby 2.3.3)\",\"93\":\"Rust (rust 1.14.0)\",\"39\":\"Scala (scala 2.12.1)\",\"33\":\"Scheme (guile 2.0.13)\",\"18\":\"Scheme (stalin 0.3)\",\"97\":\"Scheme (chicken 4.11.0)\",\"46\":\"Sed (sed 4)\",\"23\":\"Smalltalk (gst 3.2.5)\",\"40\":\"SQLite (sqlite 3.16.2)\",\"85\":\"Swift (swift 3.0.2)\",\"38\":\"TCL (tcl 8.6)\",\"62\":\"Text (plain text)\",\"115\":\"Unlambda (unlambda 0.1.4.2)\",\"50\":\"VB.net (mono 4.6.2)\",\"6\":\"Whitespace (wspace 0.3)\"}},\n" +
                "\"CodeForces\":{\"name\":\"CodeForces\",\"languages\":{\"43\":\"GNU GCC C11 5.1.0\",\"52\":\"Clang++17 Diagnostics\",\"42\":\"GNU G++11 5.1.0\",\"50\":\"GNU G++14 6.4.0\",\"54\":\"GNU G++17 7.3.0\",\"2\":\"Microsoft Visual C++ 2010\",\"59\":\"Microsoft Visual C++ 2017\",\"61\":\"GNU G++17 9.2.0 (64 bit, msys 2)\",\"9\":\"C# Mono 5.18\",\"28\":\"D DMD32 v2.091.0\",\"32\":\"Go 1.14\",\"12\":\"Haskell GHC 8.6.3\",\"60\":\"Java 11.0.5\",\"36\":\"Java 1.8.0_162\",\"48\":\"Kotlin 1.3.70\",\"19\":\"OCaml 4.02.1\",\"3\":\"Delphi 7\",\"4\":\"Free Pascal 3.0.2\",\"51\":\"PascalABC.NET 3.4.2\",\"13\":\"Perl 5.20.1\",\"6\":\"PHP 7.2.13\",\"7\":\"Python 2.7.15\",\"31\":\"Python 3.7.2\",\"40\":\"PyPy 2.7 (7.2.0)\",\"41\":\"PyPy 3.6 (7.2.0)\",\"8\":\"Ruby 2.0.0p645\",\"49\":\"Rust 1.42.0\",\"20\":\"Scala 2.12.8\",\"34\":\"JavaScript V8 4.8.0\",\"55\":\"Node.js 9.4.0\",\"14\":\"ActiveTcl 8.5\",\"15\":\"Io-2008-01-07 (Win32)\",\"17\":\"Pike 7.8\",\"18\":\"Befunge\",\"22\":\"OpenCobol 1.0\",\"25\":\"Factor\",\"26\":\"Secret_171\",\"27\":\"Roco\",\"33\":\"Ada GNAT 4\",\"38\":\"Mysterious Language\",\"39\":\"FALSE\",\"44\":\"Picat 0.9\",\"45\":\"GNU C++11 5 ZIP\",\"46\":\"Java 8 ZIP\",\"47\":\"J\",\"56\":\"Microsoft Q#\",\"57\":\"Text\"}},\n" +
                "\"ACdream\":{\"name\":\"ACdream\",\"languages\":{\"1\":\"C\",\"2\":\"C++\",\"3\":\"Java\"}},\n" +
                "\"CSU\":{\"name\":\"CSU\",\"languages\":{\"0\":\"C\",\"1\":\"C++\",\"3\":\"Java\",\"6\":\"Python\"}},\n" +
                "\"CodeChef\":{\"name\":\"CodeChef\",\"languages\":{\"7\":\"ADA 95(gnat 6.3)\",\"13\":\"Assembler(nasm 2.12.01)\",\"28\":\"Bash(bash 4.4.5)\",\"12\":\"Brainf**k(bff 1.0.6)\",\"11\":\"C(gcc 6.3)\",\"34\":\"C99 strict(gcc 6.3)\",\"8\":\"Ocaml(ocamlopt 4.01)\",\"111\":\"Clojure(clojure 1.8.0)\",\"14\":\"Clips(clips 6.24)\",\"41\":\"C++(gcc 6.3)\",\"1\":\"C++(gcc 6.3)\",\"44\":\"C++14(gcc 6.3)\",\"27\":\"C#(gmcs 4.6.2)\",\"20\":\"D(gdc 6.3)\",\"36\":\"Erlang(erl 19)\",\"5\":\"Fortran(gfortran 6.3)\",\"124\":\"F#(mono 4.0.0)\",\"114\":\"Go(go 1.7.4)\",\"21\":\"Haskell(ghc 8.0.1)\",\"9\":\"Intercal(ick 0.3)\",\"16\":\"Icon(iconc 9.5.1)\",\"10\":\"Java(HotSpot 8u112)\",\"35\":\"JavaScript(rhino 1.7.7)\",\"32\":\"Common Lisp(clisp 2.49)\",\"31\":\"Common Lisp(sbcl 1.3.13)\",\"26\":\"Lua(luac 5.3.3)\",\"30\":\"Nemerle(ncc 1.2.0)\",\"25\":\"Nice(nicec 0.9.13)\",\"56\":\"JavaScript(node 7.4.0)\",\"22\":\"Pascal(fpc 3.0.0)\",\"2\":\"Pascal(gpc 20070904)\",\"3\":\"Perl(perl 5.24.1)\",\"54\":\"Perl6(perl 6)\",\"29\":\"PHP(php 7.1.0)\",\"19\":\"Pike(pike 8.0)\",\"15\":\"Prolog(swi 7.2.3)\",\"4\":\"Python(cpython 2.7.13)\",\"116\":\"Python3(python 3.5)\",\"17\":\"Ruby(ruby 2.3.3)\",\"39\":\"Scala(scala 2.12.1)\",\"33\":\"Scheme(guile 2.0.13)\",\"18\":\"Scheme(stalin 0.3)\",\"23\":\"Smalltalk(gst 3.2.5)\",\"38\":\"Tcl(tcl 8.6)\",\"62\":\"Text(pure text)\",\"6\":\"Whitespace(wspace 0.3)\"}},\n" +
                "\"UVALive\":{\"name\":\"UVALive\",\"languages\":{\"1\":\"ANSI C 5.3.0\",\"2\":\"JAVA 1.8.0\",\"3\":\"C++ 5.3.0\",\"4\":\"PASCAL 3.0.0\",\"5\":\"C++11 5.3.0\",\"6\":\"PYTH3 3.5.1\"}},\n" +
                "\"OpenJ_POJ\":{\"name\":\"OpenJ_POJ\",\"languages\":{\"G++\":\"G++(4.5)\",\"GCC\":\"GCC(4.5)\",\"Java\":\"Java()\",\"Pascal\":\"Pascal(FreePascal)\"}},\n" +
                "\"Kattis\":{\"name\":\"Kattis\",\"languages\":{\"C\":\"C\",\"C++\":\"C++\",\"Java\":\"Java\",\"Pascal\":\"Pascal\",\"Text\":\"Text\",\"Python 2\":\"Python 2\",\"Python 3\":\"Python 3\",\"C#\":\"C#\",\"Go\":\"Go\",\"Objective-C\":\"Objective-C\",\"Haskell\":\"Haskell\",\"Prolog\":\"Prolog\",\"JavaScript\":\"JavaScript\",\"PHP\":\"PHP\",\"Ruby\":\"Ruby\",\"Kotlin\":\"Kotlin\",\"Scala\":\"Scala\"}},\n" +
                "\"POJ\":{\"name\":\"POJ\",\"languages\":{\"0\":\"G++\",\"1\":\"GCC\",\"2\":\"Java\",\"3\":\"Pascal\",\"4\":\"C++\",\"5\":\"C\",\"6\":\"Fortran\"}},\n" +
                "\"HihoCoder\":{\"name\":\"HihoCoder\",\"languages\":{\"GCC\":\"GCC\",\"G++\":\"G++\",\"C#\":\"C#\",\"Java\":\"Java\",\"Python2\":\"Python2\"}},\n" +
                "\"URAL\":{\"name\":\"URAL\",\"languages\":{\"31\":\"FreePascal 2.6\",\"39\":\"Visual C 2017\",\"40\":\"Visual C++ 2017\",\"45\":\"GCC 7.1\",\"46\":\"G++ 7.1\",\"47\":\"Clang++ 4.0.1\",\"32\":\"Java 1.8\",\"41\":\"Visual C# 2017\",\"34\":\"Python 2.7\",\"48\":\"Python 3.6\",\"14\":\"Go 1.3\",\"18\":\"Ruby 1.9\",\"19\":\"Haskell 7.6\",\"33\":\"Scala 2.11\",\"43\":\"Rust 1.9\"}},\n" +
                "\"HIT\":{\"name\":\"HIT\",\"languages\":{\"C++\":\"C++\",\"C89\":\"C89\",\"Java\":\"Java\",\"Pascal\":\"Pascal\"}},\n" +
                "\"LightOJ\":{\"name\":\"LightOJ\",\"languages\":{\"C\":\"C\",\"C++\":\"C++\",\"JAVA\":\"JAVA\",\"PASCAL\":\"PASCAL\"}},\n" +
                "\"ZOJ\":{\"name\":\"ZOJ\",\"languages\":{\"GCC\":\"C (gcc 4.7.2)\",\"GXX\":\"C++ (g++ 6.4.0)\",\"JAVAC\":\"Java (java 1.8.0)\",\"PYTHON2\":\"Python (python 2.7.12)\",\"PYTHON3\":\"Python (python 3.6.5)\"}},\n" +
                "\"Minieye\":{\"name\":\"Minieye\",\"languages\":{\"C\":\"GCC 5.4\",\"C++\":\"G++ 5.4\",\"Java\":\"OpenJDK 1.8\",\"Python3\":\"Python 3.5\",\"Golang\":\"Go 1.11\"}},\n" +
                "\"NBUT\":{\"name\":\"NBUT\",\"languages\":{\"1\":\"GCC\",\"2\":\"G++\",\"4\":\"FPC\"}},\n" +
                "\"EIJudge\":{\"name\":\"EIJudge\",\"languages\":{\"Free Pascal\":\"Free Pascal 1.8.2\",\"GNU C\":\"GNU C 3.3.3\",\"GNU C++\":\"GNU C++ 3.3.3\",\"Haskell\":\"Haskell GC 6.8.2\",\"Java\":\"java 1.5.0\",\"Kylix\":\"Kylix 14.5\",\"Lua\":\"Lua 5.1.3\",\"OCaml\":\"Objective Caml 3.10.2\",\"Perl\":\"Perl 5.8.5\",\"Python\":\"Python 2.1.3\",\"Ruby\":\"Ruby 1.8.6\",\"Scheme\":\"mzScheme 301 Swindle\"}},\n" +
                "\"计蒜客\":{\"name\":\"计蒜客\",\"languages\":{\"c\":\"C\",\"c_noi\":\"C (NOI)\",\"c++\":\"C++11\",\"c++14\":\"C++14\",\"c++_noi\":\"C++ (NOI)\",\"java\":\"Java\",\"python\":\"Python 2.7\",\"python3\":\"Python 3.5\",\"ruby\":\"Ruby\",\"blocky\":\"Blockly\",\"octave\":\"Octave\",\"pascal\":\"Pascal\",\"go\":\"Go\"}},\n" +
                "\"UESTC\":{\"name\":\"UESTC\",\"languages\":{\"1\":\"C\",\"2\":\"C++\",\"3\":\"Java\"}}}";
        updateOjLanguageMap(defaultJson);
        log.info("LanguageMap loaded from default value");
    }

    @SuppressWarnings("unchecked")
    private static void updateOjLanguageMap(String json) throws Exception {
        Map<String,Object> ojs = (Map<String, Object>) JSONUtil.deserialize(json);
        for(String oj : ojs.keySet()) {
            LinkedHashMap<String, String> languageList = new LinkedHashMap<>(
                    (Map<String, String>)((Map<String, Object>)ojs.get(oj)).get("languages"));
            if(!languageList.isEmpty()){
                ojLanguageMap.put(oj.toUpperCase(), languageList);
            }
        }
        if(ojLanguageMap.isEmpty()) {
            throw new Exception("can't get languages of any Oj from the json");
        }
    }

    public static LinkedHashMap<String, String> getDefaultLanguages(String oj) {
        LinkedHashMap<String, String> result = ojLanguageMap.get(oj.toUpperCase());
        return result == null ? emptyMap : result;
    }
}
