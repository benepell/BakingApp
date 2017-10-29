package it.instantapps.bakingapp.utility;

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
@SuppressWarnings("ALL")
public class Costants {

    private Costants() {
    }

    /**
     * Costants URI
     * It contains the recipes' instructions
     * http://go.udacity.com/android-baking-app-json
     */

    public static final String UDACITY_BASE_URL = "http://go.udacity.com/android-baking-app-json/";

    public static final String JSON_RECIPE_ID = "id";

    public static final String JSON_RECIPE_NAME = "name";
    public static final String JSON_RECIPE_INGREDIENTS = "ingredients";
    public static final String JSON_RECIPE_STEPS = "steps";
    public static final String JSON_RECIPE_SERVINGS = "servings";
    public static final String JSON_RECIPE_IMAGE = "image";
    public static final String JSON_RECIPE_INGREDIENTS_QUANTITY = "quantity";

    public static final String JSON_RECIPE_INGREDIENTS_MEASURE = "measure";
    public static final String JSON_RECIPE_INGREDIENTS_INGREDIENT = "ingredient";
    public static final String JSON_RECIPE_STEPS_ID = "id";

    public static final String JSON_RECIPE_STEPS_SHORTDESCRIPTION = "shortDescription";
    public static final String JSON_RECIPE_STEPS_DESCRIPTION = "description";
    public static final String JSON_RECIPE_STEPS_VIDEOURL = "videoURL";
    public static final String JSON_RECIPE_STEPS_THUMBNAILURL = "thumbnailURL";


    public static final int RECIPE_LOADER_ID = 0;
    public static final int INGREDIENT_LOADER_ID = 1;
    public static final int STEP_LOADER_ID = 2;

    public static final String RECIPE_WIDGET_UPDATE = "it.instantapps.bakingapp.receiver.recipe.widget.update";

    public static final int DEFAULT_TIMBER_COUNT = 3;

    public static final int TAB_ORDER_INGREDIENT = 0;
    public static final int TAB_ORDER_STEP = 1;

    public static final String EXTRA_RECIPE_ID = "it.instantapps.bakingapp.activity.recipe.id";
    public static final String EXTRA_RECIPE_POSITION = "it.instantapps.bakingapp.activity.recipe.position";
    public static final String EXTRA_TAB_ORDERTAB = "it.instantapps.bakingapp.activity.tab.ordertab";

    public static final String EXTRA_RECIPE_WIDGET_ID = "it.instantapps.bakingapp.activity.recipe.widget.id";
    public static final String ACTION_START_RECIPE= "it.instantapps.bakingapp.widget.action.start.recipe";

    public static final String EXTRA_PROGRESSBAR_MAIN = "it.instantapps.bakingapp.activity.recipe.progressbar";

    public static final String EXTRA_INGREDIENT_ID = "it.instantapps.bakingapp.activity.ingredient.id";
    public static final String EXTRA_RECIPE_NAME = "it.instantapps.bakingapp.activity.recipe.name";

    public static final String EXTRA_STEP_ID = "it.instantapps.bakingapp.activity.step.id";
    public static final String EXTRA_STEP_TYPE_LAYOUT = "it.instantapps.bakingapp.activity.step.type.layout";
    public static final String EXTRA_DETAIL_STEP_ID = "it.instantapps.bakingapp.activity.detail.step.id";

    public static final String EXTRA_NAVIGATION_TYPE = "it.instantapps.bakingapp.activity.navigation.type";


    public static final String BUNDLE_TAB_RECIPE_ID = "it.instantapps.bakingapp.bundle.tab.recipe.id";
    public static final String BUNDLE_RECIPE_NAME = "it.instantapps.bakingapp.recipe.name";
    public static final String BUNDLE_TAB_ORDERTAB = "it.instantapps.bakingapp.bundle.tab.ordertab";
    public static final String BUNDLE_RECIPE_ID = "it.instantapps.bakingapp.bundle.recipe.id";
    public static final String BUNDLE_RECIPE_WIDGET = "it.instantapps.bakingapp.bundle.recipe.widget";

