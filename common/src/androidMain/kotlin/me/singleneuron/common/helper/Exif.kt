package me.singleneuron.common.helper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

actual suspend fun getExif(path: String): Flow<Pair<String, String>> = flow<Pair<String, String>>{

}.flowOn(Dispatchers.IO)