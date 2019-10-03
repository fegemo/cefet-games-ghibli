package br.cefetmg.games.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import br.cefetmg.games.GhibliGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
                config.setTitle("Shader Studio Ghibli");
		new Lwjgl3Application(new GhibliGame(), config);
	}
}
