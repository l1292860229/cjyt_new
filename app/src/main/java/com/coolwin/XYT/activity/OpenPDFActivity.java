package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.ImageView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.PdfPageBinding;
import com.coolwin.XYT.interfaceview.UIOpenPDF;
import com.coolwin.XYT.presenter.OpenPDFPresenter;

/**
 * 关于我们页面
 * @author dongli
 *
 */
public class OpenPDFActivity extends BaseActivity<OpenPDFPresenter> implements UIOpenPDF {
	PdfPageBinding binding;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url = getIntent().getStringExtra("url");
		binding =  DataBindingUtil.setContentView(this, R.layout.pdf_page);
		binding.titleLayout.setBehavior(this);
		binding.titleLayout.title.setText("文件预览");
		ImageView leftbtn = binding.titleLayout.leftIcon;
		leftbtn.setImageResource(R.drawable.back_icon);
		mPresenter.init(url);
	}

	@Override
	public void showLoading() {
		super.showLoading("下载中....");
	}

	@Override
	public void init(String file) {
//		binding.pdfView.fromFile(new File(file))
//				.enableSwipe(true) // allows to block changing pages using swipe
//				.swipeHorizontal(false)
//				.enableDoubletap(true)
//				.defaultPage(0)
//				.enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//				.password(null)
//				.scrollHandle(null)
//				.enableAntialiasing(true) // improve rendering a little bit on low-res screens
//				// spacing between pages in dp. To define spacing color, set view background
//				.load();
	}
}
