plugins {
	kotlin("jvm") version "1.9.0" // Kotlin 플러그인
	kotlin("plugin.spring") version "1.9.0"
	id("org.springframework.boot") version "3.2.1" // Spring Boot 플러그인
	id("io.spring.dependency-management") version "1.1.3" // Spring Dependency 관리
}

java {
	sourceCompatibility = JavaVersion.VERSION_17 // Java 21 지원
	targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
	jvmToolchain(17)
}

repositories {
	mavenCentral() // Maven Central Repository
}

dependencies {
	// 공통 Spring Boot 및 프로젝트 의존성
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.1")

	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // 최신 Jakarta API

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

	// Kotlin 관련 의존성
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// MySQL Connector
	implementation("mysql:mysql-connector-java:8.0.33")

	// Lombok
	annotationProcessor("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")

	// 테스트용
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
	testImplementation("com.h2database:h2") // H2 Database
}

tasks.withType<Test> {
	useJUnitPlatform()
}
