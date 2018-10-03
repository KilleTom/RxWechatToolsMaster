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
 * Created by 易庞宙 on 2018 2018/9/29 16:27
 * email: 1986545332@qq.com
 */
public abstract class RxWeChatTools {

    protected Context context;
    protected IWXAPI iwxapi;
    protected String secret;
    protected String grant_type;


    protected RxWeChatTools() {

    }

    public void initApplication(Context context, String appId, String secret, String grant_type) {
        if (context != null && this.context == null) {
            this.context = context;
            this.secret = secret;
            this.grant_type = grant_type;
            iwxapi = WXAPIFactory.createWXAPI(context, appId, true);
            iwxapi.registerApp(appId);
        }
    }

    public void shareText(String message, String description, String transaction, SendMessageToWXReqType toWXReqType) {
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(message)) message = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/text";
        WXTextObject textObject = new WXTextObject();
        textObject.text = message;
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = textObject;
        mediaMessage.description = description;
        sendWeChat(mediaMessage, transaction, toWXReqType);
    }

    public void shareImage(Bitmap bitmap, String description, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/img";
        if (TextUtils.isEmpty(description)) description = "";
        WXImageObject wxImageObject = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxImageObject;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        msg.thumbData = bitmap2Bytes(thumbBmp, compressFormat);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        sendWeChat(msg, transaction, toWXReqType);
    }

    public void shareMusic(String url, String title, String description, Bitmap bitmap, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
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
        if (bitmap != null)
            msg.thumbData = bitmap2Bytes(bitmap, compressFormat);
        sendWeChat(msg, transaction, toWXReqType);
    }

    public void shareVideoUrl(String url, String title, String description, Bitmap bitmap, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(url)) url = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/video_url";
        WXVideoObject wxVideoObject = new WXVideoObject();
        wxVideoObject.videoUrl = url;
        WXMediaMessage msg = new WXMediaMessage(wxVideoObject);
        msg.title = title;
        msg.description = description;
        if (bitmap != null)
            msg.thumbData = bitmap2Bytes(bitmap, compressFormat);
        sendWeChat(msg, transaction, toWXReqType);
    }

    public void shareVideoFile(String path, String title, String description, Bitmap bitmap, String transaction, SendMessageToWXReqType toWXReqType, Bitmap.CompressFormat compressFormat) {
        if (TextUtils.isEmpty(path)) throw new RuntimeException("文件路径不能为空");
        if (TextUtils.isEmpty(title)) title = "";
        if (TextUtils.isEmpty(description)) description = "";
        if (TextUtils.isEmpty(transaction)) transaction = "RxWeChatTools/video_file";
        WXVideoFileObject wxVideoObject = new WXVideoFileObject();
        wxVideoObject.filePath = path;
        WXMediaMessage msg = new WXMediaMessage(wxVideoObject);
        msg.title = title;
        msg.description = description;
        if (bitmap != null)
            msg.thumbData = bitmap2Bytes(bitmap, compressFormat);
        sendWeChat(msg, transaction, toWXReqType);
    }

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


    public enum SendMessageToWXReqType {
        WXSceneSession,
        WXSceneTimeline,
        WXSceneFavorite,
        WXSceneSpecifiedContact;
    }

    public enum MiniType {
        MINI_RELEASE,
        MINI_TEST,
        MINI_PREVIEW
    }


    /**
     * bitmap 转 byteArr
     *
     * @param bitmap bitmap对象
     * @param format 格式
     * @return 字节数组
     */
    protected byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    public void iwaxapiHandleIntent(Intent intent, IWXAPIEventHandler iwxapiEventHandler){
        iwxapi.handleIntent(intent,iwxapiEventHandler);
    }

    public abstract void login(BaseResp resp);

    public abstract void pay(BaseResp baseResp);



}
