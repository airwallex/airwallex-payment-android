package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class ThreeDSecureLookupTest {

    private val params by lazy {
        ThreeDSecureLookup(
            transactionId = "0X7ffM3QEMZbLDRzZND0",
            payload = "eyJtZXNzYWdlVHlwZSI6IkNSZXMiLCJtZXNzYWdlVmVyc2lvbiI6IjIuMS4wIiwidGhyZWVEU1NlcnZlclRyYW5zSUQiOiJhMjI3ZTQxMS1iMjM2LTQ5ODItOGM0OC1kOGViNDQ2NmZjOGIiLCJhY3NUcmFuc0lEIjoiNjI5Y2FiOTEtMzFjMC00YmE3LWI3NTAtNGU3OWZjYTY4MGE3IiwiYWNzVWlUeXBlIjoiMDEiLCJjaGFsbGVuZ2VDb21wbGV0aW9uSW5kIjoiTiIsImNoYWxsZW5nZUluZm9IZWFkZXIiOiJQdXJjaGFzZSBBdXRoZW50aWNhdGlvbiIsImNoYWxsZW5nZUluZm9MYWJlbCI6IkVudGVyIHlvdXIgY29kZSBiZWxvdyIsImNoYWxsZW5nZUluZm9UZXh0IjoiV2UgaGF2ZSBzZW50IHlvdSBhIHRleHQgbWVzc2FnZSB3aXRoIGEgY29kZSB0byB5b3VyIHJlZ2lzdGVyZWQgbW9iaWxlIG51bWJlciBlbmRpbmcgaW4gNTMyOS5cblxuWW91IGFyZSBwYXlpbmcgZGVmYXVsdCBhd3ggdGVzdGluZyh1aykgdGhlIGFtb3VudCBvZiAxLjAwIHVzaW5nIGNhcmQgKioqKioqKioqKioqMTA5MS5cblxuKE9UUDogMTIzNCkiLCJpc3N1ZXJJbWFnZSI6eyJtZWRpdW0iOiJodHRwczovL21lcmNoYW50YWNzc3RhZy5jYXJkaW5hbGNvbW1lcmNlLmNvbS9NZXJjaGFudEFDU1dlYi9zY3JlZW5zL2ltYWdlcy9BbnlCYW5rXzUxMi5wbmciLCJoaWdoIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQW55QmFua181MTIucG5nIiwiZXh0cmFIaWdoIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQW55QmFua181MTIucG5nIn0sInBzSW1hZ2UiOnsibWVkaXVtIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQ2FyZF9OZXR3b3JrLnBuZyIsImhpZ2giOiJodHRwczovL21lcmNoYW50YWNzc3RhZy5jYXJkaW5hbGNvbW1lcmNlLmNvbS9NZXJjaGFudEFDU1dlYi9zY3JlZW5zL2ltYWdlcy9DYXJkX05ldHdvcmsucG5nIiwiZXh0cmFIaWdoIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQ2FyZF9OZXR3b3JrLnBuZyJ9LCJyZXNlbmRJbmZvcm1hdGlvbkxhYmVsIjoiUkVTRU5EIENPREUiLCJzZGtUcmFuc0lEIjoiYjA0NTNmOWEtMjNiYS00NmUxLWI1ZjQtMmI2MjM5NDhjOTMxIiwic3VibWl0QXV0aGVudGljYXRpb25MYWJlbCI6IlNVQk1JVCIsImFjc0NvdW50ZXJBdG9TIjoiMDAwIiwiZXhwYW5kSW5mb0xhYmVsIjoiTW9yZSBJbmZvcm1hdGlvbiIsImV4cGFuZEluZm9UZXh0IjoiSGVyZSBpcyB0aGUgYWRkaXRpb25hbCBpbmZvcm1hdGlvbiB0aGF0IHdlIHByb3ZpZGUuIiwid2h5SW5mb0xhYmVsIjoiTmVlZCBzb21lIGhlbHA",
            acsUrl = "https://0merchantacsstag.cardinalcommerce.com/MerchantACSWeb/creq.jsp",
            version = "2.1.0"
        )
    }

    @Test
    fun testParams() {
        assertEquals("0X7ffM3QEMZbLDRzZND0", params.transactionId)
        assertEquals(
            "eyJtZXNzYWdlVHlwZSI6IkNSZXMiLCJtZXNzYWdlVmVyc2lvbiI6IjIuMS4wIiwidGhyZWVEU1NlcnZlclRyYW5zSUQiOiJhMjI3ZTQxMS1iMjM2LTQ5ODItOGM0OC1kOGViNDQ2NmZjOGIiLCJhY3NUcmFuc0lEIjoiNjI5Y2FiOTEtMzFjMC00YmE3LWI3NTAtNGU3OWZjYTY4MGE3IiwiYWNzVWlUeXBlIjoiMDEiLCJjaGFsbGVuZ2VDb21wbGV0aW9uSW5kIjoiTiIsImNoYWxsZW5nZUluZm9IZWFkZXIiOiJQdXJjaGFzZSBBdXRoZW50aWNhdGlvbiIsImNoYWxsZW5nZUluZm9MYWJlbCI6IkVudGVyIHlvdXIgY29kZSBiZWxvdyIsImNoYWxsZW5nZUluZm9UZXh0IjoiV2UgaGF2ZSBzZW50IHlvdSBhIHRleHQgbWVzc2FnZSB3aXRoIGEgY29kZSB0byB5b3VyIHJlZ2lzdGVyZWQgbW9iaWxlIG51bWJlciBlbmRpbmcgaW4gNTMyOS5cblxuWW91IGFyZSBwYXlpbmcgZGVmYXVsdCBhd3ggdGVzdGluZyh1aykgdGhlIGFtb3VudCBvZiAxLjAwIHVzaW5nIGNhcmQgKioqKioqKioqKioqMTA5MS5cblxuKE9UUDogMTIzNCkiLCJpc3N1ZXJJbWFnZSI6eyJtZWRpdW0iOiJodHRwczovL21lcmNoYW50YWNzc3RhZy5jYXJkaW5hbGNvbW1lcmNlLmNvbS9NZXJjaGFudEFDU1dlYi9zY3JlZW5zL2ltYWdlcy9BbnlCYW5rXzUxMi5wbmciLCJoaWdoIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQW55QmFua181MTIucG5nIiwiZXh0cmFIaWdoIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQW55QmFua181MTIucG5nIn0sInBzSW1hZ2UiOnsibWVkaXVtIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQ2FyZF9OZXR3b3JrLnBuZyIsImhpZ2giOiJodHRwczovL21lcmNoYW50YWNzc3RhZy5jYXJkaW5hbGNvbW1lcmNlLmNvbS9NZXJjaGFudEFDU1dlYi9zY3JlZW5zL2ltYWdlcy9DYXJkX05ldHdvcmsucG5nIiwiZXh0cmFIaWdoIjoiaHR0cHM6Ly9tZXJjaGFudGFjc3N0YWcuY2FyZGluYWxjb21tZXJjZS5jb20vTWVyY2hhbnRBQ1NXZWIvc2NyZWVucy9pbWFnZXMvQ2FyZF9OZXR3b3JrLnBuZyJ9LCJyZXNlbmRJbmZvcm1hdGlvbkxhYmVsIjoiUkVTRU5EIENPREUiLCJzZGtUcmFuc0lEIjoiYjA0NTNmOWEtMjNiYS00NmUxLWI1ZjQtMmI2MjM5NDhjOTMxIiwic3VibWl0QXV0aGVudGljYXRpb25MYWJlbCI6IlNVQk1JVCIsImFjc0NvdW50ZXJBdG9TIjoiMDAwIiwiZXhwYW5kSW5mb0xhYmVsIjoiTW9yZSBJbmZvcm1hdGlvbiIsImV4cGFuZEluZm9UZXh0IjoiSGVyZSBpcyB0aGUgYWRkaXRpb25hbCBpbmZvcm1hdGlvbiB0aGF0IHdlIHByb3ZpZGUuIiwid2h5SW5mb0xhYmVsIjoiTmVlZCBzb21lIGhlbHA",
            params.payload
        )
        assertEquals(
            "https://0merchantacsstag.cardinalcommerce.com/MerchantACSWeb/creq.jsp",
            params.acsUrl
        )
        assertEquals("2.1.0", params.version)
    }
}
