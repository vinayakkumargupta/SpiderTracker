package com.spidertracker.app.domain.model

enum class SpiderActivity(val title: String, val description: String) {
    SWINGING("Swinging", "Spider-Man is swinging between buildings!"),
    ROOFTOP("Rooftop", "Rooftop movement detected!"),
    WEB_DETECTED("Web Detected", "Possible web activity detected!"),
    RUNNING("Running", "Very fast movement detected!"),
    HIDING("Hiding", "Spider-Man disappeared from radar!"),
    SPOTTED("Spotted", "Someone spotted Spider-Man nearby!")
}
