package pe.com.orbis.runtimepermissions_sample.util;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by carlos on 16/06/16.
 * Alias: CarlitosDroid
 */
public class RuntimePermissionUtils {

    final Context ctxt;

    public RuntimePermissionUtils(Context ctxt) {
        this.ctxt = ctxt.getApplicationContext();
    }

    public static boolean useRuntimePermissions() {
        return Build.VERSION.SDK_INT > 22;
    }

    public static boolean hasPermission(Context context, String perm) {
        return !(Build.VERSION.SDK_INT > 22) || ContextCompat.checkSelfPermission(context, perm) == 0;
    }

}
