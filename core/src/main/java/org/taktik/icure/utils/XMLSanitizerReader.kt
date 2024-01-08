/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.utils

import java.io.IOException
import java.io.Reader

class XMLSanitizerReader(private var nextReader: Reader?) : Reader() {
    @Throws(IOException::class)
    override fun close() {
        nextReader!!.close()
        nextReader = null
    }

    @Throws(IOException::class)
    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        var lengthRead = 0
        while (lengthRead < len) {
            val tbuf = CharArray(len)
            val res = nextReader!!.read(tbuf, lengthRead, len - lengthRead)
            if (res == -1) {
                return if (lengthRead > 0) lengthRead else -1
            }
            var delta = 0
            for (i in lengthRead until lengthRead + res) {
                val c = tbuf[i]
                if (c.code >= 0 && c.code < 0x20 && c.code != 0x9 && c.code != 0xa && c.code != 0xd) {
                    delta++
                } else {
                    cbuf[off + i - delta] = c
                }
            }
            lengthRead += res - delta
        }
        return lengthRead
    }
}
