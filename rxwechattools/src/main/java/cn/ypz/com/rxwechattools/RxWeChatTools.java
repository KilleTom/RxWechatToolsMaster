package cn.ypz.com.rxwechattools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXGameVideoFileObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoFileObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Rx we chat tools.
 *
 * @author KilleTom Created by 易庞宙 on 2018 2018/9/29 16:27 email: 1986545332@qq.com
 */
public abstract class RxWeChatTools {

    /**
     * The Context.
     */
    protected Context context;
    /**
     * The Iwxapi.
     */
    protected IWXAPI iwxapi;
    /**
     * The App id.
     */
    protected String appId;
    /**
     * The Secret.
     */
    protected String secret;
    /**
     * The Grant type.
     */
    protected String grant_type;

    /**
     * The Pay callbacks.
     */
    protected List<PayCallback> payCallbacks;

    /**
     * Instantiates a new Rx we chat tools.
     */
    protected RxWeChatTools() {
        payCallbacks = new ArrayList<>();
    }


    /**
     * Init application.
     * this void beast wast init in application
     *
     * @param context    the context
     * @param appId      the app id
     * @param secret     the secret
     * @param grant_type the grant type
     */
    public void initApplication(Context context, String appId, String secret, String grant_type) {
        if (context != null && this.context == null) {
            this.context = context;
            this.appId = appId;
            this.secret = secret;
            if (TextUtils.isEmpty(grant_type)) grant_type = "authorization_code";
            this.grant_type = grant_type;
            iwxapi = WXAPIFactory.createWXAPI(context, appId, true);
            iwxapi.registerApp(appId);
        }
    }

    /**
     * Add pay callback.
     * 添加微信支付回调
     *
     * @param payCallback the pay callback
     */
    public void addPayCallback(PayCallback payCallback) {
        if (!payCallbacks.contains(payCallback))
            payCallbacks.add(payCallback);
    }

    /**
     * Remove pay callback.
     * 当不需要的回调随着页面或者某些服务被销毁回收时，调用此方法避免内存溢出
     *
     * @param payCallback the pay callback
     */
    public void removePayCallback(PayCallback payCallback) {
        if (payCallbacks.contains(payCallback)) payCallbacks.remove(payCallback);
    }

    /**
     * WeChat with text sharing
     * 不带描述图片的文本分享
     *
     * @param message     the message
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareText(String message, String transaction, SendMessageToWXReqType toWXReqType) {
        shareTextByThumb(null,"", message, transaction, "", toWXReqType,null);
    }

    /**
     * WeChat with text sharing
     * Share text by thumb.
     *
     * @param thumbBitmap    the thumb bitmap
     * @param description    the description
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */

    public void shareTextByThumb(Bitmap thumbBitmap,String title, String description, String transaction, String url, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/textByThumb";
        WXMediaMessage mediaMessage = new WXMediaMessage();
        if (thumbBitmap != null) {
            if (TextUtils.isEmpty(title)&&context!=null) title = context.getApplicationInfo().name;
            if (TextUtils.isEmpty(url)) url = "https://www.baidu.com";
            mediaMessage.mediaObject = new WXWebpageObject(url);
            mediaMessage.setThumbImage(safeThumb(thumbBitmap,compressFormat));
            mediaMessage.title = title;
        } else {
            mediaMessage.mediaObject = new WXTextObject(description);
        }
        mediaMessage.description = description;
        sendWeChat(mediaMessage, transaction, toWXReqType);
    }


    public void shareImage(int resourcesId, String description, String transaction, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        shareImage(BitmapFactory.decodeResource(context.getResources(), resourcesId), description, transaction, toWXReqType, compressFormat);
    }

