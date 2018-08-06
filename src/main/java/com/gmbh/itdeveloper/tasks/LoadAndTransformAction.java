package com.gmbh.itdeveloper.tasks;

import com.gmbh.itdeveloper.App;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

public class LoadAndTransformAction extends RecursiveAction {

    private int index;
    private int offset;
    private Consumer<Integer[]> consumer;

    public LoadAndTransformAction(int index, int offset, Consumer<Integer[]> consumer) {
        this.offset = index;
        this.offset = offset;
        this.consumer = consumer;
    }

    @Override
    protected void compute() {
        consumer.accept(new Integer[]{index,offset});

        App.OFFSET.addAndGet(App.LIMIT);
        if (App.OFFSET.get() < App._MAX.get()) {
            List<LoadAndTransformAction> subTasks = new ArrayList<>();
            subTasks.add(new LoadAndTransformAction(index++, App.OFFSET.get(), consumer));

            App.OFFSET.addAndGet(App.LIMIT);
            if (App.OFFSET.get() < App._MAX.get()) {
                subTasks.add(new LoadAndTransformAction(index++, App.OFFSET.get(), consumer));
            } else {
                App.OFFSET.addAndGet(-App.LIMIT);
            }
            invokeAll(subTasks);
        } else {
            App.OFFSET.addAndGet(-App.LIMIT);
        }
        System.out.println("I am shutdown! GoodBy");
        quietlyComplete();
    }
}
