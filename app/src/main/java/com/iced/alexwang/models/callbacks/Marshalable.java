package com.iced.alexwang.models.callbacks;

import java.io.IOException;

public interface Marshalable {
    byte[] marshal() throws IOException;
}
