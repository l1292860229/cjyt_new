/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.coolwin.XYT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.widget.PhotoView;


/**
 * 这个是加载大图的类
 */
public class ImagePagerActivity extends BaseActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	ViewPager pager;
	private ImagePagerAdapter imagePagerAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		pager = (ViewPager) findViewById(R.id.pager);
		imagePagerAdapter = new ImagePagerAdapter();
		pager.setAdapter(imagePagerAdapter);
		SharedPreferences mPreferences = this.getSharedPreferences(IMCommon.REMENBER_SHARED, 0);
		mPreferences.edit().putString(IMCommon.FIRSTOPEN,"1").commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {
		private int[] images=new int[]{R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.denglv, R.drawable.denglv};
		private LayoutInflater inflater;
		ImagePagerAdapter() {
			inflater = getLayoutInflater();
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(final ViewGroup view, int position) {
			final View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
			imageView.setImageResource(images[position]);
			view.addView(imageLayout, 0);
			if(position==4){
				Intent intent = new Intent(ImagePagerActivity.this,LoginMainActivity.class);
				ImagePagerActivity.this.startActivity(intent);
				ImagePagerActivity.this.finish();
			}
			Log.e("instantiateItem","position="+position);
			return imageLayout;
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}