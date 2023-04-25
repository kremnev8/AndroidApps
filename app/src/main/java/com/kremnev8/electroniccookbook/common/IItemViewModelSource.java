package com.kremnev8.electroniccookbook.common;

public interface IItemViewModelSource<T, TVM extends ItemViewModel> {
    TVM CreateInstance(T item);
}
