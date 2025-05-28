package com.doubletapp_hw.screens.edit

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.doubletapp_hw.LocalNavController
import com.doubletapp_hw.viewModels.HabitEditViewModel
import com.example.domain.Habit
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: HabitEditViewModel
    private lateinit var navController: NavController

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true) {
            every { habit } returns MutableLiveData(
                Habit(
                    id = "1",
                    title = "Workout",
                    description = "Do exercise 3 times a week",
                    priority = 0,
                    type = 0,
                    count = 3,
                    frequency = 7,
                    color = 0
                )
            )
        }
        navController = mockk(relaxed = true)
        every { navController.navigateUp() } returns true
    }

    @Test
    fun testLoadingIndicatorShownWhenHabitIsNull() {
        every { viewModel.habit } returns MutableLiveData(null)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavController provides navController) {
                HabitEditScreen(habitEditViewModel = viewModel)
            }
        }
        composeTestRule.onNode(hasTestTag("ProgressIndicator")).assertExists()
    }

    @Test
    fun testInitialUIState() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavController provides navController) {
                HabitEditScreen(habitEditViewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Workout").assertExists()
        composeTestRule.onNodeWithText("Do exercise 3 times a week").assertExists()
        composeTestRule.onNodeWithTag("habitTitleTextField").assert(hasText("Workout"))
        composeTestRule.onNodeWithTag("habitDescriptionTextField")
            .assert(hasText("Do exercise 3 times a week"))
        composeTestRule.onNodeWithTag("countTextField").assert(hasText("3"))
        composeTestRule.onNodeWithTag("frequencyTextField").assert(hasText("7"))
        composeTestRule.onNodeWithText("Выбрать цвет").assertExists()
    }

    @Test
    fun testInputChanges() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavController provides navController) {
                HabitEditScreen(habitEditViewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithTag("habitTitleTextField").performTextReplacement("New habit")
        composeTestRule.onNodeWithTag("habitTitleTextField").assert(hasText("New habit"))

        composeTestRule.onNodeWithTag("habitDescriptionTextField")
            .performTextReplacement("My new description")
        composeTestRule.onNodeWithTag("habitDescriptionTextField")
            .assert(hasText("My new description"))

        composeTestRule.onNodeWithTag("countTextField").performTextReplacement("11")
        composeTestRule.onNodeWithTag("countTextField").assert(hasText("11"))

        composeTestRule.onNodeWithTag("frequencyTextField").performTextReplacement("22")
        composeTestRule.onNodeWithTag("frequencyTextField").assert(hasText("22"))

        composeTestRule.onNodeWithText("Низкий").performClick()
        composeTestRule.onNodeWithText("Высокий").performClick()
        composeTestRule.onNodeWithText("Высокий").assertExists()

        composeTestRule.onNodeWithText("Положительная").performClick()
        composeTestRule.onNodeWithText("Отрицательная").performClick()
        composeTestRule.onNodeWithText("Отрицательная").assertExists()
    }

    @Test
    fun testColorPickerInteraction() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavController provides navController) {
                HabitEditScreen(habitEditViewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithText("Выбрать цвет").performClick()
        composeTestRule.onNodeWithText(text = "RGB: (0, 0, 0)", substring = true).assertExists()

        // Выбираем красный (RGB: 255,0,0)
        composeTestRule.onNodeWithTag("colorPickerBox_1").performClick()
        composeTestRule.onNodeWithText(text = "RGB:", substring = true).assertIsDisplayed()
    }

    @Test
    fun testSelectFirstAndLastColorInColorPicker() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavController provides navController) {
                HabitEditScreen(habitEditViewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithText("Выбрать цвет").performClick()
        composeTestRule.onNodeWithTag("colorPickerBox_1").performClick()
        composeTestRule.onNodeWithText(text = "RGB:", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("colorPickerBox_15")
            .performScrollTo()
            .assertIsDisplayed()
        val lastNode = composeTestRule.onNodeWithTag("colorPickerBox_15")

        lastNode.assertIsDisplayed()
        lastNode.performClick()

        composeTestRule.onNodeWithText(text = "RGB:", substring = true).assertIsDisplayed()
    }

    @Test
    fun testSaveButtonFunctionality() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavController provides navController) {
                HabitEditScreen(habitEditViewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithTag("habitTitleTextField").performTextReplacement("Updated Habit")
        composeTestRule.onNodeWithText("Сохранить").performClick()

        verify(exactly = 1) {
            viewModel.saveHabit(match {
                it.title == "Updated Habit" && it.id == "1"
            })
        }
        verify { navController.navigateUp() }
    }

    @Test
    fun testBackNavigation() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalNavController provides navController) {
                HabitEditScreen(habitEditViewModel = viewModel)
            }
        }
        composeTestRule.onNodeWithText("Назад").performClick()
        verify { navController.navigateUp() }
    }
}