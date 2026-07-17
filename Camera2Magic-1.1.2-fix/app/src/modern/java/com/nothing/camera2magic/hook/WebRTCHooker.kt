package com.nothing.camera2magic.hook

import com.nothing.camera2magic.MagicHook
import com.nothing.camera2magic.utils.Dog
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam

object WebRTCHooker {
    private const val TAG = "[WebRTC]"
    private val ROTATION_REGEX = Regex("""(\d+)x(\d+).*rotation\s+(\d+)""")
    private lateinit var magic: MagicHook
    private var detectedRotation = 0
    fun initHooks(module: MagicHook, param: PackageReadyParam) {
        magic = module
        val classLoader = param.classLoader

        val loggingClass = runCatching {
            classLoader.loadClass("org.webrtc.Logging")
        }.getOrElse {
            Dog.i(TAG, "WebRTC logging class is not present; skip WebRTC rotation hook.", SourceManager.enableLog)
            return
        }

        loggingClass.apply {
            val nativeLog = getDeclaredMethod("nativeLog",
                Int::class.java, String::class.java, String::class.java)
            magic.hook(nativeLog).intercept { chain ->
                val tag = chain.args[1] as String
                val msg = chain.args[2] as String
                if (msg.contains("rotation", ignoreCase = true)) {
                    handleMessage(msg)
                }
                if (tag == "Camera2Session" && msg.contains("Stop Camera2 session", ignoreCase = true)) {
                    detectedRotation = 0
                    SourceManager.applyRotation()
                    NativeBridge.needStopRenderer()
                    NativeBridge.releaseLastRegisteredSurface()
                }

                chain.proceed()
            }
        }
    }

    private fun handleMessage(msg: String) {
        val matchResult = ROTATION_REGEX.find(msg)
        matchResult?.let {
            val (_, _, r) = it.destructured
            val rotation = r.toInt()
            if (detectedRotation != rotation) {
                Dog.i(TAG, "WebRTC set rotation: $rotation", SourceManager.enableLog)
                detectedRotation = rotation
                SourceManager.applyRotation(detectedRotation)
            }
        }
    }
}
