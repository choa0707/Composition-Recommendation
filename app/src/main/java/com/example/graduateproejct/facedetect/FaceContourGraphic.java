package com.example.graduateproejct.facedetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.FaceDetector;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.graduateproejct.MainActivity;
import com.example.graduateproejct.MyApplication;
import com.example.graduateproejct.R;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.example.graduateproejct.common.GraphicOverlay;
import com.example.graduateproejct.common.GraphicOverlay.Graphic;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

/** Graphic instance for rendering face contours graphic overlay view. */
public class FaceContourGraphic extends Graphic {
  String result_score;
  String roll;
  String help;
  String yaw ;
  String pitch ;
  private static final float FACE_POSITION_RADIUS = 4.0f;
  private static final float ID_TEXT_SIZE = 30.0f;
  private static final float ID_Y_OFFSET = 80.0f;
  private static final float ID_X_OFFSET = -70.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;
  private int stop;
  private final Paint facePositionPaint;

  private final Paint idPaint;
  private final Paint boxPaint;
  private Context mcontext;
  float score;
  float left_eye_x = 0, left_eye_y = 0, right_eye_x = 0, right_eye_y = 0, nose_x = 0, nose_y = 0, left_mouth_x = 0, left_mouth_y = 0, right_mouth_x = 0, right_mouth_y = 0, chin_x = 0, chin_y = 0;
  float ratio = 0;
  private volatile FirebaseVisionFace firebaseVisionFace;
  MyApplication myApplication = (MyApplication)getApplicationContext();
  public FaceContourGraphic(GraphicOverlay overlay, FirebaseVisionFace face, Context context) {
    super(overlay);

    mcontext = context;

    this.firebaseVisionFace = face;
    final int selectedColor = Color.WHITE;

    facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);
    stop = myApplication.getStop();
    boxPaint = new Paint();
    boxPaint.setColor(selectedColor);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
  }

  public void requestLogin() {
    String url = "http://ec2-54-180-120-138.ap-northeast-2.compute.amazonaws.com:4000/";

    score = myApplication.getGlobalValue();

    if (score == 1) {

      myApplication.setStop(1);
    }
    //JSON형식으로 데이터 통신을 진행합니다!
    JSONObject testjson = new JSONObject();
    try {
      //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
      testjson.put("ratio", Float.toString(ratio));
      testjson.put("lex", Float.toString(left_eye_x));
      testjson.put("ley", Float.toString(left_eye_y));
      testjson.put("rex", Float.toString(right_eye_x));
      testjson.put("rey", Float.toString(right_eye_y));
      testjson.put("nox", Float.toString(nose_x));
      testjson.put("noy", Float.toString(nose_y));
      testjson.put("lmx", Float.toString(left_mouth_x));
      testjson.put("lmy", Float.toString(left_mouth_y));
      testjson.put("rmx", Float.toString(right_mouth_x));
      testjson.put("rmy", Float.toString(right_mouth_y));
      testjson.put("chx", Float.toString(chin_x));
      testjson.put("chy", Float.toString(chin_y));
      testjson.put("score",Float.toString(score));
      String jsonString = testjson.toString(); //완성된 json 포맷

      final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
      final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, testjson, new Response.Listener<JSONObject>() {

        //데이터 전달을 끝내고 이제 그 응답을 받을 차례입니다.
        @Override
        public void onResponse(JSONObject response) {
          try {
            myApplication.setStop(0);

            myApplication.setmGlobalValue(0);

            //받은 json형식의 응답을 받아
            JSONObject jsonObject = new JSONObject(response.toString());

            //key값에 따라 value2값을 쪼개 받아옵니다.
            result_score = jsonObject.getString("score");
            if(Integer.parseInt(result_score) != -1)
            {
              myApplication.setScore(result_score);
            }
            roll = jsonObject.getString("roll");
            yaw = jsonObject.getString("yaw");
            pitch = jsonObject.getString("pitch");
            help = jsonObject.getString("help");


            ((MainActivity)mcontext).textView.setText("각도 (X: "+roll + ", Y: " + pitch + ", Z: "+yaw+")         점수: "+ myApplication.getScore());
            ((MainActivity)mcontext).scoreButton.setClickable(true);
            ((MainActivity)mcontext).scoreButton.setText("점수계산");
            //textView.setText(result_score+", "+roll + ", " + pitch + ", "+yaw);
            Log.i("Server", "점수: "+result_score+", Roll: "+roll+", Yaw: "+yaw+", Pitch: "+pitch+", 지도 방향: "+help);
            if (Integer.parseInt(result_score) >= 4){
              Toast.makeText(getApplicationContext(), "좋은 구도에요!", Toast.LENGTH_LONG).show();
            }
            switch (Integer.parseInt(help)){
              case 1:
                Toast.makeText(getApplicationContext(), "고개를 들어주세요!", Toast.LENGTH_LONG).show();
                break;
              case 2:
                Toast.makeText(getApplicationContext(), "고개를 내려주세요!", Toast.LENGTH_LONG).show();
                break;
              case 3:
                Toast.makeText(getApplicationContext(), "고개를 왼쪽으로 돌려주세요!", Toast.LENGTH_LONG).show();
                break;
              case 4:
                Toast.makeText(getApplicationContext(), "고개를 오른쪽으로 돌려주세요!", Toast.LENGTH_LONG).show();
                break;
              case 5:
                Toast.makeText(getApplicationContext(), "고개를 시계방향으로 돌려주세요!", Toast.LENGTH_LONG).show();
                break;
              case 6:
                Toast.makeText(getApplicationContext(), "고개를 반시계방향으로 돌려주세요!", Toast.LENGTH_LONG).show();
                break;
              case 7:
                Toast.makeText(getApplicationContext(), "카메라를 멀리하세요!", Toast.LENGTH_LONG).show();
                break;
            }

          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        //서버로 데이터 전달 및 응답 받기에 실패한 경우 아래 코드가 실행됩니다.
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          Log.i("Server", "server fail");
          error.printStackTrace();
        }
      });
      jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
      requestQueue.add(jsonObjectRequest);

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Draws the face annotations for position on the supplied canvas.
   */
  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    if (face == null) {
      return;
    }

    // Draws a circle at the position of the detected face, with the face's track id below.
    float x = translateX(face.getBoundingBox().centerX());
    float y = translateY(face.getBoundingBox().centerY());

    Paint paint = new Paint();
    paint.setColor(Color.BLACK);
    //canvas.drawCircle(x, y, FACE_POSITION_RADIUS + 2, paint);
    //canvas.drawText("id3: " + face.getTrackingId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);

    // Draws a bounding box around the face.
    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    float left = x - xOffset;
    float top = y - yOffset;
    float right = x + xOffset;
    float bottom = y + yOffset;

    //canvas.drawRect(left, top - 30, right, bottom - 30, boxPaint);

    paint.setTextSize(50.0f);
    //canvas.drawText(result_score+", "+roll+","+yaw+","+pitch, 100, 100, paint);
    //canvas.drawText("size : " + Float.toString(right - left) + " * " + Float.toString(bottom - top), x + face.getBoundingBox().width() * 2 + 30, y + face.getBoundingBox().height() * 2 + 30, paint);
    FirebaseVisionFaceContour contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS);

    int number = 1;

    for (FirebaseVisionPoint point : contour.getPoints()) {
      float px = translateX(point.getX());
      float py = translateY(point.getY());
      ratio = (((right - left)*(bottom - top))/(1080*1440))*100;
      if (number == 77 || number == 85 || number == 81 || number == 73 || number == 61 || number == 69 || number == 65 || number == 57 || number == 128 || number == 99 || number == 89 || number == 19) {
        //canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
        if (number == 73 || number == 81) {
          Log.i("왼쪽눈", Float.toString(px));
          left_eye_x += px;
          left_eye_y += py;
        } else if (number == 65 || number == 57) {
          right_eye_x += px;
          right_eye_y += py;
        } else if (number == 128) {
          nose_x = px;
          nose_y = py;
        } else if (number == 99) {
          left_mouth_x = px;
          left_mouth_y = py;
        } else if (number == 89) {
          right_mouth_x = px;
          right_mouth_y = py;
        } else if (number == 19) {
          chin_x = px;
          chin_y = py;
        }


      }
      number++;
    }
    left_eye_x /= 2;
    left_eye_y /= 2;
    right_eye_x /= 2;
    right_eye_y /= 2;
    Log.e("coordinates", "\nleft_eye : " + left_eye_x + ", " + left_eye_y + "\nright_eye : " + right_eye_x + ", " + right_eye_y + "\nnose : " + nose_x + ", " + nose_y + "\nleft_mouth : " + left_mouth_x + ", " + left_mouth_y + "\nright_mouth : " + right_mouth_x + ", " + right_mouth_y + "\nchin : " + chin_x + ", " + chin_y);
    stop = myApplication.getStop();

    if (stop != 1) {
      requestLogin();
    }

    //requestLogin();
    FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
    if (leftEye != null && leftEye.getPosition() != null) {
      canvas.drawCircle(
              translateX(leftEye.getPosition().getX()),
              translateY(leftEye.getPosition().getY()),
              FACE_POSITION_RADIUS,
              facePositionPaint);
    }
    FirebaseVisionFaceLandmark rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
    if (rightEye != null && rightEye.getPosition() != null) {
      canvas.drawCircle(
              translateX(rightEye.getPosition().getX()),
              translateY(rightEye.getPosition().getY()),
              FACE_POSITION_RADIUS,
              facePositionPaint);
    }

    FirebaseVisionFaceLandmark leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK);
    if (leftCheek != null && leftCheek.getPosition() != null) {
      canvas.drawCircle(
              translateX(leftCheek.getPosition().getX()),
              translateY(leftCheek.getPosition().getY()),
              FACE_POSITION_RADIUS,
              facePositionPaint);
    }
    FirebaseVisionFaceLandmark rightCheek =
            face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK);
    if (rightCheek != null && rightCheek.getPosition() != null) {
      canvas.drawCircle(
              translateX(rightCheek.getPosition().getX()),
              translateY(rightCheek.getPosition().getY()),
              FACE_POSITION_RADIUS,
              facePositionPaint);
    }
  }
}
