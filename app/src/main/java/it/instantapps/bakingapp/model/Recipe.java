
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

package it.instantapps.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import it.instantapps.bakingapp.utility.Costants;

public class Recipe implements Parcelable {


    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_ID)
    private Integer id;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_NAME)
    private String name;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_INGREDIENTS)
    private ArrayList<Ingredient> mIngredientArrayList;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_STEPS)
    private ArrayList<Step> mStepArrayList;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_SERVINGS)
    private Integer servings;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_IMAGE)
    private String image;


    public Integer getId() {
        return id;
    }

    public Integer getServings() {
        return servings;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return mIngredientArrayList;
    }

    public ArrayList<Step> getStepList() {
        return mStepArrayList;
    }

    public String getImage() {
        return image;
    }


    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        mIngredientArrayList = new ArrayList<>();
        in.readTypedList(mIngredientArrayList, Ingredient.CREATOR);
        mStepArrayList = new ArrayList<>();
        in.readTypedList(mStepArrayList, Step.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(mIngredientArrayList);
        dest.writeTypedList(mStepArrayList);
        dest.writeInt(servings);
        dest.writeString(image);
    }
}
