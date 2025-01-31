## Description
PseudoApkSigner is designed to sign APK files on an Android device without adding a ton of dependencies. However it comes with a limitation - you have to supply it with a .RSA file template (read how to make one in "How to make a RSA file template") and a .pk8 private key file.

## How it works
PseudoApkSigner uses a trick to sign APKs. While it generates .MF and .SF files properly, it doesn't actually generate .RSA file header that contains information about signing certificate in ASN.1 format. Instead, PseudoApkSigner uses pre-generated .RSA file header and then just adds .SF file signature after it.

## How to make a .RSA file template
1. Sign an APK using apksigner or jarsigner with the key you want to then use with PseudoApkSigner
2. Extract .RSA file (located in META-INF directory) from the signed APK and remove the last X bytes (where X equals your RSA key size) from it with any hex editor
3. You're done, you can now use this .RSA file as a template file for PseudoApkSigner

## Adding to project
```kotlin
repositories {
    mavenCentral()
}
```
Add this line to module-level build.gradle dependencies:
```kotlin
implementation("io.github.solrudev:pseudoapksigner:1.8")
```

## Usage
It's super simple once you have the .RSA template and private key files:

```java
new PseudoApkSigner(templateFile, privateKeyFile).sign(inputApkFile, outputSignedApkFile);
```

This will read APK from the `inputApkFile` file, sign it with .RSA file template read from the `templateFile` file and private key read from the `privateKeyFile` file and then write signed APK to the `outputSignedApkFile` file

There's also `PseudoApkSignerInputStream` which wraps an APK `InputStream` and signs the APK, so you will be reading a signed APK from it. The only problem with it is that you won't be able to know the final size of the APK you'll read:
```java
PseudoApkSignerInputStream signedApkInputStream = new PseudoApkSignerInputStream(templateFile, privateKeyFile, unsignedApkInputStream);
//Read signed apk
```
## License
```
Copyright (C) 2019 Aefyr, 2023 Ilya Fomichev

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```