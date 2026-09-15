/*
 * Copyright 2017 By_syk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.by_syk.lib.nanoiconpack.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.by_syk.lib.nanoiconpack.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by By_syk on 2017-02-04.
 * Modified to use zxing directly.
 */

public class QrcodeDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_qrcode, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(viewGroup);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String title = bundle.getString("title");
            String qrcodeUrl = bundle.getString("qrcodeUrl");
            if (!TextUtils.isEmpty(title)) {
                builder.setTitle(title);
            }
            if (!TextUtils.isEmpty(qrcodeUrl)) {
                int qrcodeSize = getResources().getDimensionPixelSize(R.dimen.qrcode_size);
                Bitmap bitmap = generateQrCode(qrcodeUrl, qrcodeSize);
                if (bitmap != null) {
                    ((ImageView) viewGroup.findViewById(R.id.iv_qrcode)).setImageBitmap(bitmap);
                }
            }
        }

        return builder.create();
    }

    /**
     * 用 zxing 生成二维码 Bitmap
     */
    private Bitmap generateQrCode(String content, int size) {
        if (TextUtils.isEmpty(content) || size <= 0) {
            return null;
        }
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, size, size, hints);

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static QrcodeDialog newInstance(String title, String qrcodeUrl) {
        QrcodeDialog dialog = new QrcodeDialog();

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("qrcodeUrl", qrcodeUrl);
        dialog.setArguments(bundle);

        return dialog;
    }
}