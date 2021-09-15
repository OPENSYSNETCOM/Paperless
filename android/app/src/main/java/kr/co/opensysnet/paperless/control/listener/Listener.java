package kr.co.opensysnet.paperless.control.listener;

import android.location.Location;
import android.os.Bundle;
import android.os.Message;

public class Listener {
    public interface IFragmentListener {
        void onChangeState(String fragmentState, Bundle bundle);
    }

    public interface ILocationListener {
        void onSendLocation(Location location);
    }

    public interface ITimerListener {
        void onSendTimer();
    }

    public interface IHandlerListener {
        void handleMessage(Message msg);
    }

    public interface IServerResponseListener {
        void onSuccess(String data, Bundle reqType);

        void onFail(String code, Bundle reqType);
    }
}
