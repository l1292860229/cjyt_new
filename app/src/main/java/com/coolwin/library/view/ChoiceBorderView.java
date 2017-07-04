package com.coolwin.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by dell on 2017/6/29.
 * 图片截取选框
 */

public class ChoiceBorderView extends View {
    private int scale = (int) this.getResources().getDisplayMetrics().density;  //屏幕像素密度
    private float borderHeight;   //总高
    private float borderWith; //总宽
    private float borderLengthX = 200 * scale; //X边框长度
    private float borderLengthY = 200 * scale; //Y边框长度
    private int borderLength = 2 * scale; //长方形框框粗
    private int roundLength = 8 * scale; //四个角的圆半径
    private int color = Color.rgb(255,74,81);
    //四个点坐标
    private static float[][] four_corner_coordinate_positions;
    private static boolean MOVE_OR_ZOOM_STATE = true; //移动或缩放状态， true 为移动
    public ChoiceBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }

    /**
     * 初始化布局
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        borderHeight = this.getHeight();
        borderWith = this.getWidth();
        //初始化四个点的坐标
        four_corner_coordinate_positions = new float[][]{//4,2(x,y)
                {(borderWith - borderLengthX) / 2, (borderHeight - borderLengthY) / 2}, //左上
                {(borderWith + borderLengthX) / 2, (borderHeight - borderLengthY) / 2}, //右上
                {(borderWith - borderLengthX) / 2, (borderHeight + borderLengthY) / 2}, //左下
                {(borderWith + borderLengthX) / 2, (borderHeight + borderLengthY) / 2}, //右上
        };
        if(onImageDetailsSizeChanggedl!=null){
            onImageDetailsSizeChanggedl.onBorderSizeChangged(
                    (int) four_corner_coordinate_positions[0][0],
                    (int) four_corner_coordinate_positions[0][1],
                    (int) borderLengthX,(int)borderLengthY
            );
        }
    }

    /**
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paintRect = new Paint();  //初始化画笔
        //画边框的画笔
        paintRect.setColor(color);    //颜色
        paintRect.setStrokeWidth(borderLength);    //宽度
        paintRect.setAntiAlias(true);   //抗锯齿
        paintRect.setStyle(Paint.Style.STROKE); //设置空心
        canvas.drawRect(four_corner_coordinate_positions[0][0],
                four_corner_coordinate_positions[0][1],
                four_corner_coordinate_positions[3][0],
                four_corner_coordinate_positions[3][1], paintRect);
        //画四个角的画笔
        paintRect.setColor(color);
        paintRect.setStyle(Paint.Style.FILL);
        paintRect.setStrokeWidth(roundLength);
        paintRect.setAntiAlias(true);
        //左上角
        canvas.drawCircle(four_corner_coordinate_positions[0][0],
                        four_corner_coordinate_positions[0][1],roundLength, paintRect);
        //左下角
        canvas.drawCircle(four_corner_coordinate_positions[2][0],
                    four_corner_coordinate_positions[2][1],roundLength,paintRect);
        //右上角
        canvas.drawCircle(four_corner_coordinate_positions[1][0],
                four_corner_coordinate_positions[1][1],roundLength,paintRect);
        //右下角
        canvas.drawCircle(four_corner_coordinate_positions[3][0],
                four_corner_coordinate_positions[3][1],roundLength,paintRect);
    }

    private int lastX = 0;  //上次按下的X位置
    private int lastY = 0;  //上次按下的Y位置
    private int offsetX = 0;    //X轴偏移量
    private int offsetY = 0;    //Y轴偏移量
    static int point = -1;// 用户按下的点
//    private boolean POINT_STATE = true;//判断用户是缩小还是放大 true放大 false缩小
    private int maxX,maxY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInTheCornerCircle(event.getX(), event.getY()) != -1) {
                    //开始缩放操作
                    MOVE_OR_ZOOM_STATE = false; //设置false为缩放状态
                    point = isInTheCornerCircle(event.getX(), event.getY());
                }
                lastX = x;
                lastY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = x - lastX;
                offsetY = y - lastY;
                //限定移动范围
                //移动状态：只有在移动状态下才能移动
                if (MOVE_OR_ZOOM_STATE) {
                    getoffsetXandoffsetY(offsetX,offsetY);
                    //四个点的坐标信息也要随之改变
                    for (int i = 0; i < four_corner_coordinate_positions.length; i++) {
                        four_corner_coordinate_positions[i][0] += offsetX;
                        four_corner_coordinate_positions[i][1] += offsetY;
                        //更新回调接口
                        onImageDetailsSizeChanggedl.onBorderSizeChangged(
                                (int) four_corner_coordinate_positions[0][0],
                                (int) four_corner_coordinate_positions[0][1],
                                (int) borderLengthX,(int)borderLengthY
                        );

                        invalidate();
                    }
                }else {//在缩放状态下
                    //按住某一个点，该点的坐标改变，其他2个点坐标跟着改变，对点坐标不变
                    maxX = offsetX;
                    maxY = offsetY;
                    getoffsetXandoffsetY(offsetX, offsetY); //边界范围判断
                    //改变坐标
                    changgeFourCoodinatePosition(point);
                    //更新边长
                    notifyNowborderLength();
                    //更新回调接口
                    onImageDetailsSizeChanggedl.onBorderSizeChangged(
                            (int) four_corner_coordinate_positions[0][0],
                            (int) four_corner_coordinate_positions[0][1],
                            (int) borderLengthX,(int)borderLengthY
                    );
                    invalidate();
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                MOVE_OR_ZOOM_STATE = true; //回归为默认的移动状态
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 更新矩形框边长的方法
     */
    private void notifyNowborderLength() {
        float a = four_corner_coordinate_positions[0][0];
        float b = four_corner_coordinate_positions[1][0];
        float c = four_corner_coordinate_positions[0][1];
        float d = four_corner_coordinate_positions[2][1];
        borderLengthX = b-a;
        borderLengthY = d-c;
    }
    /**
     * 防止X和Y溢出边界的算法
     */
    private void getoffsetXandoffsetY(int offsetX, int offsetY) {
        //如果是移动状态
        if (MOVE_OR_ZOOM_STATE) {
            if ((four_corner_coordinate_positions[0][0] + offsetX <= 0) ||
                    (four_corner_coordinate_positions[1][0] + offsetX >= borderWith)) {
                this.offsetX = 0;
            }
            if ((four_corner_coordinate_positions[0][1] + offsetY <= 0) ||
                    (four_corner_coordinate_positions[2][1] + offsetY >= borderHeight)) {
                this.offsetY = 0;
            }
        }else { //如果是缩放状态
            float tempx =  four_corner_coordinate_positions[1][0] - four_corner_coordinate_positions[0][0]- roundLength *2 ;
            float tempy =  four_corner_coordinate_positions[2][1] - four_corner_coordinate_positions[0][1]- roundLength *2 ;
            if(point == 0 || point == 2 ){
                if((tempx-maxX)<=borderWith/5 && (tempx-maxX)<tempx ){
                    maxX = 0;
                }
            }else{
                if((tempx+maxX)<=borderWith/5 && (tempx+maxX)<tempx ){
                    maxX = 0;
                }
            }
            if(point ==0 || point ==1){
                if((tempy-maxY)<borderHeight/5  && (tempy-maxY)<tempy){
                    maxY = 0;
                }
            }else{
                if((tempy+maxY)<borderHeight/5  && (tempy+maxY)<tempy){
                    maxY = 0;
                }
            }
        }
    }
    /**
     * 扩大缩放方法
     * 根据用户传来的点改变其他点的坐标
     * 按住某一个点，该点的坐标改变，其他2个点坐标跟着改变，对点坐标不变
     * 点阵示意：
     * 0   1
     * 2   3
     * @param point   用户按的点
     */
    private void changgeFourCoodinatePosition(int point) {
        if(point == 0 || point==1){
            four_corner_coordinate_positions[0][1] += maxY;
            four_corner_coordinate_positions[1][1] += maxY;
        }
        if(point == 0 || point == 2){
            four_corner_coordinate_positions[0][0] += maxX;
            four_corner_coordinate_positions[2][0] += maxX;
        }
        if(point == 1 || point == 3){
            four_corner_coordinate_positions[1][0] += maxX;
            four_corner_coordinate_positions[3][0] += maxX;
        }
        if(point == 2 || point == 3){
            four_corner_coordinate_positions[2][1] += maxY;
            four_corner_coordinate_positions[3][1] += maxY;
        }
    }

    /**
     * 判断按下的点在圆圈内
     *
     * @param x 按下的X坐标
     * @param y 按下的Y坐标
     * @return 返回按到的是哪个点, 没有则返回-1
     * 点阵示意：
     * 0   1
     * 2   3
     */
    private int isInTheCornerCircle(float x, float y) {
        for (int i = 0; i < four_corner_coordinate_positions.length; i++) {
            float a = four_corner_coordinate_positions[i][0];
            float b = four_corner_coordinate_positions[i][1];
            float temp1 = (float) Math.pow((x - a), 2);
            float temp2 = (float) Math.pow((y - b), 2);
            if (((float) roundLength*2) >= Math.sqrt(temp1 + temp2)) {
                return i;
            }
        }
        return -1;
    }
    public interface onImageDetailsSizeChangged {
        void onBorderSizeChangged(int x, int y, int lengthX,int lenghtY);
    }
    public onImageDetailsSizeChangged onImageDetailsSizeChanggedl;
    public void setonImageDetailsSizeChangged(onImageDetailsSizeChangged onImageDetailsSizeChangged) {
        this.onImageDetailsSizeChanggedl = onImageDetailsSizeChangged;
    }

}
