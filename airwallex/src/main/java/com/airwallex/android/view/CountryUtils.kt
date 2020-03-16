package com.airwallex.android.view

import java.util.*

internal object CountryUtils {

    private val legalCountries by lazy {
        arrayOf(
            "AC", "AD", "AE", "AG", "AI", "AL", "AM", "AN", "AO", "AQ", "AR", "AS", "AT", "AU", "AW",
            "AX", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BJ", "BL", "BM", "BN", "BO", "BQ",
            "BR", "BS", "BT", "BU", "BV", "BW", "BZ", "CA", "CC", "CG", "CH", "CI", "CK", "CL", "CM",
            "CN", "CO", "CP", "CR", "CS", "CV", "CW", "CX", "CY", "CZ", "DE", "DG", "DJ", "DK", "DM",
            "DO", "DZ", "EA", "EC", "EE", "EG", "EH", "ES", "ET", "EU", "EZ", "FI", "FJ", "FK", "FM",
            "FO", "FR", "FX", "GA", "GB", "GD", "GE", "GF", "GG", "GH", "GI", "GL", "GM", "GN", "GP",
            "GQ", "GR", "GS", "GT", "GU", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "IC", "ID", "IE",
            "IL", "IM", "IN", "IO", "IS", "IT", "JE", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM",
            "KN", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK", "LS", "LT", "LU", "LV", "MA",
            "MC", "MD", "ME", "MF", "MG", "MH", "MK", "ML", "MN", "MO", "MP", "MQ", "MR", "MS", "MT",
            "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP",
            "NR", "NT", "NU", "NZ", "OM", "PA", "PE", "PF", "PG", "PH", "PK", "PL", "PM", "PN", "PR",
            "PS", "PT", "PW", "PY", "QA", "RE", "RO", "RS", "RU", "SA", "SB", "SC", "SE", "SF", "SG",
            "SH", "SI", "SJ", "SK", "SM", "SN", "SR", "ST", "SU", "SV", "SX", "SZ", "TA", "TC", "TD",
            "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO", "TP", "TR", "TT", "TV", "TW", "TZ",
            "UA", "UG", "UK", "UM", "US", "UY", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF",
            "WS", "XK", "YT", "YU", "ZA", "ZM", "ZR"
        )
    }

    val COUNTRIES = Locale.getISOCountries()
        .filter { code -> legalCountries.indexOf(code) >= 0 }
        .map { code ->
            CountryAutoCompleteView.Country(code, Locale("", code).displayCountry)
        }
        .sortedBy { it.name.toLowerCase(Locale.ROOT) }

    fun getCountryByName(countryName: String): CountryAutoCompleteView.Country? {
        return COUNTRIES.firstOrNull { it.name == countryName }
    }

    fun getCountryByCode(countryCode: String): CountryAutoCompleteView.Country? {
        return COUNTRIES.firstOrNull { it.code == countryCode }
    }
}
