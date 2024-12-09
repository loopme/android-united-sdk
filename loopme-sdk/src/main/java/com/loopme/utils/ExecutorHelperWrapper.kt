package com.loopme.utils

import java.util.concurrent.ExecutorService

class ExecutorHelperWrapper {
    fun getExecutor(): ExecutorService = ExecutorHelper.getExecutor()
}