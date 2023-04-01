package com.kremnev8.electroniccookbook.common;

/// <summary>
/// Interface for objects that can be contained an a Pool
/// </summary>
public interface IPoolable {
    int getId();

    void setId(int id);

    void free();
}
