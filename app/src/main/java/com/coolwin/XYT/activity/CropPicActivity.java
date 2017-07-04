package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.rxbus.Transmission;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.CropPicBinding;
import com.coolwin.library.view.ChoiceBorderView;
import com.facebook.fresco.helper.Phoenix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gorden.rxbus2.RxBus;

import static android.graphics.Bitmap.createBitmap;
import static com.coolwin.XYT.Entity.constant.Constants.UPDATEMYINDEXPIC_PIC;

/**
 * Created by dell on 2017/6/29.
 */

public class CropPicActivity extends BaseActivity {
    public static final String DATA="data";
    public static final String POSITION="position";
    CropPicBinding binding;
    String imgpath;
    int imgx,imgy,imglengthX,imglenghtY,width,height;
    float modulus;//图片间的比例系数
    int position=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.crop_pic);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("裁减");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("保存");
        imgpath = getIntent().getStringExtra(DATA);
        position = getIntent().getIntExtra(POSITION,-1);
        //自适应本地图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgpath,options);
        width = widthPixels;
        height =  (int)(1.0 *width / options.outWidth * options.outHeight);
        modulus = (1.0f *width / options.outWidth);
        int windowheight = heightPixels-
                binding.titleLayout.llBar.getLayoutParams().height-
                binding.titleLayout.layoutTitle.getLayoutParams().height;
        if(height>windowheight){
            height = windowheight;
            width = (int)(1.0 *height / options.outHeight * options.outWidth);
            modulus = (1.0f *height / options.outHeight);
        }
        //加载图片
        Phoenix.with(binding.backgroundpic)
                .setHeight(height).setWidth(width)
                .load(imgpath);
        //创建网格
        final Bitmap icon = createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 初始化画布绘制的图像到icon上
        Canvas canvas = new Canvas(icon);
        // 建立画笔
        Paint photoPaint = new Paint();
        photoPaint.setColor(Color.rgb(218,218,218));
        // 获取更清晰的图像采样，防抖动
        photoPaint.setDither(true);
        // 过滤一下，抗剧齿
        photoPaint.setFilterBitmap(true);
        //画出四个网格图
        canvas.drawLine((width/4),0,(width/4),height,photoPaint);
        canvas.drawLine((width/2),0,(width/2),height,photoPaint);
        canvas.drawLine((width*3/4),0,(width*3/4),height,photoPaint);
        canvas.drawLine(0,(height/4),width,(height/4),photoPaint);
        canvas.drawLine(0,(height/2),width,(height/2),photoPaint);
        canvas.drawLine(0,(height*3/4),width,(height*3/4),photoPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        binding.backgroundline.setImageBitmap(icon);
        binding.crop.setonImageDetailsSizeChangged(new ChoiceBorderView.onImageDetailsSizeChangged() {
            @Override
            public void onBorderSizeChangged(int x, int y, int lengthX, int lenghtY) {
                imgx = x;
                imgy = y;
                imglengthX = lengthX;
                imglenghtY = lenghtY;
            }
        });
    }

    @Override
    public void right_text(View view) {
        binding.backgroundpic.setDrawingCacheEnabled(true);
        imgx = (int)(imgx - binding.backgroundpic.getX());
        imgy = (int)(imgy - binding.backgroundpic.getY());
        Bitmap bitmap =  Bitmap.createBitmap(binding.backgroundpic.getDrawingCache(),
                imgx,imgy,imglengthX,imglenghtY);
        binding.backgroundpic.setDrawingCacheEnabled(false);
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".jpg");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(position!=-1){
            List<String> imagepath = new ArrayList<>();
            imagepath.add(file.getAbsolutePath());
            RxBus.get().send(UPDATEMYINDEXPIC_PIC,new Transmission(position,imagepath));
        }else{
            Intent intent = new Intent();
            intent.putExtra(DATA,file.getAbsolutePath());
            setResult(RESULT_OK,intent);
        }
        this.finish();
    }
}
