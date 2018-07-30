Drift
============
![Release](https://jitpack.io/v/driftt/drift-sdk-android.svg)

Drift is the official Drift SDK for Android


# Features:
- Create conversations from your app
- View past conversations from your app.


# Getting Setup

## Installation

Drift uses [Jitpack](http://jitpack.io) for its releases

Add Jitpack to your root build.gradle file 

```
allprojects {
	repositories {	
		maven { url 'https://jitpack.io' }
	}
}
```

You can then add drift as a dependency in your app build.gradle file


```
dependencies {
	compile 'com.github.driftt:drift-sdk-android:v1.1.2'
}
```

## Registering

To get started with the Drift SDK you need the embed ID from your Drift settings page. This can be accessed [here](https://app.drift.com/settings/livechat) by looking after the drift.load method in the Javascript SDK.

For devices running API level 19 you will need to call ```ProviderInstaller.installIfNeeded(this);``` in your initial activity before registering with Drift due to a change in certificates. 

In your Application `onCreate` method call:
```java
Drift.setupDrift(this, "");
```

Once your user has successfully logged into the app registering a user with the device is done by calling register user with a unique identifier, typically the id from your database, and their email address:

```java
Drift.registerUser("123748", "sample@drift.com");
```

When your user logs out simply call logout so they stop receiving messages.

```java
Drift.logout();
```


# Messaging

A user can begin a conversation in response to a campaign or by presenting the conversations list:

```java
Drift.showConversationActivity();
```

You can also go directly to create a new conversation using:

```java
Drift.showCreateConversationActivity();
```

That's it. You're good to go!!

# Customisation

Configuring the colors used within the app can be done [here](https://app.drift.com/settings/widget/design)

To configure the accent color used in the SDK you should define a color attribute in your apps color resource file.

```xml
<color name="driftColorAccent">#157AFB</color>
```

# Contributing

Contributions are very welcome 🤘.
