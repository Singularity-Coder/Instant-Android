package com.singularitycoder.selfpermissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.singularitycoder.selfpermissions.ui.theme.SelfPermissionsTheme

class MainActivity : ComponentActivity() {

    private val multiplePermissionsResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach { it: Map.Entry<String, @JvmSuppressWildcards Boolean> ->
            Log.i("LOG", "${it.key} = ${it.value}")
        }
    }

    private val singlePermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            println("PERMISSION GRANTED")
        } else {
            println("PERMISSION DENIED")
            showWhyWeNeedPermissions(
                rationaleTitle = "Grant Camera Permissions",
                rationaleDesc = "Please grant this permissions for this App to work properly!"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SelfPermissionsTheme { Surface(color = MaterialTheme.colors.background) { UI() } } }
    }

    @Composable
    private fun UI() = Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val permissionsArray = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        val permissionResult = remember { mutableStateOf("Please grant permissions!") }
        val composeSinglePermissionResult = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) permissionResult.value = "Camera Permission Granted!" else {
                showWhyWeNeedPermissions(
                    rationaleTitle = "Grant Camera Permission",
                    rationaleDesc = "Please grant this permissions for this App to work properly!"
                )
            }
        }
        val composeMultiplePermissionResult = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, @JvmSuppressWildcards Boolean> ->
            var grantedPermissionsCount = 0
            permissions.entries.forEach { it: Map.Entry<String, @JvmSuppressWildcards Boolean> ->
                Log.i("LOG", "${it.key} = ${it.value}")
                if (it.value) grantedPermissionsCount++
            }
            if (permissions.size == grantedPermissionsCount) permissionResult.value = "Permissions Granted" else {
                showWhyWeNeedPermissions(
                    rationaleTitle = "Grant Permissions",
                    rationaleDesc = "Please grant these permissions for this App to work properly!"
                )
            }
        }

        @Composable
        fun Btn(
            text: String,
            onClick: () -> Unit
        ) = Button(modifier = Modifier.fillMaxWidth(), onClick = { onClick.invoke() }) { Text(text = text) }

        Text(text = permissionResult.value)
        VerticalSpaceOf (16.dp)
        Btn(text = "Grant Single Permission 1") { composeSinglePermissionResult.launch(Manifest.permission.CAMERA) }
        VerticalSpaceOf (8.dp)
        Btn(text = "Grant Multiple Permissions 1") { composeMultiplePermissionResult.launch(permissionsArray) }
        VerticalSpaceOf (8.dp)
        Btn(text = "Grant Single Permission 2") { singlePermissionResult.launch(Manifest.permission.CAMERA) }
        VerticalSpaceOf (8.dp)
        Btn(text = "Grant Multiple Permissions 2") { multiplePermissionsResult.launch(permissionsArray) }
        VerticalSpaceOf (8.dp)
        Btn(text = "Grant Single Permission 3") {
            requestPermission2(
                activity = this@MainActivity,
                permission = Manifest.permission.CAMERA,
                rationaleTitle = "Grant Camera Permissions",
                rationaleDesc = "Please grant this permissions for this App to work properly!",
                onPermissionsGranted = { permissionResult.value = "Camera Permissions granted!" }
            )
        }
        VerticalSpaceOf (8.dp)
        Btn(text = "Grant Multiple Permissions 3") {
            requestMultiplePermissions2(
                activity = this@MainActivity,
                permissionsArray = permissionsArray,
                rationaleTitle = "Grant Permissions",
                rationaleDesc = "Please grant these permissions for this App to work properly!",
                onPermissionsGranted = { permissionResult.value = "Permissions granted!" }
            )
        }
    }

    // These of course don't provide feedback on whether the permission is accepted or not until u click again
    private fun requestMultiplePermissions2(
        activity: Activity?,
        permissionsArray: Array<String>,
        rationaleTitle: String,
        rationaleDesc: String,
        onPermissionsGranted: () -> Unit
    ) {
        activity ?: return

        var permissionCount = 0
        var whyPermissionCount = 0

        permissionsArray.forEach { it: String ->
            val hasPermission = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
            val whyThisPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            if (hasPermission) permissionCount++
            if (whyThisPermission) whyPermissionCount++
        }

        fun askPermissions() = ActivityCompat.requestPermissions(activity, permissionsArray, 1001)

        if (permissionsArray.size == permissionCount) onPermissionsGranted.invoke() else {
            // Request permissions
            if (permissionsArray.size == whyPermissionCount) showWhyWeNeedPermissions(rationaleTitle, rationaleDesc) else askPermissions()
        }
    }

    private fun requestPermission2(
        activity: Activity?,
        permission: String,
        rationaleTitle: String,
        rationaleDesc: String,
        onPermissionsGranted: () -> Unit
    ) {
        activity ?: return

        val hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        val whyThisPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

        fun askPermission() = ActivityCompat.requestPermissions(activity, arrayOf(permission), 1002)

        if (hasPermission) onPermissionsGranted.invoke() else {
            // Request permissions
            if (whyThisPermission) showWhyWeNeedPermissions(rationaleTitle, rationaleDesc) else askPermission()
        }
    }

    private fun showWhyWeNeedPermissions(
        rationaleTitle: String,
        rationaleDesc: String
    ) = AlertDialog.Builder(this).apply {
        title = rationaleTitle
        setMessage(rationaleDesc)
        setPositiveButton("Settings") { _, _ -> showAppSettings(this@MainActivity) }
        setNegativeButton("Cancel") { _, _ -> }
        show()
    }

    private fun showAppSettings(context: Context?) {
        context ?: return
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    @Composable
    infix fun ColumnScope.VerticalSpaceOf(height: Dp) = Spacer(modifier = Modifier.height(height = height))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() = SelfPermissionsTheme {}