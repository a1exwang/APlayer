package com.iced.alexwang.models.callbacks;

import com.iced.alexwang.models.PlayerStatus;

public interface CurrentStatusCallback {
    void onCurrentStatusReceived(PlayerStatus status);
}
