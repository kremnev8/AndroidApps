package com.kremnev8.electroniccookbook.common;

import java.util.Arrays;

public abstract class Pool<T extends IPoolable> {
    public Object[] pool;
    public int poolCursor = 1;
    protected int poolCapacity;
    protected int[] poolRecycle;
    protected int recycleCursor;

    public Pool(int initialCapacity){
        init(initialCapacity);
    }

    protected abstract T getNewInstance();

    protected abstract void setInstanceData(T obj, Object[] data);

    public void free() {
        pool = null;
        poolCursor = 1;
        poolCapacity = 0;
        poolRecycle = null;
        recycleCursor = 0;
    }

    public void init(int newSize) {
        poolRecycle = new int[newSize];
        if (pool != null) {
            pool = Arrays.copyOf(pool, newSize);
        } else {
            pool = new Object[newSize];
        }

        for (int i = 0; i < pool.length; i++) {
            if (pool[i] == null)
                pool[i] = getNewInstance();
        }

        poolCapacity = newSize;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) pool[index];
    }

    public int addPoolItem(Object[] data) {
        int num;
        if (recycleCursor > 0) {
            num = poolRecycle[--recycleCursor];
        } else {
            num = poolCursor++;
            if (num == poolCapacity) {
                init(poolCapacity * 2);
            }
        }

        T item = get(num);
        item.setId(num);
        setInstanceData(item, data);

        return num;
    }

    public void removePoolItem(int id) {
        T item = get(id);
        if (item.getId() != 0) {
            item.free();
            item.setId(0);
            poolRecycle[recycleCursor++] = id;
        }
    }

    public int getCapacity() {
        return poolCapacity;
    }
}
