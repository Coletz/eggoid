![Bitbucket Version](https://img.shields.io/badge/Version-1.1.3-brightgreen.svg)

# What is this repository for?

This repository is for Eggon's Android library, Eggoid. 

# Installation

To import the library put in your app's module: 
```gradle
repositories { 
    maven { 
        url 'https://api.bitbucket.org/1.0/repositories/eggon/eggon-android-library/raw/master/maven-repo' 
        credentials { 
            // insert these in your gradle.properties like this:
            // bitbucket_username=youremail@eggonemail.com
            // bitbucket_password=yourpassword
            username bitbucket_username
            password bitbucket_password
        } 
    } 
} 
dependencies { 
    compile 'co.eggon:eggoid:1.1.3'
}
```

# Usage
### RealmActivity/RealmFragment

These classes will help you handle Realm with ease. Just extend RealmActivity/RealmFragment and you're done.
A default Realm instance will be opened at `onCreate` (for RealmActivity) or at `onStart` (for RealmFragment) and it will be closed at `onDestroy/onStop` respectively for RealmActivity and RealmFragment.
The Realm instance is simply called "realm".

To configure a Realm instance you just need to override the `onRealmSetup` method and return a customized `RealmConfiguration`

```kotlin
override fun onRealmSetup(){
    val realmConfig = RealmConfiguration.Builder().name("custom.realm")
    return realmConfig.build()
}
```

Anyway a shorter way to do that with Kotlin is using inline functions:
```kotlin
override fun onRealmSetup():RealmConfiguration = RealmConfiguration.Builder().name("custom.realm").build()
```

If you need to change the Realm instance/configuration you're using you can easily do that with  `changeConfig(RealmConfiguration.Builder().name("new.realm").build())`

### RetroRealm

This extension is just an helper to avoid Retrofit + RxJava + Realm boilerplate, and currently there are 4 methods:

* `Observable<RealmModel>.objectToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((RealmModel) -> Unit)? = null): RealmPromise<RealmModel>`

* `Observable<RealmList<RealmModel>>.listToRealm(...): RealmPromise<RealmList<RealmModel>>`

* `Observable<DataWrapper>.listToRealm(...): RealmPromise<DataWrapper>`

* `Observable<DataListWrapper>.listToRealm(...): RealmPromise<DataListWrapper>`

The last 2 methods are used when the server's actual response is wrapped in a "data" field.

To use `DataWrapper` and `DataListWrapper` simply implement the one you need and override the field as requested and specify what class will the "data" field contain.
```kotlin
class SomeResponse : DataWrapper<WrappedRealmObject> {
    override var data: WrappedRealmObject? = null
}
```
```kotlin
class SomeResponse : DataListWrapper<WrappedRealmObject> {
    override var data: RealmList<WrappedRealmObject>? = null
}
```

These methods provide a beforeSave callback, that can help you add, for example, a customized primary key (since Realm doesn't support composed keys).
Usage:
```kotlin
MyObservableRealmObject().objectToRealm(realm, false, { it.partOne + it.partTwo });

MyObservableRealmList().listToRealm(realm, beforeSave = { it.forEach { it.partOne + it.partTwo } });
```

So for a real world example, mixing RealmActivity and RetroRealm, you just need to pass the Realm instance given by the activity, and implement your ServiceFactory and REST service interface:

```kotlin
ServiceFactory().with(UserService::class)
                .getAllUsers()      // API call that returns Observable<RealmList<User>>
                .listToRealm(realm) // users are now stored on realm
```

### RealmPromise
Using RealmActivity/RealmFragment you will also get RealmPromise and some helper method like insert, remove and select. Simply chain calls that return a RealmPromise to use .then or .onError for example the previous call can be enriched with:

```kotlin
ServiceFactory().with(UserService::class)
                .getAllUsers()      // API call that returns Observable<RealmList<User>>
                .listToRealm(realm) // users are now stored on Realm
                .then {
                    // do something with the RealmList<User>
                }
                .onError {
                    handle(it)      // it is a Throwable
                }
```

### Groupie

Groupie will help you edit multiple views' attributes/listeners at once.
Usage is really simple:
`Groupie(view1, view2, viewN).setOnClickListener(..)`
or
`Groupie(...).visibility = View.GONE`

Animation are partially supported as now
`Groupie(...).animate().setDuration(..).otherAttribute(..)`