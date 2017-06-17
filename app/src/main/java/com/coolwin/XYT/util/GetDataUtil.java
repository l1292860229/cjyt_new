package com.coolwin.XYT.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.coolwin.XYT.Entity.AreaCode;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.constant.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Map;

import static com.coolwin.XYT.util.GsonUtil.gson;

/**
 * Created by Administrator on 2017/1/22.
 */

public class GetDataUtil {
    public static Login getLogin(Context context) {
        String str  = (String) get(context, Constants.LOGIN_SHARED,Constants.LOGIN_RESULT,"");
        if(StringUtil.isNull(str)){
            return null;
        }
        // 对Base64格式的字符串进行解码
        byte[] base64Bytes = Base64.decode(str.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(bais);
            // 从ObjectInputStream中读取Product对象
            // AddNewWord addWord= (AddNewWord ) ois.readObject();
            return (Login) ois.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static AreaCode[] getAreaCode(Context context) {
        AreaCode[] provinces = gson.fromJson(getAssestsFile(context, "AreaCode"), AreaCode[].class);
        return provinces;
    }
    public static void saveLogin(Context context,Login login) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(login);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将Product对象放到OutputStream中
        // 将Product对象转换成byte数组，并将其进行base64编码
        String server = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        put(context,Constants.LOGIN_SHARED,Constants.LOGIN_RESULT,server);
    }
    public static String getUsername(Context context) {
        String str  = (String) get(context, Constants.REMENBER_SHARED,Constants.USERNAME,"");
        if(StringUtil.isNull(str)){
            return null;
        }
        return str;
    }
    public static void saveUsername(Context context,String username) {
        put(context, Constants.REMENBER_SHARED,Constants.USERNAME,username);
    }
    public static String getPassword(Context context) {
        String str  = (String) get(context, Constants.REMENBER_SHARED,Constants.PASSWORD,"");
        if(StringUtil.isNull(str)){
            return null;
        }
        return str;
    }
    public static void savePassword(Context context,String password) {
        put(context, Constants.REMENBER_SHARED,Constants.PASSWORD,password);
    }
    public static void removLogin(Context context) {
        remove(context,Constants.LOGIN_SHARED,Constants.LOGIN_RESULT);
    }
    /**
     * 获得Assests目录下面的文件
     *
     * @param context
     * @param string
     * @return
     */
    public static String getAssestsFile(Context context, String string) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getResources().getAssets().open(string);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "UTF-8");
            return text;
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制文本内容
     *
     * @param context
     * @param string
     */
    public static void copy(Context context, String string) {
        ClipboardManager c = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        c.setPrimaryClip(ClipData.newPlainText("copytext", string));
    }

    public static void put(Context context, String db, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String db, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String db, String key) {
        SharedPreferences sp = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context, String db) {
        SharedPreferences sp = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String db, String key) {
        SharedPreferences sp = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context, String db) {
        SharedPreferences sp = context.getSharedPreferences(db,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }
}
