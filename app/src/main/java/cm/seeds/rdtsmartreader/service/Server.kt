package cm.seeds.rdtsmartreader.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import cm.seeds.rdtsmartreader.helper.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class Server : Service() {

    companion object{
        const val ACTION_CONNECT_TO_SERVER = "CONNECT_TO_SERVER"
        const val ACTION_DISCONNECT_TO_SERVER = "DISCONNECT_TO_SERVER"

        const val SERVER_ADDRESS = "192.168.1.100"
        const val SERVER_PORT = 9090

        private var clientSocket: Socket? = null

        val scope = CoroutineScope(Job() + Dispatchers.IO)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){

            ACTION_CONNECT_TO_SERVER -> {
                Log.e("TAG","connexion au serveur...")
                connectToServer()
            }

            ACTION_DISCONNECT_TO_SERVER ->{
                Log.e("TAG","deconnexion du serveur...")
                disconnectToServer()
            }

        }

        return flags
    }

    private fun disconnectToServer() {
        clientSocket?.close()
        showToast(applicationContext,"deconnexion effectuée")
    }

    private fun connectToServer() {
        scope.launch {
            clientSocket = Socket(SERVER_ADDRESS, SERVER_PORT)
            if(clientSocket?.isConnected == true){
/*                showToast(applicationContext,"Connexion au client réussi \n " +
                        "Addrresse ip du serveur ${clientSocket?.inetAddress?.address}")*/

                Log.e("TAG","Connexion au client réussi \n " +
                        "Addrresse ip du serveur ${clientSocket?.inetAddress?.hostAddress}")

                try {
                    while (clientSocket?.isConnected == true){
                        val reader = BufferedReader(InputStreamReader(clientSocket?.getInputStream()))
                        val message = reader.readLine()
                        //showToast(applicationContext,message)
                        Log.e("TAG","message du serveur: $message")
                    }
                }catch (e : Exception){

                }
            }
        }
    }
}