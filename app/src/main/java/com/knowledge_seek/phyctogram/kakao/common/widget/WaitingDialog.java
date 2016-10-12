package com.knowledge_seek.phyctogram.kakao.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.kakao.util.helper.log.Logger;

import com.knowledge_seek.phyctogram.R;

/**
 * Created by sjw on 2015-11-26.
 */
public class WaitingDialog {
    private static volatile Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final Object waitingDialogLock = new Object();
    private static Dialog waitingDialog;

    private static Dialog getWaitingDialog(Context context){
        synchronized (waitingDialogLock) {
            if(waitingDialog != null){
                return waitingDialog;
            }
            waitingDialog = new Dialog(context, R.style.CustomProgressDialog);
            return waitingDialog;
        }
    }

    public static void showWaitingDialog(Context context){
        showWaitingDialog(context, false);
    }

    public static void showWaitingDialog(final Context context, final boolean cancelable){
        showWaitingDialog(context, cancelable, null);
    }

    public static void showWaitingDialog(final Context context, final boolean cancelable, final DialogInterface.OnCancelListener listener){
        cancelWaitingDialog();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                waitingDialog = getWaitingDialog(context);
                //이곳에 progress dialog 레이아웃을 셋팅한다.
                waitingDialog.setContentView(R.layout.kakao_layout_waiting_dialog);
                waitingDialog.setCancelable(cancelable);
                if (listener != null) {
                    waitingDialog.setOnCancelListener(listener);
                }
                waitingDialog.show();
            }
        });
    }

    public static void cancelWaitingDialog(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if(waitingDialog != null){
                        synchronized (waitingDialogLock){
                            waitingDialog.cancel();
                            waitingDialog = null;
                        }
                    }
                } catch (Exception e){
                    Logger.d(e);
                    Log.d("-진우 ", "WaitingDialog.cancelWaitingDialog() 에러 발생");
                }
            }
        });
    }


}
