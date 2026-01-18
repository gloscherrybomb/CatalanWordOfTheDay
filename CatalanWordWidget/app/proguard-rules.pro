# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard-defaults.txt file.

# Keep widget provider
-keep class com.cozyla.catalanword.WordWidgetProvider { *; }

# Keep data classes for Gson
-keep class com.cozyla.catalanword.Word { *; }
