# Introduce

![Platform-Android](https://img.shields.io/badge/Platform-Android-blue.svg) ![SupportAPI](https://img.shields.io/badge/API-23+-green.svg) ![SupportAPI](https://img.shields.io/badge/Android-6.0+-orange.svg) ![Language](https://img.shields.io/badge/Language-Kotlin-sky.svg)

A Android SDK and Demo to Control SIMO Heater via BLE.

![Demo](demo.gif)

# How to use

## 1. Add Codes In build.gradle(Global or App)

Add the codes below in file `build.gradle(YOUR_PROJECT_NAME)` to import SMHeaterSDK in all module:

```
allprojects {
	repositories {
		google()
		jcenter()
		
		// Add this Absolute Address
		maven { url "https://raw.githubusercontent.com/Shenzhen-Simo-Technology-co-LTD/SMHeaterDemo/master" }
    }
}
```

Or 


Add the codes below in file `build.gradle(YOUR_MODULE)` to import SMHeaterSDK in only YOUR_MODULE:

```
android {
	...
	repositories {
        maven { url "https://raw.githubusercontent.com/Shenzhen-Simo-Technology-co-LTD/SMHeaterDemo/master" }
    }
}
``` 

## 2. Add Codes In build.gradle(app)


```
dependencies {
	implementation 'com.simo:SMHeaterSDK:0.0.2'
	...
}
```

## Log

### 0.0.2
- Support auto-reconnet, Add `fun didStartReconnect` in `SMHeaterDelegate`
- Add more device infomations in `SMHeater`
- Support the latest Hardware version.

### 0.0.1
- Support the oldest Hardware version.