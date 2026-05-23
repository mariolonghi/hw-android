plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.mariolonghi.helloweardroid.shared"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // `api` so that :app and :wear get these transitively.
    // The DataMap type from play-services-wearable is referenced in our
    // public PhoneState.toDataMap()/fromDataMap() extension functions, so
    // it has to be on the consumer's classpath too.
    api(libs.play.services.wearable)
    api(libs.kotlinx.coroutines.play.services)
}
