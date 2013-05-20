package com.paad.ad2.compass;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.paad.ad2.R;

public class CompassView extends View {
    private float bearing;
    private Paint circlePaint;
    private String northString;
    private String eastString;
    private String southString;
    private String westString;
    private Paint textPaint;
    private int textHeight;
    private Paint markerPaint;
    private float pitch;
    private float roll;
    private int[] borderGradientColors;
    private float[] borderGradientPositions;
    private int[] glassGradientColors;
    private float[] glassGradientPositions;
    private int skyHorizonColorForm;
    private int skyHorizonColorTo;
    private int groundHorizonColorFrom;
    private int groundHorizonColorTo;

    public CompassView(Context context) {
        super(context);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCompassView();
    }

    private void initCompassView() {
        setFocusable(true);

        final Resources resources = this.getResources();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(resources.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.STROKE);

        northString = resources.getString(R.string.cardinal_north);
        eastString = resources.getString(R.string.cardinal_east);
        southString = resources.getString(R.string.cardinal_south);
        westString = resources.getString(R.string.cardinal_west);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(resources.getColor(R.color.text_color));
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        textHeight = (int) textPaint.measureText("yY");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(resources.getColor(R.color.marker_color));
        markerPaint.setAlpha(200);
        markerPaint.setStrokeWidth(1);
        markerPaint.setStyle(Paint.Style.STROKE);
        markerPaint.setShadowLayer(2, 1, 1, resources.getColor(R.color.shadow_color));

        borderGradientColors = new int[4];
        borderGradientPositions = new float[4];

        borderGradientColors[3] = resources.getColor(R.color.outer_border);
        borderGradientColors[2] = resources.getColor(R.color.inner_border_one);
        borderGradientColors[1] = resources.getColor(R.color.inner_border_two);
        borderGradientColors[0] = resources.getColor(R.color.inner_border);
        borderGradientPositions[3] = 0.0f;
        borderGradientPositions[2] = 1 - 0.03f;
        borderGradientPositions[1] = 1 - 0.06f;
        borderGradientPositions[0] = 1.0f;

        glassGradientColors = new int[5];
        glassGradientPositions = new float[5];

        final int glassColor = 245;
        glassGradientColors[4] = Color.argb(65, glassColor, glassColor, glassColor);
        glassGradientColors[3] = Color.argb(100, glassColor, glassColor, glassColor);
        glassGradientColors[2] = Color.argb(50, glassColor, glassColor, glassColor);
        glassGradientColors[1] = Color.argb(0, glassColor, glassColor, glassColor);
        glassGradientColors[0] = Color.argb(0, glassColor, glassColor, glassColor);
        glassGradientPositions[4] = 1 - 0.0f;
        glassGradientPositions[3] = 1 - 0.06f;
        glassGradientPositions[2] = 1 - 0.10f;
        glassGradientPositions[1] = 1 - 0.20f;
        glassGradientPositions[0] = 1 - 1.0f;

        skyHorizonColorForm = resources.getColor(R.color.horizon_sky_from);
        skyHorizonColorTo = resources.getColor(R.color.horizon_sky_to);

        groundHorizonColorFrom = resources.getColor(R.color.horizon_ground_from);
        groundHorizonColorTo = resources.getColor(R.color.horizon_ground_to);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float rightWidth = textHeight + 4;

        final int height = getMeasuredHeight();
        final int width = getMeasuredWidth();

        final int px = width / 2;
        final int py = height / 2;

        final Point center = new Point(px, py);

        final int radius = Math.min(px, py) - 2;

        final RectF boundingBox = new RectF(
                center.x - radius,
                center.y - radius,
                center.y + radius,
                center.y + radius);

        final RectF innerBoundingBox = new RectF(
                center.x - radius + rightWidth,
                center.y - radius + rightWidth,
                center.y + radius - rightWidth,
                center.y + radius - rightWidth);

        final float innerRadius = innerBoundingBox.height() / 2;

        final RadialGradient borderGradient = new RadialGradient(px, py, radius,
                borderGradientColors, borderGradientPositions, Shader.TileMode.CLAMP);

        final Paint pgb = new Paint();
        pgb.setShader(borderGradient);

        final Path outerRingPath = new Path();
        outerRingPath.addOval(boundingBox, Path.Direction.CW);

        canvas.drawPath(outerRingPath, pgb);

        final LinearGradient skyShader = new LinearGradient(center.x,
                innerBoundingBox.top, center.x, innerBoundingBox.bottom,
                skyHorizonColorForm, skyHorizonColorTo, Shader.TileMode.CLAMP);

        final Paint skyPaint = new Paint();
        skyPaint.setShader(skyShader);

        final LinearGradient groundShader = new LinearGradient(center.x,
                innerBoundingBox.top, center.x, innerBoundingBox.bottom,
                groundHorizonColorFrom, groundHorizonColorTo, Shader.TileMode.CLAMP);

        final Paint groundPaint = new Paint();
        groundPaint.setShader(groundShader);

        float tiltDegree = pitch;
        while (tiltDegree > 90 || tiltDegree < -90) {
            if (tiltDegree > 90) tiltDegree = -90 + (tiltDegree - 90);
            if (tiltDegree < 90) tiltDegree = 90 - (tiltDegree + 90);
        }

        float rollDegree = roll;
        while (rollDegree > 180 || rollDegree < -180) {
            if (rollDegree > 180) rollDegree = -180 + (rollDegree - 180);
            if (rollDegree < -180) rollDegree = 180 - (rollDegree + 180);
        }

        final Path skyPath = new Path();
        skyPath.addArc(innerBoundingBox, -tiltDegree, (180 + (2 * tiltDegree)));

        canvas.save();
        canvas.rotate(-rollDegree, px, py);
        canvas.drawOval(innerBoundingBox, groundPaint);
        canvas.drawPath(skyPath, skyPaint);
        canvas.drawPath(skyPath, markerPaint);

        final int markWidth = radius / 3;
        final int startX = center.x - markWidth;
        final int endX = center.x + markWidth;

        final double h = innerRadius + Math.cos(Math.toRadians(90 - tiltDegree));
        final double justTiltY = center.y - h;

        final float pxPerDegree = (innerBoundingBox.height() / 2) / 45f;

        for (int i = 90; i >= -90; i -= 10) {
            final double ypos = justTiltY + i * pxPerDegree;

            if ((ypos < (innerBoundingBox.top + textHeight)) || (ypos > innerBoundingBox.bottom - textHeight))
                continue;

            canvas.drawLine(startX, (float) ypos, endX, (float) ypos, markerPaint);

            final int displayPos = (int) (tiltDegree - i);
            final String displayString = String.valueOf(displayPos);
            final float stringSizeWidth = textPaint.measureText(displayString);
            canvas.drawText(displayString, (int) (center.x - stringSizeWidth / 2), (int) (ypos) + 1, textPaint);
        }

        markerPaint.setStrokeWidth(2);
        canvas.drawLine(center.x - radius / 2,
                (float) justTiltY,
                center.x + radius / 2,
                (float) justTiltY,
                markerPaint);
        markerPaint.setStrokeWidth(1);

        // Draw arrow
        final Path rollArrow = new Path();
        rollArrow.moveTo(center.x - 5, innerBoundingBox.top + 24);
        rollArrow.lineTo(center.x, innerBoundingBox.top + 10);
        rollArrow.moveTo(center.x + 5, innerBoundingBox.top + 24);
        rollArrow.lineTo(center.x, innerBoundingBox.top + 10);
        canvas.drawPath(rollArrow, markerPaint);
        // Draw string
        final String rollText = String.valueOf(rollDegree);
        final double rollTextWidth = textPaint.measureText(rollText);
        canvas.drawText(rollText,
                (float) (center.x - rollTextWidth / 2),
                innerBoundingBox.top + textHeight + 2,
                textPaint);

        canvas.restore();
        canvas.save();
        canvas.rotate(180, center.x, center.y);

        for (int i = -180; i < 180; i += 10) {
            if (i % 30 == 0) {
                final String rollString = String.valueOf(i * -1);
                final float rollStringWidth = textPaint.measureText(rollString);
                final PointF rollStringCenter = new PointF(center.x - rollStringWidth / 2, innerBoundingBox.top + 1 + textHeight);
                canvas.drawText(rollString, rollStringCenter.x, rollStringCenter.y, textPaint);
            } else {
                canvas.drawLine(center.x, innerBoundingBox.top, center.x, innerBoundingBox.top + 5, markerPaint);
            }
            canvas.rotate(10, center.x, center.y);
        }

        canvas.restore();
        canvas.save();
        canvas.rotate(-1 * (bearing), px, py);

        final double increment = 22.5;

        for (double i = 0; i < 360; i += increment) {
            final CompassDirection cd = CompassDirection.values()[(int) (i / 22.5)];
            final String headString = cd.toString();

            final float headStringWidth = textPaint.measureText(headString);
            final PointF headStringCenter = new PointF(center.x - headStringWidth / 2, boundingBox.top + 1 + textHeight);

            if (i % increment == 0) {
                canvas.drawText(headString, headStringCenter.x, headStringCenter.y, textPaint);
            } else {
                canvas.drawLine(center.x, boundingBox.top, center.x, boundingBox.top+3, markerPaint);
            }
            canvas.rotate((int)increment, center.x, center.y);
        }
        canvas.restore();

//        final RadialGradient glassShader = new RadialGradient(px, py, innerRadius,
//                glassGradientColors,
//                glassGradientPositions,
//                Shader.TileMode.CLAMP);
//        final Paint glassPaint = new Paint();
//        skyPaint.setShader(glassShader);
//
//        canvas.drawOval(innerBoundingBox, glassPaint);
        canvas.drawOval(boundingBox, circlePaint);
        circlePaint.setStrokeWidth(2);
        canvas.drawOval(innerBoundingBox, circlePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int dimension = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(dimension, dimension);
    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            result = specSize;
        }

        return result;
    }

    private enum CompassDirection {N, E, S, W, NNE, ESE, SSW, WNW, NE, SE, SW, NW, ENE, SSE, WSW, NNW}

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }
}
