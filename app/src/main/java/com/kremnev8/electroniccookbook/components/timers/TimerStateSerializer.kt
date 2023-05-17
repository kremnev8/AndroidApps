package com.kremnev8.electroniccookbook.components.timers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

class TimerStateSerializer : Serializer<TimerList> {
    override val defaultValue: TimerList = TimerList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TimerList {
        try {
            return TimerList.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: TimerList,
        output: OutputStream
    ) = t.writeTo(output)
}
