package com.extendedclip.papi.expansion.sound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class SoundExpansion extends PlaceholderExpansion implements Cacheable {

	private final Map<String, CachedSound> sounds = new ConcurrentHashMap<>();
	private final String VERSION = getClass().getPackage().getImplementationVersion();

	@Override
	@Nonnull
	public String getAuthor() {
		return "clip";
	}

	@Override
	@Nonnull
	public String getIdentifier() {
		return "sound";
	}

	@Override
	@Nonnull
	public String getVersion() {
		return VERSION;
	}

	@Override
	public void clear() {
		sounds.clear();
	}

	@Override
	public String onPlaceholderRequest(Player p, @Nonnull String identifier) {
		
		if (p == null) {
			return "";
		}
		
		boolean announce = false;
		
		if (identifier.startsWith("all_")) {
			announce = true;
			identifier = identifier.replaceFirst(Pattern.quote("all_"), "");
		}
		
		if (sounds.containsKey(identifier)) {
			if (announce) {
				sounds.get(identifier).play();
			} else {
				sounds.get(identifier).play(p);
			}
			return "";
		}
		
		int first = identifier.indexOf("-");
		
		Sound s;
		
		if (first == -1) {
			try {
				String sound = identifier.toUpperCase();
				s = Sound.valueOf(sound);
				CachedSound cs = new CachedSound(s, 10, 1);
				sounds.put(identifier, cs);
				if (announce) {
					cs.play();
				} else {
					cs.play(p);
				}
				return "";
			} catch (Exception ex) {
				return null;
			}
		} else {
			try {
				String sound = identifier.substring(0, first).toUpperCase();
				s = Sound.valueOf(sound);
			} catch (Exception ex) {
				return null;
			}
		}
		
		float volume = 10;
		
		float pitch = 1;
		
		String volPitch = identifier.substring(first+1);
		
		int second = volPitch.indexOf("-");
		
		if (second >= 1) {
			try {
				volume = Float.parseFloat(volPitch.substring(0, second));
			} catch (Exception ignored) {}
			
			try {
				pitch = Float.parseFloat(volPitch.substring(second+1));
			} catch (Exception ignored) {}
		}
		
		CachedSound cs = new CachedSound(s, volume, pitch);
		sounds.put(identifier, cs);
		if (announce) {
			cs.play();
		} else {
			cs.play(p);
		}
		return "";
	}

	private static class CachedSound {

		private final Sound s;
		private final float volume;
		private final float pitch;
		
		public CachedSound(Sound s, float volume, float pitch) {
			this.s = s;
			this.volume = volume;
			this.pitch = pitch;
		}
		
		public void play() {
			for (Player p : Bukkit.getOnlinePlayers()) {
				play(p);
			}
		}
		
		public void play(Player p) {
			p.playSound(p.getLocation(), s, volume, pitch);
		}
	}
	

}
