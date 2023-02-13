package com.becroft.subhunter;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.Window;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;
import java.util.logging.Logger;

import android.os.Bundle;

public class SubHunter extends Activity {

    //Public Variables
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 40;
    int gridHeight;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    int distanceFromSub;
    boolean debugging = false;

    // Public objects
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;


    /*
     Android runs this code just before
     the app is seen by the player.
     This makes it a good place to add
     the code that is needed for
     the one-time setup.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get current device screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialise size based variables based on screen resolution
        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;

        // Initialise objects for drawing
        blankBitmap = Bitmap.createBitmap(numberHorizontalPixels, numberVerticalPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();

        // Tell android to set drawing view for the app
        setContentView(gameView);

        Log.d("Debugging", "In onCreate");
        newGame();
        draw();
    }

    /*
    This code will execute when a new
    game needs to be started. It will
    happen when the app is first started
    and after the player wins a game.
    */
    void newGame(){
        Random random = new Random();
        subHorizontalPosition = random.nextInt(gridWidth);
        subVerticalPosition = random.nextInt(gridHeight);
        shotsTaken = 0;

        Log.d("Debugging", "In newGame");

    }

    /*
    Here we will do all the drawing.
    The grid lines, the HUD and
    the touch indicator
    */
    void draw(){
        gameView.setImageBitmap(blankBitmap);

        // Wipe the screen with white color
        canvas.drawColor(Color.argb(255,255,255,255));

        // Change the paint colour to black
        paint.setColor(Color.argb(255,0,0,0));

        // Draw vertical grid lines
        for(int i = 0; i < gridWidth; i++) {
            canvas.drawLine(blockSize * i, 0, blockSize * i, numberVerticalPixels - 1, paint);
        }
        // Draw horizontal lines
        for (int i = 0; i < gridHeight; i++) {
            canvas.drawLine(0, blockSize * i, numberHorizontalPixels - 1, blockSize * i, paint);
        }
        // Draw Players shot
        canvas.drawRect(horizontalTouched * blockSize, verticalTouched * blockSize,
                (horizontalTouched * blockSize) + blockSize,
                (verticalTouched * blockSize) + blockSize, paint);

        // Re-size score and distance text for screen size
        paint.setTextSize(blockSize * 2);
        paint.setColor(Color.argb(255,0,0,255));
        canvas.drawText("Shots Taken: " + shotsTaken + " Distance: " + distanceFromSub, blockSize, blockSize * 1.75f, paint);

        Log.d("Debugging", "In draw");
        if(debugging)
            printDebuggingText();
    }

    /*
    This part of the code will
    handle detecting that the player
    has tapped the screen
    */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        Log.d("Debugging", "In onTouchEvent");

        // Has player removed finger from screen?
        if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
            // Pass touch coords to takeShot
            takeShot(motionEvent.getX(), motionEvent.getY());
        }


        return true;
    }

    /*
    The code here will execute when
    the player taps the screen. It will
    calculate the distance from the sub'
    and decide a hit or miss
    */

    void takeShot(float touchX, float touchY){
        Log.d("Debugging", "In takeShot");
        // Add one to shotsTaken variable
        shotsTaken ++;
        // convert screen float coords to int grid coords
        horizontalTouched = (int) touchX / blockSize;
        verticalTouched = (int) touchY / blockSize;
        // Assess if shot hit sub
        hit = (horizontalTouched == subHorizontalPosition) && (verticalTouched == subVerticalPosition);
        // How far from sub
        int horizontalGap = (int) horizontalTouched - subHorizontalPosition;
        int verticalGap = (int) verticalTouched - subVerticalPosition;
        // Use math (pythagoras) to get distance to submarine
        distanceFromSub = (int) Math.sqrt((horizontalGap * horizontalGap) + (verticalGap * verticalGap));
        // If hit call boom else draw as normal
        if(hit)
            boom();
        else
            draw();
    }

    // This code says "BOOM!"
    void boom(){

        gameView.setImageBitmap(blankBitmap);

        // Wipe screen with red colour
        canvas.drawColor(Color.argb(255,255,0,0));

        // Draw large white text
        paint.setColor(Color.argb(255,255,255,255));
        paint.setTextSize(blockSize * 10);

        canvas.drawText("BOOM!", blockSize * 4, blockSize * 14, paint);

        // Draw prompt for restart
        paint.setTextSize(blockSize * 2);
        canvas.drawText("Tap to start again", blockSize * 8, blockSize * 18, paint);

        // Start new game
        newGame();

    }

    // This code prints the debugging text
    void printDebuggingText(){
        paint.setTextSize(blockSize);
        canvas.drawText("numberHorizontalPixels = " + numberHorizontalPixels, 50, blockSize * 3, paint);
        canvas.drawText("numberVerticalPixels = " + numberVerticalPixels, 50, blockSize * 4, paint);
        canvas.drawText("blockSize = " + blockSize, 50, blockSize * 5, paint);
        canvas.drawText("gridWidth = " + gridWidth, 50, blockSize * 6, paint);
        canvas.drawText("gridHeight = " + gridHeight, 50, blockSize * 7, paint);
        canvas.drawText("horizontalTouched = " + horizontalTouched, 50, blockSize * 8, paint);
        canvas.drawText("verticalTouched = " + verticalTouched, 50, blockSize * 9, paint);
        canvas.drawText("subHorizontalPosition = " + subHorizontalPosition, 50, blockSize * 10, paint);
        canvas.drawText("subVerticalPosition = " + subVerticalPosition, 50, blockSize * 11, paint);
        canvas.drawText("hit = " + hit, 50, blockSize * 12, paint);
        canvas.drawText("shotsTaken = " + shotsTaken, 50, blockSize * 13, paint);
        canvas.drawText("debugging = " + debugging, 50, blockSize * 14, paint);
    }

}