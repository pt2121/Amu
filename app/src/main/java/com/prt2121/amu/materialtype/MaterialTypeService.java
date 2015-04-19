/*
 * Copyright (c) 2015 Prat Tanapaisankit and Intellibins authors
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  Neither the name of The Intern nor the names of its contributors may
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE LISTED COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.prt2121.amu.materialtype;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.prt2121.amu.AmuApp;

import android.content.SharedPreferences;

import java.lang.reflect.Type;
import java.util.Collection;

import javax.inject.Inject;

/**
 * Created by pt2121 on 3/20/15.
 *
 * MaterialTypeService provides user's selected location types.
 */
public class MaterialTypeService {

    public static final String MATERIAL_TYPE = "materialType";

    @Inject
    SharedPreferences preferences;

    @Inject
    Gson gson;

    private MaterialType[] types;

    public MaterialTypeService() {
        AmuApp.getInstance().getGraph().inject(this);
        String s = preferences.getString(MATERIAL_TYPE, null);
        Type type = new TypeToken<Collection<MaterialType>>() {
        }.getType();
        Collection<MaterialType> ts = gson.fromJson(s, type);
        if (ts == null) {
            types = new MaterialType[23];
            types[0] = new MaterialType(0, "Plastic Cup", true);
            types[1] = new MaterialType(1, "Plastic Bottle", true);
            types[2] = new MaterialType(2, "Plastic Lid", true);
            types[3] = new MaterialType(3, "Newspaper", true);
            types[4] = new MaterialType(4, "Paper Magazine", true);
            types[5] = new MaterialType(5, "Paper Container", true);
            types[6] = new MaterialType(6, "Paper Drink Carton", true);
            types[7] = new MaterialType(7, "Paper Lid", true);
            types[8] = new MaterialType(8, "Paper Sheet", true);
            types[9] = new MaterialType(9, "Glass Bottle", true);
            types[10] = new MaterialType(10, "Aluminum Can", true);
            types[11] = new MaterialType(11, "Aluminum Wrap", true);
            types[12] = new MaterialType(12, "Aluminum Bottle", true);
            types[13] = new MaterialType(13, "Plastic Utensil", true);
            types[14] = new MaterialType(14, "Plastic Wrap", true);
            types[15] = new MaterialType(15, "Plastic Bag", true);
            types[16] = new MaterialType(16, "Metal Container", true);
            types[17] = new MaterialType(17, "Bubble Wrap", true);
            types[18] = new MaterialType(18, "Paper Cardboard", true);
            types[19] = new MaterialType(19, "Paper Cup", true);
            types[20] = new MaterialType(20, "Plastic Container", true);
            types[21] = new MaterialType(21, "Glass Container", true);
            types[22] = new MaterialType(22, "Clothes", true);
            SharedPreferences.Editor e = preferences.edit();
            e.putString(MATERIAL_TYPE, gson.toJson(types));
            e.apply();
        } else {
            types = new MaterialType[ts.size()];
            types = ts.toArray(types);
        }
    }

    public MaterialType[] getMaterialTypes() {
        return types;
    }

    public void updateMaterialType(int position, boolean checked) {
        types[position].setChecked(checked);
        SharedPreferences.Editor e = preferences.edit();
        e.putString(MATERIAL_TYPE, gson.toJson(types));
        e.apply();
    }

}

