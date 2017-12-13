package com.corps.juxo.benjamin.ia;

import android.view.View;

/**
 * Created by Benjamin on 22/11/2017.
 */

public class GeneralListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        if(MainActivity.EXECUTE){
            MainActivity.me.shutDown();
        }else{
            MainActivity.me.startListenningSms();
        }

    }
}
