package com.alexsav.quiztriviaapp.repository

import android.util.Log
import com.alexsav.quiztriviaapp.data.DataOrException
import com.alexsav.quiztriviaapp.model.QuestionItem
import com.alexsav.quiztriviaapp.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api: QuestionApi) {
    private val dataOrException =
        DataOrException<ArrayList<QuestionItem>,
                Boolean,
                Exception>()

    suspend fun getAllQuestions(): DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            if (dataOrException.data.toString().isNotEmpty()) dataOrException.loading = false

        }catch (exception: Exception){
            dataOrException.e = exception
            Log.d("Ecx","getAllQuestions: ${dataOrException.e!!.localizedMessage}")
        }
        return dataOrException
    }
}