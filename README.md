Drift
============
[![Release](https://jitpack.io/v/driftt/drift-sdk-android.svg)]
(https://jitpack.io/#driftt/drift-sdk-android)

Drift is the official Drift SDK for Android


# Features:
- Create conversations from your app
- View past conversations from your app.


# Getting Setup

## Installation

Drift uses [Jitpack] (http://jitpack.io) 

Add Jitpack to your root build.gradle file 

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

You can then add drift as a dependency in your app build.gradle file


```
	dependencies {
		compile 'com.github.User:Repo:Tag'
	}
```

## Registering

To get started with the Drift SDK you need an embed ID from your Drift settings page. This can be accessed [here](https://app.drift.com/settings/livechat) by looking after the drift.load method in the Javascript SDK.

In your Application `onCreate` method call:
```Java
    Drift.setupDrift(this, "");
```

Once your user has successfully logged into the app registering a user with the device is done by calling register user with a unique identifier, typically the id from your database, and their email address:

```Java
  Drift.registerUser("123748", "sample@drift.com");
```

When your user logs out simply call logout so they stop receiving messages.

```Java
  Drift.logout();
```


Thats it. Your good to go!!

# Messaging

A user can begin a conversation in response to a campaign or by presenting the conversations list

```Swift
  Drift.showConversationActivity()
```

Thats it. Your good to go!!


# Contributing

Contributions are very welcome 🤘.
