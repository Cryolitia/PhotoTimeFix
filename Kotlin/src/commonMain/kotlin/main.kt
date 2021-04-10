fun main(vararg args: String) {
    println("Hello World!, I'm running in ${getPlatform()}")
}

expect fun getPlatform(): String