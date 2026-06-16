package com.airwallex.android.core.util

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

    // ---------- hasCity ----------

    @Test
    fun `hasCity is false for the explicit no-city set`() {
        // AE, GI, JP, KY, MO, NR, SG — fmt is defined but does not contain %C.
        // GI/SG hard-code their city ("GIBRALTAR"/"SINGAPORE") in fmt; JP folds city into
        // prefecture + area; AE/KY/MO/NR collect no city at all.
        listOf("AE", "GI", "JP", "KY", "MO", "NR", "SG").forEach { code ->
            assertFalse("expected hasCity=false for $code", AddressSpec.hasCity(code))
        }
    }

    @Test
    fun `hasCity is true for countries whose fmt includes C`() {
        listOf("US", "GB", "DE", "FR", "CA", "AU", "BR", "IN", "MX", "CN", "KR").forEach { code ->
            assertTrue("expected hasCity=true for $code", AddressSpec.hasCity(code))
        }
    }

    @Test
    fun `hasCity is true for countries with no fmt (default fallback)`() {
        // Countries with no `fmt` defined fall back to the default of street + city, so
        // city stays visible — they're not listed in COUNTRIES_WITHOUT_CITY.
        listOf("AG", "AO", "BW", "FJ", "GH", "MS", "QA", "ST", "UG", "VU").forEach { code ->
            assertTrue("expected hasCity=true for $code", AddressSpec.hasCity(code))
        }
    }

    @Test
    fun `hasCity is case-insensitive`() {
        assertFalse(AddressSpec.hasCity("jp"))
        assertFalse(AddressSpec.hasCity("sg"))
        assertTrue(AddressSpec.hasCity("us"))
    }

    @Test
    fun `hasCity returns true for unknown country codes (passthrough default)`() {
        // Unknown codes fall to the default branch (no fmt) → city shows.
        assertTrue(AddressSpec.hasCity("XX"))
        assertTrue(AddressSpec.hasCity(""))
    }

    // ---------- hasPostcode ----------

    @Test
    fun `hasPostcode is false for the explicit no-postcode set`() {
        // Mirrors COUNTRIES_WITHOUT_POSTCODE — drift here is what we want this test to catch.
        // Two categories of country end up here: (1) countries whose Google i18n fmt is defined
        // but omits %Z (AE/BF/HK/JM/etc.) and (2) countries with no fmt at all, which fall
        // back to the default street+city spec and so collect no postcode (AO/AG/BW/etc.).
        listOf(
            "AE", "AG", "AO", "AQ", "AW", "BF", "BJ", "BO",
            "BQ", "BS", "BV", "BW", "BZ", "CG", "CI", "CK",
            "CM", "CW", "DM", "FJ", "GA", "GD", "GH", "GM",
            "GQ", "GY", "HK", "JM", "KI", "KM", "KN", "LC",
            "ML", "MO", "MR", "MS", "MW", "NR", "NU", "PA",
            "PS", "QA", "SB", "SC", "SR", "ST", "SX", "TD",
            "TF", "TG", "TK", "TL", "TO", "TT", "TV", "UG",
            "VU", "WS",
        ).forEach { code ->
            assertFalse("expected hasPostcode=false for $code", AddressSpec.hasPostcode(code))
        }
    }

    @Test
    fun `hasPostcode is true for countries whose fmt includes Z`() {
        listOf("US", "GB", "DE", "FR", "CA", "AU", "BR", "IN", "JP", "SG", "GI").forEach { code ->
            assertTrue("expected hasPostcode=true for $code", AddressSpec.hasPostcode(code))
        }
    }

    @Test
    fun `hasPostcode is case-insensitive`() {
        assertFalse(AddressSpec.hasPostcode("hk"))
        assertFalse(AddressSpec.hasPostcode("ae"))
        assertTrue(AddressSpec.hasPostcode("us"))
    }

    @Test
    fun `hasPostcode returns true for unknown country codes`() {
        assertTrue(AddressSpec.hasPostcode("XX"))
    }

    // ---------- combined visibility (sanity checks for the address layout) ----------

    @Test
    fun `JP collects street + state + postcode but no city`() {
        assertTrue(AddressSpec.hasState("JP"))
        assertFalse(AddressSpec.hasCity("JP"))
        assertTrue(AddressSpec.hasPostcode("JP"))
    }

    @Test
    fun `AE collects street + state only`() {
        assertTrue(AddressSpec.hasState("AE"))
        assertFalse(AddressSpec.hasCity("AE"))
        assertFalse(AddressSpec.hasPostcode("AE"))
    }

    @Test
    fun `MO collects street only — every conditional field is hidden`() {
        assertFalse(AddressSpec.hasState("MO"))
        assertFalse(AddressSpec.hasCity("MO"))
        assertFalse(AddressSpec.hasPostcode("MO"))
    }

    @Test
    fun `GB collects street + city + postcode (no state)`() {
        assertFalse(AddressSpec.hasState("GB"))
        assertTrue(AddressSpec.hasCity("GB"))
        assertTrue(AddressSpec.hasPostcode("GB"))
    }

    // ---------- stateList ----------

    @Test
    fun `stateList returns the full set of states for known countries`() {
        // Pinned counts so any drift in the underlying data is caught here.
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

    // ---------- postcodePattern / postcodeExamples ----------

    @Test
    fun `postcodePattern matches the country's expected postcode shape`() {
        val us = requireNotNull(AddressSpec.postcodePattern("US"))
        assertTrue(us.matches("95014"))
        assertTrue(us.matches("22162-1010"))
        assertFalse(us.matches("ABC"))
        assertFalse(us.matches("123"))

        val de = requireNotNull(AddressSpec.postcodePattern("DE"))
        assertTrue(de.matches("26133"))
        assertFalse(de.matches("123"))

        // CA's pattern is case-insensitive, alpha-numeric — verify the IGNORE_CASE flag is wired.
        val ca = requireNotNull(AddressSpec.postcodePattern("CA"))
        assertTrue(ca.matches("H3Z 2Y7"))
        assertTrue(ca.matches("h3z 2y7"))
    }

    @Test
    fun `postcodePattern is null for no-postcode countries and unknown codes`() {
        // AE / HK have no fmt %Z (and no zip pattern in source data).
        assertNull(AddressSpec.postcodePattern("AE"))
        assertNull(AddressSpec.postcodePattern("HK"))
        assertNull(AddressSpec.postcodePattern("XX"))
    }

    @Test
    fun `postcodePattern is case-insensitive on the country code`() {
        assertNotNull(AddressSpec.postcodePattern("us"))
    }

    @Test
    fun `postcodeExamples returns the parsed sample list`() {
        assertEquals(listOf("95014", "22162-1010"), AddressSpec.postcodeExamples("US"))
        assertEquals(listOf("26133", "53225"), AddressSpec.postcodeExamples("DE"))
    }

    @Test
    fun `postcodeExamples is null when no examples are defined`() {
        assertNull(AddressSpec.postcodeExamples("AE"))
        assertNull(AddressSpec.postcodeExamples("XX"))
    }
}
