package com.kremnev8.electroniccookbook.interfaces;

public interface IMediaProvider {
    void requestPhoto(IMediaRequestCallback callback);
    void requestMedia(IMediaRequestCallback callback);
}
