package com.dustinconrad.loophero

import kotlin.test.Test
import kotlin.test.assertEquals

class NibbleTest {

    @Test
    fun testSetLowerNibble() {
        assertEquals(1, 0.toByte().setLowerNibble(1))

        assertEquals(17, 16.toByte().setLowerNibble(1))

        assertEquals(-1, (-15).toByte().setLowerNibble(-1))
    }

    @Test
    fun testSetUpperNibble() {
        assertEquals(16, 0.toByte().setUpperNibble(1))

        assertEquals(20, 4.toByte().setUpperNibble(1))
    }

    @Test
    fun testGetLowerNibble() {
        assertEquals(-1, (-1).toByte().lowerNibble())
    }

    @Test
    fun testGetUpperNibble() {
        assertEquals(-1, (-1).toByte().upperNibble())
    }

}