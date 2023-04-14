import java.util.*

plugins {
	id("java")
	id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
	`maven-publish`
	signing
}

java {
	withSourcesJar()
	withJavadocJar()
}

val publishArtifactId = "pseudoapksigner"
val publishGroupId = "io.github.solrudev"
val publishVersion = "1.7"

group = publishGroupId
version = publishVersion

val ossrhUsername by extra("")
val ossrhPassword by extra("")
val sonatypeStagingProfileId by extra("")
extra["signing.keyId"] = ""
extra["signing.password"] = ""
extra["signing.key"] = ""

val secretPropertiesFile = project.rootProject.file("local.properties")
if (secretPropertiesFile.exists()) {
	with(Properties()) {
		secretPropertiesFile.inputStream().use(::load)
		forEach { name, value -> extra[name as String] = value }
	}
}

extra["ossrhUsername"] = System.getenv("OSSRH_USERNAME") ?: extra["ossrhUsername"]
extra["ossrhPassword"] = System.getenv("OSSRH_PASSWORD") ?: extra["ossrhPassword"]
extra["sonatypeStagingProfileId"] = System.getenv("SONATYPE_STAGING_PROFILE_ID") ?: extra["sonatypeStagingProfileId"]
extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID") ?: extra["signing.keyId"]
extra["signing.password"] = System.getenv("SIGNING_PASSWORD") ?: extra["signing.password"]
extra["signing.key"] = System.getenv("SIGNING_KEY") ?: extra["signing.key"]

nexusPublishing {
	repositories {
		sonatype {
			stagingProfileId.set(sonatypeStagingProfileId)
			username.set(ossrhUsername)
			password.set(ossrhPassword)
			nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
			snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
		}
	}
}

afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>("release") {
				groupId = publishGroupId
				artifactId = publishArtifactId
				version = publishVersion
				from(components["java"])

				pom {
					name.set(publishArtifactId)
					description.set("A lightweight Java library to sign APK files on Android")
					url.set("https://github.com/solrudev/PseudoApkSigner")

					licenses {
						license {
							name.set("The Apache Software License, Version 2.0")
							url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
						}
					}

					developers {
						developer {
							id.set("aefyr")
							name.set("Edward Lincoln")
							email.set("polychromaticfox@gmail.com")
						}
						developer {
							id.set("solrudev")
							name.set("Ilya Fomichev")
						}
					}

					scm {
						connection.set("scm:git:github.com/solrudev/PseudoApkSigner.git")
						developerConnection.set("scm:git:ssh://github.com/solrudev/PseudoApkSigner.git")
						url.set("https://github.com/solrudev/PseudoApkSigner/tree/master")
					}
				}
			}
		}
	}
}

signing {
	val keyId = extra["signing.keyId"] as String
	val key = extra["signing.key"] as String
	val password = extra["signing.password"] as String
	useInMemoryPgpKeys(keyId, key, password)
	sign(publishing.publications)
}