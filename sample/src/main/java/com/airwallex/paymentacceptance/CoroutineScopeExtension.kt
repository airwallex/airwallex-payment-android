package com.airwallex.paymentacceptance

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
}

fun CoroutineScope.safeLaunch(
    customContext: CoroutineContext? = null,
    exceptionHandler: CoroutineExceptionHandler = coroutineExceptionHandler,
    work: suspend CoroutineScope.() -> Unit
): Job {
    return if (customContext == null) launch(
        context = exceptionHandler,
        block = work
    ) else launch(context = exceptionHandler) {
        launch(context = customContext, block = work)
    }
}
