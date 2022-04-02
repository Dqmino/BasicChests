package tk.dqmino.basicchest.blocking.interfaces;

import java.io.IOException;

public interface Base64Encoder<T> {

    T fromBase64(String data) throws IOException;

    String toBase64(T data);
}
