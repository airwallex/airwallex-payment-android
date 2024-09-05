package com.airwallex.paymentacceptance.repo

object RepositoryProvider {

    fun get(): BaseRepository {
        return PACheckoutDemoRepository()
    }
}