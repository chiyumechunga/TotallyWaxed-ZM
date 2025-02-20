package com.totallywaxed.core.utils

class Result<T> {
    companion object {
        fun Success(user: FirebaseUser): Any {

        }

        fun Error(s: String): Any {

        }

        val Loading: Any
    }
}