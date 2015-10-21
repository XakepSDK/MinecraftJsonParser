package pw.depixel.launcher.smjp.updater;

import com.google.common.primitives.Ints;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.output.CountingOutputStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;

@Data
@EqualsAndHashCode(callSuper = true)
public class DownloadCountingOutputStream extends CountingOutputStream {

    private final ActionListener listener;
    private final long total;
    private int totalPercentage;
    private int lastPercentage;

    public DownloadCountingOutputStream(OutputStream out, long total, ActionListener listener) {
        super(out);

        this.total = total;
        this.listener = listener;
    }

    @Override
    protected void afterWrite(int n) throws IOException {
        super.afterWrite(n);

        totalPercentage = Ints.checkedCast((getByteCount() * 100) / total);
        if (totalPercentage % 5 == 0) {
            if (totalPercentage != lastPercentage) {
                lastPercentage = totalPercentage;
                listener.actionPerformed(new ActionEvent(this, 0, null));
            }
        }
    }

    public int getTotalInPercents() {
        return totalPercentage;
    }
}