    /**
     * Share image.
     *
     * @param thumbBitmap    the bitmap
     * @param description    the description
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareImage(Bitmap thumbBitmap, String description, String transaction, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        if (thumbBitmap == null) shareText(description, transaction, toWXReqType);
        else
            shareImage(bitmap2Bytes(thumbBitmap, compressFormat), transaction, toWXReqType);
    }

    /**
     * Share scare image.
     *
     * @param thumbBitmap    the thumb bitmap
     * @param width          the width
     * @param height         the height
     * @param description    the description
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareScareImage(Bitmap thumbBitmap, int width, int height, String description, String transaction, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        if (thumbBitmap == null) shareText(description, transaction, toWXReqType);
        else
            shareImage(bitmap2Bytes(scaledBitmap(safeThumb(thumbBitmap,compressFormat), width, height), compressFormat),  transaction, toWXReqType);
    }

    /**
     * Share image.
     *
     * @param thumbDates  the thumb dates
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareImage(byte[] thumbDates,  String transaction, SendMessageToWXReqType toWXReqType) {
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/img";
        WXImageObject wxImageObject = new WXImageObject(thumbDates);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxImageObject;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        sendWeChat(msg, transaction, toWXReqType);
    }

    /**
     * Share music.
     *
     * @param url         the url
     * @param title       the title
     * @param description the description
     * @param thumbDates  the thumb dates
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareMusic(String url, String title, String description, byte[] thumbDates, String transaction, SendMessageToWXReqType toWXReqType) {
        WXMusicObject wxMusicObject = new WXMusicObject();
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(url)) url = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/music";
        wxMusicObject.musicUrl = url;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxMusicObject;
        msg.title = title;
        msg.description = description;
        if (thumbDates != null)
            msg.thumbData = thumbDates;
        sendWeChat(msg, transaction, toWXReqType);
    }

    /**
     * Share music.
     *
     * @param url            the url
     * @param title          the title
     * @param description    the description
     * @param thumbBitmap    the thumb bitmap
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareMusic(String url, String title, String description, Bitmap thumbBitmap, String transaction, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        shareMusic(url, title, description, bitmap2Bytes(thumbBitmap, compressFormat), transaction, toWXReqType);
    }

    /**
     * Share video url.
     *
     * @param url            the url
     * @param title          the title
     * @param description    the description
     * @param thumbBitmap    the bitmap
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareVideoUrl(String url, String title, String description, Bitmap thumbBitmap, String transaction, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        shareVideoUrl(url, title, description, bitmap2Bytes(thumbBitmap, compressFormat), transaction, toWXReqType);
    }

    /**
     * Share video url.
     *
     * @param url         the url
     * @param title       the title
     * @param description the description
     * @param thumbDates  the thumb dates
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareVideoUrl(String url, String title, String description, byte[] thumbDates, String transaction, SendMessageToWXReqType toWXReqType) {
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(url)) url = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/video_url";
        WXVideoObject wxVideoObject = new WXVideoObject();
        wxVideoObject.videoUrl = url;
        WXMediaMessage msg = new WXMediaMessage(wxVideoObject);
        msg.title = title;
        msg.description = description;
        if (thumbDates != null)
            msg.thumbData = thumbDates;
        sendWeChat(msg, transaction, toWXReqType);
    }

    /**
     * Share video file.
     *
     * @param path           the path
     * @param title          the title
     * @param description    the description
     * @param thumbBitmap    the bitmap
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareVideoFile(String path, String title, String description, Bitmap thumbBitmap, String transaction, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        shareVideoFile(path, title, description, bitmap2Bytes(thumbBitmap, compressFormat), transaction, toWXReqType);
    }

    /**
     * Share video file.
     *
     * @param path        the path
     * @param title       the title
     * @param description the description
     * @param thumbDates  the thumb dates
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareVideoFile(String path, String title, String description, byte[] thumbDates, String transaction, SendMessageToWXReqType toWXReqType) {
        if (TextUtils.isEmpty(path)) throw new RuntimeException("文件路径不能为空");
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/video_file";
        WXVideoFileObject wxVideoObject = new WXVideoFileObject();
        wxVideoObject.filePath = path;
        WXMediaMessage msg = new WXMediaMessage(wxVideoObject);
        msg.title = title;
        msg.description = description;
        if (thumbDates != null)
            msg.thumbData = thumbDates;
        sendWeChat(msg, transaction, toWXReqType);
    }

    /**
     * Share video game.
     *
     * @param path           the path
     * @param thumbUrl        the thum url
     * @param videoUrl       the video url
     * @param title          the title
     * @param description    the description
     * @param bitmap         the bitmap
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareVideoGame(String path, String thumbUrl, String videoUrl, String title, String description, Bitmap bitmap, String transaction, SendMessageToWXReqType toWXReqType, CompressFormat compressFormat) {
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/video_game";
        WXGameVideoFileObject wxGameVideoFileObject = new WXGameVideoFileObject(path, videoUrl, thumbUrl);
        WXMediaMessage msg = new WXMediaMessage(wxGameVideoFileObject);
        msg.title = title;
        msg.description = description;
        if (bitmap != null)
            msg.thumbData = bitmap2Bytes(bitmap, compressFormat);
        sendWeChat(msg, transaction, toWXReqType);
    }

    /**
     * Sharpe mini program.
     *
     * @param miniUrl                the mini url
     * @param miniId                 the mini id
     * @param miniPath               the mini path
     * @param title                  the title
     * @param description            the description
     * @param bitmap                 the bitmap
     * @param compressFormat         the compress format
     * @param sendMessageToWXReqType the send message to wx req type
     * @param transaction            the transaction
     * @param miniType               the mini type
     */
    public void shareMiniProgram(String miniUrl, String miniId, String miniPath, String title, String description, Bitmap bitmap, CompressFormat compressFormat, SendMessageToWXReqType sendMessageToWXReqType, String transaction, MiniType miniType) {
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/mini";
        WXMiniProgramObject wxMiniProgramObject = new WXMiniProgramObject();
        wxMiniProgramObject.webpageUrl = miniUrl;
        wxMiniProgramObject.userName = miniId;
        wxMiniProgramObject.path = miniPath;
        if (miniType == MiniType.MINI_RELEASE) wxMiniProgramObject.miniprogramType = 0;
        else if (miniType == MiniType.MINI_TEST) wxMiniProgramObject.miniprogramType = 1;
        else wxMiniProgramObject.miniprogramType = 2;
        WXMediaMessage msg = new WXMediaMessage(wxMiniProgramObject);
        msg.title = title;
        msg.description = description;
        if (bitmap != null) {
            msg.thumbData = bitmap2Bytes(bitmap, compressFormat);
        }
        sendWeChat(msg, transaction, sendMessageToWXReqType);
    }

