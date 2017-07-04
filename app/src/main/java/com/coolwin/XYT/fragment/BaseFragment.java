package com.coolwin.XYT.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.coolwin.XYT.presenter.BasePresenter;

import java.lang.reflect.ParameterizedType;

/**
 * Created by dell on 2017/6/15.
 */

public class BaseFragment<T extends BasePresenter> extends Fragment {
    public T mPresenter;
    public Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        mPresenter = getT(this,0);
        if(mPresenter!=null){
            mPresenter.init(this.getActivity(),this);
        }
    }
    public  <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
//            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showLoading() {
    }
    public void hideLoading() {
    }
}
