### What is this repository for? ### 

This repository is for Eggon's Android library, Eggoid. 

### Installation ### 

To import the library put in your app's module: 
```
repositories { 
    maven { 
        url 'todo' 
        credentials { 
            username 'username' 
            password 'password' 
        } 
    } 
} 
dependencies { 
    compile 'co.eggon:eggoid:1.0'
}
```

## RealmActivity/RealmFragment

These class will help you handle realm with ease. Just extend RealmActivity/RealmFragment and you're done.
A default realm instance will be opened at `onCreate` (for RealmActivity) or at `onStart` (for RealmFragment) and it will be closed at `onDestroy/onStop` respectively for RealmActivity and RealmFragment.

To configure a realm instance you just need to override the `onRealmSetup` method and return a customized `RealmConfiguration`

```
override onRealmSetup(){
    val realmConfig = RealmConfiguration.Builder().name("custom.realm")
    return realmConfig.build()
}
```

Anyawy a shorter way to do that with Kotlin is using inline functions:
```
override onRealmSetup() = RealmConfiguration.Builder().name("custom.realm").build()
```

If you need to change the realm instance/configuration you're using you can easily do that with the `changeConfig(RealmConfiguration.Builder().name("new.realm").build())` method

## RealmPromise
Using RealmActivity/RealmFragment you will also get RealmPromise and some helper method like insert, remove and select

Example usage will be added ASAP

## RetroRealm

This extension is just an helper to avoid retrofit + rxjava + realm boilerplate, and provide two methods:
`Observable.objectToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((RealmModel) -> Unit)? = null): RealmPromise`
and
`Observable.listToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((RealmList) -> Unit)? = null): RealmPromise`

These two methods are nearly identical, but as the name suggest the first is for saving a single object, the second one is for saving a list of objects.
They provide a beforeSave callback, that can help you adding for example a customized primary key (since realm doesn't support composed keys)
Usage:
```
YourObservableThatReaturnsRealmObject().objectToRealm(realm, false, { it.partOne + it.partTwo });

YourObservableThatReaturnsRealmList().listToRealm(realm, beforeSave = { it.forEach { it.partOne + it.partTwo } });
```

So for a realm world example, mixing RealmActivity and RetroRealm, you just need to pass the realm instance given by the activity, and implement your ServiceFactory and REST service interface:

```
ServiceFactory().with(UserService::kclass).listToRealm(realm) // users are now stored on realm
```

## Groupie

Groupie will help you editing View´s attribute/listener for more views at once
Usage is really simple:
`Groupie(view1, view2, viewN).setOnClickListener(..)`
or
`Groupie(..).visibility = View.GONE`

Animation are partially supported as now
`Groupie(..).animate().setDuration(..).otherAttribute(..)`

##TODO: Add bind function inside RealmActivity, RealmFragment and a View extension
https://medium.com/@quiro91/improving-findviewbyid-with-kotlin-4cf2f8f779bb
