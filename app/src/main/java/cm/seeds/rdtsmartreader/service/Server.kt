package cm.seeds.rdtsmartreader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.data.AppDatabase
import cm.seeds.rdtsmartreader.data.Dao
import cm.seeds.rdtsmartreader.data.RequestResult
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Image
import cm.seeds.rdtsmartreader.modeles.ServerState
import com.google.gson.Gson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.Executors

class Server : Service() {

    private lateinit var  dao : Dao
    private lateinit var notificationManager: NotificationManager
    private var clientSocket: Socket? = null

    private var binder = LocalBinder()
    private var serverListener : ServerListener? = null
    private var serverState = ServerState(ServerListener.State.STOPPED)

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private var serverLaunched = false
    private var imageReceiverHandler = object : FormDataHandler(){
        override fun handle(httpExchange: HttpExchange?, parts: List<MultiPart?>?) {
            when(httpExchange?.requestMethod){

                "GET" -> {
                    Log.e("TAG","MESSAGE RECU en GET, create")
                    sendResponse<String>(httpExchange,RequestResult.error(msg = ""))
                }

                "POST" -> {
                    Log.e(LOG_TAG,"Reception de la requete en post")
                    parts?.forEach {
                        Log.e(LOG_TAG,"TYPE DE PART ===> ${it?.type}")
                        if(it!=null) {
                            val image = BitmapFactory.decodeByteArray(it.bytes, 0, it.bytes?.size ?: 0)
                            if(image!=null){
                                val fileForSave = createTempFile()
                                image.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    100,
                                    fileForSave.outputStream()
                                )

                                scope.launch {
                                    val bitmaps = pythonMod.preproccessImage(fileForSave.absolutePath)
                                    if(!bitmaps.isNullOrEmpty()){
                                        val bitmap = bitmaps.first()
                                        if(bitmap!=null){
                                            val labelOfResult = pythonMod.getLabelOfCategory(pythonMod.identify(bitmap))
                                            dao.saveImages(listOf(Image(filePath = fileForSave.absolutePath,name = fileForSave.name,result = labelOfResult)))
                                        }
                                    }

                                }
                            }
                        }
                    }
                    sendResponse(httpExchange,RequestResult.success(data = "Message recu"))
                }

                else -> {

                }

            }
        }
    }
    val rootHandler = HttpHandler{

        when(it.requestMethod){

            "GET" -> {
                Log.e("TAG","MESSAGE RECU en GET, root")
                sendResponse(it,RequestResult.success("Welcome"))
            }

            "POST" -> {
                sendResponse<String>(it,RequestResult.error(msg = "Cette url n'est pas supporté en POST, veuillez pousser votre requete en get",data = null))
            }

            else -> {

            }

        }

    }
    private var httpServer : HttpServer? = null

    companion object {

        //Server actions
        const val ACTION_CONNECT_TO_SERVER = "CONNECT_TO_SERVER"
        const val ACTION_DISCONNECT_TO_SERVER = "DISCONNECT_TO_SERVER"
        const val TOGGLE_CONNECTION_DECONNEXION = "TOGGLE_CONNECTION_DECONNEXION"


        private const val SERVER_ADDRESS = "192.168.4.1"
        private const val SERVER_PORT = 5001
        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

        private val pythonMod = PythonMod()
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): Server = this@Server
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        dao = AppDatabase.database(application).getDao()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {

            ACTION_CONNECT_TO_SERVER -> {
                Log.e("TAG", "connexion au serveur...")
                connectToServer()
            }

            ACTION_DISCONNECT_TO_SERVER -> {
                Log.e("TAG", "deconnexion du serveur...")
                disconnectToServer()

            }

            TOGGLE_CONNECTION_DECONNEXION ->{
                when(serverState.state){
                    ServerListener.State.CONNECTED -> {
                       createAndLaunchServer()
                    }
                    ServerListener.State.LAUNCHED -> {
                        disconnectToServer()
                    }
                    ServerListener.State.STOPPED -> {
                        connectToServer()
                    }
                    ServerListener.State.CONNECTING -> {
                        serverListener?.onServerStateChange(newState = ServerListener.State.CONNECTING)
                    }
                }
            }

        }

        return flags
    }

    fun setListener(serverListener: ServerListener){
        this.serverListener = serverListener
        this.serverListener?.onServerStateChange(serverState.state)
    }

    private fun createAndLaunchServer() {
        notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentText("Notification de reception des images")
            .setContentTitle("Image")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_round_router_24)
            .build()

        Log.e("TAG", "Debut du server")
        startForeground(NOTIFICATION_CHANNEL_ID.hashCode(), notification)

        httpServer?.stop(0)
        httpServer = HttpServer.create(InetSocketAddress(SERVER_PORT), 0)
        httpServer?.apply {
            executor = Executors.newCachedThreadPool()
            createContext("/",rootHandler)
            createContext("/create", imageReceiverHandler)
            start()
        }

        serverListener?.onServerStateChange(ServerListener.State.LAUNCHED)
    }

    private fun<T> sendResponse(
        httpExchange: HttpExchange,
        requestResult: RequestResult<T>
    ) {
        try {
            httpExchange.responseHeaders.add("Content-Type", "application/json");
            val response = Gson().toJson(requestResult)
            httpExchange.sendResponseHeaders(
                200,
                response.toByteArray(Charset.forName("UTF-8")).size.toLong()
            )
            httpExchange.responseHeaders["Content-Type"] = "application/json, charset=UTF-8"
            val outputStream = httpExchange.responseBody
            outputStream.write(response.toByteArray(Charset.forName("UTF-8")))
            outputStream.close()
        } catch (e: IOException) {
        }
    }

    private fun disconnectToServer() {
        clientSocket?.close()
        //showToast(applicationContext, "deconnexion effectuée")
        stopForeground(true)
        serverLaunched = false
        serverListener?.onServerStateChange(ServerListener.State.STOPPED)
    }

    override fun onDestroy() {
        super.onDestroy()
        serverLaunched = false
    }

    private fun connectToServer() {
        scope.launch {
            try {
                serverListener?.onServerStateChange(ServerListener.State.CONNECTING)
                clientSocket = Socket(SERVER_ADDRESS, 80)
                if (clientSocket?.isConnected == true) {
                    serverLaunched = true
                    try {
                        if (clientSocket != null) {
                            serverListener?.onServerStateChange(ServerListener.State.CONNECTED)
                            Log.e("TAG","Connecté à la caméra")
                            createAndLaunchServer()
                        }else{
                            serverListener?.onServerStateChange(ServerListener.State.STOPPED)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        serverListener?.onServerStateChange(ServerListener.State.STOPPED)
                    }
                }else{
                    serverListener?.onServerStateChange(ServerListener.State.STOPPED)
                }
            }catch (e : Exception){
                serverListener?.onServerStateChange(ServerListener.State.STOPPED)
            }
        }
    }

    private fun parseFluxToImage(openStream: InputStream?) {

        val fileForSave = File(
            application.getExternalFilesDir(null),
            "image ${
                formatDate(
                    System.currentTimeMillis(),
                    "dd MM hh:mm"
                )
            }.jpg"
        )
        if (!fileForSave.exists()) {
            fileForSave.createNewFile()
        }

        val bitmap = BitmapFactory.decodeStream(openStream)

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileForSave.outputStream())

    }

    private fun createServer() {
        notificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentText("Notification de reception des images")
            .setContentTitle("Image")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_round_router_24)
            .build()

        Log.e("TAG", "Debut du foreground")
        startForeground(NOTIFICATION_CHANNEL_ID.hashCode(), notification)
    }

    @RequiresApi(26)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, "Image Serveur",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Serveur qui recoit les images"
            enableLights(false)
            enableVibration(false)
            setShowBadge(false)
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }


    fun createTempFile() : File{
        val fileForSave = File(
            application.getExternalFilesDir(null),
            "${
                formatDate(
                    System.currentTimeMillis(),
                    "dd MM hh:mm:ss"
                )
            }.jpg"
        )
        if (!fileForSave.exists()) {
            fileForSave.createNewFile()
        }

        return fileForSave
    }
}