package com.alexsav.quiztriviaapp.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexsav.quiztriviaapp.model.QuestionItem
import com.alexsav.quiztriviaapp.screens.QuestionViewModel
import com.alexsav.quiztriviaapp.ui.theme.*

@Composable
fun Questions(viewModel: QuestionViewModel) {
    val questions = viewModel.data.value.data?.toMutableList()

    val questionIndex = remember {
        mutableStateOf(0)
    }
    if(viewModel.data.value.loading == true) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

                CircularProgressIndicator()
            }

    }else {
        val question = try {
            questions?.get(questionIndex.value)
        }catch (ex: Exception) {
            null
        }
        if (questions != null) {
            QuestionDisplay(question = question!!, questionIndex = questionIndex,
                viewModel = viewModel) {

                questionIndex.value += 1
            }
        }
    }
}

//@Preview
@Composable
fun QuestionDisplay(
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionViewModel,
    onNextClicked: (Int) -> Unit = {}
                    ){

    val choicesState = remember(question) {
        question.choices.toMutableList()
    }
    val answerState = remember(question) {
        mutableStateOf<Int?>(null)
    }
    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }
    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            answerState.value = it
            correctAnswerState.value = choicesState[it] == question.answer
        }
    }
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f,10f), 0f)

    Surface(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
            color = mDarkPurple) {

        Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {

            if (questionIndex.value >= 3) ShowProgress(score = questionIndex.value)

            QuestionTracker(counter = questionIndex.value, viewModel.getTotalQuestionCount())
            DrawDottedLine(pathEffect = pathEffect)
            
            Column {
                Text(text = question.question,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start)
                        .fillMaxHeight(0.3f)   ,
                    fontSize = 17.sp,
                    color = mOffWhite,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp)

                //Choices
                choicesState.forEachIndexed { index, answerText ->
                    Row(modifier = Modifier
                        .padding(3.dp)
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(
                            width = 4.dp,
                            brush = Brush.linearGradient(colors = listOf(mBlue, mBlue)),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(
                            RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                                bottomStartPercent = 50
                            )
                        )
                        .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically) {

                            RadioButton(
                                selected = (answerState.value == index),
                                onClick = {
                                    updateAnswer(index)
                                },
                                    modifier = Modifier.padding(start = 16.dp),
                                    colors = RadioButtonDefaults
                                        .colors(selectedColor =
                                            if (correctAnswerState.value == true
                                                && index == answerState.value) {
                                                Color.Green.copy(alpha = 0.2f)
                                            }else {
                                                Color.Red.copy(alpha = 0.2f)
                                            })
                            ) //End Radio Button
                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style= SpanStyle(fontWeight = FontWeight.Light,
                                                    color = if (correctAnswerState.value == true
                                                        && index == answerState.value) {
                                                        Color.Green
                                                    }else if(correctAnswerState.value == false
                                                        && index == answerState.value){
                                                        Color.Red
                                                    }else {
                                                        mOffWhite
                                                    }, fontSize = 17.sp)) {
                                append(answerText)
                            }
                        }
                        Text(text = annotatedString, modifier = Modifier.padding(6.dp))
                    } // End Vertical Alignment
                }
                Button(onClick = {onNextClicked(questionIndex.value)},
                    modifier = Modifier
                        .padding(3.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = mLightBlue)) {

                        Text(text = "Next",
                            modifier = Modifier.padding(4.dp),
                            color = mOffWhite,
                            fontSize = 17.sp)
                }
            } // End Column
        } // End Column
    } // End Surface
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp),
    ){

        drawLine(color = mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect)
    }
}

@Composable
fun QuestionTracker(counter: Int,
                    outOf: Int = 100) {
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
            withStyle(style = SpanStyle(color = mLightGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 27.sp)) {

                append("Question $counter/")

                withStyle(style = SpanStyle(color = mLightGray,
                                            fontWeight = FontWeight.Light,
                                            fontSize = 14.sp)) {
                    append("$outOf")
                }
            }
        }
    },
        modifier = Modifier.padding(20.dp))
}

@Preview
@Composable
fun ShowProgress(score: Int = 0) {

    val gradient = Brush.linearGradient(listOf(
        Color(0xFF95075),
        Color(0xFFBE6BE5)
    ))

    val progressFactor = remember(score) {
        mutableStateOf(score * 0.005f)
    }

    Row(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth()
        .height(45.dp)
        .border(
            width = 4.dp,
            brush = Brush.linearGradient(colors = listOf(mLightBlue, mLightPurple)),
            shape = RoundedCornerShape(34.dp)
        )
        .clip(
            RoundedCornerShape(
                topStartPercent = 50,
                topEndPercent = 50,
                bottomEndPercent = 50,
                bottomStartPercent = 50
            )
        )
        .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically) {

        Button(
            contentPadding = PaddingValues(1.dp),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth(progressFactor.value)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent)) {

            Text(
                text = (score * 10).toString(),
                modifier = Modifier.clip(shape = RoundedCornerShape(23.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(6.dp),
                    color = mOffWhite,
                    textAlign = TextAlign.Center
            )
        }
    }
}
