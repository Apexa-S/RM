package jmini3d.demo.gvr.scenes;

import android.content.Context;

import jmini3d.Color4;
import jmini3d.CubeMapTexture;
import jmini3d.Object3d;
import jmini3d.Scene;
import jmini3d.Vector3;
import jmini3d.android.loader.ObjLoader;
import jmini3d.geometry.SkyboxGeometry;
import jmini3d.geometry.VariableGeometry;
import jmini3d.light.AmbientLight;
import jmini3d.light.PointLight;
import jmini3d.material.Material;
import jmini3d.material.PhongMaterial;

public class ObjLoaderScene extends Scene {

	private Object3d o3d;
	private float angle = 0;

	private Vector3 forward = new Vector3();
	private Vector3 side = new Vector3();
	private Vector3 up = new Vector3();

	public ObjLoaderScene(Context ctx) {
		// For VR the camera target is always constant, the position can be changed
		camera.setPosition(0, 0, 0);
		camera.setTarget(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z - 1f);
		camera.setUpAxis(0, 1, 0);

		CubeMapTexture envMap = new CubeMapTexture(new String[]{"posx.png", "negx.png",  "posz.png", "negz.png","posy.png", "negy.png"});

		VariableGeometry skyboxGeometry = new SkyboxGeometry(200);
		Material skyboxMaterial = new Material();
		skyboxMaterial.setEnvMap(envMap, 0);
		skyboxMaterial.setUseEnvMapAsMap(true);
		Object3d skybox = new Object3d(skyboxGeometry, skyboxMaterial);
		addChild(skybox);

		addLight(new AmbientLight(new Color4(255, 255, 255), 0.1f));
		addLight(new PointLight(new Vector3(0, 0, 0), new Color4(255, 255, 255), 1.0f));

		// A material for the OBJ model
		Color4 white = new Color4(255, 255, 255, 255);
		PhongMaterial mirrorMat = new PhongMaterial(white, white, white);
		mirrorMat.setEnvMap(envMap, 1f);

		// Load an OBJ model, using the OBJ2Class method is faster, but it does not work for big geometries
		ObjLoader loader = new ObjLoader();
		try {
			VariableGeometry myGeometry = loader.load(ctx.getAssets().open("monkey.obj"));
			o3d = new Object3d(myGeometry, mirrorMat);
			o3d.setPosition(0, 0, -6);
			addChild(o3d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(long timeElapsed) {
		// Rotate the object
		angle += 0.001 * timeElapsed;
		forward.setAll(1f * (float) Math.cos(angle), 0, -1 * (float) Math.sin(angle));
		up.setAll(0, 1, 0);
		side.setAll(1 * (float) Math.sin(angle), 0, 1 * (float) Math.cos(angle));

		o3d.setRotationMatrix(forward, up, side);
	}
}
