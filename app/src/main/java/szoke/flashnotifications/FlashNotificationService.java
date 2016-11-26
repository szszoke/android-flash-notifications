package szoke.flashnotifications;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;

public class FlashNotificationService extends IntentService {
    private final static String TAG = "flashnotifications";

    private CameraManager cameraManager;
    private String cameraId;

    public FlashNotificationService() {
        super("FlashNotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[1];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Flashing LED");

        try {
            cameraManager.setTorchMode(cameraId, true);
            cameraManager.setTorchMode(cameraId, false);

            Intent restartNotification = new Intent();
            restartNotification.setAction("restartNotification");
            sendBroadcast(restartNotification);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
