# TDD


Testing is one of the most important aspects of software development. We test every individual unit of source code to quickly determine the robustness of the new code. To put it simply, unit tests make sure that the new code doesn't mess up the old code. 
Every test is a method that checks the usability and reliability of the code. There are 2 types of tests: 
1. Local Unit Tests: Using JUnit n Mockito.Local means run on our own computer. They are run using JVM. They are fast and u dont need an emulator for it. It has nothing to do with Android which goes into Instrumentation testing department. It just tests pure java code. We use this to test code logic like - what does this method do, does it do the right thing, does it throw an exception, a sequence of methods are they called in correct order. Its logic based tests. There is a difference btw local n standard unit tests. Used for testing business logic. These are performed on Java Virtual Machine only and they do not interact with the OS. They are called Local as they do not need the emulator. JUnit 5 n JUnit 4, Mockito (Build mock or fake classes - given this condition it will return this response), AndroidX tests, Test-Driven Development (Build a feature, write tests, if tests pass then go for new feature). Uses JVM to write fast unit tests o test App logic. 
2. Instrumented Unit Tests: Using JUnit n Mockito. Can test Android specific functionality like Activities, Fragments, Context, Services, Lifecycle stuff that is unique to Android. They need Android framework so u need emulater for this testing. Divided into UI Tests n Unit tests. Used for testing the connection logic between our App and the Android API. We perform them on a real device or an emulator. Its kept in Android Test Directory as it uses different dependencies.
3. UI Tests: Using Espresso. Simulates a person using ur App. Pressing buttons, entering text, etc. Basically everything a use can do just super fast. Its kept in Android Test Directory.
Let's begin by adding the necessary dependencies.
Now it's important to understand the project structure for tests in Android. As of Android Studio 3.4.1, the folders are as follows. 
* com.singularitycoder.unittesting: Source code.
* com.singularitycoder.unittesting (androidTest): Instrumentation Testing directory.
* com.singularitycoder.unittesting (test): Local Unit Testing directory.
There are two different types of tests you can set up in Android
Unit Tests
* These run directly on the JVM and do not have access to the Android framework classes.
* They are kept in the test/java package
* Dependencies need to added in the build.gradle file with the command testCompile
* You generally use Mockito, Robolectric & JUnit for these tests
Instrumentation Tests
* These run on an Android emulator and have full access to all the Android classes
* They are kept in the androidTest/java package
* Dependencies need to be added to build.gradle with androidTestCompile
* You generally use Espresso and JUnit for these tests
From what I can tell you are trying to write instrumentation tests with Espresso but have your test in the test/java package which is for unit tests. In that case you need to move your test class to the androidTest/java package.

