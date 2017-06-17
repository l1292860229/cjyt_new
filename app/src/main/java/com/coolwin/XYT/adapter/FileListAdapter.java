package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.fragment.AbLoadDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.OpenPDFActivity;
import com.coolwin.XYT.databinding.FilelistBinding;
import com.coolwin.XYT.download.DownloadAPI;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Action;

/**
 * Created by dell on 2017/6/16.
 */

public class FileListAdapter extends BaseAdapter<String>{
    public AbLoadDialogFragment loadingfragment;
    public FileListAdapter(Context context,List<String> list) {
        super(context);
        this.mList = list;
    }
    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FilelistBinding filelistBinding =  DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.filelist, parent, false);
        MyRecycleViewHolder myRecycleViewHolder = new MyRecycleViewHolder(filelistBinding.getRoot());
        myRecycleViewHolder.setBinding(filelistBinding);
        return myRecycleViewHolder;
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, final int position) {
        FilelistBinding filelistBinding = (FilelistBinding) holder.getBinding();
        final String filename = mList.get(position).substring(mList.get(position).lastIndexOf("/")+1);
        if(filename.contains("ppt") ){
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.ppt);

        }else if(filename.contains("doc")){
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.word);
        }else if(filename.contains("txt")){
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.text);
        }else if(filename.contains("csv")){
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.csv);
        }else if(filename.contains("pdf")){
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.pdf);
        }else if(filename.contains("rtf")){
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.rtf);
        }else if(filename.contains("xls")){
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.xls);
        }else{
            Phoenix.with(filelistBinding.pic)
                    .load(R.drawable.plugin_camera_no_pictures);
        }
        filelistBinding.look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("url",mList.get(position));
                intent.setClass(context, OpenPDFActivity.class);
                context.startActivity(intent);
            }
        });
        filelistBinding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File outputFile = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS), mList.get(position).substring(mList.get(position).lastIndexOf("\\")+1));
                if (outputFile.exists()) {
                    share(outputFile);
                }else{
                    loadingfragment =  AbDialogUtil.showLoadDialog(context, R.mipmap.ic_load, "分享中...");
                    new DownloadAPI(UrlConstants.BASEURL,null).downloadAPK(mList.get(position), outputFile, new Action() {
                        @Override
                        public void run() throws Exception {
                            loadingfragment.dismiss();
                            share(outputFile);
                        }
                    });
                }
            }
        });
        filelistBinding.setName(filename);
    }
    public void share(File file){
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareIntent.setType("*/*");
            context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
