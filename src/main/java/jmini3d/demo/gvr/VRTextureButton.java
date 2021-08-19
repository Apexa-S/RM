package jmini3d.demo.gvr;

import jmini3d.Blending;
import jmini3d.Object3d;
import jmini3d.Texture;
import jmini3d.Utils;
import jmini3d.Vector3;
import jmini3d.geometry.VariableGeometry;
import jmini3d.material.Material;

/**
 * An animated VR button
 */
class VRTextureButton {

    private final static int TIME_CLICK = 1000; // Time watching to the button after a "click" is made, in ms
    private final static float SCALE_ANIMATION_SPEED = 3f / 10000; // in scalefactor / ms
    Object3d object3d;
    private float heigth;
    private VariableGeometry geometry;
    private float targetScale = 1;
    private boolean lookingToMe = false;
    private long timeLookingToMe;
    private VRClickListener clickListener;
    private float width;
    // Three points defining a quad for the button to check if we are looking to it
    private Vector3 a = new Vector3();
    private Vector3 b = new Vector3();
    private Vector3 c = new Vector3();

    VRTextureButton(float width, float height, String texture, VRClickListener clickListener) {
        this.width = width;
        this.heigth = height;
        this.clickListener = clickListener;

        geometry = new VariableGeometry(4, 2);
        geometry.addQuad(new Vector3(-width / 2, height / 2, 0), new Vector3(width / 2, height / 2, 0), //
                new Vector3(-width / 2, -height / 2, 0), new Vector3(width / 2, -height / 2, 0));

        Material material = new Material(new Texture(texture));
        material.setBlending(Blending.NormalBlending); // Enable transparency

        object3d = new Object3d(geometry, material);
    }

    /**
     * Sets the center of the button
     */
    void setPosition(float x, float y, float z) {
        object3d.setPosition(x, y, z);
        a.setAll(x - width / 2, y + width / 2, z);
        b.setAll(x + width / 2, y + width / 2, z);
        c.setAll(x - width / 2, y - width / 2, z);
    }

    void checkIsLooking(Vector3 cameraPosition, Vector3 lookingDirection) {
        lookingToMe = Utils.lineIntersectsQuad(cameraPosition, lookingDirection, a, b, c);
        targetScale = lookingToMe ? 1.2f : 1;
    }

    void animate(long timeElapsed) {
        float scale = object3d.getScale();
        float scaleDiff = targetScale - scale;
        if (scaleDiff > 0) {
            scale += SCALE_ANIMATION_SPEED * timeElapsed;
            if (scale > targetScale) {
                scale = targetScale;
            }
        }
        if (scaleDiff < 0) {
            scale -= SCALE_ANIMATION_SPEED * timeElapsed;
            if (scale < targetScale) {
                scale = targetScale;
            }
        }
        object3d.setScale(scale);

        // Compute "Clicks"
        if (lookingToMe) {
            timeLookingToMe += timeElapsed;
        } else {
            timeLookingToMe = 0;
        }
        if (timeLookingToMe > TIME_CLICK) {
            if (clickListener != null) {
                clickListener.onClickListener();
            }
            timeLookingToMe = 0;
            object3d.setScale(1);
        }
    }

    interface VRClickListener {
        void onClickListener();
    }
}
