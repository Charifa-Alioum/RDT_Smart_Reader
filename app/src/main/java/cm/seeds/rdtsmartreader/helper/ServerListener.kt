package cm.seeds.rdtsmartreader.helper

interface ServerListener {

    fun onServerStateChange(newState : State)

    enum class State {
        CONNECTED, LAUNCHED, STOPPED, CONNECTING
    }
}