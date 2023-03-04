package com.kremnev8.electroniccookbook.interfaces;

public interface IPhotoProvider {
    void requestPhoto(IPhotoRequestCallback callback);

    void takePicture(IPhotoRequestCallback callback);
}
