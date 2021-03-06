package com.example.graduateproejct.facedetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.example.graduateproejct.common.CameraImageGraphic;
import com.example.graduateproejct.common.FrameMetadata;
import com.example.graduateproejct.common.GraphicOverlay;
import com.example.graduateproejct.Helper.VisionProcessorBase;

import java.io.IOException;
import java.util.List;

/**
 * Face Contour Demo.
 */
public class FaceContourDetectorProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {
    int forcount = 0;
    private static final String TAG = "FaceContourDetectorProc";
    Context mcontext;
    private final FirebaseVisionFaceDetector detector;

    public FaceContourDetectorProcessor(Context mccontext) {
        mcontext = mccontext;
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Contour Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }


    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }
        forcount++;
        if (forcount >= 2100000000) { forcount = 0;}
        else {
            if(forcount%25 == 0) {
                for (int i = 0; i < faces.size(); ++i) {
                    FirebaseVisionFace face = faces.get(i);
                    FaceContourGraphic faceGraphic = new FaceContourGraphic(graphicOverlay, face, mcontext);
                    graphicOverlay.add(faceGraphic);
                }
                graphicOverlay.postInvalidate();
            }
        }
    }


    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }
}
