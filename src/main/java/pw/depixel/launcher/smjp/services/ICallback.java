package pw.depixel.launcher.smjp.services;

import java.awt.event.ActionEvent;

public interface ICallback {
    void completed(String isCompleted);

    void status(ActionEvent actionEvent);
}
