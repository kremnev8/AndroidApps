package com.kremnev8.electroniccookbook.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Pair;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;

public class TakePictureWithUriReturnContract extends ActivityResultContract<Uri, Pair<Boolean, Uri>> {

        private Uri imageUri;

        @NonNull
        @Override
        public Intent createIntent(Context context, Uri input) {
            imageUri = input;
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, input);
        }

        public SynchronousResult<Pair<Boolean, Uri>> getSynchronousResult(Context context, Uri input)
        {
            return null;
        }

        public Pair<Boolean, Uri> parseResult(int resultCode, Intent intent) {
            return new Pair<>(resultCode == Activity.RESULT_OK, imageUri);
        }
}
