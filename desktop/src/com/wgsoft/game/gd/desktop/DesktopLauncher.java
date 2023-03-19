package com.wgsoft.game.gd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.wgsoft.game.gd.MyGdxGame;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class DesktopLauncher {
	private static final boolean PACK = false;

	private static void scaleImage(Path path){
		try {
			String local = path.toString().substring(18);
			AffineTransform affineTransform = AffineTransform.getScaleInstance(0.5, 0.5);
			AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage high = ImageIO.read(new File("raw/graphics/high/"+local));
			File tmp = new File("raw/graphics/normal/"+local);
			tmp.getParentFile().mkdirs();
			tmp.createNewFile();
			BufferedImage normal = new BufferedImage(high.getWidth()/2, high.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
			normal = affineTransformOp.filter(high, normal);
			ImageIO.write(normal, "PNG", tmp);
			tmp = new File("raw/graphics/low/"+local);
			tmp.getParentFile().mkdirs();
			tmp.createNewFile();
			BufferedImage low = new BufferedImage(normal.getWidth()/2, normal.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
			low = affineTransformOp.filter(normal, low);
			ImageIO.write(low, "PNG", tmp);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	private static void deleteDirectory(File dir){
		File[] files = dir.listFiles();
		if(files != null) {
			for (File file : files) {
				deleteDirectory(file);
			}
		}
		dir.delete();
	}

	public static void main (String[] arg) throws Exception{
		if(PACK){
			deleteDirectory(new File("raw/graphics/normal"));
			deleteDirectory(new File("raw/graphics/low"));
			File tmp = new File("raw/graphics/normal");
			tmp.mkdirs();
			tmp = new File("raw/graphics/low");
			tmp.mkdirs();
			Files.walk(Paths.get("raw/graphics/high"))
					.filter(Files::isRegularFile)
					.forEach(DesktopLauncher::scaleImage);
			TexturePacker.Settings settings = new TexturePacker.Settings(){{
				combineSubdirectories = true;
				filterMin = Texture.TextureFilter.Linear;
				filterMag = Texture.TextureFilter.Linear;
			}};
			settings.maxWidth = settings.maxHeight = 512;
			TexturePacker.process(settings, "raw/graphics/low", "graphics/low", "skin");
			settings.maxWidth = settings.maxHeight = 1024;
			TexturePacker.process(settings, "raw/graphics/normal", "graphics/normal", "skin");
			settings.maxWidth = settings.maxHeight = 2048;
			TexturePacker.process(settings, "raw/graphics/high", "graphics/high", "skin");
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Greedy Dungeon";
		config.forceExit = false;
		config.width = 300;
		config.height = 550;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
