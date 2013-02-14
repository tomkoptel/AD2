package com.paad.surfacecamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.paad.ad2.R;

public class SurfaceActivity extends Activity {
    private Preview preview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);

        preview = (Preview) findViewById(R.id.preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadCamera();
    }

    private void reloadCamera() {
        try {
            preview.setCamera(Camera.open());
        } catch (RuntimeException e) {
            reloadCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        preview.stopPreviewAndFreeCamera();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        int groupId = 0;
        MenuItem menuItem1 = menu.add(groupId, 0, Menu.NONE, "take photo");
        MenuItem menuItem2 = menu.add(groupId, 0, Menu.NONE, "reload");
        MenuItem menuItem3= menu.add(groupId, 0, Menu.NONE, "save");

        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                preview.takePicture();
                return true;
            }
        });

        menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                preview.resetPreview();
                return true;
            }
        });
        menuItem3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                preview.makeScreenShot();
                return true;
            }
        });

        return true;
    }
}