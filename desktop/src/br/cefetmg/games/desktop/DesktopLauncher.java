package br.cefetmg.games.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import br.cefetmg.games.GhibliGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
                config.setTitle("Ghibli Shader Studio");
                config.setWindowedMode(800, 800);
		new Lwjgl3Application(new GhibliGame(), config);
	}
}
