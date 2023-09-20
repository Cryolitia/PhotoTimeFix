package me.singleneuron.common.helper

import kotlinx.coroutines.flow.Flow

expect suspend fun getExif(path: String): Flow<Pair<String, String>>