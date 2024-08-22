// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    id("com.google.dagger.hilt.android") version "2.47" apply false
}

buildscript {
//    ext.kotlin_version = '1.8.0'

    dependencies {
//        classpath 'com.android.tools.build:gradle:7.3.1'
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
    }
}