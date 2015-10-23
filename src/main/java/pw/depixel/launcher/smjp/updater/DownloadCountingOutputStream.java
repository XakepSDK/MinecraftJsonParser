package pw.depixel.launcher.smjp.updater;

import com.google.common.primitives.Ints;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.output.CountingOutputStream;
import pw.depixel.launcher.smjp.services.ICallback;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;

@Data
@EqualsAndHashCode(callSuper = true)
public class DownloadCountingOutputStream extends CountingOutputStream {

    private final ICallback callback;
    private final long total;
    private int lastPercentage;
    private int totalPercentage;

    public DownloadCountingOutputStream(OutputStream out, long total, ICallback callback) {
        super(out);

        this.total = total;
        this.callback = callback;
    }

    @Override
    protected void afterWrite(int n) throws IOException {
        super.afterWrite(n);

        totalPercentage = Ints.checkedCast((getByteCount() * 100) / total);
        if (totalPercentage != lastPercentage) {
            lastPercentage = totalPercentage;
            callback.status(new ActionEvent(this, 0, null));
            if (total <= getByteCount()) {
                callback.completed("complete!");
            }
        }
    }
}
