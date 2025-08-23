plugins {
    `kotlin-conventions`
}

group = "${rootProject.group}.v3"

dependencies {
    api(project(":proxy-check-v3-api"))
}
