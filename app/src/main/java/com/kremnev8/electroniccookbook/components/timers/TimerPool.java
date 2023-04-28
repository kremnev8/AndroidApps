package com.kremnev8.electroniccookbook.components.timers;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.kremnev8.electroniccookbook.common.Pool;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;

import java.util.Hashtable;

public class TimerPool extends Pool<TimerData> {

    private final Hashtable<Pair<Integer, Integer>, Integer> idLookup = new Hashtable<>();

    public TimerPool(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    protected TimerData getNewInstance() {
        return new TimerData();
    }


    @SuppressWarnings("ConstantConditions")
    public TimerData get(int recipeId, int stepId){
        var pair = Pair.create(recipeId, stepId);
        if (idLookup.containsKey(pair)){
           int id = idLookup.get(pair);
           return get(id);
        }
        return null;
    }

    @Override
    protected void setInstanceData(@NonNull TimerData obj, Object[] data) {
        obj.set((RecipeStep) data[0]);
    }

    public TimerData addTimer(@NonNull RecipeStep step) {
        int id = addPoolItem(new Object[]{step});
        idLookup.put(Pair.create(step.recipe, step.id), id);
        return get(id);
    }

    @SuppressWarnings("ConstantConditions")
    public void removeTimer(int recipeId, int stepId){
        var pair = Pair.create(recipeId, stepId);
        if (idLookup.containsKey(pair)){
            int id = idLookup.get(pair);
            removePoolItem(id);
            idLookup.remove(pair);
        }
    }

    public void removeTimer(int id){
        TimerData timerData = get(id);
        var pair = Pair.create(timerData.recipeId, timerData.stepId);
        idLookup.remove(pair);
        removePoolItem(id);
    }
}
