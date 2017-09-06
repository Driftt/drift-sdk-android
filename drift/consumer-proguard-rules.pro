
-dontobfuscate
# See https://speakerdeck.com/chalup/proguard
-optimizations !code/allocation/variable

-keep public class * {
    public protected *;
}

-keep public class drift.com.drift.Drift