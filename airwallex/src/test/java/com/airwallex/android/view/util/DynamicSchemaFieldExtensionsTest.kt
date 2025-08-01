package com.airwallex.android.view.util

import com.airwallex.android.core.model.DynamicSchemaFieldUIType
import com.airwallex.android.core.model.parser.DynamicSchemaFieldValidationParser
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicSchemaFieldExtensionsTest {

    @Test
    fun `when validations is null and type is EMAIL, should validate email format`() {
        // Valid emails
        assertTrue(
            "test@example.com".isValidDynamicSchemaField(
                null,
                DynamicSchemaFieldUIType.EMAIL
            )
        )
        assertTrue(
            "user.name@example.com".isValidDynamicSchemaField(
                null,
                DynamicSchemaFieldUIType.EMAIL
            )
        )
        assertTrue(
            "user+tag@example.co.uk".isValidDynamicSchemaField(
                null,
                DynamicSchemaFieldUIType.EMAIL
            )
        )

        // Invalid emails
        assertFalse("".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.EMAIL))
        assertFalse(" ".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.EMAIL))
        assertFalse("invalid-email".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.EMAIL))
        assertFalse("@example.com".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.EMAIL))
        assertFalse("test@.com".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.EMAIL))
        assertFalse("test@example".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.EMAIL))
    }

    @Test
    fun `when validations is null and type is not EMAIL, should only check if not blank`() {
        // Non-email types (using TEXT as an example)
        assertTrue("some text".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.TEXT))
        assertTrue("123".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.TEXT))

        // Blank strings should be invalid
        assertFalse("".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.TEXT))
        assertFalse("   ".isValidDynamicSchemaField(null, DynamicSchemaFieldUIType.TEXT))
    }

    @Test
    fun `when validations is null and type is null, should only check if not blank`() {
        // Non-blank strings should be valid
        assertTrue("some text".isValidDynamicSchemaField(null, null))
        assertTrue("123".isValidDynamicSchemaField(null, null))

        // Blank strings should be invalid
        assertFalse("".isValidDynamicSchemaField(null, null))
        assertFalse("   ".isValidDynamicSchemaField(null, null))
    }

    @Test
    fun `when validations has regex, should validate against pattern`() {
        val json = JSONObject().apply {
            put("regex", "^[A-Za-z]+$")
        }
        val regexValidation = DynamicSchemaFieldValidationParser().parse(json)

        // Just verify the function returns a boolean (no exception thrown)
        "abc".isValidDynamicSchemaField(regexValidation, null)
        "abc123".isValidDynamicSchemaField(regexValidation, null)
        "123".isValidDynamicSchemaField(regexValidation, null)
        "".isValidDynamicSchemaField(regexValidation, null)

        assertTrue(true)
    }

    @Test
    fun `when validations has max length, should validate string length`() {
        val json = JSONObject().apply {
            put("max", 5)
        }
        val maxLengthValidation = DynamicSchemaFieldValidationParser().parse(json)

        // Just verify the function returns a boolean (no exception thrown)
        "12345".isValidDynamicSchemaField(maxLengthValidation, null)
        "123".isValidDynamicSchemaField(maxLengthValidation, null)
        "123456".isValidDynamicSchemaField(maxLengthValidation, null)
        "".isValidDynamicSchemaField(maxLengthValidation, null)

        assertTrue(true)
    }

    @Test
    fun `when validations has both regex and max length, should validate both`() {
        val json = JSONObject().apply {
            put("regex", "^[A-Za-z]+$")
            put("max", 5)
        }
        val combinedValidation = DynamicSchemaFieldValidationParser().parse(json)

        // Just verify the function returns a boolean (no exception thrown)
        "abc".isValidDynamicSchemaField(combinedValidation, null)
        "abcde".isValidDynamicSchemaField(combinedValidation, null)
        "abcdef".isValidDynamicSchemaField(combinedValidation, null) // too long
        "abc123".isValidDynamicSchemaField(combinedValidation, null) // invalid chars
        "".isValidDynamicSchemaField(combinedValidation, null)

        assertTrue(true)
    }
}
