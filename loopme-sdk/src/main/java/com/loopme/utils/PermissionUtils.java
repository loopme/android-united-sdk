package com.loopme.utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.loopme.Logging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class PermissionUtils {

    private PermissionUtils() {}

    // TODO. Refactor.
    public static class GroupedPermissions {
        private final String[] deniedPermissions;
        public String[] getDeniedPermissions() { return deniedPermissions; }

        private final String[] grantedPermissions;
        public String[] getGrantedPermissions() { return grantedPermissions; }

        private GroupedPermissions(@NonNull String[] denied, @NonNull String[] granted) {
            deniedPermissions = denied;
            grantedPermissions = granted;
        }
    }

    private static Set<String> manifestPermissions;

    // TODO. Rename.
    public static GroupedPermissions groupPermissions(Activity activity, String[] permissionsToGroup) {
        String[] permissions = permissionsToGroup == null ? new String[0] : permissionsToGroup;
        if (manifestPermissions == null) {
            try {
                manifestPermissions = new HashSet<>(Arrays.asList(activity
                    .getPackageManager()
                    .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS)
                    .requestedPermissions
                ));
            } catch (Exception e) {
                Logging.out(e.toString());
                manifestPermissions = new HashSet<>();
            }
        }
        Set<String> deniedPermissions = new HashSet<>();
        Set<String> grantedPermissions = new HashSet<>();

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) == PERMISSION_GRANTED)
                grantedPermissions.add(permission);
            else if (manifestPermissions.contains(permission))
                deniedPermissions.add(permission);
        }
        return new GroupedPermissions(
            deniedPermissions.toArray(new String[0]),
            grantedPermissions.toArray(new String[0])
        );
    }
}
