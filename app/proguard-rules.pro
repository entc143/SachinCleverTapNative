# CleverTap ships its own consumer ProGuard/R8 rules inside the AAR,
# so no extra keep rules are required for the SDK itself.

# Glide (used by CleverTap App Inbox and the Native Display screen)
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** { *; }
