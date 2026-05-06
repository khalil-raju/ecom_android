package com.ecom.app.ui.screens.payment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ecom.app.BuildConfig
import com.ecom.app.network.RetrofitClient

@Composable
fun PaymentWebViewScreen(
    url: String,
    onPaymentFinished: (success: Boolean, orderToken: String?) -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val container = FrameLayout(context)

            fun setupWebView(webView: WebView) {
                webView.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webView.settings.javaScriptEnabled = true
                webView.settings.domStorageEnabled = true
                webView.settings.javaScriptCanOpenWindowsAutomatically = true
                webView.settings.setSupportMultipleWindows(true)
                webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                webView.settings.loadWithOverviewMode = true
                webView.settings.useWideViewPort = true

                webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val requestUrl = request?.url.toString()

                        if (requestUrl.startsWith("upi://") || requestUrl.startsWith("intent://")) {
                            try {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                )
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "No supported payment app found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return true
                        }

                        if (requestUrl.contains("/orders/order/details/")) {
                            val orderToken = requestUrl
                                .substringAfter("/orders/order/details/")
                                .substringBefore("/")
                                .substringBefore("?")

                            onPaymentFinished(true, orderToken)
                            return true
                        }

                        if (requestUrl.contains("/baskets/")) {
                            onPaymentFinished(false, null)
                            return true
                        }

                        return false
                    }
                }

                webView.webChromeClient = object : WebChromeClient() {
                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        val popupWebView = WebView(context)
                        setupWebView(popupWebView)

                        container.addView(popupWebView)

                        val transport = resultMsg?.obj as WebView.WebViewTransport
                        transport.webView = popupWebView
                        resultMsg.sendToTarget()

                        return true
                    }

                    override fun onCloseWindow(window: WebView?) {
                        container.removeView(window)
                    }
                }
            }

            val mainWebView = WebView(context)
            setupWebView(mainWebView)

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(mainWebView, true)

            RetrofitClient.getCookieHeader()?.let { cookie ->
                cookieManager.setCookie(BuildConfig.BASE_URL, cookie)
                cookieManager.setCookie(url, cookie)
                cookieManager.flush()
            }

            container.addView(mainWebView)
            mainWebView.loadUrl(url)

            container
        }
    )
}