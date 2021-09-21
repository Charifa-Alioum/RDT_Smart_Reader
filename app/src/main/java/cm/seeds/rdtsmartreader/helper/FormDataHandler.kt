package cm.seeds.rdtsmartreader.helper

import android.util.Log
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

abstract class FormDataHandler : HttpHandler {

    companion object{

        var LOG_TAG = FormDataHandler.javaClass.simpleName

        fun getInputAsBinary(requestStream: InputStream): ByteArray {
            val bos = ByteArrayOutputStream()
            try {
                val buf = ByteArray(100000)
                var bytesRead = 0
                while (requestStream.read(buf).also { bytesRead = it } != -1) {
                    //while (requestStream.available() > 0) {
                    //    int i = requestStream.read(buf);
                    bos.write(buf, 0, bytesRead)
                }
                requestStream.close()
                bos.close()
            } catch (e: IOException) {
                Log.e(LOG_TAG,e.stackTraceToString())
            }
            return bos.toByteArray()
        }


        /**
         * Search bytes in byte array returns indexes within this byte-array of all
         * occurrences of the specified(search bytes) byte array in the specified
         * range
         * borrowed from https://github.com/riversun/finbin/blob/master/src/main/java/org/riversun/finbin/BinarySearcher.java
         *
         * @param srcBytes
         * @param searchBytes
         * @param searchStartIndex
         * @param searchEndIndex
         * @return result index list
         */
        fun searchBytes(
            srcBytes: ByteArray?,
            searchBytes: ByteArray,
            searchStartIndex: Int,
            searchEndIndex: Int
        ): List<Int> {
            val destSize = searchBytes.size
            val positionIndexList: MutableList<Int> = ArrayList()
            var cursor = searchStartIndex
            while (cursor < searchEndIndex + 1) {
                val index: Int = indexOf(srcBytes, searchBytes, cursor, searchEndIndex)
                if (index >= 0) {
                    positionIndexList.add(index)
                    cursor = index + destSize
                } else {
                    cursor++
                }
            }
            return positionIndexList
        }


        /**
         * Returns the index within this byte-array of the first occurrence of the
         * specified(search bytes) byte array.<br></br>
         * Starting the search at the specified index, and end at the specified
         * index.
         * borrowed from https://github.com/riversun/finbin/blob/master/src/main/java/org/riversun/finbin/BinarySearcher.java
         *
         * @param srcBytes
         * @param searchBytes
         * @param startIndex
         * @param endIndex
         * @return
         */
        fun indexOf(
            srcBytes: ByteArray?,
            searchBytes: ByteArray?,
            startIndex: Int,
            endIndex: Int
        ): Int {
            if (searchBytes?.size == 0 || (endIndex - startIndex + 1) < searchBytes!!.size) {
                return -1
            }
            val maxScanStartPosIdx = srcBytes!!.size - searchBytes.size
            val loopEndIdx: Int = if (endIndex < maxScanStartPosIdx) {
                endIndex
            } else {
                maxScanStartPosIdx
            }
            var lastScanIdx = -1
            label@ // goto label
            for (i in startIndex..loopEndIdx) {
                for (j in searchBytes.indices) {
                    if (srcBytes[i + j] != searchBytes[j]) {
                        continue@label
                    }
                    lastScanIdx = i + j
                }
                return if (endIndex < lastScanIdx || (lastScanIdx - i + 1) < searchBytes.size) {
                    // it becomes more than the last index
                    // or less than the number of search bytes
                    -1
                } else i
            }
            return -1
        }
    }

    inner class MultiPart {
        var type: PartType? = null
        var contentType: String? = null
        var name: String? = null
        var filename: String? = null
        var value: String? = null
        var bytes: ByteArray? = null
    }

    enum class PartType {
        TEXT, FILE
    }

    @Throws(IOException::class)
    abstract fun handle(httpExchange: HttpExchange?, parts: List<MultiPart?>?)


    override fun handle(httpExchange: HttpExchange) {
        //Log.e(LOG_TAG,InputStreamReader(httpExchange.requestBody).readText())
        val headers: Headers = httpExchange.requestHeaders
        val contentType: String = headers.getFirst("Content-Type")
        if (contentType.startsWith("multipart/form-data")) {
            //found form data
            val boundary = contentType.substring(contentType.indexOf("boundary=") + 9)
            // as of rfc7578 - prepend "\r\n--"
            val ourBoundary = "\r\n--$boundary"
            val boundaryBytes: ByteArray = boundary.toByteArray(Charset.forName("UTF-8"))
            val payload = getInputAsBinary(httpExchange.requestBody)
            val list: ArrayList<MultiPart> = ArrayList()
            val offsets = searchBytes(payload, boundaryBytes, 0, payload.size - 1)
            for (idx in offsets.indices) {
                val startPart = offsets[idx]
                var endPart = payload.size
                if (idx < offsets.size - 1) {
                    endPart = offsets[idx + 1]
                }
                val part: ByteArray = Arrays.copyOfRange(payload, startPart, endPart)
                //look for header
                val headerEnd = indexOf(
                    part,
                    "\r\n\r\n".toByteArray(Charset.forName("UTF-8")),
                    0,
                    part.size - 1
                )
                if (headerEnd > 0) {
                    val p = MultiPart()
                    val head: ByteArray = Arrays.copyOfRange(part, 0, headerEnd)
                    val header = String(head)
                    // extract name from header
                    val nameIndex =
                        header.indexOf("\r\nContent-Disposition: form-data; name=")
                    if (nameIndex >= 0) {
                        val startMarker = nameIndex + 39
                        //check for extra filename field
                        val fileNameStart = header.indexOf("; filename=")
                        if (fileNameStart >= 0) {
                            val filename = header.substring(
                                fileNameStart + 11,
                                header.indexOf("\r\n", fileNameStart)
                            )
                            p.filename = filename.replace('"', ' ').replace('\'', ' ')
                                .trim { it <= ' ' }
                            p.name = header.substring(startMarker, fileNameStart)
                                .replace('"', ' ').replace('\'', ' ').trim { it <= ' ' }
                            p.type = PartType.FILE
                        } else {
                            var endMarker = header.indexOf("\r\n", startMarker)
                            if (endMarker == -1) endMarker = header.length
                            p.name =
                                header.substring(startMarker, endMarker).replace('"', ' ')
                                    .replace('\'', ' ').trim { it <= ' ' }
                            p.type = PartType.TEXT
                        }
                    } else {
                        // skip entry if no name is found
                        continue
                    }
                    // extract content type from header
                    val typeIndex = header.indexOf("\r\nContent-Type:")
                    if (typeIndex >= 0) {
                        val startMarker = typeIndex + 15
                        var endMarker = header.indexOf("\r\n", startMarker)
                        if (endMarker == -1) endMarker = header.length
                        p.contentType =
                            header.substring(startMarker, endMarker).trim { it <= ' ' }
                    }

                    //handle content
                    if (p.type == PartType.TEXT) {
                        //extract text value
                        val body: ByteArray =
                            Arrays.copyOfRange(part, headerEnd + 4, part.size)
                        p.value = String(body)
                    } else {
                        //must be a file upload
                        p.bytes = Arrays.copyOfRange(part, headerEnd + 4, part.size)
                    }
                    list.add(p)
                }
            }
            handle(httpExchange, list)
        } else {
            //if no form data is present, still call handle method
            handle(httpExchange, null)
        }
    }
}