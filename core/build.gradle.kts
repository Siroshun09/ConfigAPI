plugins {
    id("configapi.common-conventions")
}

dependencies {
    testImplementation(projects.configapiTestSharedClasses)
}

afterEvaluate {
    aggregator.JavadocAggregator.addProject(this)
}
