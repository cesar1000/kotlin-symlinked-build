# Friend paths are not supported with a symlinked build directory

This is a repro for an [issue](https://youtrack.jetbrains.com/issue/KT-35341) where the Kotlin compiler fails to set up friend paths correctly if the build directory uses a symbolic link.

The following repro creates a second build directory `build2` and then creates `build` as a symbolic link to `build2` before running the compilation:

```
$ ./gradlew :test
```

The build fails with:

```
e: /Users/cpuerta/temp/KT-35341_symlink_test/src/test/kotlin/com/twitter/test/MyTest.kt: (8, 17): Cannot access 'MyClass': it is internal in 'com.twitter.test'
```
Running the following shows the compiler command-line:
```
$ ./gradlew :test -d | grep \\-Xfriend-paths
2020-06-13T14:28:44.688-0700 [DEBUG] [org.gradle.api.Task] [KOTLIN] :compileTestKotlin Kotlin compiler args: -Xallow-no-source-files -classpath /Users/cpuerta/temp/KT-35341_symlink_test/build/classes/kotlin/main:/Users/cpuerta/.gradle/caches/modules-2/files-2.1/junit/junit/4.12/2973d150c0dc1fefe998f834810d68f278ea58ec/junit-4.12.jar:/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/1.3.72/8032138f12c0180bc4e51fe139d4c52b46db6109/kotlin-stdlib-1.3.72.jar:/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar:/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib-common/1.3.72/6ca8bee3d88957eaaaef077c41c908c9940492d8/kotlin-stdlib-common-1.3.72.jar:/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains/annotations/13.0/919f0dfe192fb4e063e7dacadee7f8bb9a2672a9/annotations-13.0.jar -d /Users/cpuerta/temp/KT-35341_symlink_test/build/classes/kotlin/test -Xfriend-paths=/Users/cpuerta/temp/KT-35341_symlink_test/build2/classes/java/main,/Users/cpuerta/temp/KT-35341_symlink_test/build2/classes/kotlin/main -module-name kotlin-symlinked-build -no-reflect -no-stdlib -Xplugin=/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-script-runtime/1.3.72/657d8d34d91e1964b4439378c09933e840bfe8d5/kotlin-script-runtime-1.3.72.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-scripting-common/1.3.72/e09990437040879d692655d66f58a64318681ffe/kotlin-scripting-common-1.3.72.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-scripting-compiler-embeddable/1.3.72/f8fb26323755b46c221da2b37a65da20381bc896/kotlin-scripting-compiler-embeddable-1.3.72.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-scripting-compiler-impl-embeddable/1.3.72/fe188748283b313eb2ef17029bcdc4b861dfb53d/kotlin-scripting-compiler-impl-embeddable-1.3.72.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-scripting-jvm/1.3.72/7dde2c909e6f1b80245c7ca100d32a8646b5666d/kotlin-scripting-jvm-1.3.72.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib-common/1.3.72/6ca8bee3d88957eaaaef077c41c908c9940492d8/kotlin-stdlib-common-1.3.72.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/1.3.72/8032138f12c0180bc4e51fe139d4c52b46db6109/kotlin-stdlib-1.3.72.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlinx/kotlinx-coroutines-core/1.2.1/3839faf625f4197acaeceeb6da000f011a2acb49/kotlinx-coroutines-core-1.2.1.jar,/Users/cpuerta/.gradle/caches/modules-2/files-2.1/org.jetbrains/annotations/13.0/919f0dfe192fb4e063e7dacadee7f8bb9a2672a9/annotations-13.0.jar -verbose /Users/cpuerta/temp/KT-35341_symlink_test/src/test/kotlin/com/twitter/test/MyTest.kt
```
The following command-line arguments explain the mismatch, since the friend paths use the canonical path, but the classpath does not:
```
-classpath /Users/cpuerta/temp/KT-35341_symlink_test/build/classes/kotlin/main:...
-Xfriend-paths=/Users/cpuerta/temp/KT-35341_symlink_test/build2/classes/java/main,/Users/cpuerta/temp/KT-35341_symlink_test/build2/classes/kotlin/main
```
The bug was likely introduced by [this change](https://github.com/JetBrains/kotlin/commit/d1016f0221abdc6e70eb9e3f4b57a5339a601ba9#diff-d19da3a6bc24dc285cb9a3f83a1c73ecR239).
