package com.kremnev8.electroniccookbook.common.recycler;

public interface ISearchFilter<T> {
    boolean shouldShow(T item);
}