    public static final String BUNDLE_DETAIL_STEP_VIDEOURI = "it.instantapps.bakingapp.activity.detail.step.videouri";
    public static final String BUNDLE_DETAIL_STEP_THUMBNAILURL = "it.instantapps.bakingapp.activity.detail.step.thumbnailurl";
    public static final String BUNDLE_DETAIL_STEP_DESCRIPTION = "it.instantapps.bakingapp.activity.detail.step.description";
    public static final String BUNDLE_DETAIL_STEP_SHORT_DESCRIPTION = "it.instantapps.bakingapp.activity.detail.step.shortdescription";
    public static final String BUNDLE_DETAIL_STEP_ID = "it.instantapps.bakingapp.activity.detail.step.id";
    public static final String BUNDLE_DETAIL_STEP_NAVIGATION_ID = "it.instantapps.bakingapp.activity.detail.step.navigation.id";

    public static final String BUNDLE_EXOPLAYER_WINDOW = "it.instantapps.bakingapp.activity.exoplayer.window";
    public static final String BUNDLE_EXOPLAYER_POSITION = "it.instantapps.bakingapp.activity.exoplayer.position";
    public static final String BUNDLE_EXOPLAYER_AUTOPLAY = "it.instantapps.bakingapp.activity.exoplayer.autoplay";

    public static final String USER_AGENT_CACHE = "CacheDataSourceFactory";
    public static final String CACHE_VIDEO_DIR = "MediaBakingApp";
    public static final long EXT_CACHE_SIZE_MAX = 500 * 1024 * 1024;
    public static final long EXT_CACHE_FILE_SIZE_MAX = 40 * 1024 * 1024;
    public static final long CACHE_SIZE_MAX = 100 * 1024 * 1024;
    public static final long CACHE_FILE_SIZE_MAX = 20 * 1024 * 1024;

    public static final int EXO_PROGRESSBAR_DELAY = 200;

    public static final long EXO_UPDATE_DELAY = 25;

    public static final String BAKING_SYNC_TAG = "baking-sync";
    public static final String EXO_PLAYER_MANAGER_TAG = "StepActivity";

    public static final int NOTIFICATION_ID = 1;

    public static final String NOTIFICATION_CHANNEL_ID = "it.instantapps.bakingapp.activity.exoplayer.ONE";
    public static final String NOTIFICATION_CHANNEL_NAME = "Channel Media EXOPLAYER";

    public static final int TAB_TITLE_OFFSET_DIPS = 16;
    public static final int TAB_VIEW_PADDING_DIPS = 16;
    public static final int TAB_VIEW_TEXT_SIZE_SP = 16;

    public static final int TAB_DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 2;
    public static final byte TAB_DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26;
    public static final int TAB_SELECTED_INDICATOR_THICKNESS_DIPS = 3;
    public static final int COLOR_BLACK = 0xFF000000;
    public static final int COLOR_DKGRAY = 0xFF444444;
    public static final int COLOR_GRAY = 0xFF888888;
    public static final int COLOR_LTGRAY = 0xFFCCCCCC;
    public static final int COLOR_WHITE = 0xFFFFFFFF;
    public static final int COLOR_RED = 0xFFFF0000;
    public static final int COLOR_GREEN = 0xFF00FF00;
    public static final int COLOR_BLUE = 0xFF0000FF;
    public static final int COLOR_YELLOW = 0xFFFFFF00;
    public static final int COLOR_CYAN = 0xFF00FFFF;
    public static final int TAB_DEFAULT_SELECTED_INDICATOR_COLOR = COLOR_YELLOW;

    public static final int TAB_DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
    public static final byte TAB_DEFAULT_DIVIDER_COLOR_ALPHA = 0x20;
    public static final float TAB_DEFAULT_DIVIDER_HEIGHT = 1f;

    public static final String COLOR_BACKGROUND_ACTIONBAR_OFFLINE = "#BDBDBD";

    public static final int GLIDE_IMAGE_WIDTH_RECIPE = 100;
    public static final int GLIDE_IMAGE_HEIGHT_RECIPE = 100;
    public static final float WP_GLIDE_BRIGHTNESS_RECIPE = 0.1f;

    public static final int GLIDE_BITMAP_ALPHA_STEP = 120;
    public static final float WP_GLIDE_BRIGHTNESS_STEP = 0.1f;
    public static final int GLIDE_IMAGE_WIDTH_STEP = 300;
    public static final int GLIDE_IMAGE_HEIGHT_STEP = 200;



    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10;


}
