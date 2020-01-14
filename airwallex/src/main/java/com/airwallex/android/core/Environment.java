package com.airwallex.android.core;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Contains the set of environments that can be used.
 *
 * @see #STAGING
 * @see #PRODUCTION
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({Environment.STAGING, Environment.PRODUCTION})
public @interface Environment {
    String STAGING = "https://staging-pacheckoutdemo.airwallex.com";
    String PRODUCTION = "https://";
}