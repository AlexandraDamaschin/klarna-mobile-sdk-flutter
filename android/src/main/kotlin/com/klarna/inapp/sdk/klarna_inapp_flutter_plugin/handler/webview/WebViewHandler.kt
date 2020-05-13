package com.klarna.inapp.sdk.klarna_inapp_flutter_plugin.handler.webview

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import com.klarna.inapp.sdk.klarna_inapp_flutter_plugin.PluginContext
import com.klarna.inapp.sdk.klarna_inapp_flutter_plugin.ResultError
import com.klarna.inapp.sdk.klarna_inapp_flutter_plugin.util.evaluateJavascriptCompat
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


internal class WebViewHandler : MethodChannel.MethodCallHandler {

    private val webView: WebView by lazy {
        WebView(PluginContext.activity)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        try {
            val method = WebViewMethod.findMethod(call)
            method?.let { onMethod(it, result) } ?: result.notImplemented()
        } catch (e: Throwable) {
            result.error(ResultError.EXCEPTION.errorCode, call.method, e.message)
        }
    }

    private fun onMethod(method: WebViewMethod, result: MethodChannel.Result) {
        when (method) {
            is WebViewMethod.Show -> show(method, result)
            is WebViewMethod.Hide -> hide(method, result)
            is WebViewMethod.LoadURL -> loadURL(method, result)
            is WebViewMethod.LoadJS -> loadJS(method, result)
        }
    }

    private fun show(method: WebViewMethod.Show, result: MethodChannel.Result) {
        addToActivityIfDetached()
        webView.visibility = View.VISIBLE
        result.success(null)
    }

    private fun hide(method: WebViewMethod.Hide, result: MethodChannel.Result) {
        webView.visibility = View.GONE
        result.success(null)
    }

    private fun loadURL(method: WebViewMethod.LoadURL, result: MethodChannel.Result) {
        addToActivityIfDetached()
        webView.loadUrl(method.url)
        result.success(method.url)
    }

    private fun loadJS(method: WebViewMethod.LoadJS, result: MethodChannel.Result) {
        addToActivityIfDetached()
        webView.evaluateJavascriptCompat(method.js, null)
        result.success(method.js)
    }

    private fun addToActivityIfDetached() {
        PluginContext.activity?.let {
            if (webView.parent == null) {
                val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )

                it.addContentView(webView, params)
            }
        }
    }

}