package com.loopme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.loopme.Logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public final class PermissionUtils {

    private PermissionUtils() {}

    // TODO. Refactor.
    public static class GroupedPermissions {

        private GroupedPermissions() {
            this.deniedPermissions = new ArrayList<>();
            this.grantedPermissions = new ArrayList<>();
        }

        private GroupedPermissions(
                List<String> deniedPermissions,
                List<String> grantedPermissions) {
            this.deniedPermissions = deniedPermissions;
            this.grantedPermissions = grantedPermissions;
        }

        private List<String> deniedPermissions;
        private List<String> grantedPermissions;

        public List<String> getDeniedPermissions() {
            return deniedPermissions;
        }

        public List<String> getGrantedPermissions() {
            return grantedPermissions;
        }
    }

    // TODO. Rename.
    public static GroupedPermissions groupPermissions(
            Activity activity,
            String[] permissionsToGroup) {

        if (permissionsToGroup == null)
            return new GroupedPermissions();

        List<String> deniedPermissions = new ArrayList<>();
        List<String> grantedPermissions = new ArrayList<>();

        for (String permission : permissionsToGroup) {
            if (ActivityCompat.checkSelfPermission(activity, permission) == PERMISSION_GRANTED)
                grantedPermissions.add(permission);
            else if (isPermissionInManifest(activity, permission))
                deniedPermissions.add(permission);
        }

        return new GroupedPermissions(deniedPermissions, grantedPermissions);
    }

    // TODO. Refactor.
    private static boolean isPermissionInManifest(Context context, String permission) {
        Set<String> manifestPermissions = getManifestPermissions(context);
        return manifestPermissions != null && manifestPermissions.contains(permission);
    }

    // TODO. Refactor.
    private static Set<String> getManifestPermissions(Context context) {
        if (manifestPermissions == null) {
            try {
                manifestPermissions = new HashSet<>(
                        Arrays.asList(
                                context.getPackageManager()
                                        .getPackageInfo(
                                                context.getPackageName(),
                                                PackageManager.GET_PERMISSIONS
                                        ).requestedPermissions
                        )
                );
            } catch (Exception e) {
                Logging.out(e.toString());
            }
        }

        return manifestPermissions;
    }

    // TODO. Refactor.
    private static Set<String> manifestPermissions;
}
