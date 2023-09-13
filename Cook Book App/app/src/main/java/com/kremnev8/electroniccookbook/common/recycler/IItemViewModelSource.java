package com.kremnev8.electroniccookbook.common.recycler;

public interface IItemViewModelSource<T, TVM extends ItemViewModel> {
    TVM CreateInstance(T item);
}
