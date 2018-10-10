package cn.ypz.com.rxwechattools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

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
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

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
     * Instantiates a new Rx we chat tools.
     */
    protected RxWeChatTools() {

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
     * WeChat with text sharing
     * 不带描述图片的文本分享
     *
     * @param message     the message
     * @param description the description
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareText(String message, String description, String transaction, SendMessageToWXReqType toWXReqType) {
        shareTextByThumb(null, message, description, transaction, toWXReqType);
    }

    /**
     * WeChat with text sharing
     * Share text by thumb.
     *
     * @param thumbBitmap    the thumb bitmap
     * @param message        the message
     * @param description    the description
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareTextByThumb(Bitmap thumbBitmap, String message, String description, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
        shareTextByThumb(bitmap2Bytes(thumbBitmap, compressFormat), message, description, transaction, toWXReqType);
    }

    /**
     * WeChat with text sharing
     * Share text by thumb.
     *
     * @param thumbDates  the thumb dates
     * @param message     the message
     * @param description the description
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareTextByThumb(byte[] thumbDates, String message, String description, String transaction, SendMessageToWXReqType toWXReqType) {
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(message)) message = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/textByThumb";
        WXTextObject textObject = new WXTextObject();
        textObject.text = message;
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.description = description;
        if (thumbDates != null)
            mediaMessage.thumbData = thumbDates;
        sendWeChat(mediaMessage, transaction, toWXReqType);
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
    public void shareImage(Bitmap thumbBitmap, String description, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
        if (thumbBitmap == null) shareText(description, description, transaction, toWXReqType);
        else
            shareImage(bitmap2Bytes(thumbBitmap, compressFormat), description, transaction, toWXReqType);
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
    public void shareScareImage(Bitmap thumbBitmap, int width, int height, String description, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
        if (thumbBitmap == null) shareText(description, description, transaction, toWXReqType);
        else
            shareImage(bitmap2Bytes(scaledBitmap(thumbBitmap, width, height), compressFormat), description, transaction, toWXReqType);
    }

    /**
     * Share image.
     *
     * @param thumbDates  the thumb dates
     * @param description the description
     * @param transaction the transaction
     * @param toWXReqType the to wx req type
     */
    public void shareImage(byte[] thumbDates, String description, String transaction, SendMessageToWXReqType toWXReqType) {
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/img";
        if (TextUtils.isEmpty(description)) description = "";
        WXImageObject wxImageObject = new WXImageObject(thumbDates);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxImageObject;
        msg.description = description;
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
    public void shareMusic(String url, String title, String description, Bitmap thumbBitmap, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
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
    public void shareVideoUrl(String url, String title, String description, Bitmap thumbBitmap, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
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
    public void shareVideoFile(String path, String title, String description, Bitmap thumbBitmap, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
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
     * @param thumUrl        the thum url
     * @param videoUrl       the video url
     * @param title          the title
     * @param description    the description
     * @param bitmap         the bitmap
     * @param transaction    the transaction
     * @param toWXReqType    the to wx req type
     * @param compressFormat the compress format
     */
    public void shareVideoGame(String path, String thumUrl, String videoUrl, String title, String description, Bitmap bitmap, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/video_game";
        WXGameVideoFileObject wxGameVideoFileObject = new WXGameVideoFileObject(path, videoUrl, thumUrl);
        WXMediaMessage msg = new WXMediaMessage(wxGameVideoFileObject);
        msg.title = title;
        msg.description = description;
        if (bitmap != null)
            msg.thumbData = bitmap2Bytes(bitmap, compressFormat);
        sendWeChat(msg, transaction, toWXReqType);
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
    protected void sharpeMiniProgram(String miniUrl, String miniId, String miniPath, String title, String description, Bitmap bitmap, Bitmap.CompressFormat compressFormat, SendMessageToWXReqType sendMessageToWXReqType, String transaction, MiniType miniType) {
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
        byte[] bitmapSize = null;
        if (bitmap != null) {
            bitmapSize = bitmap2Bytes(bitmap, compressFormat);
            int size = 1024 * 127;
            int length = bitmapSize.length;
            if (length >= size) {
                double prang = bitmapSize.length / size;
                prang += 1;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) prang;
                bitmapSize = bitmap2Bytes(BitmapFactory.decodeByteArray(bitmapSize, 0, length, options), compressFormat);
            }
            msg.thumbData = bitmapSize;
        }
        sendWeChat(msg, transaction, sendMessageToWXReqType);
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
    protected byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baas = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baas);
        return baas.toByteArray();
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
     * Pay.
     *
     * @param baseResp the base resp
     */
    public abstract void pay(BaseResp baseResp);


}
