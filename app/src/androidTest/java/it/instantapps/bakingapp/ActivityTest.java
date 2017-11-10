
/*
 *  ____        _    _                  _
 * | __ )  __ _| | _(_)_ __   __ _     / \   _ __  _ __
 * |  _ \ / _` | |/ / | '_ \ / _` |   / _ \ | '_ \| '_ \
 * | |_) | (_| |   <| | | | | (_| |  / ___ \| |_) | |_) |
 * |____/ \__,_|_|\_\_|_| |_|\__, | /_/   \_\ .__/| .__/
 *                           |___/          |_|   |_|
 *
 * Copyright (C) 2017 Benedetto Pellerito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.instantapps.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.instantapps.bakingapp.activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class ActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void checkFirstText_RecipeActivity() {
        onView(withId(R.id.rv_recipe))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));
    }

    @Test
    public void checkFirstContentDescription_RecipeActivity() {
        onView(withId(R.id.rv_recipe))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withContentDescription("Nutella Pie")).check(matches(isDisplayed()));
    }

    @Test
    public void checkLastText_RecipeActivity() {
        onView(withId(R.id.rv_recipe))
                .perform(RecyclerViewActions.scrollToPosition(3));
        onView(withText("Cheesecake")).check(matches(isDisplayed()));
    }

    @Test
    public void checkLastContentDescription_RecipeActivity() {
        onView(withId(R.id.rv_recipe))
                .perform(RecyclerViewActions.scrollToPosition(3));
        onView(withContentDescription("Cheesecake")).check(matches(isDisplayed()));
    }

    @Test
    public void checkFirstIngredient_DetailActivity() {
        onView(ViewMatchers.withId(R.id.rv_recipe))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));
        onView(withText("Graham cracker crumbs"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkFirstClickIngredient_DetailActivity() {
        onView(ViewMatchers.withId(R.id.rv_recipe))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));
        onView(ViewMatchers.withId(R.id.rv_detail_ingredient))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(3, click()));
        onView(withText("Salt"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkTitle_StepActivity() {
        onView(ViewMatchers.withId(R.id.rv_recipe))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));
        onView(ViewMatchers.withId(R.id.rv_step))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));
        onView(withId(R.id.tv_short_detail_step_description))
                .check(matches(isDisplayed()));

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
