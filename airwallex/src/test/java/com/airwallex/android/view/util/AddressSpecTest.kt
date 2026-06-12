package com.airwallex.android.view.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AddressSpecTest {

    // ---------- hasState ----------

    @Test
    fun `hasState is true for countries whose fmt contains S and have a state list`() {
        listOf("US", "JP", "AU", "CN", "IN", "MX", "CA", "BR", "AE", "KR").forEach { code ->
            assertTrue("expected hasState=true for $code", AddressSpec.hasState(code))
        }
    }

    @Test
    fun `hasState is true for SC (state without sub_keys, free-text)`() {
        // SC is the lone country whose fmt has %S but ships no sub_keys list.
        // It must still return true so the UI renders a free-text state input.
        assertTrue(AddressSpec.hasState("SC"))
    }

    @Test
    fun `hasState is false for countries whose fmt has no S`() {
        listOf("GB", "DE", "FR", "NL", "SG", "NZ", "DK", "SE", "NO", "BE").forEach { code ->
            assertFalse("expected hasState=false for $code", AddressSpec.hasState(code))
        }
    }

    @Test
    fun `hasState is case-insensitive`() {
        assertTrue(AddressSpec.hasState("us"))
        assertTrue(AddressSpec.hasState("jp"))
        assertFalse(AddressSpec.hasState("gb"))
    }

    @Test
    fun `hasState returns false for unknown country codes`() {
        assertFalse(AddressSpec.hasState("XX"))
        assertFalse(AddressSpec.hasState(""))
    }

    // ---------- stateList ----------

    @Test
    fun `stateList returns the full set of states for known countries`() {
        // Counts match the source data in address.json so any drift is caught here.
        assertEquals(62, AddressSpec.stateList("US")?.size)
        assertEquals(47, AddressSpec.stateList("JP")?.size)
        assertEquals(13, AddressSpec.stateList("CA")?.size)
        assertEquals(8, AddressSpec.stateList("AU")?.size)
        assertEquals(7, AddressSpec.stateList("AE")?.size)
    }

    @Test
    fun `stateList returns entries with value and label`() {
        val us = AddressSpec.stateList("US")
        assertNotNull(us)
        // US entries use the same string for value and label (Latin script).
        assertTrue(us?.any { it.first == "CA" && it.second == "California" } ?: false)
        assertTrue(us?.any { it.first == "NY" && it.second == "New York" } ?: false)
    }

    @Test
    fun `stateList for AE preserves bilingual labels`() {
        val ae = AddressSpec.stateList("AE")
        assertNotNull(ae)
        // AE's labels include both the Arabic original and a Latin transliteration.
        assertTrue(ae?.any { it.second.contains("Abu Dhabi") } ?: false)
        assertTrue(ae?.any { it.second.contains("Dubai") } ?: false)
    }

    @Test
    fun `stateList is null for countries without a state list`() {
        // No %S in fmt:
        assertNull(AddressSpec.stateList("GB"))
        assertNull(AddressSpec.stateList("DE"))
        assertNull(AddressSpec.stateList("FR"))
        // %S but no sub_keys (free-text state):
        assertNull(AddressSpec.stateList("SC"))
        // Unknown code:
        assertNull(AddressSpec.stateList("XX"))
    }

    @Test
    fun `stateList is case-insensitive`() {
        assertEquals(AddressSpec.stateList("US"), AddressSpec.stateList("us"))
        assertEquals(AddressSpec.stateList("JP"), AddressSpec.stateList("jp"))
    }

    // ---------- mapState ----------

    @Test
    fun `mapState returns the canonical value when given the label`() {
        assertEquals("CA", AddressSpec.mapState("US", "California"))
        assertEquals("NY", AddressSpec.mapState("US", "New York"))
    }

    @Test
    fun `mapState returns the value unchanged when given the canonical value`() {
        assertEquals("CA", AddressSpec.mapState("US", "CA"))
        assertEquals("NY", AddressSpec.mapState("US", "NY"))
    }

    @Test
    fun `mapState matches case-insensitively`() {
        assertEquals("CA", AddressSpec.mapState("US", "california"))
        assertEquals("CA", AddressSpec.mapState("US", "CALIFORNIA"))
        assertEquals("CA", AddressSpec.mapState("US", "ca"))
    }

    @Test
    fun `mapState trims surrounding whitespace before matching`() {
        assertEquals("CA", AddressSpec.mapState("US", "  California  "))
        assertEquals("CA", AddressSpec.mapState("US", "\tCA\n"))
    }

    @Test
    fun `mapState is case-insensitive on the country code itself`() {
        assertEquals("CA", AddressSpec.mapState("us", "California"))
    }

    @Test
    fun `mapState returns input unchanged when the country has no state list`() {
        // No %S in fmt — passthrough so the caller can still send free-text.
        assertEquals("anything", AddressSpec.mapState("GB", "anything"))
        // %S but no sub_keys list (SC):
        assertEquals("Mahé", AddressSpec.mapState("SC", "Mahé"))
        // Unknown country:
        assertEquals("anything", AddressSpec.mapState("XX", "anything"))
    }

    @Test
    fun `mapState returns input unchanged when no entry matches`() {
        assertEquals("Atlantis", AddressSpec.mapState("US", "Atlantis"))
    }

    @Test
    fun `mapState passes empty input through without consulting the list`() {
        assertEquals("", AddressSpec.mapState("US", ""))
        assertEquals("   ", AddressSpec.mapState("US", "   "))
    }
}