    /**
     * Send we chat.
     *
     * @param mediaMessage           the media message
     * @param transaction            the transaction
     * @param sendMessageToWXReqType the send message to wx req type
     */
    protected void sendWeChat(WXMediaMessage mediaMessage, String transaction, SendMessageToWXReqType sendMessageToWXReqType) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = mediaMessage;
        req.transaction = transaction;
        if (sendMessageToWXReqType == null) req.scene = SendMessageToWX.Req.WXSceneSession;
        else
            switch (sendMessageToWXReqType) {
                case WXSceneSession:
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    break;
                case WXSceneTimeline:
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                    break;
                case WXSceneFavorite:
                    req.scene = SendMessageToWX.Req.WXSceneFavorite;
                    break;
                case WXSceneSpecifiedContact:
                    req.scene = SendMessageToWX.Req.WXSceneSpecifiedContact;
                    break;
            }
        iwxapi.sendReq(req);
    }


    /**
     * The enum Send message to wx req type.
     */
    public enum SendMessageToWXReqType {
        /**
         * Wx scene session send message to wx req type.
         */
        WXSceneSession,
        /**
         * Wx scene timeline send message to wx req type.
         */
        WXSceneTimeline,
        /**
         * Wx scene favorite send message to wx req type.
         */
        WXSceneFavorite,
        /**
         * Wx scene specified contact send message to wx req type.
         */
        WXSceneSpecifiedContact;
    }

    /**
     * The enum Mini type.
     */
    public enum MiniType {
        /**
         * Mini release mini type.
         */
        MINI_RELEASE,
        /**
         * Mini test mini type.
         */
        MINI_TEST,
        /**
         * Mini preview mini type.
         */
        MINI_PREVIEW
    }


    /**
     * bitmap 转 byteArr
     *
     * @param bitmap bitmap对象
     * @param format 格式
     * @return 字节数组 byte [ ]
     */
    protected byte[] bitmap2Bytes(Bitmap bitmap, CompressFormat format) {
        if (bitmap == null) return null;
        bitmap = safeThumb(bitmap, format);
        ByteArrayOutputStream baas = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baas);
        return baas.toByteArray();
    }

    protected Bitmap safeThumb(Bitmap bitmap, CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baas = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baas);
        Bitmap.Config config = bitmap.getConfig();
        int length = baas.toByteArray().length;
        Log.i("ypz", baas.toByteArray().length + "");
        Log.i("ypz", length + "");
        int max = 30720;
        switch (config) {
            case ALPHA_8:
            case RGB_565:
                max *= 2;
                break;
            case ARGB_4444:
                max *= 8;
                break;
            case ARGB_8888:
                max *= 16;
                break;
        }
        if (length > max) {
            float range = length / max;
            baas.reset();
            int width = (int) (bitmap.getWidth() / range);
            int h = (int) (bitmap.getHeight() / range);
            return Bitmap.createScaledBitmap(bitmap, width, h, true);
        } else return bitmap;
    }

    /**
     * Scaled bitmap bitmap.
     *
     * @param bitmap the bitmap
     * @param width  the width
     * @param height the height
     * @return the bitmap
     */
    protected Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * Iwaxapi handle intent.
     *
     * @param intent             the intent
     * @param iwxapiEventHandler the iwxapi event handler
     */
    public void iwaxapiHandleIntent(Intent intent, IWXAPIEventHandler iwxapiEventHandler) {
        iwxapi.handleIntent(intent, iwxapiEventHandler);
    }

    /**
     * Login.
     *
     * @param resp the resp
     */
    public abstract void login(BaseResp resp);

    /**
     * Pay.微信支付回调
     *
     * @param baseResp the base resp
     */
    public void payCall(BaseResp baseResp) {
        int errorCode = baseResp.errCode;
        if (payCallbacks == null && payCallbacks.size() == 0) {
            return;
        }
        if (errorCode == 0) {
            for (PayCallback callback : payCallbacks) {
                callback.queryServerPurchaseOrder();
            }
        } else if (errorCode == -1) {
            for (PayCallback callback : payCallbacks) {
                callback.notFinshServerPurchaseOrder();
            }
        } else if (errorCode == -2) {
            for (PayCallback callback : payCallbacks) {
                callback.cancleServerPurchaseOrder();
            }
        }
    }

    /**
     * Pay send wechat.
     *
     * @param partnerId    the partner id
     * @param prepayId     the prepay id
     * @param packageValue the package value
     * @param nonceStr     the nonce str
     * @param timeStamp    the time stamp
     * @param sign         the sign
     */
    public void paySendWeChat(String partnerId, String prepayId, String packageValue, String nonceStr, String timeStamp, String sign) {
        PayReq request = new PayReq();
        request.appId = appId;
        request.partnerId = partnerId;
        request.prepayId = prepayId;
        request.packageValue = packageValue;
        request.nonceStr = nonceStr;
        request.timeStamp = timeStamp;
        request.sign = sign;
        iwxapi.sendReq(request);
    }

    /**
     * The interface Pay callback.
     */
    public interface PayCallback {
        /**
         * 这里写实际微信支付成功后查询服务器订单是否成功
         */
        void queryServerPurchaseOrder();

        /**
         * 这里写实际微信支付未完成的时候回调
         */
        void notFinshServerPurchaseOrder();

        /**
         * 这里写微信拒绝支付或者取消支付时候通知服务器取消订单
         */
        void cancleServerPurchaseOrder();
    }
}
