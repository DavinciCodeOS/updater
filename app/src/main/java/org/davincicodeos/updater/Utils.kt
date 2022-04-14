package org.davincicodeos.updater

import android.annotation.SuppressLint
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Method

object SystemProperties {
    private var failedUsingReflection = false
    private var getPropMethod: Method? = null

    @SuppressLint("PrivateApi")
    fun getProp(propName: String, defaultResult: String = ""): String {
        if (!failedUsingReflection) try {
            if (getPropMethod == null) {
                val clazz = Class.forName("android.os.SystemProperties")
                getPropMethod = clazz.getMethod("get", String::class.java, String::class.java)
            }
            return getPropMethod!!.invoke(null, propName, defaultResult) as String? ?: defaultResult
        } catch (e: Exception) {
            getPropMethod = null
            failedUsingReflection = true
        }
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("getprop \"$propName\" \"$defaultResult\"")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            return reader.readLine()
        } catch (e: IOException) {
        } finally {
            process?.destroy()
        }
        return defaultResult
    }
}


object Utils {
    private val propBuildVersionDisplay = "org.pixelexperience.version.display";
    private val dcosxBuildName = "DavinciCodeOSX";

    fun getCurrentFlavour(): String {
        val buildVersionDisplay = SystemProperties.getProp(propBuildVersionDisplay, "");

        if (buildVersionDisplay == "") {
            // Fallback, always use dcos in case of doubt
            return "dcos";
        } else {
            val osNamePretty = buildVersionDisplay.split("_")[0];

            if (osNamePretty == dcosxBuildName) {
                return "dcosx";
            } else {
                return "dcos";
            }
        }
    }
}